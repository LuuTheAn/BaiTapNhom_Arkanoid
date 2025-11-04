package Game;

import sound.Sound;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

/**
 * Lớp {@code MenuPanel} đại diện cho màn hình menu chính của trò chơi Arkanoid.
 * <p>
 * Lớp này bao gồm các nút để chơi game, xem bảng xếp hạng và thoát.
 * Nó cũng hiển thị một tiêu đề phát sáng hoạt họa và phát nhạc nền.
 */
public class MenuPanel extends JPanel {
    /** Nút để bắt đầu chơi. */
    private JButton playButton;

    /** Nút để xem bảng xếp hạng. */
    private JButton leaderboardButton;

    /** Nút để thoát game. */
    private JButton quitButton;

    /** Panel cha (sử dụng CardLayout) để quản lý việc chuyển đổi màn hình. */
    private JPanel container;

    /** Hình ảnh nền cho menu. */
    private Image backgroundImage;

    /** Đối tượng quản lý âm thanh (Singleton). */
    private Sound sound;

    // 🟡 Biến cho hiệu ứng nhấp nháy tiêu đề
    /** Giá trị alpha (độ mờ) hiện tại cho hiệu ứng sáng của tiêu đề. */
    private float glowAlpha = 1.0f;

    /** Cờ kiểm soát hiệu ứng tiêu đề: đang mờ đi (true) hay sáng lên (false). */
    private boolean fadingOut = true;

    /**
     * Khởi tạo một {@code MenuPanel} mới với panel cha (container) được cung cấp.
     *
     * @param container panel cha (thường dùng {@link CardLayout}) chứa panel menu này.
     */
    public MenuPanel(JPanel container) {
        this.container = container;
        setLayout(new GridBagLayout());
        setBackground(Color.BLACK);

        // 🔹 Tải hình nền
        try {
            backgroundImage = ImageIO.read(getClass().getResource("/img/menu_bg.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 🔹 Khởi tạo nhạc nền
        sound = Sound.getInstance();
        sound.setVolume(Sound.MUSIC_BACKGROUND, 0.3f); // Sử dụng hằng số nếu có
        sound.loop(Sound.MUSIC_BACKGROUND);

        // 🔹 Cài đặt layout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        // 🔹 Tiêu đề hiệu ứng sáng mờ dần
        JLabel titleLabel = new JLabel("ARKANOID") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setFont(getFont());
                // Vẽ bóng mờ
                g2d.setColor(new Color(0, 0, 0, 150));
                g2d.drawString(getText(), 5, getHeight() - 10);
                // Vẽ chữ phát sáng
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, glowAlpha));
                g2d.setColor(Color.WHITE);
                g2d.drawString(getText(), 0, getHeight() - 15);
                g2d.dispose();
            }
        };
        titleLabel.setFont(new Font("Arial", Font.BOLD, 56));
        titleLabel.setPreferredSize(new Dimension(400, 100));
        gbc.gridy = 0;
        add(titleLabel, gbc);

        // 🔹 Hẹn giờ cho hiệu ứng nhấp nháy
        Timer glowTimer = new Timer(60, e -> {
            if (fadingOut) {
                glowAlpha -= 0.03f;
                if (glowAlpha <= 0.4f) fadingOut = false;
            } else {
                glowAlpha += 0.03f;
                if (glowAlpha >= 1.0f) fadingOut = true;
            }
            titleLabel.repaint();
        });
        glowTimer.start();

