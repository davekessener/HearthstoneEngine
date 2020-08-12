package dave.hs.game;

import dave.hs.common.BaseCardData;
import dave.hs.common.Effect;

public class SpellCard extends BaseCardData
{
	public final boolean targeted;
	public final Effect[] effects;
	
	public SpellCard(String id, String name, String description, int cost, CardRarity rarity, HeroClass klass, boolean targeted, Effect[] effects)
	{
		super(id, name, description, cost, rarity, CardType.SPELL, klass);
		
		this.targeted = targeted;
		this.effects = effects;
	}
}
