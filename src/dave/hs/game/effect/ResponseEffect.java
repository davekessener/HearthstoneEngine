package dave.hs.game.effect;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import dave.hs.Engine;
import dave.hs.common.Event;
import dave.hs.common.EventType;
import dave.hs.game.entity.EffectEntity;

public class ResponseEffect extends EffectEntity
{
	private final EventType mTrigger;
	private final BiConsumer<ResponseEffect, Event> mCallback;

	public ResponseEffect(Engine e, String id, EventType trigger, Consumer<Event> cb)
	{
		this(e, id, trigger, (self, evnt) -> cb.accept(evnt));
	}
	
	public ResponseEffect(Engine e, String id, EventType trigger, BiConsumer<ResponseEffect, Event> cb)
	{
		super(e, id);
		
		mTrigger = trigger;
		mCallback = cb;
	}
	
	@Override
	public void notice(Event e)
	{
		if(e.type() == mTrigger)
		{
			mCallback.accept(this, e);
		}
	}
}
