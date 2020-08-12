package dave.hs.common;

import java.util.ArrayList;
import java.util.List;

import dave.hs.Engine;
import dave.hs.game.PlayerType;
import dave.hs.game.entity.BoardEntity;
import dave.hs.game.entity.CardEntity;
import dave.hs.game.entity.DeckEntity;
import dave.hs.game.entity.HeroEntity;

public class Player
{
	public final String id;
	public final PlayerType type;
	public HeroEntity hero;
	public final DeckEntity deck;
	public final List<BoardEntity> board;
	public final List<CardEntity> hand;
	public Engine game;
	public int fatigue, armor;
	public int max_life, mana, mana_crystals;
	
	public Player(String id, PlayerType type, DeckEntity deck)
	{
		this.id = id;
		this.type = type;
		this.deck = deck;
		this.board = new ArrayList<>();
		this.hand = new ArrayList<>();
		
		this.fatigue = 0;
		this.max_life = 30;
		this.armor = 0;
		this.mana_crystals = this.mana = 0;
	}
}
