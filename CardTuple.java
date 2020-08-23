/**
 * Holds a tuple of 3 Cards, with each card being of a 
 * different type.
 * 
 * @author Elijah Guarina
 */
public class CardTuple {
	private Card charCard, weapCard, roomCard;
	
	public CardTuple(Card first, Card second, Card third) {
		for (int i = 0; i < 3; i++) {
			if (i == 0) { setCard(first); }
			if (i == 1) { setCard(second); }
			if (i == 2) { setCard(third); }
		}
	}
	
	public Card characterCard() { return charCard; }
	
	public Card weaponCard() { return weapCard; }
	
	public Card roomCard() { return roomCard; }
	
	/**
	 * Set a given card as one of the cards for this tuple.
	 * 
	 * @param card is the card to set
	 */
	private void setCard(Card card) {
		if (card.getType() == Card.CardType.CHARACTER) { 
			if (charCard != null) { throw new IllegalArgumentException("Duplicate card types found. CardTuple only accepts 1 card of each type. Cards type: " + card.getType()); }
			charCard = card;
		}
		if (card.getType() == Card.CardType.WEAPON) { 
			if (weapCard != null) { throw new IllegalArgumentException("Duplicate card types found. CardTuple only accepts 1 card of each type. Cards type: " + card.getType()); }
			weapCard = card;
		}
		if (card.getType() == Card.CardType.ROOM) {
			if (roomCard != null) { throw new IllegalArgumentException("Duplicate card types found. CardTuple only accepts 1 card of each type. Cards type: " + card.getType()); }
			roomCard = card;
		}
	}
	
	public String toString() {
		return charCard.getName() + " with the " + weapCard.getName() + " in the " + roomCard.getName();
	}
}