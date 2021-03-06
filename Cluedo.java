import java.awt.event.*;
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
    private final int CARD_WIDTH = 85;
    private final int CARD_HEIGHT = 119;
    private final int DIE_SIZE = 50;
    private final int MAX_CARD_COUNT = 6;

    public static Map<String, Image> pieceImages = new HashMap<String, Image>();
    public static Map<String, Image> cardImages = new HashMap<String, Image>();
    public static Map<Integer, Image> diceImages = new HashMap<Integer, Image>();

    private Game game;
    private JTextArea displayMessage;
    private JLabel suggesterPlayerNameDisplay;
    private JLabel currentPlayerNameDisplay;
    private JLabel die1, die2;
    private JPanel currentPlayerHand;
    private JButton rollButton;
    private JButton suggestButton;
    private JButton accuseButton;
    private JButton endTurnButton;
    private ImageIcon emptyCardSlotImage;
    private final String[] okOption = {"Okay"};

    Cluedo(){
        super("Cluedo");
    }

    public Game getGame() { return game; }

    public void setupAndRunCluedo() {
        if (askYesOrNo("Welcome to Cluedo! Would you like to play?", "Cluedo Game")) {
            // Initialize the Game model and the UI
            this.game = new Game(this);
            initUI(game);
            // Ask how many players will join
            chooseCharacters(askNumOfPlayers());
            // Set up the game
            game.setup();
            // Play the game
            game.play();
        } else { System.exit(0); }
    }

    public void finishGame(String endMessage, CardTuple murderConditions) {
        // Display the end-of-game result, as well as the murder conditions of the game
        JPanel endGamePanel = new JPanel();
        endGamePanel.setLayout(new BoxLayout(endGamePanel, BoxLayout.PAGE_AXIS));

        JPanel labelPanel1 = new JPanel(new BorderLayout());
        labelPanel1.add(new JLabel(endMessage, JLabel.LEFT), BorderLayout.WEST);
        endGamePanel.add(labelPanel1);
        endGamePanel.add(new JPanel());

        JPanel labelPanel2 = new JPanel(new BorderLayout());
        labelPanel2.add(new JLabel("Winning combination:", JLabel.LEFT), BorderLayout.WEST);
        endGamePanel.add(labelPanel2);

        JPanel winConditionPanel = new JPanel(new FlowLayout());
        showCardTuple(winConditionPanel, murderConditions);
        endGamePanel.add(winConditionPanel);

        JOptionPane.showMessageDialog(null, endGamePanel, "Cluedo Game", JOptionPane.PLAIN_MESSAGE);

        // Ask user if they want to play again
        if (askYesOrNo("Game over! Would you like to play again?", "Cluedo Game")) {
            this.dispose();
            setupAndRunCluedo();
        }
        else { System.exit(0); }
    }

    // --------------------- FOR SETUP ------------------------

    private void initUI(Game game){
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
                if (response == 0) { System.exit(0); }
            }
        });

        pack();
        setResizable(true);
        setVisible(true);
    }

    private void loadPieceImages(){
        try{
            // Load Weapon Images
            pieceImages.put("Spanner", ImageIO.read(new File("resources/spanner.png")));
            pieceImages.put("Revolver" , ImageIO.read(new File("resources/revolver.png")));
            pieceImages.put("Rope" , ImageIO.read(new File("resources/rope.png")));
            pieceImages.put("Lead Pipe" , ImageIO.read(new File("resources/lead_pipe.png")));
            pieceImages.put("Candlestick", ImageIO.read(new File("resources/candlestick.png")));
            pieceImages.put("Dagger", ImageIO.read(new File("resources/dagger.png")));

            // Load Player Images
            pieceImages.put("Colonel Mustard", ImageIO.read(new File("resources/mustard.png")));
            pieceImages.put("Miss Scarlet", ImageIO.read(new File("resources/scarlet.png")));
            pieceImages.put("Professor Plum", ImageIO.read(new File("resources/plum.png")));
            pieceImages.put("Miss Peacock", ImageIO.read(new File("resources/peacock.png")));
            pieceImages.put("Mr Green", ImageIO.read(new File("resources/green.png")));
            pieceImages.put("Mrs White", ImageIO.read(new File("resources/white.png")));
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
            cardImages.put("Lounge", ImageIO.read(new File("resources/lounge_card.png")));
            cardImages.put("Hall", ImageIO.read(new File("resources/hall_card.png")));
            cardImages.put("Study", ImageIO.read(new File("resources/study_card.png")));

            // Empty Card Slot Image
            Image cardSlotImage = ImageIO.read(new File("resources/card_slot.png"));
            emptyCardSlotImage = new ImageIcon(cardSlotImage.getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH));
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

        constraints.fill = GridBagConstraints.VERTICAL;
        constraints.weightx = 0.1;

        // Player Name Display
        this.currentPlayerNameDisplay = new JLabel("[Current Player's Name Here]", JLabel.CENTER);
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

        // Player's Cards Panel
        constraints.gridx = 0; constraints.gridy = 4;
        leftPanel.add(new JPanel());
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
        rollButton=new JButton("Roll");
        rollButton.setFont(rollButton.getFont().deriveFont(16.0f));
        setRollButton(true);
        rollButton.addActionListener((event) -> game.playerRollsDice());
        constraints.weightx = 1.0;
        constraints.weighty = 0.3;
        constraints.gridx = 0; constraints.gridy = 0;
        panel.add(rollButton, constraints);

        // Spacer
        constraints.gridx = 1; constraints.gridy = 0;
        JPanel spacer = new JPanel();
        spacer.setBackground(Color.gray);
        panel.add(spacer, constraints);

        // Dice display
        this.die1 = new JLabel();
        this.die2 = new JLabel();
        showDiceRoll(1, 1);
        constraints.gridx = 2; constraints.gridy = 0;
        panel.add(die1, constraints);
        constraints.gridx = 3; constraints.gridy = 0;
        panel.add(die2, constraints);

        //Suggest button
        suggestButton = new JButton("Suggest");
        suggestButton.setFont(suggestButton.getFont().deriveFont(16.0f));
        setSuggestButton(true);
        suggestButton.addActionListener((event) -> game.suggestionMade());
        constraints.gridwidth = 2;
        constraints.gridx = 0; constraints.gridy = 1;
        panel.add(suggestButton, constraints);

        //Accuse button
        accuseButton = new JButton("Accuse");
        accuseButton.setFont(accuseButton.getFont().deriveFont(16.0f));
        setAccuseButton(true);
        accuseButton.addActionListener((event) -> game.accusationMade());
        constraints.gridx = 2; constraints.gridy = 1;
        panel.add(accuseButton, constraints);

        // End Turn Button
        endTurnButton = new JButton("End Turn");
        endTurnButton.setFont(endTurnButton.getFont().deriveFont(16.0f));
        endTurnButton.setBackground(PASSAGEWAY_COLOR);
        endTurnButton.addActionListener((event) -> game.endCurrentTurn());
        constraints.weighty = 0.3;
        constraints.gridwidth = 4;
        constraints.gridx = 0; constraints.gridy = 2;
        panel.add(endTurnButton, constraints);

        return panel;
    }

    private JPanel createCardPanel(Game game){
        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.setPreferredSize(new Dimension(3*CARD_WIDTH+50, 3*CARD_HEIGHT));

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
        JPanel board = new EntireBoard();
        board.setLayout(new GridLayout(25,24,0,0));
        for (int row = 0; row < 25; row += 1) {
            for (int col = 0; col < 24; col += 1) {
                board.add(cell(row,col,game));
            }
        }
        return board;
    }

    class EntireBoard extends JPanel{
        @Override
        public void paint(Graphics g){
            super.paint(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            int height = getHeight();
            int width = getWidth();
            g.setFont(new Font("default", Font.BOLD, 15));

            g2d.drawString("KITCHEN", (int)(width*0.08), (int)(height * 0.16));
            g2d.drawString("BALL ROOM", (int)(width*0.44), (int)(height * 0.2));
            g2d.drawString("CONSERVATORY", (int)(width*0.795), (int)(height * 0.14));
            g2d.drawString("DINING ROOM", (int)(width*0.09), (int)(height * 0.52));
            g2d.drawString("BILLIARD ROOM", (int)(width*0.775), (int)(height * 0.43));
            g2d.drawString("LIBRARY", (int)(width*0.81), (int)(height * 0.67));
            g2d.drawString("LOUNGE", (int)(width*0.1), (int)(height * 0.89));
            g2d.drawString("HALL", (int)(width*0.475), (int)(height * 0.87));
            g2d.drawString("STUDY", (int)(width*0.82), (int)(height * 0.925));
        }
    }

    class BoardSquare extends JPanel{

        Location cell;
        Game game;

        BoardSquare(Location location, Game game){
            super();
            this.cell = location;
            this.game = game;
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    if(game.movePlayerByMouse(location)){
                        getTopLevelAncestor().repaint();
                    }
                }
            });
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
                                g2d.drawImage(Cluedo.pieceImages.get("Candlestick"), padding, padding, width, height,this);
                                this.setToolTipText("Candlestick");
                                break;
                            case "d":
                                g2d.drawImage(Cluedo.pieceImages.get("Dagger"), padding, padding, width, height,this);
                                this.setToolTipText("Dagger");
                                break;
                            case "l":
                                g2d.drawImage(Cluedo.pieceImages.get("Lead Pipe"), padding, padding, width, height,this);
                                this.setToolTipText("Lead Pipe");
                                break;
                            case "g":
                                g2d.drawImage(Cluedo.pieceImages.get("Revolver"), padding, padding, width, height,this);
                                this.setToolTipText("Revolver");
                                break;
                            case "r":
                                g2d.drawImage(Cluedo.pieceImages.get("Rope"), padding, padding, width, height,this);
                                this.setToolTipText("Rope");
                                break;
                            case "s":
                                g2d.drawImage(Cluedo.pieceImages.get("Spanner"), padding, padding, width, height,this);
                                this.setToolTipText("Spanner");
                                break;
                            case "P":
                                g2d.drawImage(Cluedo.pieceImages.get("Miss Peacock"), padding, padding, width, height,this);
                                this.setToolTipText("Miss Peacock");
                                break;
                            case "L":
                                g2d.drawImage(Cluedo.pieceImages.get("Professor Plum"), padding, padding, width, height,this);
                                this.setToolTipText("Professor Plum");
                                break;
                            case "M":
                                g2d.drawImage(Cluedo.pieceImages.get("Colonel Mustard"), padding, padding, width, height,this);
                                this.setToolTipText("Colonel Mustard");
                                break;
                            case "W":
                                g2d.drawImage(Cluedo.pieceImages.get("Mrs White"), padding, padding, width, height,this);
                                this.setToolTipText("Mrs White");
                                break;
                            case "G":
                                g2d.drawImage(Cluedo.pieceImages.get("Mr Green"), padding, padding, width, height,this);
                                this.setToolTipText("Mr Green");
                                break;
                            case "S":
                                g2d.drawImage(Cluedo.pieceImages.get("Miss Scarlet"), padding, padding, width, height,this);
                                this.setToolTipText("Miss Scarlet");
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
            while (!playerCreationSuccessful) {
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
     * @param roomName is the name of the room to suggest for (only for suggesting). If not suggesting, this should be null.
     * @return a CardTuple consisting of the 3 chosen cards
     */
    public CardTuple askForThreeCards(String message, String titleMessage, String buttonName, String roomName) {
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

        JComboBox<String> roomChoices = null;
        String chosenRoomName = roomName;
        if (roomName == null) {
            // When accusing (since any room can be chosen)
            String[] roomsArray = new String[game.rooms.size()];
            roomChoices = new JComboBox<String>(game.rooms.toArray(roomsArray));
            selectionPanel.add(new JLabel("Select a Room:"), BorderLayout.WEST);
            selectionPanel.add(roomChoices, BorderLayout.EAST);
        } else {
            // When suggesting (since only the current room can be suggested)
            selectionPanel.add(new JLabel("Selected Room: " + roomName));
        }

        overallPanel.add(labelPanel);
        overallPanel.add(new JPanel());
        overallPanel.add(selectionPanel);

        String[] buttonOption = {buttonName};
        JOptionPane.showOptionDialog(null, overallPanel, titleMessage, JOptionPane.PLAIN_MESSAGE, JOptionPane.QUESTION_MESSAGE, null, buttonOption, buttonOption[0]);
        if (roomChoices != null) { chosenRoomName = (String)roomChoices.getSelectedItem(); }
        return new CardTuple(game.getCard((String)characterChoices.getSelectedItem()), game.getCard((String)weaponChoices.getSelectedItem()), game.getCard(chosenRoomName));
    }

    /**
     * Ask a player to choose a card to refute with (if any), given
     * a suggestion.
     * @param suggester is the Player that made the suggestion
     * @param refuter is the Player to attempt to refute the suggestion
     * @param suggestion
     * @param refuteOptions is a collection of cards that the refuter can choose to refute with
     * @return the Card chosen to refute with
     */
    public Card askToRefute(Player suggester, Player refuter, CardTuple suggestion, Set<Card> refuteOptions) {
        JPanel refutingPanel = new JPanel();
        refutingPanel.setLayout(new BoxLayout(refutingPanel, BoxLayout.PAGE_AXIS));

        JPanel labelPanel1 = new JPanel(new BorderLayout());
        labelPanel1.add(new JLabel(suggester.getPlayerName() + " (" + suggester.getCharacterName() + ") made the following suggestion:"), BorderLayout.WEST);
        refutingPanel.add(labelPanel1);

        JPanel suggestionPanel = new JPanel(new FlowLayout());
        showCardTuple(suggestionPanel, suggestion);
        refutingPanel.add(suggestionPanel);
        refutingPanel.add(new JPanel());

        if (refuteOptions.size() > 0) {
            Card refuteCard = null;
            JComboBox<String> refutingOptions = new JComboBox<String>();
            JPanel refutingOptionsPanel = new JPanel(new FlowLayout());

            for(Card card : refuteOptions) {
                refutingOptions.addItem(card.getName());
                refutingOptionsPanel.add(new JLabel(new ImageIcon(cardImages.get(card.getName()).getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH))));
            }

            JPanel labelPanel2 = new JPanel(new BorderLayout());
            labelPanel2.add(new JLabel("You can use one of these cards to refute. Select a card to refute with:"), BorderLayout.WEST);
            refutingPanel.add(labelPanel2);

            refutingPanel.add(refutingOptionsPanel);
            refutingPanel.add(new JLabel());
            refutingPanel.add(refutingOptions);

            while (refuteCard == null) {
                JOptionPane.showOptionDialog(null, refutingPanel, refuter.getPlayerName() + "'s (" + refuter.getCharacterName() + ") turn to refute",
                        JOptionPane.PLAIN_MESSAGE, JOptionPane.QUESTION_MESSAGE, null, okOption, okOption[0]);
                refuteCard = game.getCard((String)refutingOptions.getSelectedItem());
            }
            return refuteCard;
        } else {
            JPanel labelPanel3 = new JPanel(new BorderLayout());
            labelPanel3.add(new JLabel("Unfortunately, you have no cards you can refute with. Click \"OK\" to end your refutation."));
            refutingPanel.add(labelPanel3);
            JOptionPane.showMessageDialog(null, refutingPanel, refuter.getPlayerName() + "'s (" + refuter.getCharacterName() + ") turn to Refute", JOptionPane.PLAIN_MESSAGE);
            return null;
        }
    }

    public void showCardTuple(JPanel panel, CardTuple tuple) {
        ImageIcon cardIcon = new ImageIcon(cardImages.get(tuple.characterCard().getName()).getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH));
        panel.add(new JLabel(cardIcon));
        cardIcon = new ImageIcon(cardImages.get(tuple.weaponCard().getName()).getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH));
        panel.add(new JLabel(cardIcon));
        cardIcon = new ImageIcon(cardImages.get(tuple.roomCard().getName()).getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH));
        panel.add(new JLabel(cardIcon));
    }

    /**
     * Update the game state description
     * @param text
     */
    public void displayGameStateMessage(String text) {
        displayMessage.setText(text);
        this.repaint();
    }

    public void showCurrentPlayerText(String text) {
        currentPlayerNameDisplay.setText(text);
        this.repaint();
    }

    /**
     * Graphically display the values of rolled dice
     * @param firstDieValue
     * @param secondDieValue
     */
    public void showDiceRoll(int firstDieValue, int secondDieValue) {
        ImageIcon dieIcon = new ImageIcon(diceImages.get(firstDieValue).getScaledInstance(DIE_SIZE, DIE_SIZE, Image.SCALE_SMOOTH));
        this.die1.setIcon(dieIcon);
        dieIcon = new ImageIcon(diceImages.get(secondDieValue).getScaledInstance(DIE_SIZE, DIE_SIZE, Image.SCALE_SMOOTH));
        this.die2.setIcon(dieIcon);
        this.repaint();
    }

    public void showRefutation(Card refuteCard, CardTuple suggestion) {
        JPanel refutationResultPanel = new JPanel();
        refutationResultPanel.setLayout(new BoxLayout(refutationResultPanel, BoxLayout.PAGE_AXIS));
        JPanel labelPanel1 = new JPanel(new BorderLayout());
        if (refuteCard != null) {
            // Someone refuted the suggestion
            labelPanel1.add(new JLabel("Your suggestion was refuted by the " + refuteCard.getName() + " card:"), BorderLayout.WEST);
            refutationResultPanel.add(labelPanel1);
            refutationResultPanel.add(new JLabel(new ImageIcon(cardImages.get(refuteCard.getName()).getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH))));
        } else {
            // The suggestion was not refuted
            labelPanel1.add(new JLabel("Your suggestion was not refuted by anyone!"));
            refutationResultPanel.add(labelPanel1);
        }
        refutationResultPanel.add(new JPanel());

        JPanel labelPanel2 = new JPanel(new BorderLayout());
        labelPanel2.add(new JLabel("Your suggestion:"));
        refutationResultPanel.add(labelPanel2);

        JPanel suggestionPanel = new JPanel(new FlowLayout());
        showCardTuple(suggestionPanel, suggestion);
        refutationResultPanel.add(suggestionPanel);

        JOptionPane.showMessageDialog(null, refutationResultPanel, "Refutation Results", JOptionPane.PLAIN_MESSAGE);
        displayGameStateMessage("Suggestion done");
    }

    public void setRollButton(boolean active) {
        if (active) {
            rollButton.setEnabled(true);
            rollButton.setBackground(PASSAGEWAY_COLOR);
        } else {
            rollButton.setEnabled(false);
            rollButton.setBackground(Color.gray);
        }
        this.repaint();
    }

    public void setSuggestButton(boolean active) {
        if (active) {
            suggestButton.setEnabled(true);
            suggestButton.setBackground(PASSAGEWAY_COLOR);
        } else {
            suggestButton.setEnabled(false);
            suggestButton.setBackground(Color.gray);
        }
        this.repaint();
    }

    public void setAccuseButton(boolean active) {
        if (active) {
            accuseButton.setEnabled(true);
            accuseButton.setBackground(PASSAGEWAY_COLOR);
        } else {
            accuseButton.setEnabled(false);
            accuseButton.setBackground(Color.gray);
        }
        this.repaint();
    }

    /**
     * Graphically display all cards in the player's hand
     * @param player
     */
    public void showPlayerHand(Player player) {
        currentPlayerHand.removeAll();
        for (Map.Entry<String,Card> card : player.getHand().entrySet()) {
            ImageIcon cardIcon = new ImageIcon(cardImages.get(card.getValue().getName()).getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH));
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
        if (numOfSlotsToAdd > 0) {
            for (int i = 0; i < numOfSlotsToAdd; i++) { currentPlayerHand.add(new JLabel(emptyCardSlotImage)); }
            this.repaint();
        }
    }

    public static void main(String[] args){
        EventQueue.invokeLater(() -> {
            Cluedo frame = new Cluedo();
            frame.setupAndRunCluedo();
        });
    }
}
