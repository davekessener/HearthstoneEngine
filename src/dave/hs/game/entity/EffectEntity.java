package dave.hs.game.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import dave.hs.Engine;
import dave.hs.common.Event;
import dave.hs.common.RequestChain;
import dave.hs.game.EntityType;
import dave.hs.game.Request;

public class EffectEntity extends BaseEntity
{
	private final Map<Request, Consumer<RequestChain<?>>> mLUT;
	private boolean mValid;
	
	public EffectEntity(Engine e, String id)
	{
		super(e, id, EntityType.EFFECT);
		
		mLUT = new HashMap<>();
		mValid = true;
	}
	
	public boolean valid() { return mValid; }
	
	public void invalidate() { mValid = false; }
	
	public void register(Request r, Consumer<RequestChain<?>> f)
	{
		mLUT.put(r, f);
	}
	
	public <T> void affect(RequestChain<T> c)
	{
		Consumer<RequestChain<?>> f = mLUT.get(c.request());
		
		if(f != null)
		{
			f.accept(c);
		}
	}
	
	public void notice(Event e)
	{
	}
}
