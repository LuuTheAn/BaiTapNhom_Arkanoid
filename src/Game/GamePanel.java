package Game;

import entity.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

/**
 * GamePanel là JPanel chính để chạy trò chơi Arkanoid.
 * <p>
 * Chịu trách nhiệm vẽ đồ họa, nhận input từ bàn phím và điều khiển GameManager.
 * Có khả năng quay lại menu và xử lý nhạc nền.
 * </p>
 */
public class GamePanel extends JPanel implements ActionListener, KeyListener {
    public static final int WIDTH = 800, HEIGHT = 600;

    /** Quản lý trạng thái game, logic và vẽ game */
    private GameManager gameManager;

    /** Timer để cập nhật game theo khung hình */
    private Timer timer;

    /** Tham chiếu container chính chứa panel (CardLayout) */
    private JPanel container;

    /** Ảnh nền của game */
    private Image backgroundImage;

    /** Lưu level hiện tại */
    private int currentLevel = 1;

    /**
     * Khởi tạo GamePanel.
     *
     * @param container JPanel chứa GamePanel (CardLayout) để xử lý chuyển panel
     */
    public GamePanel(JPanel container) {
        this.container = container;
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        addKeyListener(this);

        gameManager = new GameManager(WIDTH, HEIGHT);
        timer = new Timer(1000 / 60, this);

        // Gán callback cho gameManager để quay về menu
        gameManager.setOnReturnToMenu(() -> {
            SwingUtilities.invokeLater(() -> {
                if (container != null) {
                    CardLayout cl = (CardLayout) container.getLayout();
                    cl.show(container, "MENU");
                    timer.stop();

                    // Phát lại nhạc nền của menu
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

        // Load ảnh nền
        try {
            var url = getClass().getResource("/img/game_bg.jpg");
            if (url != null)
                backgroundImage = ImageIO.read(url);
            else
                backgroundImage = null;
        } catch (IOException e) {
            System.out.println("Không tìm thấy ảnh nền, dùng nền đen mặc định");
            backgroundImage = null;
        }
    }

    /**
     * Thiết lập lại container chính.
     *
     * @param container JPanel chứa GamePanel
     */
    public void setContainer(JPanel container) {
        this.container = container;
    }

    /**
     * Bắt đầu chơi game với level cụ thể.
     *
     * @param level Level muốn chơi
     */
    public void startGame(int level) {
        this.currentLevel = level;
        gameManager.loadLevel(level);
        timer.start();

        requestFocusInWindow();
        SwingUtilities.invokeLater(this::requestFocusInWindow);
    }

    /**
     * Bắt đầu hoặc reset game tại level hiện tại.
     */
    public void startGame() {
        startGame(currentLevel);
    }

    /**
     * Lấy GameManager đang sử dụng.
     *
     * @return GameManager
     */
    public GameManager getGameManager() {
        return gameManager;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (backgroundImage != null)
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

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

        // Khi ấn M ở trạng thái Game Over / Win → quay về menu
        if ((gameManager.isGameOver() || gameManager.isGameWin()) && key == KeyEvent.VK_M) {
            returnToMenuByKey();
            return;
        }

        // Khi đang ở trạng thái hoàn thành màn (LEVEL COMPLETE)
        if (key == KeyEvent.VK_M || key == KeyEvent.VK_N) {
            gameManager.onKeyPressed(key);
            return;
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

    /**
     * Hàm riêng xử lý quay lại menu khi nhấn phím M.
     */
    private void returnToMenuByKey() {
        if (container == null) {
            System.out.println("⚠ container null, không quay lại menu được!");
            return;
        }

        SwingUtilities.invokeLater(() -> {
            CardLayout cl = (CardLayout) container.getLayout();
            cl.show(container, "MENU");
            timer.stop();

            for (Component comp : container.getComponents()) {
                if (comp instanceof MenuPanel menuPanel) {
                    menuPanel.resumeBackgroundMusic();
                    break;
                }
            }
            System.out.println("↩ Quay về MENU bằng phím M.");
        });
    }
}
