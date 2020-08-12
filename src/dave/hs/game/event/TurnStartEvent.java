package dave.hs.game.event;

import dave.hs.common.Event;
import dave.hs.common.EventType;
import dave.hs.common.Player;

public class TurnStartEvent extends Event
{
	private final Player mPlayer;
	
	public TurnStartEvent(Player p)
	{
		super(EventType.TURN_START);
		
		mPlayer = p;
	}
	
	public Player player() { return mPlayer; }
}
