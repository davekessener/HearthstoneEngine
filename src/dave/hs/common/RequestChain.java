package dave.hs.common;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import dave.hs.game.Request;

public class RequestChain<T>
{
	private final Request mRequest;
	private final Entity mSource;
	private final T mStart;
	private final List<Link> mChanges;
	private final Object[] mArguments;
	private boolean mChanged;
	
	public RequestChain(Request r, Entity s, T t, Object ... a)
	{
		mRequest = r;
		mSource = s;
		mStart = t;
		mChanges = new ArrayList<>();
		mArguments = a;
		mChanged = false;
	}
	
	public Request request() { return mRequest; }
	public Entity source() { return mSource; }
	
	@SuppressWarnings("unchecked")
	public <S> S argument(int i)
	{
		return (S) mArguments[i];
	}
	
	public boolean pollChanged() { boolean r = mChanged; mChanged = false; return r; }
	
	public Stream<Link> effects() { return mChanges.stream(); }
	
	public boolean involved(Entity e) { return effects().anyMatch(l -> l.source == e); }
	
	public T resolve()
	{
		T e = mStart;
		StringBuilder sb = new StringBuilder();
		
		sb.append(e);
		
		for(Link l : mChanges)
		{
			if(l.enabled())
			{
				e = l.transformation.apply(e);
				
				sb.append(" -> ").append(e).append(" (").append(l.source).append(")");
			}
		}
		
//		Logger.DEFAULT.info("Resolving '%s': %s", mRequest, sb.toString());
		
		return e;
	}
	
	@SuppressWarnings("unchecked")
	public void affect(Entity src, Function<T, ?> f)
	{
		mChanges.add(new Link(src, (Function<T, T>) f));
		
		affect();
	}
	
	public void affect()
	{
		mChanged = true;
	}
	
	public class Link
	{
		public final Entity source;
		public final Function<T, T> transformation;
		public final List<Object> blockers;
		
		public Link(Entity source, Function<T, T> transformation)
		{
			this.source = source;
			this.transformation = transformation;
			this.blockers = new ArrayList<>();
		}
		
		public boolean enabled() { return this.blockers.isEmpty(); }
	}
}
