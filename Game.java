import java.util.*;

/**
 * Sets up the game and holds the connection between players 
 * and the board.
 * 
 * @author Elijah Guarina
 */

public class Game {
	/**
	 * A list of the names of all characters from Cluedo. Order goes clockwise starting from Miss Scarlet.
	 */
	public final List<String> characters = Arrays.asList("Miss Scarlet", "Colonel Mustard", "Mrs White", "Mr Green", "Mrs Peacock", "Professor Plum");
	/**
	 * A list of the names of all weapons from Cluedo.
	 */
	public final List<String> weapons = Arrays.asList("Candlestick", "Dagger", "Lead Pipe", "Revolver", "Rope", "Spanner");
	/**
	 * A list of the names of all rooms from Cluedo.
	 */
	public final List<String> rooms = Arrays.asList("Kitchen", "Ball Room", "Conservatory", "Dining Room", "Billiard Room", "Library", "Lounge", "Hall", "Study");
	/**
	 * A collection of all the cards in the game, mapped to by their names.
	 */
	private final Map<String,Card> allCards = new HashMap<String,Card>();
	/**
	 * A map of all players, and the player number they are associated with. Is implemented as a TreeMap to always maintain ordering of the key.
	 */
	private Map<Integer,Player> players = new TreeMap<Integer,Player>();
	/** 
	 * A set to store all the cards used for the murder condition.
	 */
	private CardTuple murderConditions;
	/**
	 * The board associated with this game.
	 */
	private final Board board;
	private int turnNum;
	/**
	 * The UI associated with this game of Cluedo
	 */
	private final Cluedo userInterface;

	private Player currentPlayer;
	
	//variables for buttons
	private boolean canSuggest = false;
	private boolean canAccuse = true;
	private boolean canMove = true;

	
	public boolean canSuggest() { return canSuggest; }
	public void setSuggested(Boolean bool) {
		canSuggest = bool;
	}

	public boolean canAccuse() {
		return canAccuse;
	}
	public void setAccused(Boolean bool) {
		canAccuse = bool;
	}

	public boolean canMove() {
		return canMove;
	}
	public void setMoved(Boolean bool) {
		canMove = bool;
	}
	
	/**
	 * Game constructor.
	 */
	public Game(Cluedo ui) {
		this.board = new Board();
		this.userInterface = ui;
	}
	
	// ----------------- WHILE GAME RUNS -------------------
	
	/**
	 * Play through the game until a player correctly guesses 
	 * the murder condition, or all players cannot accuse.
	 */
	public void play() {
		int gameState = 0;
		while (gameState == 0) {
			// Play through each player's turn
			gameState = playThroughPlayerTurns();
		}
		if (gameState > 0) {
			// A player won, now use gameState to get the player that won
			Player winner = players.get(gameState-1);
		} else {
			// Every player made unsuccessful accusations, so nobody wins
		}
		
		//if (allPlayersCanAccuse()) { System.out.println("Congratulations! Player " + turnNum + " (" + players.get(turnNum).getName() + ") found the correct combination! They won!"); }
		//else { System.out.println("No player can accuse anymore. Nobody wins!"); }
	}
	
	/**
	 * Play through each player's turn once. Determine if the  
	 * game has ended after each turn.
	 * 
	 * @return >0 if a Player accused correctly
	 * 		   -1 if all Players accused incorrectly
	 * 			0 if the game is still ongoing
	 */
	public int playThroughPlayerTurns() {
		for (Map.Entry<Integer,Player> player : players.entrySet()) {
			if (player.getValue().playTurn()) { return player.getKey()+1; }
			if (!allPlayersCanAccuse()) { return -1; }
		}
		return 0;
	}
	
	/**
	 * Get a card with the matching name.
	 * 
	 * @param cardName is the name of the card to get
	 * @return
	 */
	public Card getCard(String cardName) { return allCards.get(cardName.toLowerCase()); }

