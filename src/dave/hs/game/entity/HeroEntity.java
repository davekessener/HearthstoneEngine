package dave.hs.game.entity;

import dave.hs.Engine;
import dave.hs.common.Player;
import dave.hs.common.RequestChain;
import dave.hs.game.EntityType;
import dave.hs.game.Request;
import dave.util.stream.StreamUtils;

public class HeroEntity extends CombatEntity
{
	private final Player mPlayer;
	
	public HeroEntity(Engine e, String id, Player p)
	{
		super(e, id, EntityType.HERO, p.max_life, 0);
		
		mPlayer = p;
		
		addEffect(new EffectEntity(game(), id + "-has-no-atk-on-opp-turn") {
			@Override
			public <T> void affect(RequestChain<T> r)
			{
				if(r.request() == Request.GET_ATTACK && r.source() == HeroEntity.this && game().activePlayer() != mPlayer)
				{
					EffectEntity self = this;
					
					if(!r.involved(self) || !StreamUtils.last(r.effects()).filter(l -> l.source == self).isPresent())
					{
						r.affect(self, v -> 0);
					}
				}
			}
		});
	}
	
	public Player player() { return mPlayer; }
	
	public int getMaxHandSize()
	{
		return game().resolve(new RequestChain<>(Request.MAX_HAND_SIZE, this, 10));
	}
	
	public int getSpellPower()
	{
		return game().resolve(new RequestChain<>(Request.SPELL_POWER, this, 0));
	}

	@Override
	public int getMaxLife()
	{
		return mPlayer.max_life;
	}
}
