package dave.hs.game.entity;

import dave.hs.Engine;
import dave.hs.common.BaseCardData;
import dave.hs.common.Card;
import dave.hs.common.Player;
import dave.hs.common.RequestChain;
import dave.hs.game.EntityType;
import dave.hs.game.Request;

public class CardEntity extends BaseEntity
{
	private final Player mPlayer;
	private final Card mCard;
	
	public CardEntity(Engine e, String id, Player p, Card c)
	{
		super(e, id, EntityType.CARD);
		
		mPlayer = p;
		mCard = c;
		
		c.instantiateEffects(this);
	}
	
	public Player player() { return mPlayer; }
	public Card card() { return mCard; }
	public BaseCardData cardData() { return (BaseCardData) mCard.card(); }

	public String getName()
	{
		return game().resolve(new RequestChain<>(Request.CARD_NAME, this, cardData().name));
	}
	
	public int getCost()
	{
		return game().resolve(new RequestChain<>(Request.CARD_COST, this, cardData().cost));
	}
	
	public boolean requiresTarget()
	{
		return game().resolve(new RequestChain<>(Request.REQUIRE_CARD_TARGET, this, false));
	}
}
