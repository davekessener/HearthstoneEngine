package dave.hs.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import dave.hs.Engine;
import dave.hs.common.BaseCard;
import dave.hs.common.Card;
import dave.hs.common.CardData;
import dave.hs.common.ComLibrary;
import dave.hs.common.Effect;
import dave.hs.common.HeroPower;
import dave.hs.common.Player;
import dave.hs.common.PlayerInfo;
import dave.hs.game.CardRarity;
import dave.hs.game.HeroClass;
import dave.hs.game.MinionCard;
import dave.hs.game.MinionFamily;
import dave.hs.game.SpellCard;
import dave.hs.game.entity.CardEntity;
import dave.hs.game.entity.CombatEntity;
import dave.hs.game.entity.MinionEntity;
import dave.hs.game.event.DamageEvent;
import dave.hs.game.event.RefreshManaEvent;
import dave.util.RNG;
import dave.util.ShutdownService;
import dave.util.Utils;
import dave.util.ShutdownService.Priority;
import dave.util.XoRoRNG;
import dave.util.actor.Bus;
import dave.util.actor.Message;
import dave.util.actor.SimpleBus;
import dave.util.actor.SimpleLookupAgent;
import dave.util.log.LogBase;
import dave.util.log.LogSink;
import dave.util.log.Logger;
import dave.util.log.Stdout;
import dave.util.stream.StreamUtils;

public class TestApp
{
	public static void main(String[] args)
	{
		LogBase.INSTANCE.registerSink(e -> true, LogSink.build());
		
		LogBase.INSTANCE.start();
		
		ShutdownService.INSTANCE.register(Priority.LAST, LogBase.INSTANCE::stop);
		
		try
		{
			run();
		}
		finally
		{
			ShutdownService.INSTANCE.shutdown();
		}
		
		Stdout.println("Goodbye.");
	}
	
	private static void run()
	{
		RNG rng = new XoRoRNG();
		Bus bus = new SimpleBus();
		HumanPlayer p1 = new HumanPlayer(bus, "dave", true);
		HumanPlayer p2 = new HumanPlayer(bus, "daiki", false);

		bus.register(p1);
		bus.register(p2);
		
		PlayerInfo pi1 = new PlayerInfo(p1.getID(), "Anduinn", new HeroPower("Lesser Heal", null, 2), generateDeck(rng));
		PlayerInfo pi2 = new PlayerInfo(p2.getID(), "Rexxar", new HeroPower("Hunty Thing", null, 2), generateDeck(rng));
		
		Engine game = new Engine(bus, pi1, pi2, rng);
		
		game.start();
		
		while(true)
		{
			bus.flush();
		}
	}
	
	private static Card[] generateDeck(RNG rng)
	{
		List<Card> deck = new ArrayList<>();
		
		CardData data_river_crocolisk = new MinionCard("river-crocolisk",
			"River Crocolisk", "A crocodile. LOL", 2, CardRarity.COMMON, HeroClass.NONE, 2, 3, MinionFamily.BEAST);
		CardData data_coin = new SpellCard("coin",
			"The Coin", "Gain 1 Mana this turn only.", 0, CardRarity.BASIC, HeroClass.NONE, false,
			new Effect[] {
				(e, p, s, t) -> e.enqueueEvent(new RefreshManaEvent(s.player()))
		});
		CardData data_fireball = new SpellCard("fireball",
			"Fireball", "Deal 6 damage.", 4, CardRarity.BASIC, HeroClass.MAGE, true,
			new Effect[] {
				(e, p, s, t) -> e.enqueueEvent(new DamageEvent(t, s, p + 6))
		});
		
		Card river_crocolisk = new BaseCard(data_river_crocolisk);
		Card coin = new BaseCard(data_coin);
		Card fireball = new BaseCard(data_fireball);
		
		Card[] cards = new Card[] { river_crocolisk, coin, fireball };
		
		for(int i = 0 ; i < 30 ; ++i)
		{
			deck.add(cards[rng.nextInt(cards.length)]);
		}
		
		return deck.toArray(new Card[deck.size()]);
	}
	
	private static class HumanPlayer extends SimpleLookupAgent
	{
		private final Bus mBus;
		private final boolean mLog;
		private final Scanner mIn;
		private Player mPlayer;
		private String mGameID;
		
