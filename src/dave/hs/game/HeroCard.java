package dave.hs.game;

import dave.hs.common.BaseCardData;
import dave.hs.common.HeroPower;

public class HeroCard extends BaseCardData
{
	public final HeroPower power;
	
	public HeroCard(String id, String name, String description, int cost, CardRarity rarity, HeroClass klass, HeroPower power)
	{
		super(id, name, description, cost, rarity, CardType.HERO, klass);
		
		this.power = power;
	}
}
