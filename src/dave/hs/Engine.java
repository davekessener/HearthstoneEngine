package dave.hs;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

import dave.hs.common.BaseCardData;
import dave.hs.common.Card;
import dave.hs.common.ComLibrary;
import dave.hs.common.Entity;
import dave.hs.common.Event;
import dave.hs.common.EventType;
import dave.hs.common.Player;
import dave.hs.common.PlayerInfo;
import dave.hs.common.RequestChain;
import dave.hs.game.PlayerType;
import dave.hs.game.effect.ResponseEffect;
import dave.hs.game.entity.CardEntity;
import dave.hs.game.entity.CombatEntity;
import dave.hs.game.entity.DeckEntity;
import dave.hs.game.entity.EffectEntity;
import dave.hs.game.entity.HeroEntity;
import dave.hs.game.event.AttackEvent;
import dave.hs.game.event.CardBurnEvent;
import dave.hs.game.event.CardDrawEvent;
import dave.hs.game.event.GainManaCrystalEvent;
import dave.hs.game.event.GameStartEvent;
import dave.hs.game.event.PayManaEvent;
import dave.hs.game.event.PlayCardEvent;
import dave.hs.game.event.RefreshAllManaEvent;
import dave.hs.game.event.RefreshManaEvent;
import dave.hs.game.event.TurnEndEvent;
import dave.hs.game.event.TurnStartEvent;
import dave.util.RNG;
import dave.util.Utils;
import dave.util.actor.Bus;
import dave.util.actor.LookupAgent;
import dave.util.actor.Message;
import dave.util.actor.SimpleLookupAgent;

public class Engine
{
	private final Bus mBus;
	private final RNG mRandom;
	private final LookupAgent mAgent;
	private final Set<EffectEntity> mEffects;
	private final Queue<Event> mBacklog;
	private final Player mP1, mP2;
	private Player mActivePlayer;
	private boolean mProcessing;
	private int mTurn;
	
	public Engine(Bus bus, PlayerInfo p1, PlayerInfo p2, RNG rng)
	{
		mBus = bus;
		mRandom = rng;
		mAgent = new SimpleLookupAgent("game-engine");
		mEffects = new HashSet<>();
		mBacklog = new ArrayDeque<>();
		mP1 = buildPlayer(PlayerType.PLAYER_1, p1);
		mP2 = buildPlayer(PlayerType.PLAYER_2, p2);
		mActivePlayer = null;
		mProcessing = false;
		mTurn = 0;
		
		mBus.register(mAgent);

		mAgent.registerHandler(ComLibrary.Messages.GAME_PLAY, this::handlePlay);
		mAgent.registerHandler(ComLibrary.Messages.GAME_PASS, this::handlePass);
		mAgent.registerHandler(ComLibrary.Messages.GAME_ATTACK, this::handleAttack);
	}
	
	public RNG random() { return mRandom; }
	public int turn() { return mTurn; }
	public Player activePlayer() { return mActivePlayer; }
	
	public void registerEffect(EffectEntity e) { mEffects.add(e); }
	public void removeEffect(EffectEntity e) { mEffects.remove(e); }
	public Player opponentOf(Player p) { return mP1.id.equals(p.id) ? mP2 : mP1; }
	
	public <T> T resolve(RequestChain<T> c)
	{
		do
		{
			mEffects.forEach(e -> e.affect(c));
		}
		while(c.pollChanged());
		
		return c.resolve();
	}
	
	public void event(Event e)
	{
		if(e.meetsPreconditions())
		{
			mProcessing = true;
			
			eachPlayer(p -> mBus.send(mAgent, p.id, ComLibrary.Messages.EVENT_TRIGGER, e));
		
			do
			{
				List<EffectEntity> effects = new ArrayList<>(mEffects);
				
				effects.stream().filter(ee -> ee.valid()).forEach(ee -> ee.notice(e));
			}
			while(e.pollChanged());
		
			eachPlayer(p -> mBus.send(mAgent, p.id, (e.resolves() ? ComLibrary.Messages.EVENT_RESOLVE : ComLibrary.Messages.EVENT_FAIL), e));
			
			if(e.resolves())
			{
				e.resolve();
			}
			
			mProcessing = false;
		}
		
		while(!mBacklog.isEmpty())
		{
			event(mBacklog.poll());
		}
	}
	
	public void enqueueEvent(Event e)
	{
		mBacklog.add(e);
		
		if(!mProcessing)
		{
			event(mBacklog.poll());
		}
	}
	
	public void start()
	{
		eachPlayer(p -> mBus.send(mAgent, p.id, ComLibrary.Messages.GAME_START, p));
		
		enqueueEvent(new GameStartEvent());
		
		Utils.times(3, i -> mP1.hand.add(mP1.deck.draw()));
		Utils.times(4, i -> mP2.hand.add(mP2.deck.draw()));
		
		// TODO add coin
		
		mTurn = 0;
		startTurn(mP1);
	}
	
	private void handlePlay(Message msg)
	{
		checkPlayer(msg);
		
		Utils.Pair<Integer, CombatEntity> a = msg.payload();
		int card = a.first;
		CombatEntity target = a.second;
		
		if(card < 0 || card >= mActivePlayer.hand.size())
			throw new IllegalArgumentException("Card idx out of bounds: " + card);
		
		if(mActivePlayer.hand.get(card).requiresTarget() && target == null)
			throw new IllegalArgumentException("Card requires target: " + card);
		
		enqueueEvent(new PlayCardEvent(mActivePlayer, card, target));
		
		requestPlayerAction();
	}
	
