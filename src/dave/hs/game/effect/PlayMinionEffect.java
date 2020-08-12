package dave.hs.game.effect;

import dave.hs.common.Card;
import dave.hs.common.Event;
import dave.hs.common.EventType;
import dave.hs.common.Player;
import dave.hs.game.CardType;
import dave.hs.game.MinionCard;
import dave.hs.game.entity.CardEntity;
import dave.hs.game.entity.EffectEntity;
import dave.hs.game.entity.MinionEntity;
import dave.hs.game.event.PlayCardEvent;
import dave.hs.game.event.SummonMinionEvent;

public class PlayMinionEffect extends EffectEntity
{
	private final CardEntity mCard;
	
	public PlayMinionEffect(CardEntity ce)
	{
		super(ce.game(), ce.id() + "-summon");
		
		mCard = ce;
	}
	
	public Card card() { return mCard.card(); }
	
	@Override
	public void notice(Event e)
	{
		if(e.type() != EventType.PLAY_CARD)
			return;
		
		PlayCardEvent pce = (PlayCardEvent) e;
		CardEntity card = pce.player().hand.get(pce.card());
		
		if(card != mCard)
			return;
		
		if(card.cardData().type != CardType.MINION)
			return;
		
		Player player = card.player();
		MinionCard data = (MinionCard) card.cardData();
		MinionEntity minion = new MinionEntity(game(), player.id + "-minion-" + card.cardData().id, data, player);
		Event summon = new SummonMinionEvent(minion, 0);
		boolean target = card.requiresTarget();
		
		summon.addCondition(e);
		
		game().enqueueEvent(summon);
		
		if(target)
		{
			// TODO battlecry
		}
	}
}
