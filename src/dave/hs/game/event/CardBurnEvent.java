package dave.hs.game.event;

import dave.hs.common.Event;
import dave.hs.common.EventType;
import dave.hs.game.entity.CardEntity;

public class CardBurnEvent extends Event
{
	private final CardEntity mCard;
	
	public CardBurnEvent(CardEntity card)
	{
		super(EventType.BURN_CARD);
		
		mCard = card;
	}
	
	public CardEntity card() { return mCard; }
	
	@Override
	public void resolve()
	{
		mCard.destroy();
	}
}
