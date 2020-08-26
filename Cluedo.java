import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import javax.imageio.ImageIO;
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

    public static final int WALL_THICKNESS = 8;

    public static Map<String, Image> pieceImages = new HashMap<>();

    private Game game;
    private JTextArea displayMessage;
    private final String[] okOption = {"Okay"};

    Cluedo(){
        super("Cluedo");
    }

    public Game getGame() { return game; }

    public void playGame() {
        boolean playing = askYesOrNo("Welcome to Cluedo! Would you like to play?", "Cluedo Game");
        while (playing) {
            this.game = new Game(this);
            initUI(game);
            // First, ask how many players will join
            chooseCharacters(askNumOfPlayers());
            // Then, set up the game
            game.setup();

            askForThreeCards("Choose three cards to Suggest:", "Make a Suggestion", "Suggest!").toString();
            playing = askYesOrNo("Game over! Would you like to play again?", "Cluedo Game");
        }
        System.exit(0);
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

        loadPieceImages();

        getContentPane().add(createBoardCanvas(game), BorderLayout.CENTER);
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);
        setJMenuBar(createMenuBar());

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
            System.out.println("Could not load images from the resources directory");
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

    private static JPanel createCardPanel(Game game){
        JPanel cards = new JPanel();

        return cards;
    }

    private static JPanel createButtonPanel(Game game){
        return new JPanel();
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

    public void displayText(String text) {
        displayMessage.setText(text);
    }

    public static void main(String[] args){
        EventQueue.invokeLater(() -> {
            Cluedo frame = new Cluedo();
            frame.playGame();
        });
    }
}
