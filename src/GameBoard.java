import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

@SuppressWarnings("serial")
public class GameBoard extends JPanel implements KeyListener {

    private GameLogic logic; // model for the game
    
    // Game constants
    public static final int BOARD_WIDTH = 500;
    public static final int BOARD_HEIGHT = 500;
    private static final Color BG_COLOR = new Color(250, 248, 239);
    private static final Color BOARD_BG_COLOR = new Color(187, 173, 160);
    private static final Color TILE_COLOR = new Color(119, 110, 101);
    private static final Color TILE_COLOR_LARGE = new Color(249, 246, 242);
    private static final String FONT_NAME = "Arial";
    private static final int SPACING = 14;
    private static final int TILE_SIZE = 107;
    private static final int FONT_SIZE_LARGE = 55; // start at 2
    private static final int FONT_SIZE_MED = 45; // start at 128
    private static final int FONT_SIZE_SMALL = 35; // start at 1028

    private JButton tryAgain;
    private JButton newGame;

    /**
     * Initializes the game board.
     */
    public GameBoard() {

        // Enable keyboard focus on the court area.
        // When this component has the keyboard focus, key events are handled by its key
        // listener.
        setFocusable(true);

        setSize(500, 500);
        setLayout(null);

        // initializes model for the game
        logic = new GameLogic();

        setBackground(BG_COLOR);

        addKeyListener(this);

        // Try Again Button - Only show when game is over
        tryAgain = new JButton("Try Again");
        tryAgain.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tryAgain.setVisible(false);
                newGame();
            }
        });
        tryAgain.setBounds(300 - 45, 270 + 20, 90, 40);
        add(tryAgain);
        tryAgain.setVisible(false);

        // New Game Button - Only show when user won
        newGame = new JButton("New Game");
        newGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                newGame.setVisible(false);
                newGame();
            }
        });
        newGame.setBounds(300 - 45, 270 + 20, 90, 40);
        add(newGame);
        newGame.setVisible(false);

    }

    /**
     * (Re-)sets the game to its initial state.
     */

    public void newGame() {

        logic.newGame();

        // Update score label
        ControlPanel.setScore(logic.getScore());
        ControlPanel.setBestScore(logic.getBestScore());

        repaint();

        // Make sure try again and new game button is not visible
        // Prevent button staying if user clicked "New Game" instead of one of these
        // button
        if (tryAgain.isVisible()) {
            tryAgain.setVisible(false);
        }

        if (newGame.isVisible()) {
            newGame.setVisible(false);
        }
        // Makes sure this component has keyboard/mouse focus
        requestFocusInWindow();
    }

    public void loadGame() {

        logic.loadGame();

        // Update score label
        ControlPanel.setScore(logic.getScore());
        ControlPanel.setBestScore(logic.getBestScore());
        repaint();

        // Makes sure this component has keyboard/mouse focus
        requestFocusInWindow();
    }

    // Undo move if possible
    public void undo() {

        // If move is undo-ed, repaint and update score
        if (logic.undoMove()) {
            repaint();
            ControlPanel.setScore(logic.getScore());
            repaint();
        }

        // Makes sure this component has keyboard/mouse focus
        requestFocusInWindow();

    }

    @Override
    public void keyPressed(KeyEvent e) {

        // Only trigger if game is being played, prevent user from moving tiles when
        // game ended
        if (logic.getGameState() == GameState.STARTED) {

            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    logic.makeMove(1);
                    break;
                case KeyEvent.VK_RIGHT:
                    logic.makeMove(2);
                    break;
                case KeyEvent.VK_DOWN:
                    logic.makeMove(3);
                    break;
                case KeyEvent.VK_LEFT:
                    logic.makeMove(4);
                    break;
                default:
                    break;
            }

            // Update score label
            ControlPanel.setScore(logic.getScore());
            repaint();

        }

    }

    // Draws the game board and end screen if appropriate.
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(BOARD_BG_COLOR);
        g2.fillRoundRect(50, 0, 500, 500, 6, 6);

        // Get board
        int[][] board = logic.getBoard();

        // Draws Tiles
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[0].length; col++) {
                // Get value of tile
                int value = board[row][col];

                // Get x and y position of starting point
                int xPosition = 50 + (col * 107) + ((col + 1) * SPACING);
                int yPosition = 0 + (row * 107) + ((row + 1) * SPACING);

                // Set color of tile based on value
                g2.setColor(getTileBackground(value));

                // Draw background
                g2.fillRoundRect(xPosition, yPosition, TILE_SIZE, TILE_SIZE, 4, 4);

                // If not an empty tile, add text to represent value
                if (value != 0) {

                    String stringValue = String.valueOf(value);
                    Font font = getTileFont(value);

                    // If 2 or 4, brown text color, otherwise white text color
                    if (value <= 4) {
                        g.setColor(TILE_COLOR);
                    } else {
                        g.setColor(TILE_COLOR_LARGE);
                    }

                    // Draw text in the center of rect
                    drawTextCentered(font, g, stringValue, xPosition, yPosition, TILE_SIZE, 0);

                }
            }
        }

        // If no possible moves, display losing screen
        if (logic.getGameState() == GameState.LOSS) {
            g2.setColor(new Color(238, 228, 218, 186));
            g2.fillRoundRect(50, 0, 500, 500, 6, 6);

            g.setColor(TILE_COLOR);
            Font font = new Font(FONT_NAME, Font.BOLD, 60);
            drawTextCentered(font, g, "Game Over", 50, 0, 500, 30);

            tryAgain.setVisible(true);

            // If a tile is 2048, display winning screen
        } else if (logic.getGameState() == GameState.WIN) {
            g2.setColor(new Color(238, 228, 218, 186));
            g2.fillRoundRect(50, 0, 500, 500, 6, 6);

            g.setColor(TILE_COLOR);
            Font font = new Font(FONT_NAME, Font.BOLD, 60);
            drawTextCentered(font, g, "You Won", 50, 0, 500, 30);

            newGame.setVisible(true);
        }
    }

    // Draw text in center of rectangle
    public void drawTextCentered(Font font, Graphics g, String text, 
            int startX, int startY, int size, int yOffset) {

        // Get size of font
        FontMetrics fontMetrics = g.getFontMetrics(font);

        // Calculate center position
        int centerX = startX + (size - fontMetrics.stringWidth(text)) / 2;
        int centerY = startY + ((size - fontMetrics.getHeight()) / 2) + fontMetrics.getAscent();

        // Set font and draw
        g.setFont(font);
        g.drawString(text, centerX, centerY - yOffset);
    }

    // Get background color of tile based on value
    public Color getTileBackground(int value) {
        switch (value) {
            case 0:
                return new Color(205, 193, 180);
            case 2:
                return new Color(238, 228, 218);
            case 4:
                return new Color(238, 225, 201);
            case 8:
                return new Color(243, 178, 122);
            case 16:
                return new Color(246, 150, 100);
            case 32:
                return new Color(247, 124, 95);
            case 64:
                return new Color(247, 95, 59);
            case 128:
                return new Color(237, 208, 115);
            case 256:
                return new Color(237, 204, 98);
            case 512:
                return new Color(237, 201, 80);
            case 1024:
                return new Color(237, 197, 63);
            case 2048:
                return new Color(237, 194, 46);
            default:
                return new Color(205, 193, 180);

        }

    }

    // Get font of tile's text based on value
    public Font getTileFont(int value) {
        if (value < 128) {
            return new Font(FONT_NAME, Font.BOLD, FONT_SIZE_LARGE);
        } else if (value < 1028) {
            return new Font(FONT_NAME, Font.BOLD, FONT_SIZE_MED);
        } else {
            return new Font(FONT_NAME, Font.BOLD, FONT_SIZE_SMALL);
        }
    }

    // Returns the size of the game board.
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}