		public HumanPlayer(Bus bus, String id, boolean log)
		{
			super(id);
			
			mBus = bus;
			mLog = log;
			mIn = new Scanner(System.in);
			mPlayer = null;
			mGameID = null;

			registerHandler(ComLibrary.Messages.GAME_START, this::startGame);
			registerHandler(ComLibrary.Messages.GAME_REQUEST, this::gameRequest);
			registerHandler(ComLibrary.Messages.EVENT_TRIGGER, this::triggerEvent);
			registerHandler(ComLibrary.Messages.EVENT_RESOLVE, this::resolveEvent);
			registerHandler(ComLibrary.Messages.EVENT_FAIL, this::failEvent);
		}
		
		@Override
		public void accept(Message msg)
		{
			log("Received %s", msg);
			
			super.accept(msg);
		}
		
		private void startGame(Message msg)
		{
			mPlayer = msg.payload();
			mGameID = msg.from;
		}
		
		private void gameRequest(Message msg)
		{
			Utils.sleep(100);
			
			Stdout.printf("Player %s has %d/%d life, %d armor and %d/%d mana.\n", getID(),
					mPlayer.hero.getLife(), mPlayer.max_life, mPlayer.armor, mPlayer.mana, mPlayer.mana_crystals);
			
			mPlayer.board.stream().map(StreamUtils.stream_with_index()).forEach(e -> {
				if(e.value instanceof MinionEntity)
				{
					MinionEntity me = (MinionEntity) e.value;
					
					Stdout.printf("My minion %d: %s %d ATK, %d/%d HP\n", e.index + 1, me.card().name, me.getAttack(), me.getLife(), me.getMaxLife());
				}
				else
				{
					throw new RuntimeException("" + e.value);
				}
			});
			Player opp = mPlayer.game.opponentOf(mPlayer);
			opp.board.stream().map(StreamUtils.stream_with_index()).forEach(e -> {
				if(e.value instanceof MinionEntity)
				{
					MinionEntity me = (MinionEntity) e.value;
					
					Stdout.printf("Opp minion %d: %s %d ATK, %d/%d HP\n", e.index + 1, me.card().name, me.getAttack(), me.getLife(), me.getMaxLife());
				}
				else
				{
					throw new RuntimeException("" + e.value);
				}
			});
			Stdout.printf("Opponent has %d/%d life, %d armor and %d cards in hand.\n",
				opp.hero.getLife(), opp.max_life, opp.armor, opp.hand.size());
			mPlayer.hand.stream().map(StreamUtils.stream_with_index()).forEach(e -> {
				CardEntity card = e.value;
				
				Stdout.printf("Card %d is %s (%d)\n", e.index, card.getName(), card.getCost());
			});
			
			Stdout.printf("[%s] $ ", getID());
			String cmd = mIn.nextLine();
			
			if(cmd.startsWith("quit"))
			{
				System.exit(0);
			}
			else if(cmd.startsWith("play "))
			{
				String[] a = cmd.substring(5).split(" +");
				int card = Integer.parseInt(a[0]);
				boolean me = (a[1].charAt(0) == '+');
				int tidx = Integer.parseInt(a[1].substring(1));
				CombatEntity target = null;
				
				if(me)
				{
					if(tidx == 0) target = mPlayer.hero;
					else target = (CombatEntity) mPlayer.board.get(tidx - 1);
				}
				else
				{
					if(tidx == 0) target = opp.hero;
					else target = (CombatEntity) opp.board.get(tidx - 1);
				}
				
				mBus.send(this, mGameID, ComLibrary.Messages.GAME_PLAY, Utils.pair(card, target));
			}
			else if(cmd.startsWith("pass"))
			{
				mBus.send(this, mGameID, ComLibrary.Messages.GAME_PASS, null);
			}
			else if(cmd.startsWith("attack "))
			{
				String[] a = cmd.substring(7).split(" +");
				int from = Integer.parseInt(a[0]);
				int to = Integer.parseInt(a[1]);
				
				mBus.send(this, mGameID, ComLibrary.Messages.GAME_ATTACK, Utils.pair(from, to));
			}
		}
		
		private void triggerEvent(Message msg)
		{
		}
		
		private void resolveEvent(Message msg)
		{
		}
		
		private void failEvent(Message msg)
		{
		}
		
		private void log(String fmt, Object ... a)
		{
			if(mLog)
			{
				Logger.DEFAULT.info(fmt, a);
			}
		}
	}
}