	/**
	 * Run through all players to find a player that can refute 
	 * a given suggestion.
	 * 
	 * @param suggester is the person who made the suggestion
	 * @param suggestion is the suggestion made by suggester
	 * @return the card used to refute the suggestion, if any.
	 * 		   If there is no refutation card, return null.
	 */
	public Card refutationProcess(Player suggester, CardTuple suggestion) {
		Card refuteCard = null;
		int playerNum;
		Player player;
		for (int i = 1; i <= players.size(); i++) {
			// Calculate the correct player number to get from the collection of players
			playerNum = i + turnNum;
			if (playerNum > players.size()) { playerNum -= players.size(); }
			// Get the player and ask them to refute
			player = players.get(playerNum);
			if (!player.equals(suggester)) {
				System.out.println("Player " + playerNum + " to try to refute Player " + turnNum + "'s suggestion");
				refuteCard = player.refute(suggestion);
				if (refuteCard != null) { break; }
			}
		}
		return refuteCard;
	}
	
	public Card refutationProcessV2(Player suggester, CardTuple suggestion) {
		Card refuteCard = null;
		int playerNum;
		Player player;
		for (int i = 1; i <= players.size(); i++) {
			// Calculate the correct player number to get from the collection of players
			playerNum = i + turnNum;
			if (playerNum > players.size()) { playerNum -= players.size(); }
			// Get the player and ask them to refute
			player = players.get(playerNum);
			if (!player.equals(suggester)) {
				Set<Card> refuteOptions = player.refuteV2(suggestion);
				if (refuteOptions != null) {
					refuteCard = userInterface.refuting(refuteOptions);
					break;
				}
			}
		}
		return refuteCard;
	}
	
