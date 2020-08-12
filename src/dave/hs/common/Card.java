package dave.hs.common;

import dave.hs.game.entity.CardEntity;

public interface Card
{
	public abstract String id( );
	public abstract CardData card( );
	public abstract void instantiateEffects(CardEntity e);
}
