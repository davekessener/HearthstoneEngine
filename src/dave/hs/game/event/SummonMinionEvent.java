package dave.hs.game.event;

import dave.hs.common.Event;
import dave.hs.common.EventType;
import dave.hs.common.Player;
import dave.hs.game.entity.MinionEntity;

public class SummonMinionEvent extends Event
{
	private final MinionEntity mMinion;
	private final int mPosition;
	
	public SummonMinionEvent(MinionEntity e, int p)
	{
		super(EventType.SUMMON_MINION);
		
		mMinion = e;
		mPosition = p;
	}

	public MinionEntity minion() { return mMinion; }
	public int position() { return mPosition; }
	
	@Override
	public void resolve()
	{
		Player p = minion().player();
		
		if(mPosition < 0 || mPosition > p.board.size())
			throw new IllegalStateException();
		
		p.board.add(mPosition, mMinion);
	}
}
