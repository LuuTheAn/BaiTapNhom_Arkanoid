package Game;

import entity.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GamePanel extends JPanel implements ActionListener, KeyListener {
    public static final int WIDTH = 400, HEIGHT = 600;
    private GameManager gameManager;
    private Timer timer;
    private JPanel container; // 🔹 tham chiếu về container chính (CardLayout)

    public GamePanel(JPanel container) {
        this.container = container;
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        addKeyListener(this);
        gameManager = new GameManager(WIDTH, HEIGHT);
        timer = new Timer(1000 / 60, this);

    }

    public void setContainer(JPanel container) {
        this.container = container;
    }

    public void startGame() {
        gameManager.reset();
        timer.start();
        requestFocusInWindow(); // 🔹 tự động focus khi bắt đầu game
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
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
        requestFocusInWindow(); // 🔹 tự động focus khi hiển thị
    }
}
