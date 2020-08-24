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

    private void initUI(Game game){
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        //getRootPane().setLayout(new BorderLayout());
        getContentPane().setLayout(new BorderLayout());
        
        // --------------- TOP PANEL -----------------
        JPanel topPanel = new JPanel();
        ButtonGroup characterSelection = new ButtonGroup();
        //topPanel.add(new JTextField());
        for (int i = 0; i < Game.characters.size(); i++) {
        	JRadioButton characterRadioButton = new JRadioButton(Game.characters.get(i));
        	characterSelection.add(characterRadioButton);
        	topPanel.add(characterRadioButton);
        }
        
        // -------------- BOTTOM PANEL -----------------
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.PAGE_AXIS));
        JPanel cards = createCardPanel(game);
        JPanel buttons = createButtonPanel(game);
        this.displayMessage = new JTextField("Game start");
        this.displayMessage.setEditable(false);
        bottomPanel.add(displayMessage);
        bottomPanel.add(buttons);
        bottomPanel.add(cards);
        
        getContentPane().add(createBoardCanvas(game), BorderLayout.CENTER);
        getContentPane().add(topPanel, BorderLayout.NORTH);
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

    public static void main(String[] args){
        EventQueue.invokeLater(() -> {
            Cluedo frame = new Cluedo();
            boolean playing = frame.askYesOrNo("Welcome to Cluedo! Would you like to play?", "Cluedo Game");
            while (playing) {
            	// First, ask how many players will join
            	int numOfPlayers = frame.askNumOfPlayers("How many players?", "Cluedo Game");
            	if (numOfPlayers < 0) { }//break; }
            	frame.chooseCharacters(frame.getGame(), numOfPlayers);
            	
            	playing = frame.askYesOrNo("Game over! Would you like to play again?", "Cluedo Game");
            }
            System.exit(0);
            //if (!playing) { System.exit(0); }
        });
    }

}
