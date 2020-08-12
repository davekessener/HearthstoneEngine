package dave.hs.game.event;

import dave.hs.common.Entity;
import dave.hs.common.Event;
import dave.hs.common.EventType;
import dave.hs.common.RequestChain;
import dave.hs.game.Request;
import dave.hs.game.entity.CombatEntity;

public class DamageEvent extends Event
{
	private final CombatEntity mTarget;
	private final Entity mSource;
	private final int mDamage;
	
	public DamageEvent(CombatEntity target, Entity source, int dmg)
	{
		super(EventType.DAMAGE);
		
		mTarget = target;
		mSource = source;
		mDamage = dmg;
	}
	
	public CombatEntity target() { return mTarget; }
	public Entity source() { return mSource; }
	public int damage() { return mDamage; }
	
	@Override
	public void resolve()
	{
		int dmg = mSource.game().resolve(new RequestChain<>(Request.DAMAGE, mSource, mDamage, mTarget));
		
		if(dmg > 0)
		{
			mTarget.changeLife(-dmg);
		}
	}
}
