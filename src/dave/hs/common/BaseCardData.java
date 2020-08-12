package dave.hs.common;

import dave.hs.game.CardRarity;
import dave.hs.game.CardType;
import dave.hs.game.HeroClass;

public abstract class BaseCardData implements CardData
{
	public final String id;
	public final String name;
	public final String description;
	public final int cost;
	public final CardRarity rarity;
	public final CardType type;
	public final HeroClass klass;
	
	public BaseCardData(String id, String name, String description, int cost, CardRarity rarity, CardType type, HeroClass klass)
	{
		this.id = id;
		this.name = name;
		this.description = description;
		this.cost = cost;
		this.rarity = rarity;
		this.type = type;
		this.klass = klass;
	}
}
