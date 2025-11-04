package Powerup;

import entity.Paddle;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

/**
 * Đại diện cho một vật phẩm (power-up) giúp mở rộng chiều rộng của thanh đỡ (paddle) khi thu thập.
 * <p>
 * Vật phẩm này xuất hiện dưới dạng một quả bóng màu xanh lá cây trên màn hình.
 * Khi được kích hoạt, nó sẽ tăng gấp đôi chiều rộng của thanh đỡ (nếu chưa được mở rộng).
 */
public class ExpandPaddlePowerUp extends PowerUp {

    /** * Lượng chiều rộng thanh đỡ tăng thêm (tính bằng pixel).
     * (Lưu ý: Mã hiện tại đang nhân đôi, không dùng biến này, nhưng Javadoc giữ lại mô tả).
     */
    private final int expandAmount = 50;

    /** Hình ảnh được sử dụng để biểu thị vật phẩm. */
    private BufferedImage image;

    /**
     * Khởi tạo một vật phẩm {@code ExpandPaddlePowerUp} mới tại vị trí chỉ định.
     *
     * @param x tọa độ x của vật phẩm
     * @param y tọa độ y của vật phẩm
     */
    public ExpandPaddlePowerUp(int x, int y) {
        super(x, y, 30, 30); // Kích thước hitbox

        try {
            // Tải hình ảnh vật phẩm (bóng xanh)
            image = ImageIO.read(getClass().getResourceAsStream("/img/greenball.png"));
        } catch (IOException e) {
            System.err.println("❌ Không thể tải ảnh greenball.png: " + e.getMessage());
        }
    }

    /**
     * Áp dụng hiệu ứng của vật phẩm lên thanh đỡ (paddle) được chỉ định.
     * <p>
     * Nếu thanh đỡ chưa được mở rộng, phương thức này sẽ nhân đôi chiều rộng của nó
     * và đánh dấu là đã mở rộng.
     *
     * @param paddle đối tượng {@link Paddle} để áp dụng hiệu ứng
     */
    @Override
    public void applyEffect(Paddle paddle) {
        if (!paddle.isExpanded()) {
            paddle.setWidth(paddle.getWidth() * 2);
            paddle.setExpanded(true);
        }
    }

    /**
     * Vẽ (render) vật phẩm lên màn hình.
     * Nếu hình ảnh tải thất bại, một hình tròn màu xanh lá cây dự phòng sẽ được vẽ thay thế.
     *
     * @param g đối tượng {@link Graphics2D} được sử dụng để vẽ
     */
    @Override
    public void render(Graphics2D g) {
        // Tính toán để vẽ hình ảnh/hình tròn vuông vức
        int size = Math.max(width, height);
        int drawX = x - (size - width) / 2;
        int drawY = y - (size - height) / 2;

        if (image != null) {
            // Vẽ hình ảnh
            g.drawImage(image, drawX, drawY, size, size, null);
        } else {
            // Vẽ hình tròn dự phòng (nếu ảnh lỗi)
            g.setColor(Color.GREEN);
            g.fillOval(drawX, drawY, size, size);
            g.setColor(Color.BLACK);
            g.drawOval(drawX, drawY, size, size);
        }
    }
}