package dave.hs.game.event;

import dave.hs.common.Event;
import dave.hs.common.EventType;
import dave.hs.common.Player;

public class TurnEndEvent extends Event
{
	private final Player mPlayer;
	
	public TurnEndEvent(Player p)
	{
		super(EventType.TURN_END);
		
		mPlayer = p;
	}
	
	public Player player() { return mPlayer; }
}
