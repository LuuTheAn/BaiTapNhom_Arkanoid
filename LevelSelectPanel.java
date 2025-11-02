package Game;

import sound.Sound;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class LevelSelectPanel extends JPanel {
    private JPanel container;
    private JButton[] levelButtons;
    private Image backgroundImage;
    private Sound sound;

    public LevelSelectPanel(JPanel container) {
        this.container = container;
        setLayout(new GridBagLayout());
        setBackground(Color.BLACK);

        sound = new Sound();

        try {
            backgroundImage = ImageIO.read(getClass().getResource("/img/menu_bg.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel title = new JLabel("SELECT LEVEL");
        title.setFont(new Font("Arial", Font.BOLD, 42));
        title.setForeground(Color.WHITE);
        gbc.gridy = 0;
        add(title, gbc);

        // 🔹 Tạo 5 nút level
        levelButtons = new JButton[5];
        for (int i = 0; i < 5; i++) {
            final int level = i + 1;
            levelButtons[i] = createButton("LEVEL " + level);
            gbc.gridy = i + 1;
            add(levelButtons[i], gbc);
            levelButtons[i].addActionListener(e -> {
                playClickSound();
                startLevel(level);
            });

        }

        gbc.gridy = 6;
        JButton back = createButton("BACK");
        add(back, gbc);
        back.addActionListener(e -> {
            playClickSound();
            showMenu();
        });

    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 28));
        btn.setBackground(Color.DARK_GRAY);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // 🔹 Cập nhật trạng thái mở khóa
    public void refreshUnlockStatus() {
        int unlocked = ProgressManager.getUnlockedLevel();

        for (int i = 0; i < levelButtons.length; i++) {
            JButton btn = levelButtons[i];
            if (i + 1 <= unlocked) {
                btn.setEnabled(true);
                btn.setBackground(Color.DARK_GRAY);
                btn.setText("LEVEL " + (i + 1));
            } else {
                btn.setEnabled(false);
                btn.setBackground(Color.GRAY);
                btn.setText("🔒 LOCKED");
            }
        }
    }

    private void startLevel(int level) {
        // 🔹 Tìm GamePanel để bắt đầu chơi
        GamePanel gamePanel = null;
        MenuPanel menuPanel = null;

        for (Component comp : container.getComponents()) {
            if (comp instanceof GamePanel gp) {
                gamePanel = gp;
            } else if (comp instanceof MenuPanel mp) {
                menuPanel = mp;
            }
        }

        // 🔹 Nếu có menuPanel → dừng nhạc nền
        if (menuPanel != null) {
            menuPanel.stopBackgroundMusic();
        }

        // 🔹 Bắt đầu level
        if (gamePanel != null) {
            gamePanel.startGame(level);
            CardLayout cl = (CardLayout) container.getLayout();
            cl.show(container, "GAME");
        }
    }

    private void showMenu() {
        CardLayout cl = (CardLayout) container.getLayout();
        cl.show(container, "MENU");
    }

    private void playClickSound() {
        sound.play(1); // 🔊 Giả sử âm thanh click là sound số 1 trong Sound class
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) refreshUnlockStatus(); // 🔁 Cập nhật khi mở lại
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
