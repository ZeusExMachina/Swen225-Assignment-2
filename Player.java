import java.util.*;

public class Player {
	private final Map<String, Card> hand;
	private final int playerNumber;
	private final String playerName;
	private final String characterName;
	private final Random rand;
	public Game g;
	private boolean canAccuse;
	private int counter;
	private Stack<Location> prevLocations;
	private Set<Location> locationsVisited;

	public Player(int playerNum, String username, String character, Game game) {
		this.hand = new HashMap<>();
		this.playerNumber = playerNum;
		this.playerName = username;
		this.characterName = character;
		this.rand = new Random();
		this.prevLocations = new Stack<Location>();
		this.locationsVisited = new HashSet<Location>();
		this.g = game;
		this.canAccuse = true;
	}
	
	/**
	 * Returns the number of this player (e.g. Player "1")
	 * @return Integer
	 */
	public int getPlayerNumber() { return playerNumber; }
	
	/**
	 * Returns the username of this Player
	 * @return String
	 */
	public String getPlayerName() { return playerName; }
	
	/**
	 * Returns the name of the character associated with this Player
	 * @return String 
	 */
	public String getCharacterName() { return characterName; }
	
	public Map<String,Card> getHand(){
		return hand;
	}
	
	public boolean canAccuse() { return canAccuse; }
	
	public void setCannotAccuse() { canAccuse = false; }
	
	/**
	 * Puts Card into players hand
	 * @param card
	 */
	public void giveCard(Card card) { hand.put(card.getName(),card); }

	public Integer prepareForMove(){
		locationsVisited.clear();
		prevLocations.clear();
		return rollDice();
	}
	
	/**
	 * Roll two dice and get their sum. Two numbers are generated
	 * to try to mimic real dice.
	 *
	 * @return the total of the two dice
	 */
	private Integer rollDice() {
		int first = rand.nextInt(6) + 1, second = rand.nextInt(6) + 1;
		this.g.showDiceRollUI(first, second);
		this.g.displayGameStateMessageUI("You rolled a " + first + " and a " + second + ".\nClick on the board to move! (one tile at a time)");
		return first + second;
	}
	
	public boolean move(Location destination) {
		if(destination.room == null || counter < 1){
			return false;
		}

		Location currentLocation;
		Location newLocation;


			// First valid option is a player exiting a room
			if (g.checkPlayerInRoom(this) && g.getPlayerRoom(this).getUnoccupiedExits().contains(destination)) {
				g.movePlayer(this, destination);
				counter--;
				return true;
			}

			// Second valid option is a player entering a room
			if(!destination.room.getName().equals("Passageway")
					&& destination.room.getEntrances().contains(g.getPlayerLocation(this))){
				g.movePlayer(this, destination.room.getRandomRoomLocation());
				counter = 0;
				return true;
			}


			// Finally the third option is moving to an adjacent square
			// in the passageway
			else {
				// Before anything, record the location we're moving from
				currentLocation = g.getPlayerLocation(this);
				String direction = currentLocation.checkAdjacent(destination);
				if(direction.equals("Invalid")){
					return false;
				}
				// Then first, check whether or not the move is valid
				int moveAttemptResult = g.movePlayer(this, direction, locationsVisited, prevLocations);
				if (moveAttemptResult == 0) {
					// Successful move
					newLocation = g.getPlayerLocation(this);
					if (!prevLocations.isEmpty() && newLocation.equals(prevLocations.peek())) {
						locationsVisited.remove(prevLocations.pop());
						counter++;
						return true;
					} else {
						locationsVisited.add(currentLocation);
						prevLocations.push(currentLocation);
						counter--;
						return true;
					}
				} else if (moveAttemptResult < 0) {
					System.out.println("Cannot move in that direction, please try again.");
				} else if (moveAttemptResult > 0) {
					System.out.println("Already moved there this turn, please try again.");
				}
			}
		return false;
	}
	
	public Set<Card> refuteV2(CardTuple tup){
		Set<Card> refuteOptions = new HashSet<Card>();

		for(Card c : hand.values()) {
			if(tup.characterCard().equals(c)) {
				refuteOptions.add(c);
			}
			if(tup.weaponCard().equals(c)) {
				refuteOptions.add(c);;
			}
			if(tup.roomCard().equals(c)) {
				refuteOptions.add(c);
			}


		}

		if(refuteOptions.size() > 0) {
			return refuteOptions;
		}

		else {return null;}
	}

	/**
	 * prints the name of every card in players hand
	 */
	public void printCards() {
		for(Card c : hand.values()) {
			System.out.println(c.getName());
		}
	}
	
	public String toString() { return "player " + playerNumber + " - username: " + playerName + ", character: " + characterName + ", in hand: " + hand.toString(); }
}
