import Game.*;
import javax.swing.*;
import java.awt.*;

/**
 * Lớp Main là điểm khởi chạy (entry point) chính cho trò chơi Arkanoid.
 * <p>
 * Lớp này chịu trách nhiệm thiết lập cửa sổ game chính ({@link JFrame})
 * và sử dụng {@link CardLayout} để quản lý việc chuyển đổi giữa các màn hình
 * (Menu, Chọn màn, Chơi game, Bảng xếp hạng).
 *
 * @author [Tên của bạn]
 * @version 1.0
 */
public class Main {

    /**
     * Phương thức main, điểm bắt đầu của ứng dụng.
     * <p>
     * Sử dụng {@link SwingUtilities#invokeLater(Runnable)} để đảm bảo rằng
     * tất cả các tác vụ khởi tạo GUI được thực thi trên
     * Event Dispatch Thread (EDT), theo đúng chuẩn của Swing.
     *
     * @param args Các đối số dòng lệnh (không được sử dụng trong ứng dụng này).
     */
    public static void main(String[] args) {
        // Đảm bảo mã giao diện người dùng chạy trên Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            // Tạo cửa sổ game chính
            JFrame frame = new JFrame("Arkanoid");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);

            // 🔹 Panel chứa tất cả màn hình (Menu, Level, Game, Leaderboard)
            // Sử dụng CardLayout để dễ dàng chuyển đổi qua lại giữa các panel
            JPanel container = new JPanel(new CardLayout());

            // 🔹 Tạo các panel màn hình
            MenuPanel menuPanel = new MenuPanel(container);
            LevelSelectPanel levelSelectPanel = new LevelSelectPanel(container);
            GamePanel gamePanel = new GamePanel(container);
            LeaderboardPanel leaderboardPanel = new LeaderboardPanel(container, menuPanel);

            // 🔹 Thêm các panel vào container với tên định danh
            container.add(menuPanel, "MENU");
            container.add(levelSelectPanel, "LEVEL");
            container.add(gamePanel, "GAME");
            container.add(leaderboardPanel, "LEADERBOARD");

            // 🔹 Hiển thị màn hình Menu đầu tiên
            CardLayout cl = (CardLayout) container.getLayout();
            cl.show(container, "MENU");

            // 🔹 Cấu hình cửa sổ
            frame.add(container); // Thêm container (chứa CardLayout) vào frame
            frame.pack(); // Tự động điều chỉnh kích thước frame vừa với nội dung (container)
            frame.setLocationRelativeTo(null); // Hiển thị cửa sổ ở giữa màn hình
            frame.setVisible(true); // Hiển thị cửa sổ
        });
    }
}