        // 🔹 Panel chứa các nút
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);
        GridBagConstraints btnGbc = new GridBagConstraints();
        btnGbc.insets = new Insets(15, 0, 15, 0);
        btnGbc.gridx = 0;

        playButton = createMenuButton("PLAY");
        leaderboardButton = createMenuButton("RANKING");
        quitButton = createMenuButton("QUIT");

        btnGbc.gridy = 0;
        buttonPanel.add(playButton, btnGbc);

        btnGbc.gridy = 1;
        buttonPanel.add(leaderboardButton, btnGbc);

        btnGbc.gridy = 2;
        buttonPanel.add(quitButton, btnGbc);

        gbc.gridy = 1;
        add(buttonPanel, gbc);

        // 🔹 Sự kiện các nút
        playButton.addActionListener(e -> {
            playSE(Sound.FX_CLICK); // Sử dụng hằng số nếu có
            // Tìm GamePanel và reset điểm
            for (Component comp : container.getComponents()) {
                if (comp instanceof GamePanel gamePanel) {
                    gamePanel.getGameManager().resetSession();
                    System.out.println("🧹 Reset session khi ấn PLAY → totalScore = 0");
                    break;
                }
            }
            CardLayout cl = (CardLayout) container.getLayout();
            cl.show(container, "LEVEL");
        });

        leaderboardButton.addActionListener(e -> {
            playSE(Sound.FX_CLICK);
            // Tìm LeaderboardPanel và làm mới
            for (Component comp : container.getComponents()) {
                if (comp instanceof LeaderboardPanel leaderboardPanel) {
                    leaderboardPanel.refreshLeaderboard();
                    break;
                }
            }
            CardLayout cl = (CardLayout) container.getLayout();
            cl.show(container, "LEADERBOARD");
        });

        quitButton.addActionListener(e -> {
            playSE(Sound.FX_CLICK);
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn có chắc chắn muốn thoát game không?",
                    "Xác nhận thoát",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );
            if (confirm == JOptionPane.YES_OPTION) {
                sound.stop(Sound.MUSIC_BACKGROUND);
                System.exit(0);
            } else {
                playSE(Sound.FX_CLICK); // Phát lại âm thanh nếu hủy
            }
        });
    }

    /**
     * Tạo một nút menu đã được định dạng (styled) với các hiệu ứng khi di chuột qua.
     *
     * @param text văn bản hiển thị trên nút.
     * @return một đối tượng {@link JButton} đã được tùy chỉnh.
     */
    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text) {
            /** Ghi đè để vẽ nút tùy chỉnh với góc bo tròn và bóng mờ. */
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Vẽ bóng
                g2.setColor(new Color(0, 0, 0, 100));
                g2.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 20, 20);
                // Vẽ nền nút
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth() - 10, getHeight() - 10, 20, 20);
                // Vẽ chữ (text)
                super.paintComponent(g);
            }
        };

        btn.setFont(new Font("Arial", Font.BOLD, 28));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(40, 40, 40)); // Màu nền mặc định
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(15, 50, 15, 50));
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Thêm hiệu ứng khi di chuột
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(70, 130, 180)); // Màu khi di chuột vào
                btn.setForeground(Color.YELLOW);
                btn.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(40, 40, 40)); // Màu khi di chuột ra
                btn.setForeground(Color.WHITE);
                btn.repaint();
            }
        });

        return btn;
    }

    /**
     * Phát lại nhạc nền khi quay trở lại menu.
     * <p>
     * (Ví dụ: khi người chơi thoát khỏi Bảng xếp hạng để quay lại Menu).
     */
    public void resumeBackgroundMusic() {
        sound.stop(Sound.MUSIC_BACKGROUND);
        sound.setVolume(Sound.MUSIC_BACKGROUND, 0.3f);
        sound.loop(Sound.MUSIC_BACKGROUND);
    }

    /**
     * Dừng nhạc nền khi rời khỏi menu.
     * <p>
     * (Ví dụ: khi người chơi bắt đầu một màn chơi mới).
     */
    public void stopBackgroundMusic() {
        sound.stop(Sound.MUSIC_BACKGROUND);
    }

    /**
     * Phát một hiệu ứng âm thanh (Sound Effect) ngắn dựa trên chỉ số.
     *
     * @param i chỉ số của hiệu ứng âm thanh cần phát (tham khảo lớp {@link Sound}).
     */
    private void playSE(int i) {
        sound.play(i);
    }

    /**
     * Vẽ hình ảnh nền cho menu.
     * <p>
     * Ghi đè phương thức này để vẽ hình nền {@code backgroundImage}
     * lên toàn bộ panel.
     *
     * @param g đối tượng {@link Graphics} được sử dụng để vẽ.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            // Vẽ hình nền vừa với kích thước panel
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            // Dự phòng nếu không tải được ảnh
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}