	private void handlePass(Message msg)
	{
		checkPlayer(msg);
		
		enqueueEvent(new TurnEndEvent(mActivePlayer));
		++mTurn;
		startTurn(opponentOf(mActivePlayer));
	}
	
	private void handleAttack(Message msg)
	{
		checkPlayer(msg);
		
		Player opponent = opponentOf(mActivePlayer);
		Utils.Pair<Integer, Integer> atk = msg.payload();
		Entity from = mActivePlayer.hero;
		Entity to = opponent.hero;
		
		if(atk.first > 0)
		{
			from = mActivePlayer.board.get(atk.first - 1);
		}
		
		if(atk.second > 0)
		{
			to = opponent.board.get(atk.second - 1);
		}
		
		if((from instanceof CombatEntity) && (to instanceof CombatEntity))
		{
			CombatEntity attacker = (CombatEntity) from;
			CombatEntity defender = (CombatEntity) to;
			
			if(attacker.canAttack(defender))
			{
				enqueueEvent(new AttackEvent(this, attacker, defender));
			}
		}
		
		requestPlayerAction();
	}
	
	private void checkPlayer(Message msg)
	{
		if(!mBacklog.isEmpty())
			throw new ConcurrentModificationException();
		
		if(!msg.from.equals(mActivePlayer.id))
			throw new IllegalStateException("Player " + msg.from + " not currently active!");
	}
	
	private void startTurn(Player p)
	{
		mActivePlayer = p;
		
		enqueueEvent(new TurnStartEvent(p));
		
		requestPlayerAction();
	}
	
	private void requestPlayerAction()
	{
		mBus.send(mAgent, mActivePlayer.id, ComLibrary.Messages.GAME_REQUEST, null);
	}
	
	public void draw(Player p)
	{
		int max_hand_size = p.hero.getMaxHandSize();
		CardEntity card = p.deck.draw();
		
		if(p.hand.size() >= max_hand_size)
		{
			burnCard(card);
		}
		else
		{
			enqueueEvent(new CardDrawEvent(p, card));
		}
	}
	
	public void burnCard(CardEntity card)
	{
		enqueueEvent(new CardBurnEvent(card));
	}
	
	private void eachPlayer(Consumer<Player> f) { Stream.of(mP1, mP2).forEach(f); }
	
	public void gainManaCrystals(Player p, int n)
	{
		Utils.times(n, i -> enqueueEvent(new GainManaCrystalEvent(p)));
	}
	
	public void refreshMana(Player p) { refreshMana(p, p.mana_crystals - p.mana); }
	public void refreshMana(Player p, int n)
	{
		Utils.times(n, i -> enqueueEvent(new RefreshManaEvent(p)));
	}
	
	private Player buildPlayer(PlayerType t, PlayerInfo p)
	{
		String pid = t.toString();
		DeckEntity deck = new DeckEntity(this, pid + "-deck");
		Player player = new Player(p.id, t, deck);
		HeroEntity hero = new HeroEntity(this, pid + "-hero-" + p.hero, player);
		
		player.game = this;
		player.hero = hero;
		
		for(int i = 0 ; i < p.cards.length ; ++i)
		{
			Card card = p.cards[i];
			BaseCardData cd = (BaseCardData) card.card();
			
			deck.addCardRandom(new CardEntity(this, pid + "-card-" + cd.name, player, card));
		}
		
		player.hero.addEffect(new ResponseEffect(this, pid + "-gain-mana-at-turn-start", EventType.TURN_START, e -> {
			TurnStartEvent te = (TurnStartEvent) e;
			
			if(te.player() == player)
			{
				if(player.mana_crystals < 10)
				{
					gainManaCrystals(player, 1);
				}
				
				enqueueEvent(new RefreshAllManaEvent(this, player));
			}
		}));
		player.hero.addEffect(new ResponseEffect(this, pid + "-draw-card-at-turn-start", EventType.TURN_START, e -> {
			TurnStartEvent te = (TurnStartEvent) e;
			
			if(te.player() == player)
			{
				draw(player);
			}
		}));
		
		player.hero.addEffect(new ResponseEffect(this, pid + "-discard-excess-mana", EventType.TURN_END, e -> {
			TurnEndEvent te = (TurnEndEvent) e;
			
			if(te.player() == player)
			{
				if(player.mana > player.mana_crystals)
				{
					player.mana = player.mana_crystals;
				}
			}
		}));
		
		player.hero.addEffect(new ResponseEffect(this, pid + "-pays-mana-for-cards", EventType.PLAY_CARD, (self, e) -> {
			PlayCardEvent pce = (PlayCardEvent) e;
			
			if(e.blockers().anyMatch(ee -> ee == self))
				return;
			
			if(pce.player() == player)
			{
				CardEntity card = player.hand.get(pce.card());
				int cost = card.getCost();
				
				if(player.mana >= cost)
				{
					event(new PayManaEvent(player, cost));
				}
				else
				{
					e.block(self);
				}
			}
		}));
		
		return player;
	}
}
