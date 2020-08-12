package dave.hs.game.event;

import dave.hs.common.Event;
import dave.hs.common.EventType;
import dave.hs.common.Player;
import dave.hs.game.entity.CardEntity;

public class CardDrawEvent extends Event
{
	private final Player mPlayer;
	private final CardEntity mCard;
	
	public CardDrawEvent(Player p, CardEntity e)
	{
		super(EventType.DRAW_CARD);
		
		mPlayer = p;
		mCard = e;
	}
	
	public Player player() { return mPlayer; }
	public CardEntity card() { return mCard; }
	
	@Override
	public void resolve()
	{
		player().hand.add(card());
	}
}
