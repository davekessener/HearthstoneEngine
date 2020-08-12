package dave.hs.common;

import dave.hs.Engine;
import dave.hs.game.EntityType;

public interface Entity
{
	public abstract String id( );
	public abstract EntityType type( );
	public abstract Engine game( );
}
