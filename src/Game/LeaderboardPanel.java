package Game;

import sound.Sound;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.util.List;

/**
 * {@code LeaderboardPanel} là giao diện hiển thị bảng xếp hạng của trò chơi.
 * <p>
 * Panel này cho phép người chơi xem danh sách điểm cao nhất, cùng với
 * giao diện nền và hiệu ứng đồ họa bo góc. Ngoài ra còn có nút "Back" để
 * quay về menu chính.
 * </p>
 *
 * <h3>Chức năng chính:</h3>
 * <ul>
 *   <li>Hiển thị danh sách top điểm từ {@link LeaderboardManager}</li>
 *   <li>Tô màu đặc biệt cho top 3 người chơi (vàng, bạc, đồng)</li>
 *   <li>Cho phép người chơi quay về menu chính bằng nút "Back"</li>
 *   <li>Tự động tải ảnh nền và cập nhật danh sách khi mở lại</li>
 * </ul>
 *
 * @see LeaderboardManager
 * @see MenuPanel
 */
public class LeaderboardPanel extends JPanel {

    /** Container chính (dùng CardLayout) để chuyển giữa các màn hình. */
    private final JPanel container;

    /** Tham chiếu đến menu chính, dùng để bật lại nhạc nền khi quay về. */
    private final MenuPanel menuPanel;

    /** Nút quay lại menu chính. */
    private final JButton backButton;

    /** Khu vực hiển thị danh sách điểm. */
    private final JPanel scoreListPanel;

    /** Ảnh nền của bảng xếp hạng. */
    private Image backgroundImage;

    /** Hệ thống âm thanh dùng chung. */
    private final Sound sound;

    /**
     * Khởi tạo panel bảng xếp hạng.
     *
     * @param container  panel chứa (CardLayout) cho phép chuyển giữa các màn hình
     * @param menuPanel  panel menu chính (để bật lại nhạc nền)
     */
    public LeaderboardPanel(JPanel container, MenuPanel menuPanel) {
        this.container = container;
        this.menuPanel = menuPanel;
        this.sound = Sound.getInstance();

        // 🔹 Load background
        try {
            backgroundImage = ImageIO.read(getClass().getResource("/img/menu_bg.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        setLayout(new GridBagLayout());
        setBackground(Color.BLACK);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        // 🏆 Tiêu đề
        JLabel titleLabel = new JLabel("🏆 RANKING 🏆");
        titleLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 42));
        titleLabel.setForeground(new Color(255, 215, 0));
        titleLabel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(new Color(255, 215, 0), 2, 30),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(0, 0, 0, 100));
        gbc.gridy = 0;
        add(titleLabel, gbc);

        // 📋 Danh sách điểm
        scoreListPanel = new JPanel();
        scoreListPanel.setLayout(new BoxLayout(scoreListPanel, BoxLayout.Y_AXIS));
        scoreListPanel.setBackground(new Color(0, 0, 0, 180));
        scoreListPanel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(Color.GRAY, 2, 25),
                BorderFactory.createEmptyBorder(15, 30, 15, 30)
        ));

        JScrollPane scrollPane = new JScrollPane(scoreListPanel);
        scrollPane.setPreferredSize(new Dimension(400, 400));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        gbc.gridy = 1;
        add(scrollPane, gbc);

        // ⏪ Nút quay lại menu
        backButton = new JButton("BACK");
        styleButton(backButton);
        gbc.gridy = 2;
        add(backButton, gbc);

        // 🔹 Sự kiện quay lại
        backButton.addActionListener(e -> {
            sound.play(1);
            CardLayout cl = (CardLayout) container.getLayout();
            cl.show(container, "MENU");
            menuPanel.resumeBackgroundMusic();
        });

        // 🔹 Tải dữ liệu ban đầu
        refreshLeaderboard();
    }

    /**
     * Làm mới danh sách điểm xếp hạng.
     * <p>
     * Phương thức này tải lại dữ liệu từ {@link LeaderboardManager}
     * và cập nhật hiển thị của các nhãn điểm trên màn hình.
     * </p>
     */
    public void refreshLeaderboard() {
        scoreListPanel.removeAll();
        LeaderboardManager leaderboardManager = new LeaderboardManager();
        List<Integer> scores = leaderboardManager.getScores();

        if (scores.isEmpty()) {
            JLabel emptyLabel = new JLabel("Chưa có dữ liệu xếp hạng");
            emptyLabel.setFont(new Font("Arial", Font.ITALIC, 22));
            emptyLabel.setForeground(Color.LIGHT_GRAY);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            scoreListPanel.add(emptyLabel);
        } else {
            int rank = 1;
            for (int i = 0; i < scores.size() && i < 7; i++) {
                int s = scores.get(i);
                JLabel scoreLabel = new JLabel(rank + ".   " + s + " điểm");
                scoreLabel.setFont(new Font("Arial", Font.BOLD, 26));
                scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

                // 🌈 Màu cho top 3
                if (rank == 1) scoreLabel.setForeground(new Color(255, 215, 0));
                else if (rank == 2) scoreLabel.setForeground(new Color(192, 192, 192));
                else if (rank == 3) scoreLabel.setForeground(new Color(205, 127, 50));
                else scoreLabel.setForeground(Color.WHITE);

                scoreLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                scoreListPanel.add(scoreLabel);
                rank++;
            }
        }

        scoreListPanel.revalidate();
        scoreListPanel.repaint();
    }

    /**
     * Định dạng giao diện nút cho thống nhất về màu sắc, viền và hiệu ứng hover.
     *
     * @param button nút cần được áp dụng style
     */
    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 24));
        button.setBackground(new Color(50, 50, 50, 200));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(Color.WHITE, 2, 20),
                BorderFactory.createEmptyBorder(10, 25, 10, 25)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addChangeListener(e -> {
            if (button.getModel().isRollover()) {
                button.setBackground(new Color(80, 80, 80, 220));
            } else {
                button.setBackground(new Color(50, 50, 50, 200));
            }
        });
    }

    /**
     * Lớp viền bo góc tùy chỉnh cho các thành phần giao diện.
     * <p>
     * Dùng cho tiêu đề, khung danh sách điểm và nút Back.
     * </p>
     */
    private static class RoundedBorder extends AbstractBorder {
        private final Color color;
        private final int thickness;
        private final int radius;

        /**
         * Khởi tạo một đường viền bo góc tùy chỉnh.
         *
         * @param color     màu viền
         * @param thickness độ dày của đường viền
         * @param radius    bán kính bo góc
         */
        public RoundedBorder(Color color, int thickness, int radius) {
            this.color = color;
            this.thickness = thickness;
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.draw(new RoundRectangle2D.Double(x + thickness / 2.0, y + thickness / 2.0,
                    width - thickness, height - thickness, radius, radius));
            g2.dispose();
        }
    }

    /**
     * Vẽ lại ảnh nền khi panel được hiển thị.
     *
     * @param g đối tượng {@link Graphics} để vẽ
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}
