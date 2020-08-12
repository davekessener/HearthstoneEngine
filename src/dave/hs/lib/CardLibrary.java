package dave.hs.lib;

import java.util.HashMap;
import java.util.Map;

import dave.hs.common.Card;

public final class CardLibrary
{
	public static final CardLibrary INSTANCE = new CardLibrary();
	
	private final Map<String, Card> mCards;
	
	private CardLibrary()
	{
		mCards = new HashMap<>();
	}
	
	public void registerCard(Card card)
	{
		if(mCards.put(card.id(), card) != null)
			throw new IllegalStateException("Duplicate card id: " + card.id());
	}
	
	public Card getCard(String id)
	{
		Card c = mCards.get(id);
		
		if(c == null)
			throw new IllegalArgumentException("No such card: " + id);
		
		return c;
	}
}
