package dave.hs.common;

import dave.hs.Engine;
import dave.hs.game.entity.CombatEntity;
import dave.hs.game.entity.HeroEntity;

public interface Effect
{
	public abstract void trigger(Engine e, int power, HeroEntity self, CombatEntity target);
}
