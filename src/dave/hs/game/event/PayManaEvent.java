package dave.hs.game.event;

import dave.hs.common.Event;
import dave.hs.common.EventType;
import dave.hs.common.Player;

public class PayManaEvent extends Event
{
	private final Player mPlayer;
	private final int mAmount;
	
	public PayManaEvent(Player p, int n)
	{
		super(EventType.PAY_MANA);
		
		mPlayer = p;
		mAmount = n;
	}
	
	public Player player() { return mPlayer; }
	public int amount() { return mAmount; }
	
	@Override
	public void resolve()
	{
		mPlayer.mana -= mAmount;
	}
}
