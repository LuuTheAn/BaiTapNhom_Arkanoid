package Game;

import sound.Sound;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Objects;

public class MenuPanel extends JPanel {
    private JButton playButton;
    private JButton quitButton;
    private JPanel container;
    private Image backgroundImage;
    private Sound sound;

    public MenuPanel(JPanel container) {
        this.container = container;
        setLayout(new GridBagLayout());

        // 🔹 Load background
        try {
            backgroundImage = ImageIO.read(Objects.requireNonNull(getClass().getResource("/img/menu_bg.jpg")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 🔹 Khởi tạo âm thanh
        sound = new Sound();
        sound.setVolume(0, 0.3f);
        sound.loop(0); // 🔁 Nhạc nền menu

        // 🔹 Setup layout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        // 🔹 Tiêu đề
        JLabel titleLabel = new JLabel("ARKANOID");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridy = 0;
        add(titleLabel, gbc);

        // 🔹 Nút PLAY
        playButton = new JButton("PLAY");
        styleButton(playButton);
        gbc.gridy = 1;
        add(playButton, gbc);

        // 🔹 Nút QUIT
        quitButton = new JButton("QUIT");
        styleButton(quitButton);
        gbc.gridy = 2;
        add(quitButton, gbc);

        // 🔹 Sự kiện PLAY
        playButton.addActionListener(e -> {
            playSE(1);


            // 👉 Chuyển sang màn Level Select
            CardLayout cl = (CardLayout) container.getLayout();
            cl.show(container, "LEVEL");

            // ⚠️ Không gọi GamePanel ở đây — việc startGame sẽ được gọi trong LevelSelectPanel khi chọn level
        });

        // 🔹 Sự kiện QUIT
        quitButton.addActionListener(e -> {
            playSE(1);
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn có chắc chắn muốn thoát game không?",
                    "Xác nhận thoát",
                    JOptionPane.YES_NO_OPTION
            );

            playSE(1);
            if (confirm == JOptionPane.YES_OPTION) {
                sound.stop(0);
                playSE(1);
                System.exit(0);
            }
        });
    }

    // 🔹 Hàm gọi lại khi quay về menu
    public void resumeBackgroundMusic() {
        sound.stop(0);
        sound.setVolume(0, 0.3f);
        sound.loop(0);
    }
    // 🔹 Hàm gọi lại khi rời khỏi menu
    public void stopBackgroundMusic() {
        sound.stop(0);
    }


    // 🔹 Tạo style cho nút
    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 24));
        button.setBackground(Color.DARK_GRAY);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
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

    // 🔹 Phát âm thanh hiệu ứng
    private void playSE(int i) {
        sound.play(i);
    }
}
