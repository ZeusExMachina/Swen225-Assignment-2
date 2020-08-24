import java.util.*;

/**
 * Sets up the game and holds the connection between players 
 * and the board.
 * 
 * @author Elijah Guarina
 */

public class Game {
	/**
	 * A collection of all the cards in the game, mapped to by their names.
	 */
	private final Map<String,Card> allCards = new HashMap<String,Card>();
	/**
	 * A list of the names of all characters from Cluedo. Order goes clockwise starting from Miss Scarlet.
	 */
	public static final List<String> characters = Arrays.asList("Miss Scarlet", "Colonel Mustard", "Mrs White", "Mr Green", "Mrs Peacock", "Professor Plum");
	/**
	 * A map of all players, and the player number they are associated with. Is implemented as a TreeMap to always maintain ordering of the key.
	 */
	private Map<Integer,Player> players = new TreeMap<Integer,Player>();
	/** 
	 * A set to store all the cards used for the murder condition.
	 * (suggestion: maybe make a new class for 3-card tuples for easy comparing)
	 */
	private CardTuple murderConditions;
	/**
	 * The board associated with this game.
	 */
	private Board board;
	private int turnNum;
	/**
	 * A flag for if the game is over or not.
	 */
	private boolean gameOver;

	private Scanner scan;
	
	/**
	 * Game constructor.
	 *
	 */
	public Game() {
		this.scan = new Scanner(System.in);
		board = new Board();
	}
	
	// ----------------- WHILE GAME RUNS -------------------
	
	/**
	 * Play through the game until a player correctly guesses 
	 * the murder condition, in which case they win and the 
	 * game is over.
	 */
	public void play() {
		while (!gameOver) {
			// Play a turn
			System.out.println("Player " + turnNum + "'s [" + players.get(turnNum).getName() + "] turn. Cards: ");
			if(players.get(turnNum).playTurn(this)) { gameOver = true; break; }
			turnNum++;
			if (turnNum > players.size()) { turnNum = 1; }
			// Check if all players can still make an accusation
			gameOver = !allPlayersCanAccuse();
		}
		if (allPlayersCanAccuse()) { System.out.println("Congratulations! Player " + turnNum + " (" + players.get(turnNum).getName() + ") found the correct combination! They won!"); }
		else { System.out.println("No player can accuse anymore. Nobody wins!"); }
	}
	
	/**
	 * Get a card with the matching name.
	 * 
	 * @param cardName is the name of the card to get
	 * @return
	 */
	public Card getCard(String cardName) { return allCards.get(cardName.toLowerCase()); }

	/**
	 * Provide access to the System.in Scanner for other classes
	 *
	 * @return the Game's Scanner
	 */
	public Scanner getScanner() {
		return scan;
	}

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
	
	/**
	 * Move a player piece on the baord to a new position,
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
	
	/**
	 * Instructs the Board to label the different exits
	 * a Player may exit from, and returns a list of the exits.
	 * 
	 * @param location The player's current Location in a room
	 * @return A list of exits that are available to move into
	 */
	public List<Location> labelRoomExits(Location location){
		return board.labelRoomExits(location);
	}

	public void drawBoard(){
		board.draw();
	}
	
	private boolean allPlayersCanAccuse() {
		boolean aPlayerCanAccuse = false;
		for (Map.Entry<Integer,Player> player : players.entrySet()) { aPlayerCanAccuse = player.getValue().canAccuse(); }
		return aPlayerCanAccuse;
	}

	public Location getLocation(int row, int col){
		return board.currentBoard[row][col];
	}
	
	// ----------------- PRE-GAME SETUP --------------------
	
	/**
	 * Set up a new Cluedo game.
	 * 
	 * @param playerCount is the number of players 
	 * 		  in the game
	 */
	private void setup(int playerCount) {
		turnNum = 1;
		gameOver = false;
		// Create the cards and decide on the murder/win conditions
		createAllCards();
		List<Card> cardsToDeal = new ArrayList<Card>();
		for (Map.Entry<String,Card> card : allCards.entrySet()) { cardsToDeal.add(card.getValue()); }
		setUpMurder(cardsToDeal);
		// Now create players and deal the rest of the cards to them
		createPlayers(playerCount);
		dealCards(cardsToDeal);
	}
	
