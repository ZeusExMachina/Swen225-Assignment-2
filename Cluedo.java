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
    public static final Color WALL_COLOR = new Color(102,57,38);

    public static void main(String[] args){
        EventQueue.invokeLater(() -> {
            Cluedo frame = new Cluedo();
            Game game = new Game();

            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.getRootPane().setLayout(new BorderLayout());

            JPanel board = createBoardCanvas(game);
            JPanel bottomPanel = new JPanel();
            JPanel cards = createCardPanel(game);
            JPanel buttons = createButtonPanel(game);
            bottomPanel.add(buttons);
            bottomPanel.add(cards);

            frame.getRootPane().add(board, BorderLayout.CENTER);
            frame.getRootPane().add(bottomPanel, BorderLayout.SOUTH);

            frame.pack();
            frame.setVisible(true);

        });
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
                Color color = EMPTY_COLOR;

                if (current.getRoom() != null) {
                    if (current.getRoom().getName().equals("Passageway")) {
                        color = PASSAGEWAY_COLOR;
                        Border blackLine = BorderFactory.createLineBorder(Color.black);
                        this.setBorder(blackLine);
                    } else {
                        color = ROOM_COLOR;
                    }
                }
                this.setBackground(color);
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(32, 32);
            }
        };
    }
}
