import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class StartPanel extends JPanel {
    private static final Color TITLE_COLOR = new Color(119, 110, 101);

    public StartPanel() {
        setBackground(Game.BG_COLOR);
        setPreferredSize(new Dimension(600, 280));
        setLayout(null);

        // Title
        final JLabel titleLabel = new JLabel("2048");
        titleLabel.setForeground(TITLE_COLOR);
        Font font = new Font("Arial", Font.BOLD, 80);
        titleLabel.setFont(font);
        titleLabel.setBounds(50, 10, 250, 100);

        add(titleLabel);

        Font font2 = new Font("Arial", Font.PLAIN, 15);

        // Description
        final JLabel aboutLabel = new JLabel(
                "<html>" + "2048 is a single-player sliding block puzzle game." + "</html>");
        aboutLabel.setForeground(TITLE_COLOR);

        aboutLabel.setFont(font2);
        aboutLabel.setBounds(50, 70, 400, 100);
        add(aboutLabel);

        // Instructions
        final JLabel howToPlayLabel = new JLabel("<html>" + "<b>" + "HOW TO PLAY: " 
                + "</b>" + "Use the " + "<b>"
                + "arrow keys " + "</b>" + "to move tiles." + "<br/>"
                + "After a successful move, an additional tile will"
                + " pop up in a random spot." + "<br/>"
                + "Tiles with the same number merge when they collide with one another." 
                + "<br/>" + "Reach " + "<b>"
                + "2048" + "</b>" + " to win or lose when no valid move is available." + "</html>");
        howToPlayLabel.setForeground(TITLE_COLOR);

        howToPlayLabel.setFont(font2);
        howToPlayLabel.setBounds(50, 80, 500, 200);

        add(howToPlayLabel);
    }
}
