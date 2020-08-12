package dave.hs.game.event;

import dave.hs.common.Event;
import dave.hs.common.EventType;

public class GameStartEvent extends Event
{
	public GameStartEvent()
	{
		super(EventType.GAME_START);
	}
}
