package dave.hs.common;

public class PlayerInfo
{
	public final String id;
	public final String hero;
	public final HeroPower power;
	public final Card[] cards;
	
	public PlayerInfo(String id, String hero, HeroPower power, Card[] cards)
	{
		this.id = id;
		this.hero = hero;
		this.power = power;
		this.cards = cards;
	}
}
