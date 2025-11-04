package entity;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Lớp {@code Paddle} đại diện cho thanh đỡ (thanh trượt) do người chơi điều khiển trong game Arkanoid.
 * <p>
 * Thanh Paddle di chuyển theo chiều ngang, có thể thay đổi kích thước (mở rộng hoặc thu nhỏ),
 * và được hiển thị bằng ảnh hoặc màu dự phòng nếu ảnh không tải được.
 * </p>
 *
 * <p><b>Chức năng chính:</b></p>
 * <ul>
 *   <li>Di chuyển sang trái/phải với tốc độ xác định</li>
 *   <li>Giới hạn di chuyển trong phạm vi màn hình</li>
 *   <li>Hỗ trợ hiển thị bằng hình ảnh (texture)</li>
 *   <li>Có thể mở rộng kích thước khi nhận item</li>
 * </ul>
 *
 * @see MovableObject
 * @see Ball
 * @author An
 * @version 1.0
 */
public class Paddle extends MovableObject {

    /** Tốc độ di chuyển của thanh Paddle */
    private int speed;

    /** Hình ảnh hiển thị của thanh Paddle */
    private BufferedImage image;

    /** Trạng thái mở rộng kích thước của Paddle */
    private boolean expanded = false;

    /**
     * Khởi tạo một Paddle với vị trí, kích thước và tốc độ xác định.
     *
     * @param x hoành độ của Paddle
     * @param y tung độ của Paddle
     * @param width chiều rộng ban đầu
     * @param height chiều cao
     * @param speed tốc độ di chuyển (sẽ được nhân đôi để tạo cảm giác mượt hơn)
     */
    public Paddle(int x, int y, int width, int height, int speed) {
        super(x, y, width, height, 0, 0);
        this.speed = speed * 2;

        try {
            // 🔹 Tải ảnh Paddle từ thư mục tài nguyên
            image = ImageIO.read(getClass().getResourceAsStream("/img/pipe_ngan.png"));
            System.out.println("✅ Paddle image loaded successfully!");
        } catch (IOException e) {
            System.out.println("❌ Paddle image NOT loaded: " + e.getMessage());
            image = null;
        }
    }

    /**
     * Lấy chiều rộng hiện tại của Paddle.
     *
     * @return chiều rộng của Paddle
     */
    public int getWidth() {
        return width;
    }

    /**
     * Cập nhật chiều rộng của Paddle.
     *
     * @param width chiều rộng mới
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Kiểm tra xem Paddle có đang ở trạng thái mở rộng không.
     *
     * @return {@code true} nếu đang mở rộng, ngược lại {@code false}
     */
    public boolean isExpanded() {
        return expanded;
    }

    /**
     * Đặt trạng thái mở rộng cho Paddle.
     *
     * @param expanded {@code true} nếu muốn mở rộng Paddle
     */
    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    /**
     * Di chuyển Paddle sang trái, giới hạn không vượt ra ngoài biên trái của màn hình.
     */
    public void moveLeft() {
        x -= speed;
        if (x < 0) x = 0;
    }

    /**
     * Di chuyển Paddle sang phải, giới hạn không vượt ra ngoài biên phải của màn hình.
     *
     * @param panelWidth chiều rộng của khung chơi (panel)
     */
    public void moveRight(int panelWidth) {
        x += speed;
        if (x + width > panelWidth) x = panelWidth - width;
    }

    /**
     * Hiển thị Paddle lên màn hình.
     * <p>Nếu ảnh không tải được, vẽ một hình chữ nhật màu xanh thay thế.</p>
     *
     * @param g đối tượng {@link Graphics2D} dùng để vẽ
     */
    @Override
    public void render(Graphics2D g) {
        if (image != null) {
            g.drawImage(image, x, y, width, height, null);
        } else {
            g.setColor(Color.GREEN);
            g.fillRect(x, y, width, height);
        }
    }
}
