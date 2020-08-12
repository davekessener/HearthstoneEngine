package dave.hs.game.event;

import dave.hs.common.Event;
import dave.hs.common.EventType;
import dave.hs.common.Player;
import dave.hs.game.entity.CombatEntity;

public class PlayCardEvent extends Event
{
	private final Player mPlayer;
	private final CombatEntity mTarget;
	private final int mCard;
	
	public PlayCardEvent(Player p, int c, CombatEntity t)
	{
		super(EventType.PLAY_CARD);
		
		mPlayer = p;
		mTarget = t;
		mCard = c;
	}
	
	public Player player() { return mPlayer; }
	public int card() { return mCard; }
	public CombatEntity target() { return mTarget; }
	
	@Override
	public void resolve()
	{
		mPlayer.hand.remove(mCard);
	}
}
