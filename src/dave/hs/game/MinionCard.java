package dave.hs.game;

import dave.hs.common.BaseCardData;

public class MinionCard extends BaseCardData
{
	public final int attack, life;
	public final MinionFamily family;
	
	public MinionCard(String id, String name, String description, int cost, CardRarity rarity, HeroClass klass, int attack, int life, MinionFamily family)
	{
		super(id, name, description, cost, rarity, CardType.MINION, klass);
		
		this.attack = attack;
		this.life = life;
		this.family = family;
	}
}
