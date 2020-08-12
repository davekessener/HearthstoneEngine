package dave.hs.game.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import dave.hs.Engine;
import dave.hs.common.Entity;
import dave.hs.game.EntityType;

public class BaseEntity implements Entity
{
	private final Engine mGame;
	private final String mID;
	private final EntityType mType;
	private final List<EffectEntity> mEffects;
	
	protected BaseEntity(Engine e, String id, EntityType t)
	{
		mGame = e;
		mID = id;
		mType = t;
		mEffects = new ArrayList<>();
	}
	
	public Engine game() { return mGame; }
	
	public Stream<EffectEntity> effects() { return mEffects.stream(); }

	@Override
	public String id()
	{
		return mID;
	}

	@Override
	public EntityType type()
	{
		return mType;
	}
	
	public void addEffect(EffectEntity e)
	{
		mEffects.add(e);
		
		game().registerEffect(e);
	}
	
	public void removeEffect(EffectEntity e)
	{
		mEffects.remove(e);
		
		game().removeEffect(e);
	}
	
	public void destroy()
	{
		effects().forEach(mGame::removeEffect);
		
		mEffects.clear();
	}
}