	/**
	 * Compares 3 cards to the murder conditions.
	 * 
	 * @param accusation is the 3 cards (in a CardTuple) to 
	 * 		  compare
	 * @return whether or not they match
	 */
	public boolean checkAccusation(CardTuple accusation) {
		if(murderConditions.characterCard().getName().equals(accusation.characterCard().getName())) {
			if(murderConditions.roomCard().getName().equals(accusation.roomCard().getName())) {
				if(murderConditions.weaponCard().getName().equals(accusation.weaponCard().getName())) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
     	* Get the current location of a Player Piece that corresponds 
     	* to a given Player.
     	* 
     	* @param player is the player to find the location for
     	* @return the location of player's piece
     	*/
	public Location getPlayerLocation(Player player) { return board.getPlayerLocation(player); }

	public boolean movePlayerByMouse(Location location){
		return currentPlayer.move(location);
	}

	public Integer[] prepareForMove(){
		return currentPlayer.prepareForMove();
	}

	/**
	 * Move a player piece on the board to a new position,
	 * no validation of the location is performed.
	 * @param player The player to move
	 * @param location A destination Location object
	 */
	public void movePlayer(Player player, Location location){
		board.movePlayer(player, location);
	}
	
	/**
	 * Move a player piece on the board to a new position.
	 * 
	 * @param player The player to move
	 * @param direction A string containing WASD for up, left,
	 *                  down or right movement.
	 * @return whether or not the move was successful
	 */
	public Integer movePlayer(Player player, String direction, Set<Location> locationsVisited, Stack<Location> prevLocations) {
		return board.movePlayer(player,direction,locationsVisited,prevLocations);
	}
	
	/**
	 * Move a player and a weapon to a room according to 
	 * a given suggestion.
	 * 
	 * @param suggestion is the suggestion that holds the  
	 * 		  details of this move (what and where to move)
	 */
	public void moveViaSuggestion(CardTuple suggestion) {
		board.movePiece(suggestion);
	}

	public CardTuple askThreeCardsUI(String message, String titleMessage, String buttonName){
		return userInterface.askForThreeCards(message, titleMessage, buttonName);
	}
	
	/**
	 * Check on the board if the given player is in a room.
	 * 
	 * @param player is the player to check for
	 * @return whether or not the player is in a room
	 */
	public boolean checkPlayerInRoom(Player player){
		return board.checkPlayerInRoom(player);
	}
	
	/**
	 * Get the room that the player is currently in.
	 * 
	 * @param player is the player to check for
	 * @return the room the player is in
	 */
	public Room getPlayerRoom(Player player){
		return board.getPlayerRoom(player);
	}
	
	private boolean allPlayersCanAccuse() {
		boolean aPlayerCanAccuse = false;
		for (Map.Entry<Integer,Player> player : players.entrySet()) { aPlayerCanAccuse = player.getValue().canAccuse(); }
		return aPlayerCanAccuse;
	}

	public Location getLocation(int row, int col){
		return board.currentBoard[row][col];
	}
	
	public Map<String,Piece> getPieces(){
		return board.getPieces();
	}
	
	// ----------------- PRE-GAME SETUP --------------------
	
	/**
	 * Set up a new Cluedo game.
	 */
	public void setup() {
		turnNum = 1;
		// Create the cards and decide on the murder/win conditions
		createAllCards();
		List<Card> cardsToDeal = new ArrayList<Card>();
		for (Map.Entry<String,Card> card : allCards.entrySet()) { cardsToDeal.add(card.getValue()); }
		setUpMurder(cardsToDeal);
		// Deal the rest of the cards to the players
		dealCards(cardsToDeal);
	}
	
	/**
	 * Manually create all the cards of the Cluedo Game.
	 */
	private void createAllCards() {
		allCards.clear();
		for (String character : characters) { allCards.put(character.toLowerCase(), new Card(character, Card.CardType.CHARACTER)); }
		for (String weapon : weapons) { allCards.put(weapon.toLowerCase(), new Card(weapon, Card.CardType.WEAPON)); }
		for (String room : rooms) { allCards.put(room.toLowerCase(), new Card(room, Card.CardType.ROOM)); }
	}
	
	/**
	 * Create the murder conditions (winning combination) by 
	 * randomly selecting one Character, Weapon, and Room card.
	 */
	private void setUpMurder(List<Card> cards) {
		Collections.shuffle(cards);
		Card charCard = null, weapCard = null, roomCard= null;
		for (int i = 0; i < 3; i++) {
			if (i == 0) { charCard = getMurderCard(cards, Card.CardType.CHARACTER); }
			if (i == 1) { weapCard = getMurderCard(cards, Card.CardType.WEAPON); }
			if (i == 2) { roomCard = getMurderCard(cards, Card.CardType.ROOM); }
		}
		murderConditions = new CardTuple(charCard, weapCard, roomCard);
	}
	
	/**
	 * Select the first card from allCards of a certain type.
	 * 
	 * @param type is the type of card to look for
	 */
	private Card getMurderCard(List<Card> cards, Card.CardType type)  {
		// Get a random card of a particular type (given by the "type" parameter)
		Card murderCard = null;
		for (Card card : cards) { if (card.getType() == type) { murderCard = card; break; } }
		// Check if a card was selected at all.
		// If not, there must not be any cards of that type in allCards
		if (murderCard == null) { throw new NullPointerException("Murder card for type " + type + " not found. Check that cards of all types are added to the list of all cards."); }
		else {
			cards.remove(murderCard);
			return murderCard;
		}
	}
	
	/**
	 * Add a new player to the game
	 * 
	 * @param playerNumber is the player's number (e.g. Player 1)
	 * @param playerName is the username of the player
	 * @param characterName is the character played by the player
	 * @return false if a Player with the same playerName is already in the game, otherwise true
	 */
	public boolean addPlayer(int playerNumber, String playerName, String characterName) {
		// First, check if that username is already used
		for (Map.Entry<Integer,Player> player : players.entrySet()) { if (playerName.equals(player.getValue().getPlayerName())) { return false; } }
		players.put(characters.indexOf(characterName), new Player(playerNumber, playerName, characterName, this));
		return true;
	}
	
	/**
	 * Deal the all cards (except murder cards) to evenly between 
	 * each player. Some players may end up with more than others.
	 */
	private void dealCards(List<Card> cards) {
		while (cards.size() > 0) {
			for (Map.Entry<Integer,Player> player : players.entrySet()) {
				player.getValue().giveCard(cards.get(cards.size()-1));
				cards.remove(cards.size()-1);
				if (cards.size() < 1) { break; }
			}
		}
	}

	public void setCurrentPlayer(Player currentPlayer) {
		this.currentPlayer = currentPlayer;
	}

	public static void main(String[] args) {
		/*Game game;
		int playerCount;
		boolean playing = true;
		while (playing) {
			// Set up and play the game
			System.out.println("New game started");
			game.setup();
			game.play();
		}
		System.out.println("Cluedo game ended. Thanks for playing!");*/
	}
}
