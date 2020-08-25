import java.util.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Cluedo extends JFrame {

    public static final Color PASSAGEWAY_COLOR = new Color(232,176,96);
    public static final Color ROOM_COLOR = new Color(201,191,192);
    public static final Color EMPTY_COLOR = new Color(79,156,100);
    public static final Color WALL_COLOR = new Color(87, 47, 32);

    private Game game;
    private JTextField displayMessage;
    
    Cluedo(){
    	super("Cluedo");
    	this.game = new Game();
        initUI(game);
        setResizable(true);
        setVisible(true);
    }
    
    public Game getGame() { return game; }

    public void playGame() {
    	boolean playing = askYesOrNo("Welcome to Cluedo! Would you like to play?", "Cluedo Game");
        while (playing) {
        	// First, ask how many players will join
        	int numOfPlayers = askNumOfPlayers();
        	chooseCharacters(game, numOfPlayers);
        	
        	playing = askYesOrNo("Game over! Would you like to play again?", "Cluedo Game");
        }
        System.exit(0);
        //if (!playing) { System.exit(0); }
    }

    private void initUI(Game game){
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        //getRootPane().setLayout(new BorderLayout());
        getContentPane().setLayout(new BorderLayout());
        
        // -------------- BOTTOM PANEL -----------------
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.PAGE_AXIS));
        JPanel cards = createCardPanel(game);
        JPanel buttons = createButtonPanel(game);
        this.displayMessage = new JTextArea("Game start");
        this.displayMessage.setEditable(false);
        bottomPanel.add(displayMessage);
        bottomPanel.add(buttons);
        bottomPanel.add(cards);
        
        getContentPane().add(createBoardCanvas(game), BorderLayout.CENTER);
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);
        setJMenuBar(createMenuBar());

        pack();
    }

    private JMenuBar createMenuBar(){
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.setToolTipText("Exit the game");
        exitMenuItem.addActionListener((event) -> System.exit(0));

        fileMenu.add(exitMenuItem);
        menuBar.add(fileMenu);

        JMenu gameMenu = new JMenu("Game");
        JMenuItem restartMenuItem = new JMenuItem("Restart");
        restartMenuItem.setToolTipText("Restart the game");
        gameMenu.add(restartMenuItem);
        menuBar.add(gameMenu);

        return menuBar;
    }

    private static JPanel createCardPanel(Game game){
        return new JPanel();
    }

    private static JPanel createButtonPanel(Game game){
        return new JPanel();
    }

    private static JPanel createBoardCanvas(Game game){
        JPanel board = new JPanel();
        board.setLayout(new GridLayout(25,24,0,0));
        for (int row = 0; row < 25; row += 1) {
            for (int col = 0; col < 24; col += 1) {
                board.add(cell(row,col,game));
            }
        }
        return board;
    }

    public static JPanel cell(int row, int col, Game g) {
        return new JPanel() {
            {
                this.setLayout(null);

                Location current = g.getLocation(row,col);
                Color color;

                if (current.getRoom() != null && current.getRoom().getName().equals("Passageway")) {
                    color = PASSAGEWAY_COLOR;
                    Border blackLine = BorderFactory.createLineBorder(Color.black);
                    this.setBorder(blackLine);
                } else {
                    color = current.getRoom() != null ? ROOM_COLOR : EMPTY_COLOR;
                    int wallThickness = 8;
                    int north = current.northWall ? wallThickness : 0;
                    int east = current.eastWall ? wallThickness : 0;
                    int west = current.westWall ? wallThickness : 0;
                    int south = current.southWall ? wallThickness : 0;
                    Border wall = BorderFactory.createMatteBorder(north, west, south, east, WALL_COLOR);
                    this.setBorder(wall);
                }
                this.setBackground(color);
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(32, 32);
            }
        };
    }
    
    private boolean askYesOrNo(String askMessage, String windowTitle) {
    	int response;
    	while (true) {
    		response = JOptionPane.showConfirmDialog(null, askMessage, windowTitle, JOptionPane.YES_NO_OPTION);
    		if (response <= 0) { return true; }
    		if (response > 0) { return false; }
    	}
    }
    
    private int askNumOfPlayers() {
    	String[] okOption = {"Okay"};
    	JPanel playerCountPanel = new JPanel();
    	JPanel labelPanel = new JPanel();
    	JComboBox<Integer> playerCountChoices = new JComboBox<Integer>(new Integer[]{3,4,5,6});
    	
    	playerCountPanel.setLayout(new BoxLayout(playerCountPanel, BoxLayout.PAGE_AXIS));
    	labelPanel.setLayout(new BorderLayout());
    	labelPanel.add(new JLabel("How many players?"), BorderLayout.WEST);
    	playerCountPanel.add(labelPanel);
    	playerCountPanel.add(playerCountChoices);
    	JOptionPane.showOptionDialog(null, playerCountPanel, "Cludo Game", JOptionPane.PLAIN_MESSAGE, JOptionPane.QUESTION_MESSAGE, null, okOption, okOption[0]);
    	return (int)playerCountChoices.getSelectedItem();
    }
    
    private void chooseCharacters(Game game, int numOfPlayers) {
    	String playerName;
    	String characterName;
    	JPanel playerDetailsPanel = new JPanel();
    	JPanel selectCharLabelPanel = new JPanel();
    	JPanel enterNameLabelPanel = new JPanel();
    	JPanel characterPanel = new JPanel();
    	JTextField playerNameField = new JTextField();
    	ButtonGroup characterButtonGroup = new ButtonGroup();
    	Set<JRadioButton> characterSet = new HashSet<JRadioButton>();
    	// Set up the character selection popup screen
    	playerDetailsPanel.setLayout(new BoxLayout(playerDetailsPanel, BoxLayout.PAGE_AXIS));
    	selectCharLabelPanel.setLayout(new BorderLayout());
    	selectCharLabelPanel.add(new JLabel("Select a character to play:"), BorderLayout.WEST);
    	playerDetailsPanel.add(selectCharLabelPanel);
        for (int i = 0; i < Game.characters.size(); i++) {
        	JRadioButton characterRadioButton = new JRadioButton(Game.characters.get(i));
        	characterButtonGroup.add(characterRadioButton);
        	characterSet.add(characterRadioButton);
        	characterPanel.add(characterRadioButton);
        }
        playerDetailsPanel.add(characterPanel);
        enterNameLabelPanel.setLayout(new BorderLayout());
        enterNameLabelPanel.add(new JLabel("Enter your name:"), BorderLayout.WEST);
        playerDetailsPanel.add(enterNameLabelPanel);
        playerDetailsPanel.add(playerNameField);
        // Ask players for their name and the character they pick
        String[] okOption = {"Okay"};
	boolean playerCreationSuccessful;
    	for (int i = 1; i < numOfPlayers+1; i++) {
    		playerName = null;
    		characterName = null;
    		playerNameField.setText("");
    		characterButtonGroup.clearSelection();
		playerCreationSuccessful = false;
    		while (playerName == null || playerName.length() < 1 || characterName == null || playerCreationSuccessful == false) {
	    		JOptionPane.showOptionDialog(null, playerDetailsPanel, "Player "+i+" Character Selection", JOptionPane.PLAIN_MESSAGE, JOptionPane.QUESTION_MESSAGE, null, okOption, okOption[0]);
	    		playerName = playerNameField.getText();
	    		for (JRadioButton button : characterSet) {
	    			if (button.isSelected()) { 
	    				characterName = button.getText();
	    				button.setEnabled(false);
	    			}
	    		}
	    		// Send the details of a player to the model (Game class)
	    		playerCreationSuccessful = game.addPlayer(i, playerName, characterName);
    		}
    	}
    }

    public static void main(String[] args){
        EventQueue.invokeLater(() -> {
            Cluedo frame = new Cluedo();
            frame.playGame();
        });
    }

}
