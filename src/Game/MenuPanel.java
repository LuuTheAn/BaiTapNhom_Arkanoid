package Game;

import javax.swing.*;
import java.awt.*;

public class MenuPanel extends JPanel {
    private JButton playButton;
    private JButton quitButton;
    private JPanel container;

    public MenuPanel(JPanel container) {
        this.container = container;
        setLayout(new GridBagLayout());
        setBackground(Color.BLACK);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        // 🔹 Thêm tiêu đề "ARKANOID"
        JLabel titleLabel = new JLabel("ARKANOID");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridy = 0;
        add(titleLabel, gbc);

        // 🔹 Nút Play
        playButton = new JButton("PLAY");
        playButton.setFont(new Font("Arial", Font.BOLD, 24));
        playButton.setBackground(Color.DARK_GRAY);
        playButton.setForeground(Color.WHITE);
        gbc.gridy = 1;
        add(playButton, gbc);

        // 🔹 Nút Quit
        quitButton = new JButton("QUIT");
        quitButton.setFont(new Font("Arial", Font.BOLD, 24));
        quitButton.setBackground(Color.DARK_GRAY);
        quitButton.setForeground(Color.WHITE);
        gbc.gridy = 2;
        add(quitButton, gbc);

        // 🔹 Khi nhấn PLAY
        playButton.addActionListener(e -> {
            CardLayout cl = (CardLayout) container.getLayout();
            cl.show(container, "GAME");

            GamePanel gamePanel = (GamePanel) container.getComponent(1);
            gamePanel.startGame();
            SwingUtilities.invokeLater(() -> gamePanel.requestFocusInWindow());
        });

        // 🔹 Khi nhấn QUIT
        quitButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn có chắc chắn muốn thoát game không?",
                    "Xác nhận thoát",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
    }
}
