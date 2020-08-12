package dave.hs.common;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import dave.hs.game.entity.EffectEntity;

public abstract class Event
{
	private final EventType mType;
	private final List<EffectEntity> mBlockers;
	private final List<Event> mPreconditions;
	private boolean mChanged;
	
	public Event(EventType type)
	{
		mType = type;
		mBlockers = new ArrayList<>();
		mPreconditions = new ArrayList<>();
		mChanged = false;
	}
	
	public void resolve( ) { }
	
	public EventType type() { return mType; }
	public boolean blocked() { return !mBlockers.isEmpty(); }
	
	public Stream<EffectEntity> blockers() { return mBlockers.stream(); }
	
	public void addCondition(Event e) { mPreconditions.add(e); }
	
	public boolean resolves()
	{
		return !blocked() && meetsPreconditions();
	}
	
	public boolean meetsPreconditions()
	{
		return mPreconditions.stream().allMatch(e -> e.resolves());
	}
	
	public void block(EffectEntity e)
	{
		mBlockers.add(e);
		mChanged = true;
	}
	
	public void unblock(EffectEntity e)
	{
		mBlockers.remove(e);
		mChanged = true;
	}
	
	public boolean pollChanged()
	{
		boolean f = mChanged;
		
		mChanged = false;
		
		return f;
	}
}
