package Game;

import entity.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

/**
 * {@code GamePanel} là lớp chính để hiển thị và điều khiển toàn bộ giao diện chơi game.
 * <p>
 * Lớp này xử lý:
 * <ul>
 *   <li>Vẽ khung hình trò chơi (background, vật thể...)</li>
 *   <li>Nhận sự kiện bàn phím từ người chơi</li>
 *   <li>Gọi {@link GameManager} để cập nhật trạng thái game</li>
 *   <li>Quay lại menu khi người chơi thắng, thua, hoặc nhấn phím tương ứng</li>
 * </ul>
 * Lớp kế thừa {@link JPanel} và cài đặt {@link ActionListener}, {@link KeyListener}.
 */
public class GamePanel extends JPanel implements ActionListener, KeyListener {

    /** Chiều rộng khung chơi (px). */
    public static final int WIDTH = 800;

    /** Chiều cao khung chơi (px). */
    public static final int HEIGHT = 600;

    /** Quản lý toàn bộ logic và đối tượng trong game. */
    private GameManager gameManager;

    /** Bộ hẹn giờ cập nhật game (FPS ~ 60). */
    private Timer timer;

    /** Panel chứa (thường là panel chính với CardLayout để chuyển qua lại giữa menu và game). */
    private JPanel container;

    /** Ảnh nền của trò chơi. */
    private Image backgroundImage;

    /** Level hiện tại của trò chơi. */
    private int currentLevel = 1;

    /**
     * Khởi tạo {@code GamePanel}.
     *
     * @param container panel chứa chính (thường dùng {@link CardLayout} để chuyển qua lại giữa MENU và GAME)
     */
    public GamePanel(JPanel container) {
        this.container = container;
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        addKeyListener(this);

        gameManager = new GameManager(WIDTH, HEIGHT);
        timer = new Timer(1000 / 60, this); // 60 FPS

        // ✅ Thiết lập callback quay lại menu khi người chơi chọn
        gameManager.setOnReturnToMenu(() -> {
            SwingUtilities.invokeLater(() -> {
                if (container != null) {
                    CardLayout cl = (CardLayout) container.getLayout();
                    cl.show(container, "MENU");
                    timer.stop();

                    // ✅ Phát lại nhạc nền menu
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
            backgroundImage = (url != null) ? ImageIO.read(url) : null;
        } catch (IOException e) {
            System.out.println("Không tìm thấy ảnh nền, dùng nền đen mặc định");
            backgroundImage = null;
        }
    }

    /**
     * Gán lại container chứa GamePanel.
     *
     * @param container panel chứa mới
     */
    public void setContainer(JPanel container) {
        this.container = container;
    }

    /**
     * Bắt đầu chơi ở một level cụ thể.
     *
     * @param level level cần chơi
     */
    public void startGame(int level) {
        this.currentLevel = level;
        gameManager.loadLevel(level);
        timer.start();

        requestFocusInWindow();
        SwingUtilities.invokeLater(this::requestFocusInWindow);
    }

    /**
     * Bắt đầu lại level hiện tại.
     */
    public void startGame() {
        startGame(currentLevel);
    }

    /**
     * Lấy đối tượng {@link GameManager} đang được sử dụng.
     *
     * @return đối tượng quản lý game
     */
    public GameManager getGameManager() {
        return gameManager;
    }

    /**
     * Vẽ giao diện game.
     *
     * @param g đối tượng {@link Graphics} để vẽ
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (backgroundImage != null)
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

        gameManager.render((Graphics2D) g);
    }

    /**
     * Cập nhật logic game mỗi khung hình (được gọi bởi {@link Timer}).
     *
     * @param e sự kiện hành động từ timer
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        gameManager.update();
        repaint();
    }

    /**
     * Xử lý khi người chơi nhấn phím.
     *
     * @param e sự kiện phím nhấn
     */
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        // 🔹 Khi ấn M ở trạng thái Game Over / Win → quay về menu
        if ((gameManager.isGameOver() || gameManager.isGameWin()) && key == KeyEvent.VK_M) {
            returnToMenuByKey();
            return;
        }

        // 🔹 Khi đang ở trạng thái hoàn thành màn
        if (key == KeyEvent.VK_M || key == KeyEvent.VK_N) {
            gameManager.onKeyPressed(key);
            return;
        }

        gameManager.onKeyPressed(key);
    }

    /**
     * Xử lý khi người chơi nhả phím.
     *
     * @param e sự kiện phím nhả
     */
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
     * Quay về menu khi người chơi nhấn phím M.
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