	/**
	 * Manually create all the cards of the Cluedo Game.
	 */
	private void createAllCards() {
		allCards.clear();
		allCards.put("miss scarlet", new Card("Miss Scarlet", Card.CardType.CHARACTER));
		allCards.put("colonel mustard", new Card("Colonel Mustard", Card.CardType.CHARACTER));
		allCards.put("mrs white", new Card("Mrs White", Card.CardType.CHARACTER));
		allCards.put("mr green", new Card("Mr Green", Card.CardType.CHARACTER));
		allCards.put("mrs peacock", new Card("Mrs Peacock", Card.CardType.CHARACTER));
		allCards.put("professor plum", new Card("Professor Plum", Card.CardType.CHARACTER));
		allCards.put("candlestick", new Card("Candlestick", Card.CardType.WEAPON));
		allCards.put("dagger", new Card("Dagger", Card.CardType.WEAPON));
		allCards.put("lead pipe", new Card("Lead Pipe", Card.CardType.WEAPON));
		allCards.put("revolver", new Card("Revolver", Card.CardType.WEAPON));
		allCards.put("rope", new Card("Rope", Card.CardType.WEAPON));
		allCards.put("spanner", new Card("Spanner", Card.CardType.WEAPON));
		allCards.put("kitchen", new Card("Kitchen", Card.CardType.ROOM));
		allCards.put("ball room", new Card("Ball Room", Card.CardType.ROOM));
		allCards.put("conservatory", new Card("Conservatory", Card.CardType.ROOM));
		allCards.put("dining room", new Card("Dining Room", Card.CardType.ROOM));
		allCards.put("billiard room", new Card("Billiard Room", Card.CardType.ROOM));
		allCards.put("library", new Card("Library", Card.CardType.ROOM));
		allCards.put("lounge", new Card("Lounge", Card.CardType.ROOM));
		allCards.put("hall", new Card("Hall", Card.CardType.ROOM));
		allCards.put("study", new Card("Study", Card.CardType.ROOM));
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
		//System.out.println(murderConditions.characterCard());
		//System.out.println(murderConditions.weaponCard());
		//System.out.println(murderConditions.roomCard());
		//System.out.println(murderConditions);
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
	 * Create some new players and  assign them a character 
	 * randomly.
	 * 
	 * @param playerCount is the number of players to create
	 */
	private void createPlayers(int playerCount) {
		players.clear();
		selectPlayerCharacters(playerCount);
		assignCharacters();
		//for (Map.Entry<Integer,Player> player : players.entrySet()) { System.out.println(player.toString()); }
	}
	
	/**
	 * Randomly select all characters that will be used by the players
	 * 
	 * @param playerCount is the number of characters to select
	 */
	private void selectPlayerCharacters(int playerCount) {
		List<String> charactersNames = new ArrayList<String>(characters);
		Collections.shuffle(charactersNames);
		String charName = "";
		for (int i = 0; i < playerCount; i++) {
			charName = charactersNames.get(i);
			players.put(characters.indexOf(charName), new Player(charName));
		}
	}
	
	/**
	 * Assign each character to a player number (e.g. Player 1)
	 */
	private void assignCharacters() {
		Map<Integer,Player> playersReplacement = new TreeMap<Integer,Player>();
		int playerNum = 1;
		for (Map.Entry<Integer,Player> player : players.entrySet()) { 
			playersReplacement.put(playerNum,player.getValue());
			playerNum++;
		}
		players = playersReplacement;
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
		//for (Map.Entry<Integer,Player> player : players.entrySet()) { System.out.println(player.getValue().toString()); }
	}
	
	/**
	 * Greet the players to the game and ask how many people will 
	 * play.
	 *
	 * @return the number of players
	 */
	private Integer getPlayerCount() {
		System.out.print("Welcome to Cluedo!\nHow many players? (3-6 allowed).\nNumber of players: ");
		String answer = scan.nextLine();
		while (!answer.matches("[3456]")) {
			System.out.print("Invalid input. Please enter the number of players (3-6 players only).\nNumber of players: ");
			answer = scan.nextLine();
		}
		return Integer.parseInt(answer);
	}
	
	/**
	 * Ask the players if they want to start another game.
	 *
	 * @return whether or not a new game should be started
	 */
	private boolean askToPlayAgain() {
		String input;
		System.out.print("Would you like to play again? (yes/no): ");
		while (true) {
			input = scan.nextLine();
			if (input.equalsIgnoreCase("y") || input.equalsIgnoreCase("yes")) { return true; } 
			else if (input.equalsIgnoreCase("n") || input.equalsIgnoreCase("no")) { return false; }
			System.out.print("Yes or no answers only: ");
		}
	}
	
	public static void main(String[] args) {
		Game game;
		int playerCount;
		boolean playing = true;
		while (playing) {
			// Set up and play the game

			System.out.println("New game started");
			game = new Game();
			playerCount = game.getPlayerCount();
			game.setup(playerCount);
			game.play();
			// Ask to play again
			playing = game.askToPlayAgain();
		}
		System.out.println("Cluedo game ended. Thanks for playing!");
	}
}
