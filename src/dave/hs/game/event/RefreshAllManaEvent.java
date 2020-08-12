package dave.hs.game.event;

import dave.hs.Engine;
import dave.hs.common.Event;
import dave.hs.common.EventType;
import dave.hs.common.Player;
import dave.util.Utils;

public class RefreshAllManaEvent extends Event
{
	private final Engine mGame;
	private final Player mPlayer;
	
	public RefreshAllManaEvent(Engine e, Player p)
	{
		super(EventType.REFRESH_ALL_MANA);
		
		mGame = e;
		mPlayer = p;
	}
	
	@Override
	public void resolve()
	{
		Utils.times(mPlayer.mana_crystals - mPlayer.mana, i -> mGame.enqueueEvent(new RefreshManaEvent(mPlayer)));
	}
}
