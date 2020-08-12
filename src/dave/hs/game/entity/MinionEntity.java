package dave.hs.game.entity;

import dave.hs.Engine;
import dave.hs.common.Player;
import dave.hs.common.RequestChain;
import dave.hs.game.EntityType;
import dave.hs.game.MinionCard;
import dave.hs.game.Request;
import dave.hs.game.effect.SummoningSicknessEffect;

public class MinionEntity extends CombatEntity implements BoardEntity
{
	private final MinionCard mCard;
	private final Player mPlayer;
	private final int mSummoned;
	
	public MinionEntity(Engine e, String id, MinionCard card, Player p)
	{
		super(e, id, EntityType.MINION, card.life, card.attack);
		
		mCard = card;
		mPlayer = p;
		mSummoned = game().turn();
		
		setLife(getMaxLife());
		
		addEffect(new SummoningSicknessEffect(game(), id + "-summoning-sickness", this));
	}
	
	public Player player() { return mPlayer; }
	public MinionCard card() { return mCard; }
	public int turnSummoned() { return mSummoned; }
	
	public int getMaxLife()
	{
		return game().resolve(new RequestChain<>(Request.GET_MINION_BASE_LIFE, this, mCard.life));
	}
	
	public boolean canAttack()
	{
		return game().resolve(new RequestChain<>(Request.CAN_ATTACK_THIS, this, true));
	}
	
	@Override
	public void destroy()
	{
		super.destroy();
		
		mPlayer.board.remove(this);
	}
}
