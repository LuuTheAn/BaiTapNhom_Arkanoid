package Game;

import entity.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class GamePanel extends JPanel implements ActionListener, KeyListener {
    public static final int WIDTH = 800, HEIGHT = 600;
    private GameManager gameManager;
    private Timer timer;
    private JPanel container; // 🔹 tham chiếu container chính (CardLayout)
    private Image backgroundImage;
    private int currentLevel = 1; // 🔹 lưu level hiện tại

    public GamePanel(JPanel container) {
        this.container = container;
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        addKeyListener(this);

        gameManager = GameManager.getInstance(WIDTH, HEIGHT);
        timer = new Timer(1000 / 60, this);

        // ✅ GÁN CALLBACK CHO GAME MANAGER
        gameManager.setOnReturnToMenu(() -> {
            SwingUtilities.invokeLater(() -> {
                if (container != null) {
                    CardLayout cl = (CardLayout) container.getLayout();
                    cl.show(container, "MENU");
                    timer.stop();

                    // ✅ Gọi phát lại nhạc nền menu
                    for (Component comp : container.getComponents()) {
                        if (comp instanceof MenuPanel menuPanel) {
                            menuPanel.resumeBackgroundMusic();
                            break;
                        }
                    }
                    System.out.println("↩ Quay về MENU thành công (callback).");
                } else {
                    System.out.println("⚠ container null, không quay lại menu được!");
                }
            });
        });

        try {
            var url = getClass().getResource("/img/game_bg.jpg");
            backgroundImage = ImageIO.read(url);
        } catch (Exception e) {
            System.out.println("Không tìm thấy ảnh nền, dùng nền đen mặc định");
            backgroundImage = null;
        }
    }

    public void setContainer(JPanel container) {
        this.container = container;
    }

    // 🔹 Gọi khi bắt đầu chơi level
    public void startGame(int level) {
        this.currentLevel = level;
        gameManager.loadLevel(level); // 👉 gọi hàm mới trong GameManager
        timer.start();

        // đảm bảo focus nhận phím
        requestFocusInWindow();
        SwingUtilities.invokeLater(this::requestFocusInWindow);
    }

    // 🔹 Giữ lại cho trường hợp reset từ trong game
    public void startGame() {
        startGame(currentLevel);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
        gameManager.render((Graphics2D) g);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        gameManager.update();
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        // 🔹 Khi ấn M ở trạng thái Game Over/Win → quay về menu
        if ((gameManager.isGameOver() || gameManager.isGameWin()) && key == KeyEvent.VK_M) {
            if (container != null) {
                CardLayout cl = (CardLayout) container.getLayout();
                cl.show(container, "MENU");
                timer.stop();

                // ✅ Gọi phát lại nhạc nền menu
                for (Component comp : container.getComponents()) {
                    if (comp instanceof MenuPanel menuPanel) {
                        menuPanel.resumeBackgroundMusic();
                        break;
                    }
                }
                System.out.println("↩ Quay về MENU bằng phím M.");
            }
        }

        gameManager.onKeyPressed(key);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        gameManager.onKeyReleased(e.getKeyCode());
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void addNotify() {
        super.addNotify();
        requestFocusInWindow();
    }
}
