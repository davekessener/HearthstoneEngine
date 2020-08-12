package dave.hs.common;

public class HeroPower
{
	public final String name;
	public final Effect effect;
	public final int cost;
	
	public HeroPower(String name, Effect effect, int cost)
	{
		this.name = name;
		this.effect = effect;
		this.cost = cost;
	}
}
