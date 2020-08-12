package dave.hs.game.event;

import dave.hs.common.Event;
import dave.hs.common.EventType;
import dave.hs.common.Player;

public class RefreshManaEvent extends Event
{
	private final Player mPlayer;
	
	public RefreshManaEvent(Player p)
	{
		super(EventType.REFRESH_MANA);
		
		mPlayer = p;
	}
	
	@Override
	public void resolve()
	{
		++mPlayer.mana;
	}
}
