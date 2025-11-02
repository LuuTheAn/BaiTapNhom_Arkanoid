import Game.*;
import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Arkanoid");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);

            // 🔹 Panel chứa tất cả màn hình (Menu, Level, Game, Leaderboard)
            JPanel container = new JPanel(new CardLayout());

            // 🔹 Tạo các panel
            MenuPanel menuPanel = new MenuPanel(container);
            LevelSelectPanel levelSelectPanel = new LevelSelectPanel(container);
            GamePanel gamePanel = new GamePanel(container);
            LeaderboardPanel leaderboardPanel = new LeaderboardPanel(container, menuPanel);

            // 🔹 Thêm vào container (chỉ add 1 lần mỗi loại panel)
            container.add(menuPanel, "MENU");
            container.add(levelSelectPanel, "LEVEL");
            container.add(gamePanel, "GAME");
            container.add(leaderboardPanel, "LEADERBOARD");

            // 🔹 Hiển thị menu đầu tiên
            CardLayout cl = (CardLayout) container.getLayout();
            cl.show(container, "MENU");

            // 🔹 Cấu hình cửa sổ
            frame.add(container);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
