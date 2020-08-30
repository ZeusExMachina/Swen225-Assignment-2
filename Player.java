import java.util.*;

public class Player {
	private final Map<String, Card> hand;
	private final int playerNumber;
	private final String playerName;
	private final String characterName;
	private final Random rand;
	public Game g;
	private boolean canAccuse = true;
	private Stack<Location> prevLocations;
	private Set<Location> locationsVisited;
	private int counter;

	public Player(int playerNum, String username, String character, Game g) {
		this.hand = new HashMap<>();
		this.playerNumber = playerNum;
		this.playerName = username;
		this.characterName = character;
		this.rand = new Random();
		this.prevLocations = new Stack<Location>();
		this.locationsVisited = new HashSet<Location>();
		this.g = g;
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
	
	/**
	 * Puts Card into players hand
	 * @param card
	 */
	public void giveCard(Card card) { hand.put(card.getName(),card); }
	
	public boolean playTurnV2() {
		int roll;
		CardTuple CardTup;
		//Cluedo UI = g.getUI();
		
		if(g.canMove()) {
			//move with roll
			g.setMoved(false);
		}
		
		if(g.canSuggest()) {
			CardTup = g.askThreeCardsUI("Choose Three Cards", "Make A Suggestion", "Suggest");
			g.moveViaSuggestion(CardTup);

			Card refuteCard = g.refutationProcess(this, CardTup);
			
		}
		
		if(g.canAccuse()) {
			//do accuse shit
		}
		return true;
	}

	/**
	 * Plays through a players turn running through every scenario
	 * @return boolean
	 */
	public boolean playTurn() {
		boolean receivedValidInput = false;
		this.g = g;
		Scanner scan = new Scanner(System.in);
		printCards();
		while(!receivedValidInput){
			System.out.println("Would you like to see the board? (Y/N): ");
			String userInput = scan.nextLine().toUpperCase();
			if (userInput.equals("Y")) {
				receivedValidInput = true;
			} else if (userInput.equals("N")) {
				receivedValidInput = true;
			} else {
				System.out.println("Invalid input, please try again.");
			}
		}
		receivedValidInput = false;
		while (!receivedValidInput && g.getPlayerRoom(this).playerCanRoll()) {
			System.out.println("Would you like to roll? (Y/N): ");
			String userInput = scan.nextLine().toUpperCase();
			if (userInput.equals("Y")) {
				receivedValidInput = true;
				//move();
			} else if (userInput.equals("N")) {
				receivedValidInput = true;
			} else {
				System.out.println("Invalid input, please try again.");
			}
		}
		if(!g.getPlayerRoom(this).playerCanRoll()){
			System.out.println("Sorry, you cannot roll to move out of this Room, the exits are blocked!");
		}

		if (g.checkPlayerInRoom(this)) {
			receivedValidInput = false;
			boolean canSuggest = true;
			while (!receivedValidInput && canSuggest) {
				System.out.print("Would you like to suggest? (Y/N): ");
				String userInput = scan.nextLine().toUpperCase();
				if (userInput.equals("N")) {
					receivedValidInput = true;
				} else if (userInput.equals("Y")) {
					canSuggest = false;
					Card charCard;
					System.out.print("Character: ");
					charCard = isCard(scan.nextLine(), Card.CardType.CHARACTER);
					if (charCard == null) {
						while (charCard == null) {
							System.out.println("Not a valid Character card, try again: ");
							charCard = isCard(scan.nextLine(), Card.CardType.CHARACTER);
						}
					}

					Card weapCard;
					System.out.print("Weapon: ");
					weapCard = isCard(scan.nextLine(), Card.CardType.WEAPON);
					if (weapCard == null) {
						while (weapCard == null) {
							System.out.println("Not a valid Weapon card, try again: ");
							weapCard = isCard(scan.nextLine(), Card.CardType.WEAPON);
						}
					}

					Card roomCard = g.getCard(g.getPlayerRoom(this).getName());


					CardTuple cardTup = new CardTuple(charCard, weapCard, roomCard);
					g.moveViaSuggestion(cardTup);

					Card refuteCard = g.refutationProcess(this, cardTup);
					if (refuteCard == null) {
						System.out.println("Your suggestion " + cardTup.toString() + " was not refuted!");
					} else {
						System.out.println("Your suggestion " + cardTup.toString() + " was refuted by the card " + refuteCard.getName());
					}
				} else {
					System.out.println("Invalid input, please try again.");
				}
			}
		}

		receivedValidInput = false;
		while (!receivedValidInput) {
			System.out.print("Would you like to accuse? (Y/N): ");
			String userInput = scan.nextLine().toUpperCase();
			if (userInput.equals("Y")) {
				return accuse();
			} else if (userInput.equals("N")) {
				receivedValidInput = true;
			} else {
				System.out.println("Invalid input, please try again.");
			}
		}
		return false;
	}



	/**
	 * Roll two dice and get their sum. Two numbers are generated
	 * to try to mimic real dice.
	 *
	 * @return the numbers of both dice
	 */
	public Integer[] rollDice() {
		Integer[] diceNumbers = new Integer[2];
		diceNumbers[0] = rand.nextInt(6) + 1;
		diceNumbers[1] = rand.nextInt(6) + 1;
		counter = diceNumbers[0] + diceNumbers[1];
		return diceNumbers;
	}

	public Integer[] prepareForMove(){
		locationsVisited.clear();
		prevLocations.clear();
		return rollDice();
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

	/**
	 * Helper method to check if the card params cardtype matches the param cardtype
	 * @param card
	 * @param ct
	 * @return Card
	 */
	private Card isCard(String card, Card.CardType ct) {
		Card returnCard = g.getCard(card);
		if(returnCard != null) {
			if(returnCard.getType().equals(ct)) {
				return returnCard;
			}
			return returnCard;
		}
		return returnCard;
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
	 * Creates a card tuple of refutable cards then asks player which one they would like to refute with
	 * @param tup
	 * @return Card
	 */
	public Card refute(CardTuple tup) {
		Set<Card> refuteOptions = new HashSet<Card>();
		Card refuteCard = null;

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

		if(!refuteOptions.isEmpty()) {
			Scanner scan = new Scanner(System.in);
			System.out.println("Refutable Cards:");
			for(Card c : refuteOptions) {
				System.out.println(c.getName());
			}
			System.out.println("What card would you like to refute with?: ");

			while(refuteCard == null) {
				String choice = scan.nextLine().toUpperCase();


				if(choice.equalsIgnoreCase(tup.weaponCard().getName())) {
					for(Card c : refuteOptions) {
						if(c.getName().equalsIgnoreCase(choice)) {
							refuteCard = tup.weaponCard();
							break;
						}
					}
					break;
				}

				if(choice.equalsIgnoreCase(tup.characterCard().getName())) {
					for(Card c : refuteOptions) {
						if(c.getName().equalsIgnoreCase(choice)) {
							refuteCard = tup.characterCard();
							break;
						}
					}
					break;
				}

				if(choice.equalsIgnoreCase(tup.roomCard().getName())) {
					for(Card c : refuteOptions) {
						if(c.getName().equalsIgnoreCase(choice)) {
							refuteCard = tup.roomCard();
							break;
						}
					}
					break;
				}

				else { System.out.println("Not a valid option please choose from your refutable cards: ");}
			}
			scan.close();
		}

		else {System.out.println("No cards to refute");}

		return refuteCard;
	}

	/**
	 * prints the name of every card in players hand
	 */
	public void printCards() {
		for(Card c : hand.values()) {
			System.out.println(c.getName());
		}
	}

	/**
	 * Asks user for 3 cards if they can accuse and create a CardTuple out of the
	 * getThreeCards method then returns a boolean based off the checkAccusation method in game
	 * @return boolean
	 */
	public boolean accuse() {
		if (!canAccuse) {
			System.out.println("You have already made an accusation before, and cannot make another.");
			return false;
		} else {
			System.out.println("Make a suggestion - type in 3 cards:");
			CardTuple accusation = getThreeCards();
			canAccuse = false;
			return g.checkAccusation(accusation);
		}
	}

	/**
	 * Asks for 3 cards and makes sure the are 1 of each type then returns them in a cardTuple
	 * @return CardTuple
	 */
	public CardTuple getThreeCards() {
		Scanner scan = new Scanner(System.in);
		Card charCard, weapCard, roomCard;
		System.out.print("Character: ");
		charCard = isCard(scan.nextLine(),Card.CardType.CHARACTER);
		while(charCard == null) {
			System.out.println("Not a valid Character card try again: ");
			charCard = isCard(scan.nextLine(),Card.CardType.CHARACTER);
		}

		System.out.print("Weapon: ");
		weapCard = isCard(scan.nextLine(),Card.CardType.WEAPON);
		while(weapCard == null) {
			System.out.println("Not a valid Weapon card try again: ");
			weapCard = isCard(scan.nextLine(),Card.CardType.WEAPON);
		}

		System.out.print("Room: ");
		roomCard = isCard(scan.nextLine(),Card.CardType.ROOM);
		while(roomCard == null) {
			System.out.println("Not a valid room card try again: ");
			roomCard = isCard(scan.nextLine(),Card.CardType.ROOM);
		}
		return new CardTuple(charCard,weapCard,roomCard);
	}
	
	public String toString() { return "player " + playerNumber + " - username: " + playerName + ", character: " + characterName + ", in hand: " + hand.toString(); }
}
