package dave.hs.game.event;

import dave.hs.common.Event;
import dave.hs.common.EventType;
import dave.hs.game.entity.CombatEntity;

public class DeathEvent extends Event
{
	private final CombatEntity mEntity;
	
	public DeathEvent(CombatEntity e)
	{
		super(EventType.DEATH);
		
		mEntity = e;
	}
	
	public CombatEntity entity() { return mEntity; }
	
	@Override
	public void resolve()
	{
		mEntity.destroy();
	}
}
