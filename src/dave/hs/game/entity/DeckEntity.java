package dave.hs.game.entity;

import java.util.ArrayList;
import java.util.List;

import dave.hs.Engine;
import dave.hs.common.RequestChain;
import dave.hs.game.EntityType;
import dave.hs.game.Request;

public class DeckEntity extends BaseEntity
{
	private final List<CardEntity> mCards;

	public DeckEntity(Engine e, String id)
	{
		super(e, id, EntityType.DECK);
		
		mCards = new ArrayList<>();
	}
	
	public CardEntity draw()
	{
		CardEntity c = null;
		
		if(!mCards.isEmpty())
		{
			c = mCards.get(0);
		}
		
		c = game().resolve(new RequestChain<>(Request.CARD_DRAWN, this, c));
		
		if(c != null)
		{
			mCards.remove(c);
		}
		
		return c;
	}
	
	public boolean empty() { return mCards.isEmpty(); }
	
	public void shuffle()
	{
		if(empty()) return;
		
		List<CardEntity> c = new ArrayList<>(mCards);
		
		mCards.clear();
		
		c.forEach(this::addCardRandom);
	}
	
	public void addCardFront(CardEntity e)
	{
		add(0, e);
	}
	
	public void addCardBack(CardEntity e)
	{
		add(mCards.size() - 1, e);
	}
	
	public void addCardRandom(CardEntity e)
	{
		add(game().random().nextInt(mCards.size()), e);
	}
	
	private void add(int i, CardEntity e)
	{
		mCards.add(i, e);
	}
}
