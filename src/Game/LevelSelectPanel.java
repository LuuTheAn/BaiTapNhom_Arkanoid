package Game;

import sound.Sound;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;

/**
 * {@code LevelSelectPanel} là giao diện chọn màn chơi (level) trong game.
 * <p>
 * Người chơi có thể chọn các level đã được mở khóa để bắt đầu chơi,
 * hoặc quay lại menu chính. Panel này cũng hiển thị nền ảnh và các nút
 * được thiết kế với viền bo tròn và hiệu ứng hover.
 * </p>
 *
 * @author
 * @version 1.0
 */
public class LevelSelectPanel extends JPanel {

    /** Container chứa toàn bộ các panel khác (menu, game, leaderboard, v.v.) */
    private final JPanel container;

    /** Mảng chứa các nút chọn level */
    private final JButton[] levelButtons;

    /** Ảnh nền của màn chọn level */
    private Image backgroundImage;

    /** Đối tượng âm thanh dùng để phát hiệu ứng click */
    private final Sound sound;

    /**
     * Khởi tạo giao diện chọn level.
     *
     * @param container {@link JPanel} cha, chứa tất cả các panel khác trong game.
     */
    public LevelSelectPanel(JPanel container) {
        this.container = container;
        this.sound = Sound.getInstance();

        setLayout(new GridBagLayout());
        setBackground(Color.BLACK);

        // 🔹 Load ảnh nền
        try {
            backgroundImage = ImageIO.read(getClass().getResource("/img/menu_bg.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        // 🏁 Tiêu đề
        JLabel title = new JLabel("SELECT LEVEL");
        title.setFont(new Font("Verdana", Font.BOLD, 42));
        title.setForeground(Color.WHITE);
        title.setOpaque(true);
        title.setBackground(new Color(0, 0, 0, 120));
        title.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(Color.WHITE, 2, 35),
                BorderFactory.createEmptyBorder(10, 30, 10, 30)
        ));
        gbc.gridy = 0;
        add(title, gbc);

        // 🔹 Các nút chọn level
        int totalLevels = 5;
        levelButtons = new JButton[totalLevels];
        for (int i = 0; i < totalLevels; i++) {
            final int level = i + 1;
            JButton btn = createButton("LEVEL " + level);
            gbc.gridy = i + 1;
            add(btn, gbc);
            levelButtons[i] = btn;

            btn.addActionListener(e -> {
                playClickSound();
                startLevel(level);
            });
        }

        // 🔹 Nút quay lại menu chính
        gbc.gridy = totalLevels + 1;
        JButton back = createButton("BACK");
        back.addActionListener(e -> {
            playClickSound();
            showMenu();
        });
        add(back, gbc);
    }

    /**
     * Tạo nút bo tròn có hiệu ứng hover.
     *
     * @param text nội dung hiển thị trên nút.
     * @return {@link JButton} đã được tạo style.
     */
    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 28));
        btn.setBackground(new Color(40, 40, 40, 230));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(Color.WHITE, 2, 25),
                BorderFactory.createEmptyBorder(10, 25, 10, 25)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 💡 Hiệu ứng hover (sáng hơn khi di chuột)
        btn.addChangeListener(e -> {
            if (btn.getModel().isRollover()) {
                btn.setBackground(new Color(80, 80, 80, 250));
            } else {
                btn.setBackground(new Color(40, 40, 40, 230));
            }
        });

        return btn;
    }

    /**
     * Cập nhật trạng thái khóa/mở của các level.
     * <p>
     * Các level đã được mở khóa sẽ sáng lên và có thể click được,
     * còn những level chưa mở thì hiển thị biểu tượng "🔒 LOCKED".
     * </p>
     */
    public void refreshUnlockStatus() {
        int unlocked = ProgressManager.getUnlockedLevel();

        for (int i = 0; i < levelButtons.length; i++) {
            JButton btn = levelButtons[i];
            if (i + 1 <= unlocked) {
                btn.setEnabled(true);
                btn.setText("LEVEL " + (i + 1));
                btn.setBackground(new Color(40, 40, 40, 230));
            } else {
                btn.setEnabled(false);
                btn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 28));
                btn.setText("🔒 LOCKED");
                btn.setBackground(new Color(80, 80, 80, 150));
            }
        }
    }

    /**
     * Bắt đầu chơi level cụ thể.
     *
     * @param level số thứ tự của level (bắt đầu từ 1).
     */
    private void startLevel(int level) {
        GamePanel gamePanel = null;
        MenuPanel menuPanel = null;

        for (Component comp : container.getComponents()) {
            if (comp instanceof GamePanel gp) gamePanel = gp;
            else if (comp instanceof MenuPanel mp) menuPanel = mp;
        }

        if (menuPanel != null) {
            menuPanel.stopBackgroundMusic();
        }

        if (gamePanel != null) {
            gamePanel.getGameManager().resetSession();
            gamePanel.startGame(level);

            CardLayout cl = (CardLayout) container.getLayout();
            cl.show(container, "GAME");

            System.out.println("🧩 Bắt đầu lại từ level " + level + " → reset totalScore = 0");
        }
    }

    /**
     * Quay lại màn menu chính.
     */
    private void showMenu() {
        CardLayout cl = (CardLayout) container.getLayout();
        cl.show(container, "MENU");
    }

    /**
     * Phát hiệu ứng âm thanh khi click nút.
     */
    private void playClickSound() {
        sound.play(1);
    }

    /**
     * Khi panel hiển thị lại (setVisible = true), tự động cập nhật trạng thái mở khóa.
     *
     * @param visible true nếu hiển thị panel.
     */
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) refreshUnlockStatus();
    }

    /**
     * Border có góc bo tròn tuỳ chỉnh dùng để vẽ cho các nút hoặc khung tiêu đề.
     */
    private static class RoundedBorder extends AbstractBorder {
        private final Color color;
        private final int thickness;
        private final int radius;

        /**
         * @param color     màu viền
         * @param thickness độ dày viền
         * @param radius    độ cong góc
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
            g2.draw(new RoundRectangle2D.Double(
                    x + thickness / 2.0, y + thickness / 2.0,
                    width - thickness, height - thickness,
                    radius, radius
            ));
            g2.dispose();
        }
    }

    /**
     * Vẽ nền panel (ảnh nền + lớp overlay làm mờ).
     *
     * @param g đối tượng {@link Graphics} dùng để vẽ.
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

        // Overlay nhẹ giúp tăng độ tương phản
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(new Color(0, 0, 0, 100));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
    }
}
