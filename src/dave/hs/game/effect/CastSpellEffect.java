package dave.hs.game.effect;

import dave.hs.Engine;
import dave.hs.common.Event;
import dave.hs.common.EventType;
import dave.hs.game.entity.CardEntity;
import dave.hs.game.entity.EffectEntity;
import dave.hs.game.event.CastSpellEvent;
import dave.hs.game.event.PlayCardEvent;

public class CastSpellEffect extends EffectEntity
{
	private final CardEntity mCard;
	
	public CastSpellEffect(Engine e, String id, CardEntity c)
	{
		super(e, id);
		
		mCard = c;
	}
	
	public CardEntity card() { return mCard; }
	
	@Override
	public void notice(Event e)
	{
		if(e.type() == EventType.PLAY_CARD)
		{
			PlayCardEvent pce = (PlayCardEvent) e;
			CardEntity card = pce.player().hand.get(pce.card());
			
			if(card == mCard)
			{
				game().enqueueEvent(new CastSpellEvent(card, pce.target()));
			}
		}
	}
}
