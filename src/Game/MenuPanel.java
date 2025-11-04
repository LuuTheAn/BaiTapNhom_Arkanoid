package Game;

import sound.Sound;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class MenuPanel extends JPanel {
    private JButton playButton;
    private JButton leaderboardButton;
    private JButton quitButton;
    private JPanel container;
    private Image backgroundImage;
    private Sound sound;

    // 🟡 Biến cho hiệu ứng nhấp nháy tiêu đề
    private float glowAlpha = 1.0f;
    private boolean fadingOut = true;

    public MenuPanel(JPanel container) {
        this.container = container;
        setLayout(new GridBagLayout());
        setBackground(Color.BLACK);

        // 🔹 Load background
        try {
            backgroundImage = ImageIO.read(getClass().getResource("/img/menu_bg.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 🔹 Âm thanh nền
        sound = Sound.getInstance();
        sound.setVolume(0, 0.3f);
        sound.loop(0);

        // 🔹 Layout setup
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        // 🔹 Tiêu đề có hiệu ứng sáng mờ dần
        JLabel titleLabel = new JLabel("ARKANOID") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setFont(getFont());

                // Viền bóng mờ đen phía sau
                g2d.setColor(new Color(0, 0, 0, 150));
                g2d.drawString(getText(), 5, getHeight() - 10);

                // Hiệu ứng sáng dần
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

        // 🔹 Tạo Timer để nhấp nháy chữ
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
        btnGbc.anchor = GridBagConstraints.WEST;
        buttonPanel.add(playButton, btnGbc);

        btnGbc.gridy = 1;
        btnGbc.anchor = GridBagConstraints.EAST;
        buttonPanel.add(leaderboardButton, btnGbc);

        btnGbc.gridy = 2;
        btnGbc.anchor = GridBagConstraints.WEST;
        buttonPanel.add(quitButton, btnGbc);

        gbc.gridy = 1;
        add(buttonPanel, gbc);

        // 🔹 Xử lý sự kiện nút
        playButton.addActionListener(e -> {
            playSE(1);

            // 🧹 Reset toàn bộ game trước khi sang màn chọn level
            for (Component comp : container.getComponents()) {
                if (comp instanceof GamePanel gamePanel) {
                    gamePanel.getGameManager().resetSession();
                    System.out.println("🧹 Reset session khi ấn PLAY từ menu → totalScore = 0");
                    break;
                }
            }

            CardLayout cl = (CardLayout) container.getLayout();
            cl.show(container, "LEVEL");
        });

        leaderboardButton.addActionListener(e -> {
            playSE(1);

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
            playSE(1);
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn có chắc chắn muốn thoát game không?",
                    "Xác nhận thoát",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );
            if (confirm == JOptionPane.YES_OPTION) {
                sound.stop(0);
                System.exit(0);
            } else {
                playSE(1);
            }
        });
    }

    // 🟢 Nút menu có hiệu ứng hover và bo tròn
    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 100));
                g2.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 20, 20);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth() - 10, getHeight() - 10, 20, 20);
                super.paintComponent(g);
            }
        };

        btn.setFont(new Font("Arial", Font.BOLD, 28));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(40, 40, 40));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(15, 50, 15, 50));
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(70, 130, 180));
                btn.setForeground(Color.YELLOW);
                btn.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(40, 40, 40));
                btn.setForeground(Color.WHITE);
                btn.repaint();
            }
        });

        return btn;
    }

    public void resumeBackgroundMusic() {
        sound.stop(0);
        sound.setVolume(0, 0.3f);
        sound.loop(0);
    }

    public void stopBackgroundMusic() {
        sound.stop(0);
    }

    // 🔹 Phát âm thanh hiệu ứng
    private void playSE(int i) {
        sound.play(i);
    }

    // 🔹 Vẽ nền menu
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null)
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}
