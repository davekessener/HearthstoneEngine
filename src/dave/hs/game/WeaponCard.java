package dave.hs.game;

import dave.hs.common.BaseCardData;

public class WeaponCard extends BaseCardData
{
	public final int attack;
	public final int durability;

	public WeaponCard(String id, String name, String description, int cost, CardRarity rarity, HeroClass klass, int attack, int durability)
	{
		super(id, name, description, cost, rarity, CardType.WEAPON, klass);
		
		this.attack = attack;
		this.durability = durability;
	}
}
