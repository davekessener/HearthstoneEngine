package dave.hs.game.entity;

import dave.hs.Engine;
import dave.hs.common.EventType;
import dave.hs.common.RequestChain;
import dave.hs.game.EntityType;
import dave.hs.game.Request;
import dave.hs.game.effect.ResponseEffect;
import dave.hs.game.event.DeathEvent;

public abstract class CombatEntity extends BaseEntity
{
	private int mLife, mAttack, mAtkCounter;
	private boolean mAlive;
	
	protected CombatEntity(Engine e, String id, EntityType t, int life, int atk)
	{
		super(e, id, t);
		
		mLife = life;
		mAttack = atk;
		mAtkCounter = 0;
		mAlive = true;
		
		addEffect(new ResponseEffect(game(), id + "-reset-atk-counter", EventType.TURN_START, ee -> {
			mAtkCounter = 0;
		}));
	}
	
	public boolean alive() { return mLife > 0 && mAlive; }
	public int getLife( ) { return mLife; }
	public void attack( ) { ++mAtkCounter; }

	public boolean canAttack(CombatEntity e)
	{
		return game().resolve(new RequestChain<>(Request.CAN_ATTACK_THIS, this, mAtkCounter < getMaxAttacks(), e));
	}

	public int getAttack()
	{
		return game().resolve(new RequestChain<>(Request.GET_ATTACK, this, mAttack));
	}
	
	public int getMaxAttacks()
	{
		return game().resolve(new RequestChain<>(Request.MAX_ATTACKS, this, 1));
	}
	
	public abstract int getMaxLife( );
	
	public void changeLife(int d)
	{
		setLife(mLife + d);
	}
	
	public void setLife(int v)
	{
		if(mLife > 0)
		{
			mLife = Math.min(getMaxLife(), v);
			
			if(mLife <= 0)
			{
				game().enqueueEvent(new DeathEvent(this));
			}
		}
	}
}
