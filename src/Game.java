
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class Game implements Runnable {

    static final Color BG_COLOR = new Color(250, 248, 239);

    public void run() {

        // Start frame
        JFrame startFrame = new JFrame();
        startFrame.setTitle("2048");
        startFrame.setSize(600, 450);
        startFrame.setResizable(false);

        // Start Screen
        final StartPanel startPanel = new StartPanel();
        startFrame.add(startPanel, BorderLayout.NORTH);

        JPanel gameButtons = new JPanel(new GridLayout(0, 2));

        // Create new game
        final JButton newGame = new JButton("New Game");
        newGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFrame frame = new JFrame();
                frame.setTitle("2048");

                frame.setSize(600, 750);
                frame.setResizable(false);

                GameBoard board = new GameBoard();
                frame.add(board, BorderLayout.CENTER);

                final ControlPanel control_panel = new ControlPanel(board);
                frame.add(control_panel, BorderLayout.NORTH);

                frame.getContentPane().setBackground(BG_COLOR);

                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                startFrame.setVisible(false);
                frame.setVisible(true);

                board.newGame();

            }
        });

        gameButtons.add(newGame);

        // Load game
        final JButton loadGame = new JButton("Load Game");
        loadGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFrame frame = new JFrame();
                frame.setTitle("2048");

                frame.setSize(600, 750);
                frame.setResizable(false);

                GameBoard board = new GameBoard();
                frame.add(board, BorderLayout.CENTER);

                final ControlPanel control_panel = new ControlPanel(board);
                frame.add(control_panel, BorderLayout.NORTH);

                frame.getContentPane().setBackground(BG_COLOR);

                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                startFrame.setVisible(false);
                frame.setVisible(true);

                board.loadGame();

            }
        });

        // Only enable load game button if file exist
        loadGame.setEnabled(GameLogic.fileExist("game_state.txt"));
        gameButtons.add(loadGame);
        startFrame.add(gameButtons, BorderLayout.CENTER);

        startFrame.getContentPane().setBackground(BG_COLOR);
        startFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        startFrame.setVisible(true);

    }

    /**
     * Main method run to start and run the game. Initializes the GUI elements
     * specified in Game and runs it. IMPORTANT: Do NOT delete! You MUST include
     * this in your final submission.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Game());
    }
}