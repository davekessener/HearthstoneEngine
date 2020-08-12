package dave.hs.game.event;

import dave.hs.common.Event;
import dave.hs.common.EventType;
import dave.hs.game.entity.CardEntity;
import dave.hs.game.entity.CombatEntity;

public class CastSpellEvent extends Event
{
	private final CardEntity mCard;
	private final CombatEntity mTarget;
	
	public CastSpellEvent(CardEntity e, CombatEntity t)
	{
		super(EventType.CAST_SPELL);
		
		mCard = e;
		mTarget = t;
	}
	
	public CardEntity card() { return mCard; }
	public CombatEntity target() { return mTarget; }
}
