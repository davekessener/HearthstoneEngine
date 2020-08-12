package dave.hs.game.effect;

import dave.hs.Engine;
import dave.hs.common.Effect;
import dave.hs.common.Event;
import dave.hs.common.EventType;
import dave.hs.game.entity.CardEntity;
import dave.hs.game.entity.EffectEntity;
import dave.hs.game.entity.HeroEntity;
import dave.hs.game.event.CastSpellEvent;

public class TriggerSpellEffect extends EffectEntity
{
	private final CardEntity mCard;
	private final Effect mSpell;
	
	public TriggerSpellEffect(Engine e, String id, CardEntity ce, Effect s)
	{
		super(e, id);
		
		mCard = ce;
		mSpell = s;
	}
	
	public CardEntity card() { return mCard; }
	public Effect spell() { return mSpell; }
	
	@Override
	public void notice(Event e)
	{
		if(e.type() == EventType.CAST_SPELL)
		{
			CastSpellEvent cse = (CastSpellEvent) e;
			
			if(cse.card() == mCard)
			{
				HeroEntity p = mCard.player().hero;
				
				mSpell.trigger(game(), p.getSpellPower(), p, cse.target());
			}
		}
	}
}
