package dave.hs.game.event;

import dave.hs.Engine;
import dave.hs.common.Event;
import dave.hs.common.EventType;
import dave.hs.game.entity.CombatEntity;

public class AttackEvent extends Event
{
	private final Engine mGame;
	private final CombatEntity mFrom, mTo;
	
	public AttackEvent(Engine e, CombatEntity from, CombatEntity to)
	{
		super(EventType.ATTACK);
		
		mGame = e;
		mFrom = from;
		mTo = to;
	}
	
	public CombatEntity attacker() { return mFrom; }
	public CombatEntity defender() { return mTo; }
	
	@Override
	public void resolve()
	{
		if(!mFrom.alive() || !mTo.alive())
			return;
		
		int fatk = mFrom.getAttack();
		int tatk = mTo.getAttack();
		
		mFrom.attack();
		mGame.enqueueEvent(new DamageEvent(mTo, mFrom, fatk));
		mGame.enqueueEvent(new DamageEvent(mFrom, mTo, tatk));
	}
}
