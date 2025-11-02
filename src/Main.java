import Game.*;
import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Arkanoid");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);

            JPanel container = new JPanel(new CardLayout());
            MenuPanel menuPanel = new MenuPanel(container);
            LevelSelectPanel levelSelectPanel = new LevelSelectPanel(container);
            GamePanel gamePanel = new GamePanel(container);
            //abc
            container.add(menuPanel, "MENU");
            container.add(levelSelectPanel, "LEVEL");
            container.add(gamePanel, "GAME");

            CardLayout cl = (CardLayout) container.getLayout();
            cl.show(container, "MENU");

            frame.add(container);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
