package dave.hs.game.event;

import dave.hs.common.Event;
import dave.hs.common.EventType;
import dave.hs.common.Player;

public class GainManaCrystalEvent extends Event
{
	private final Player mPlayer;
	
	public GainManaCrystalEvent(Player p)
	{
		super(EventType.GAIN_MANA_CRYSTAL);
		
		mPlayer = p;
	}
	
	@Override
	public void resolve()
	{
		++mPlayer.mana_crystals;
	}
}
