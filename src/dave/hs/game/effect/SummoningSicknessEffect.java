package dave.hs.game.effect;

import dave.hs.Engine;
import dave.hs.common.RequestChain;
import dave.hs.game.Request;
import dave.hs.game.entity.EffectEntity;
import dave.hs.game.entity.MinionEntity;

public class SummoningSicknessEffect extends EffectEntity
{
	private final MinionEntity mMinion;
	
	public SummoningSicknessEffect(Engine e, String id, MinionEntity minion)
	{
		super(e, id);
		
		mMinion = minion;
	}
	
	@Override
	public <T> void affect(RequestChain<T> c)
	{
		if(c.involved(this))
			return;
		
		if(c.request() == Request.CAN_ATTACK_THIS)
		{
			MinionEntity me = (MinionEntity) c.source();
			
			if(me == mMinion && me.turnSummoned() == game().turn())
			{
				c.affect(this, v -> false);
			}
		}
	}
}
