package dave.hs.common;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import dave.hs.game.HeroCard;
import dave.hs.game.MinionCard;
import dave.hs.game.SpellCard;
import dave.hs.game.WeaponCard;
import dave.hs.game.effect.CastSpellEffect;
import dave.hs.game.effect.PlayMinionEffect;
import dave.hs.game.effect.TriggerSpellEffect;
import dave.hs.game.entity.CardEntity;
import dave.util.relay.Overloaded;
import dave.util.relay.Relay;

public class BaseCard implements Card
{
	private final CardData mCard;
	private final List<Consumer<CardEntity>> mCallbacks;
	private final Relay mRelay;
	
	public BaseCard(CardData card)
	{
		mCard = card;
		mCallbacks = new ArrayList<>();
		mRelay = new Relay(this);
		
		mRelay.call(mCard);
	}
	
	public BaseCard registerEffect(Consumer<CardEntity> f)
	{
		mCallbacks.add(f);
		
		return this;
	}
	
	@Override
	public String id()
	{
		return ((BaseCardData) mCard).id;
	}

	@Override
	public CardData card()
	{
		return mCard;
	}

	@Override
	public void instantiateEffects(CardEntity e)
	{
		mCallbacks.forEach(f -> f.accept(e));
	}
	
	@Overloaded
	private void onMinionCard(MinionCard card)
	{
		registerEffect(e -> e.addEffect(new PlayMinionEffect(e)));
	}
	
	@Overloaded
	private void onSpellCard(SpellCard card)
	{
		Stream.of(card.effects).forEach(s -> registerEffect(e -> e.addEffect(new TriggerSpellEffect(e.game(), e.id() + "-effect-" + s.hashCode(), e, s))));
		registerEffect(e -> e.addEffect(new CastSpellEffect(e.game(), e.id() + "-cast", e)));
	}
	
	@Overloaded
	private void onHeroCard(HeroCard card)
	{
		throw new UnsupportedOperationException();
	}
	
	@Overloaded
	private void onWeaponCard(WeaponCard card)
	{
		throw new UnsupportedOperationException();
	}
}
