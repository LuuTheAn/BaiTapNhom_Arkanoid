package Game;

import sound.Sound;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.util.List;

public class LeaderboardPanel extends JPanel {
    private final JPanel container;
    private final MenuPanel menuPanel;
    private final JButton backButton;
    private final JPanel scoreListPanel;
    private Image backgroundImage;
    private final Sound sound;

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
                new RoundedBorder(new Color(255, 215, 0), 2, 30), // 🎨 viền bo tròn
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
                new RoundedBorder(Color.GRAY, 2, 25), // 🎨 viền bo tròn
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

    // ✅ Làm mới danh sách xếp hạng
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

    // 🎨 Style cho nút
    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 24));
        button.setBackground(new Color(50, 50, 50, 200));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(Color.WHITE, 2, 20), // 🎨 bo tròn nút
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

    // 🎨 Border bo góc tùy chỉnh
    private static class RoundedBorder extends AbstractBorder {
        private final Color color;
        private final int thickness;
        private final int radius;

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
