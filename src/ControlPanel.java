import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ControlPanel extends JPanel {

    private static final Color TITLE_COLOR = new Color(119, 110, 101);
    private static JLabel scoreLabel;
    private static JLabel bestScoreLabel;

    public ControlPanel(GameBoard board) {
        setBackground(Game.BG_COLOR);
        setPreferredSize(new Dimension(600, 150));
        setLayout(null);

        final JPanel sidePanel = new JPanel(new GridLayout(2, 0, 0, 0));
        Font font = new Font("Arial", Font.BOLD, 20);

        // Display score
        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setForeground(TITLE_COLOR);
        scoreLabel.setFont(font);
        scoreLabel.setBounds(50, 100, 250, 30);
        add(scoreLabel);

        // Display best score
        bestScoreLabel = new JLabel("Best: 0");
        bestScoreLabel.setForeground(TITLE_COLOR);
        bestScoreLabel.setFont(font);
        bestScoreLabel.setBounds(50, 120, 250, 30);
        add(bestScoreLabel);

        // New game button
        final JButton reset = new JButton("New Game");
        reset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                board.newGame();
            }
        });

        sidePanel.add(reset);

        // Undo move button
        final JButton undo = new JButton("Undo Move");
        undo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                board.undo();
            }
        });

        sidePanel.add(undo);

        sidePanel.setBackground(Game.BG_COLOR);

        sidePanel.setBounds(450, 20, 100, 60);
        add(sidePanel);
    }

    @Override
    public void paintComponent(Graphics g) {
        Font font = new Font("Arial", Font.BOLD, 80);

        // Draw title text
        g.setColor(TITLE_COLOR);
        g.setFont(font);
        g.drawString("2048", 50, 80);

    }

    public static void setScore(int score) {
        scoreLabel.setText("Score: " + String.valueOf(score));
    }

    public static void setBestScore(int bestScore) {
        bestScoreLabel.setText("Best: " + String.valueOf(bestScore));
    }

}
