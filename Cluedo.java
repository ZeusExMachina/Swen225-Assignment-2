import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Cluedo extends JFrame {

    public static final Color PASSAGEWAY_COLOR = new Color(232,176,96);
    public static final Color ROOM_COLOR = new Color(201,191,192);
    public static final Color EMPTY_COLOR = new Color(79,156,100);
    public static final Color WALL_COLOR = new Color(87, 47, 32);
    
    public static final int WALL_THICKNESS = 8;
    private final int CARD_WIDTH = 80;
    private final int CARD_HEIGHT = 112;
    private final int DIE_SIZE = 50;
    private final int MAX_CARD_COUNT = 7;

    public static Map<String, Image> pieceImages = new HashMap<String, Image>();
    public static Map<String, Image> cardImages = new HashMap<String, Image>();
    public static Map<Integer, Image> diceImages = new HashMap<Integer, Image>();

    private Game game;
    private JTextArea displayMessage;
    private JLabel suggesterPlayerNameDisplay;
    private JLabel currentPlayerNameDisplay;
    private JLabel die1, die2;
    private JPanel suggestedCardsPanel;
    private JPanel currentPlayerHand;
    private ImageIcon emptyCardSlotImage;
    private final String[] okOption = {"Okay"};

    Cluedo(){
        super("Cluedo");
    }

    public Game getGame() { return game; }

    public void playGame() {
        boolean playing = askYesOrNo("Welcome to Cluedo! Would you like to play?", "Cluedo Game");
        //while (playing) {
            this.game = new Game(this);
            initUI(game);
            // First, ask how many players will join
            chooseCharacters(askNumOfPlayers());
            // Then, set up the game
            game.setup();
            
            Player player = new Player(1, "Meep", "Colonel Mustard");
            player.giveCard(game.getCard("Miss Scarlet"));
            player.giveCard(game.getCard("Colonel Mustard"));
            player.giveCard(game.getCard("Mr Green"));
            player.giveCard(game.getCard("Spanner"));
            player.giveCard(game.getCard("Candlestick"));
            player.giveCard(game.getCard("Revolver"));
            //player.giveCard(game.getCard("Lead Pipe"));
            showPlayerHand(player);
            
            CardTuple suggestion = askForThreeCards("Choose three cards to Suggest:", "Make a Suggestion", "Suggest!");
            showSuggestion(player, suggestion);
            showDiceRoll(4, 2);
            
            playing = askYesOrNo("Game over! Would you like to play again?", "Cluedo Game");
        //}
        //System.exit(0);
    }
    
    public Card refuting(Set<Card> refuteOptions) {
		Card refuteCard = null;
		JPanel overallPanel = new JPanel();
        overallPanel.setLayout(new BoxLayout(overallPanel, BoxLayout.PAGE_AXIS));
        JComboBox<String> refutingOptions = new JComboBox<String>();

        for(Card c : refuteOptions) {
        	refutingOptions.addItem(c.toString());
        }

        overallPanel.add(new JLabel("Choose Card to refute"));
        overallPanel.add(refutingOptions);

        for(Card c : refuteOptions) {
        	if(c.toString().equals(refutingOptions.getSelectedItem())) {
        		refuteCard = c;
        	}
        }
        return refuteCard;
    }
    
    // --------------------- FOR SETUP ------------------------

    private void initUI(Game game){
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setLayout(new BorderLayout());

        loadPieceImages();
        loadCardImages();
        loadDiceImages();
        
        setJMenuBar(createMenuBar());
        getContentPane().add(createBoardCanvas(game), BorderLayout.EAST);
        getContentPane().add(createLeftPanel(), BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                int response = JOptionPane.showConfirmDialog(null, "Are you sure you want to leave the game?", "Closing Cluedo", JOptionPane.YES_NO_OPTION);
                if (response == JOptionPane.YES_OPTION) { System.exit(0); }
            }
        });
        
        pack();
        setResizable(true);
        setVisible(true);
    }

    private void loadPieceImages(){
        try{
            // Load Weapon Images
            BufferedImage spannerImage = ImageIO.read(new File("resources/spanner.png"));
            BufferedImage revolverImage = ImageIO.read(new File("resources/revolver.png"));
            BufferedImage ropeImage = ImageIO.read(new File("resources/rope.png"));
            BufferedImage leadPipeImage = ImageIO.read(new File("resources/lead_pipe.png"));
            BufferedImage candlestickImage = ImageIO.read(new File("resources/candlestick.png"));
            BufferedImage daggerImage = ImageIO.read(new File("resources/dagger.png"));
            pieceImages.put("Spanner", spannerImage);
            pieceImages.put("Revolver" , revolverImage);
            pieceImages.put("Rope" , ropeImage);
            pieceImages.put("Lead Pipe" , leadPipeImage);
            pieceImages.put("Candlestick", candlestickImage);
            pieceImages.put("Dagger", daggerImage);

            // Load Player Images
            BufferedImage colonelMustardImage = ImageIO.read(new File("resources/mustard.png"));
            BufferedImage missScarletImage = ImageIO.read(new File("resources/scarlet.png"));
            BufferedImage professorPlumImage = ImageIO.read(new File("resources/plum.png"));
            BufferedImage missPeacockImage = ImageIO.read(new File("resources/peacock.png"));
            BufferedImage mrGreenImage = ImageIO.read(new File("resources/green.png"));
            BufferedImage mrsWhiteImage = ImageIO.read(new File("resources/white.png"));
            pieceImages.put("Colonel Mustard", colonelMustardImage);
            pieceImages.put("Miss Scarlet", missScarletImage);
            pieceImages.put("Professor Plum", professorPlumImage);
            pieceImages.put("Miss Peacock", missPeacockImage);
            pieceImages.put("Mr Green", mrGreenImage);
            pieceImages.put("Mrs White", mrsWhiteImage);
        } catch(IOException e) {
            System.out.println("Could not load piece images from the resources directory" + e);
        }
    }
    
    private void loadCardImages() {
    	try {
    		// Load Character Cards
    		cardImages.put("Miss Scarlet", ImageIO.read(new File("resources/scarlet_card.png")));
    		cardImages.put("Colonel Mustard", ImageIO.read(new File("resources/mustard_card.png")));
    		cardImages.put("Mrs White", ImageIO.read(new File("resources/white_card.png")));
    		cardImages.put("Mr Green", ImageIO.read(new File("resources/green_card.png")));
    		cardImages.put("Mrs Peacock", ImageIO.read(new File("resources/peacock_card.png")));
    		cardImages.put("Professor Plum", ImageIO.read(new File("resources/plum_card.png")));
    		
    		// Load Weapon Cards
    		cardImages.put("Candlestick", ImageIO.read(new File("resources/candlestick_card.png")));
    		cardImages.put("Dagger", ImageIO.read(new File("resources/dagger_card.png")));
    		cardImages.put("Lead Pipe", ImageIO.read(new File("resources/lead_pipe_card.png")));
    		cardImages.put("Rope", ImageIO.read(new File("resources/rope_card.png")));
    		cardImages.put("Revolver", ImageIO.read(new File("resources/revolver_card.png")));
    		cardImages.put("Spanner", ImageIO.read(new File("resources/spanner_card.png")));
    		
    		// Load Room Cards
    		cardImages.put("Kitchen", ImageIO.read(new File("resources/kitchen_card.png")));
    		cardImages.put("Ball Room", ImageIO.read(new File("resources/ball_room_card.png")));
    		cardImages.put("Conservatory", ImageIO.read(new File("resources/conservatory_card.png")));
    		cardImages.put("Dining Room", ImageIO.read(new File("resources/dining_room_card.png")));
    		cardImages.put("Billiard Room", ImageIO.read(new File("resources/billiard_room_card.png")));
    		cardImages.put("Library", ImageIO.read(new File("resources/library_card.png")));
    		cardImages.put("lounge", ImageIO.read(new File("resources/lounge_card.png")));
    		cardImages.put("Hall", ImageIO.read(new File("resources/hall_card.png")));
    		cardImages.put("Study", ImageIO.read(new File("resources/study_card.png")));
    		
    		// Empty Card Slot Image
    		Image cardSlotImage = ImageIO.read(new File("resources/card_slot.png"));
    		emptyCardSlotImage = new ImageIcon(cardSlotImage.getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_DEFAULT));
    	} catch(IOException e) {
            System.out.println("Could not load card images from the resources directory - " + e);
        }
    }
    
    private void loadDiceImages() {
    	try {
    		diceImages.put(1, ImageIO.read(new File("resources/dice_one.png")));
    		diceImages.put(2, ImageIO.read(new File("resources/dice_two.png")));
    		diceImages.put(3, ImageIO.read(new File("resources/dice_three.png")));
    		diceImages.put(4, ImageIO.read(new File("resources/dice_four.png")));
    		diceImages.put(5, ImageIO.read(new File("resources/dice_five.png")));
    		diceImages.put(6, ImageIO.read(new File("resources/dice_six.png")));
    	} catch(IOException e) {
            System.out.println("Could not load dice images from the resources directory - " + e);
        }
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
    
    private JPanel createLeftPanel() {
    	JPanel leftPanel = new JPanel(new GridBagLayout());
    	GridBagConstraints constraints = new GridBagConstraints();
        
    	constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 0.1;
    	
        // Player Name Display
        this.currentPlayerNameDisplay = new JLabel("[Current Player's Name Here]");
        this.currentPlayerNameDisplay.setFont(currentPlayerNameDisplay.getFont().deriveFont(15.0f));
        constraints.gridx = 0; constraints.gridy = 0;
        constraints.weighty = 0.15;
        constraints.ipady = 5;
        leftPanel.add(this.currentPlayerNameDisplay, constraints);
        
        // Current Game State Description (e.g. A's turn to refute B)
        this.displayMessage = new JTextArea("Game start");
        this.displayMessage.setEditable(false);
        constraints.gridx = 0; constraints.gridy = 1;
        constraints.weighty = 0.35;
        constraints.ipady = 0;
        leftPanel.add(this.displayMessage, constraints);
        
        // Player Actions Label
        constraints.gridx = 0; constraints.gridy = 2;
        constraints.weighty = 0.1;
        leftPanel.add(new JLabel("Player Actions"), constraints);
        
        // Button Panel
        constraints.gridx = 0; constraints.gridy = 3;
        constraints.gridheight = 1;
        constraints.weighty = 1.0; 
        leftPanel.add(createButtonPanel(game), constraints);
        
        // Suggested Cards Panel
        constraints.gridx = 0; constraints.gridy = 4;
        constraints.weighty = 0.1;
        leftPanel.add(createSuggestedCardsPanel(), constraints);
        
        // Player's Cards Panel
        constraints.gridx = 0; constraints.gridy = 5;
        constraints.ipady = CARD_HEIGHT/3;
        leftPanel.add(createCardPanel(game), constraints);
        
        return leftPanel;
    }

    private JPanel createButtonPanel(Game game){
		JPanel panel = new JPanel();
		panel.setBounds(40,80,200,200);
        panel.setBackground(Color.gray);
        panel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(12, 12, 12 , 12);

        // Roll button
		JButton rollButton=new JButton("Roll"); 
		rollButton.setFont(rollButton.getFont().deriveFont(16.0f));
        rollButton.setBackground(Color.GREEN);
        if(game.canRoll() == false || game.canSuggest() == false) {
        	b1.setEnabled(false);
        	b1.setBackground(Color.gray);
        }

        if(game.canRoll() == true) {
        	b1.setEnabled(true);
        	b1.setBackground(Color.GREEN);
        }

        b1.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e){
        		//roll()
        		game.setMoved(true);
                game.setRolled(false); 
            }
        });
        
        constraints.weightx = 1.0;
        constraints.weighty = 0.35;
        constraints.gridx = 0; constraints.gridy = 0;
        panel.add(rollButton, constraints);
        
        // Move button
        JButton moveButton = new JButton("Move");
        moveButton.setFont(moveButton.getFont().deriveFont(16.0f));
        moveButton.setBackground(Color.GREEN);
        if(game.canMove() == false) {
        	b4.setEnabled(false);
        	b4.setBackground(Color.gray);
        }

        if(game.canMove() == true) {
        	b4.setEnabled(true);
        	b4.setBackground(Color.GREEN);
        }

        b4.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e){  
                game.setMoved(true); 
            }
        });
        constraints.gridx = 1; constraints.gridy = 0;
        panel.add(moveButton, constraints);
        
        // Dice display
        this.die1 = new JLabel();
        this.die2 = new JLabel();
        showDiceRoll(1, 1);
        constraints.gridx = 2; constraints.gridy = 0;
        panel.add(die1, constraints);
        constraints.gridx = 3; constraints.gridy = 0;
        panel.add(die2, constraints);
        
        //Suggest button
        JButton suggestButton = new JButton("Suggest");
        suggestButton.setFont(suggestButton.getFont().deriveFont(16.0f));
        suggestButton.setBackground(Color.GREEN);
        if(game.canSuggest() == false) {
        	b2.setEnabled(false);
        	b2.setBackground(Color.gray);
        }

        if(game.canSuggest() == true) {
        	b2.setEnabled(true);
        	b2.setBackground(Color.GREEN);
        }

        b2.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e){  
        		//suggest()
        		game.setAccused(true);
                game.setSuggested(false); 
            }
        });
        constraints.gridwidth = 2;
        constraints.gridx = 0; constraints.gridy = 1;
        panel.add(suggestButton, constraints);

        //Accuse button
        JButton accuseButton = new JButton("Accuse");
        accuseButton.setFont(accuseButton.getFont().deriveFont(16.0f));
        accuseButton.setBackground(Color.GREEN);
        if(game.canAccuse() == false) {
        	b3.setEnabled(false);
        	b3.setBackground(Color.gray);
        }

        if(game.canAccuse() == true) {
        	b3.setEnabled(true);
        	b3.setBackground(Color.GREEN);
        }

        b3.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e){
        		//accuse();
                game.setAccused(false); 
            }
        });
        constraints.gridx = 2; constraints.gridy = 1;
        panel.add(accuseButton, constraints);

        // End Turn Button
        JButton endTurnButton = new JButton("End Turn");
        endTurnButton.setFont(endTurnButton.getFont().deriveFont(16.0f));
        endTurnButton.setBackground(Color.GREEN);
        constraints.weighty = 0.3;
        constraints.gridwidth = 4;
        constraints.gridx = 0; constraints.gridy = 2;
        panel.add(endTurnButton, constraints);
        
        return panel;
    }
    
    private JPanel createSuggestedCardsPanel() {
    	JPanel suggestedPanel = new JPanel(new BorderLayout());
    	
    	this.suggesterPlayerNameDisplay = new JLabel("Suggestion Panel");
    	this.suggesterPlayerNameDisplay.setFont(suggesterPlayerNameDisplay.getFont().deriveFont(13.0f));
    	suggestedPanel.add(this.suggesterPlayerNameDisplay, BorderLayout.NORTH);
    	
    	this.suggestedCardsPanel = new JPanel(new FlowLayout());
    	suggestedPanel.add(this.suggestedCardsPanel);
    	clearSuggestion();
    	
    	return suggestedPanel;
    }
    
    private JPanel createCardPanel(Game game){
        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.setPreferredSize(new Dimension(4*CARD_WIDTH+50, 2*CARD_HEIGHT));
        
        JPanel labelPanel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Cards in Hand:");
        label.setFont(label.getFont().deriveFont(17.0f));
        labelPanel.add(label);
        cardPanel.add(labelPanel, BorderLayout.NORTH);
        
        this.currentPlayerHand = new JPanel(new FlowLayout());
        cardPanel.add(currentPlayerHand, BorderLayout.CENTER);
        addEmptyCardSlotsToHand(MAX_CARD_COUNT);
        this.repaint();
        
        return cardPanel;
    }
    
    private JPanel createBoardCanvas(Game game){
        JPanel board = new JPanel();
        board.setLayout(new GridLayout(25,24,0,0));
        for (int row = 0; row < 25; row += 1) {
            for (int col = 0; col < 24; col += 1) {
                board.add(cell(row,col,game));
            }
        }
        return board;
    }

    static class BoardSquare extends JPanel{

        Location cell;
        Game game;

        BoardSquare(Location location, Game game){
            super();
            this.cell = location;
            this.game = game;
        }

        private void drawPiece(Graphics g){
            Graphics2D g2d = (Graphics2D) g;

            if(cell.occupied){
                int padding = Cluedo.WALL_THICKNESS;
                int height = this.getHeight() - padding*2;
                int width = this.getWidth() - padding*2;

                for(Map.Entry<String, Piece> p : game.getPieces().entrySet()){
                    if(p.getValue().equals(cell.piece)){
                        Piece current = p.getValue();
                        switch(current.icon()){
                            case "c":
                                g2d.drawImage(Cluedo.pieceImages.get("Candlestick"), padding, padding,width,height,this);
                                break;
                            case "d":
                                g2d.drawImage(Cluedo.pieceImages.get("Dagger"), padding, padding,width,height,this);
                                break;
                            case "l":
                                g2d.drawImage(Cluedo.pieceImages.get("Lead Pipe"), padding, padding,width,height,this);
                                break;
                            case "g":
                                g2d.drawImage(Cluedo.pieceImages.get("Revolver"), padding, padding,width,height,this);
                                break;
                            case "r":
                                g2d.drawImage(Cluedo.pieceImages.get("Rope"), padding, padding,width,height,this);
                                break;
                            case "s":
                                g2d.drawImage(Cluedo.pieceImages.get("Spanner"), padding, padding,width,height,this);
                                break;
                            case "P":
                                g2d.drawImage(Cluedo.pieceImages.get("Miss Peacock"), padding, padding,width,height,this);
                                break;
                            case "L":
                                g2d.drawImage(Cluedo.pieceImages.get("Professor Plum"), padding, padding,width,height,this);
                                break;
                            case "M":
                                g2d.drawImage(Cluedo.pieceImages.get("Colonel Mustard"), padding, padding,width,height,this);
                                break;
                            case "W":
                                g2d.drawImage(Cluedo.pieceImages.get("Mrs White"), padding, padding,width,height,this);
                                break;
                            case "G":
                                g2d.drawImage(Cluedo.pieceImages.get("Mr Green"), padding, padding,width,height,this);
                                break;
                            case "S":
                                g2d.drawImage(Cluedo.pieceImages.get("Miss Scarlet"), padding, padding,width,height,this);
                                break;
                        }
                    }
                }
            }
        }

        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            drawPiece(g);
        }
    }

    public JPanel cell(int row, int col, Game g) {
        Location current = g.getLocation(row,col);
        return new BoardSquare(current, game) {
            {
                this.setLayout(null);

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
    
    // ---------- FOR CHANGING COMPONENTS ON SCREEN DURING GAME -----------------
    
    private boolean askYesOrNo(String askMessage, String windowTitle) {
        int response;
        while (true) {
            response = JOptionPane.showConfirmDialog(null, askMessage, windowTitle, JOptionPane.YES_NO_OPTION);
            if (response <= 0) { return true; }
            if (response > 0) { return false; }
        }
    }

    private int askNumOfPlayers() {
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

    private void chooseCharacters(int numOfPlayers) {
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
        for (int i = 0; i < game.characters.size(); i++) {
            JRadioButton characterRadioButton = new JRadioButton(game.characters.get(i));
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
        String playerName;
        String characterName;
        boolean playerCreationSuccessful;
        JRadioButton selectedCharacterButton;
        for (int i = 1; i < numOfPlayers+1; i++) {
            playerCreationSuccessful = false;
            selectedCharacterButton = null;
            while (playerCreationSuccessful == false) {
                playerName = null;
                characterName = null;
                playerNameField.setText("");
                characterButtonGroup.clearSelection();
                while (playerName == null || characterName == null) {
                    JOptionPane.showOptionDialog(null, playerDetailsPanel, "Player "+i+" Character Selection", JOptionPane.PLAIN_MESSAGE, JOptionPane.QUESTION_MESSAGE, null, okOption, okOption[0]);
                    playerName = playerNameField.getText();
                    for (JRadioButton button : characterSet) {
                        if (button.isSelected() && playerName.length() > 0) {
                            characterName = button.getText();
                            selectedCharacterButton = button;
                        }
                    }
                }
                // Send the details of a player to the model (Game class)
                playerCreationSuccessful = game.addPlayer(i, playerName, characterName);
            }
            selectedCharacterButton.setEnabled(false);
        }
    }

    /**
     * Ask a Player to select 3 cards
     *
     * @param message is the message to display (e.g. "Choose three cards:")
     * @param titleMessage is the title of the popup window (e.g. "Make a suggestion")
     * @param buttonName is the text to display on the confirmation button (e.g. "Okay" or "Accuse!")
     * @return a CardTuple consisting of the 3 chosen cards
     */
    public CardTuple askForThreeCards(String message, String titleMessage, String buttonName) {
        JPanel overallPanel = new JPanel();
        overallPanel.setLayout(new BoxLayout(overallPanel, BoxLayout.PAGE_AXIS));

        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new BorderLayout());
        labelPanel.add(new JLabel(message));

        JPanel selectionPanel = new JPanel();
        selectionPanel.setLayout(new GridLayout(3,2));

        String[] charactersArray = new String[game.characters.size()];
        JComboBox<String> characterChoices = new JComboBox<String>(game.characters.toArray(charactersArray));
        selectionPanel.add(new JLabel("Select a Character:"), BorderLayout.WEST);
        selectionPanel.add(characterChoices, BorderLayout.EAST);

        String[] weaponsArray = new String[game.weapons.size()];
        JComboBox<String> weaponChoices = new JComboBox<String>(game.weapons.toArray(weaponsArray));
        selectionPanel.add(new JLabel("Select a Weapon:"), BorderLayout.WEST);
        selectionPanel.add(weaponChoices, BorderLayout.EAST);

        String[] roomsArray = new String[game.rooms.size()];
        JComboBox<String> roomChoices = new JComboBox<String>(game.rooms.toArray(roomsArray));
        selectionPanel.add(new JLabel("Select a Room:"), BorderLayout.WEST);
        selectionPanel.add(roomChoices, BorderLayout.EAST);

        overallPanel.add(labelPanel);
        overallPanel.add(new JPanel());
        overallPanel.add(selectionPanel);

        String[] buttonOption = {buttonName};
        JOptionPane.showOptionDialog(null, overallPanel, titleMessage, JOptionPane.PLAIN_MESSAGE, JOptionPane.QUESTION_MESSAGE, null, buttonOption, buttonOption[0]);
        return new CardTuple(game.getCard((String)characterChoices.getSelectedItem()), game.getCard((String)weaponChoices.getSelectedItem()), game.getCard((String)roomChoices.getSelectedItem()));
    }
    
    /**
     * Update the game state description
     * @param text
     */
    public void displayGameState(String text) {
        displayMessage.setText(text);
    }
    
    /**
     * Graphically display the values of rolled dice
     * @param firstDieValue
     * @param secondDieValue
     */
    public void showDiceRoll(int firstDieValue, int secondDieValue) {
    	ImageIcon dieIcon = new ImageIcon(diceImages.get(firstDieValue).getScaledInstance(DIE_SIZE, DIE_SIZE, Image.SCALE_DEFAULT));
    	this.die1.setIcon(dieIcon);
    	dieIcon = new ImageIcon(diceImages.get(secondDieValue).getScaledInstance(DIE_SIZE, DIE_SIZE, Image.SCALE_DEFAULT));
    	this.die2.setIcon(dieIcon);
    	this.repaint();
    }
    
    /**
     * Graphically display all cards in the player's hand
     * @param player
     */
    public void showPlayerHand(Player player) {
    	currentPlayerHand.removeAll();
    	for (Map.Entry<String,Card> card : player.getHand().entrySet()) {
    		ImageIcon cardIcon = new ImageIcon(cardImages.get(card.getValue().getName()).getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_DEFAULT));
    		currentPlayerHand.add(new JLabel(cardIcon));
    	}
    	addEmptyCardSlotsToHand(MAX_CARD_COUNT-player.getHand().size());
    	this.repaint();
    }
    
    /**
     * Append/Add empty card slots to the Player's Card Panel
     * @param numOfSlotsToAdd
     */
    public void addEmptyCardSlotsToHand(int numOfSlotsToAdd) {
    	for (int i = 0; i < numOfSlotsToAdd; i++) { currentPlayerHand.add(new JLabel(emptyCardSlotImage)); }
    	this.repaint();
    }
    
    /**
     * Graphically display a suggestion made by a player, according to a given suggestion
     * @param player
     * @param suggestion
     */
    public void showSuggestion(Player player, CardTuple suggestion) {
    	suggesterPlayerNameDisplay.setText(player.getPlayerName() + " (" + player.getCharacterName() + ") made the following suggestion: ");
    	
    	suggestedCardsPanel.removeAll();
    	ImageIcon cardIcon;
    	
    	cardIcon = new ImageIcon(cardImages.get(suggestion.characterCard().getName()).getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_DEFAULT));
    	suggestedCardsPanel.add(new JLabel(cardIcon));
    	cardIcon = new ImageIcon(cardImages.get(suggestion.weaponCard().getName()).getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_DEFAULT));
    	suggestedCardsPanel.add(new JLabel(cardIcon));
    	cardIcon = new ImageIcon(cardImages.get(suggestion.roomCard().getName()).getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_DEFAULT));
    	suggestedCardsPanel.add(new JLabel(cardIcon));
    	
    	this.repaint();
    }
    
    /**
     * Clear the Suggestion Panel of any cards, emptying the panel
     */
    public void clearSuggestion() {
    	suggesterPlayerNameDisplay.setText("Suggestion Panel");
    	suggestedCardsPanel.removeAll();
    	for (int i = 0; i < 3; i++) { suggestedCardsPanel.add(new JLabel(emptyCardSlotImage)); }
    	this.repaint();
    }

    public static void main(String[] args){
        EventQueue.invokeLater(() -> {
            Cluedo frame = new Cluedo();
            frame.playGame();
        });
    }
}
