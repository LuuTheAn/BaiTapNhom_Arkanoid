package Powerup;

import java.awt.*;
import entity.GameObject;
import entity.Paddle;

/**
 * Lớp {@code PowerUp} là một lớp cơ sở trừu tượng (abstract base class)
 * đại diện cho các vật phẩm thưởng có thể thu thập được,
 * rơi ra từ các viên gạch bị phá hủy.
 * <p>
 * Mỗi vật phẩm di chuyển xuống dưới với tốc độ không đổi và có thể tương tác với
 * thanh đỡ ({@link Paddle}) của người chơi để áp dụng một hiệu ứng cụ thể.
 * Các lớp con phải triển khai (implement) phương thức {@link #applyEffect(Paddle)}
 * để định nghĩa hành vi cụ thể (ví dụ: mở rộng thanh đỡ, tăng tốc quả bóng, v.v.).
 * </p>
 *
 * <p>
 * Lớp này cũng xử lý logic cơ bản về vẽ (rendering), di chuyển,
 * và hộp giới hạn (bounding-box) để phát hiện va chạm với thanh đỡ.
 * </p>
 *
 * @see entity.GameObject
 * @see entity.Paddle
 * @see Powerup.ExpandPaddlePowerUp
 * @see Powerup.FastBallPowerUp
 */
public abstract class PowerUp extends GameObject {

    /** Tốc độ rơi của vật phẩm (tính bằng pixel mỗi khung hình). */
    protected int speed = 3;

    /** Trạng thái cho biết vật phẩm đã được kích hoạt (được thanh đỡ thu thập). */
    protected boolean active = false;

    /** Màu sắc được sử dụng để vẽ nếu không có hình ảnh tùy chỉnh. */
    protected Color color = Color.YELLOW;

    /**
     * Tạo một {@code PowerUp} mới tại các tọa độ và kích thước được chỉ định.
     *
     * @param x      tọa độ x của vị trí vật phẩm
     * @param y      tọa độ y của vị trí vật phẩm
     * @param width  chiều rộng của vật phẩm
     * @param height chiều cao của vật phẩm
     */
    public PowerUp(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    /**
     * Định nghĩa hiệu ứng được áp dụng khi vật phẩm này được thanh đỡ thu thập.
     * Các lớp con phải triển khai phương thức này để định nghĩa hành vi riêng của chúng.
     *
     * @param paddle thanh đỡ ({@link Paddle}) đã thu thập vật phẩm
     */
    public abstract void applyEffect(Paddle paddle);

    // ====== Getters ======

    /** @return tọa độ x hiện tại */
    public int getX() { return x; }

    /** @return tọa độ y hiện tại */
    public int getY() { return y; }

    /** @return chiều rộng của vật phẩm */
    public int getWidth() { return width; }

    /** @return chiều cao của vật phẩm */
    public int getHeight() { return height; }

    /** @return hình chữ nhật giới hạn (bounding rectangle) dùng cho phát hiện va chạm */
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    /** @return {@code true} nếu vật phẩm này đang hoạt động, ngược lại {@code false} */
    public boolean isActive() { return active; }

    /**
     * Thiết lập trạng thái hoạt động của vật phẩm này.
     *
     * @param active {@code true} nếu vật phẩm này đang hoạt động
     */
    public void setActive(boolean active) { this.active = active; }

    // ====== Behavior ======

    /**
     * Cập nhật vị trí của vật phẩm mỗi khung hình.
     * <p>
     * Triển khai mặc định sẽ di chuyển vật phẩm xuống dưới
     * theo một tốc độ {@link #speed} không đổi.
     * </p>
     */
    public void update() {
        y += speed;
    }

    /**
     * Vẽ (render) vật phẩm lên màn hình.
     * <p>
     * Các lớp con có thể ghi đè (override) phương thức này để vẽ hình ảnh (sprites)
     * hoặc hoạt ảnh tùy chỉnh.
     * Theo mặc định, phương thức này vẽ một hình tròn đơn giản có màu.
     * </p>
     *
     * @param g đối tượng {@link Graphics2D} được sử dụng để vẽ
     */
    @Override
    public void render(Graphics2D g) {
        g.setColor(color);
        g.fillOval(x, y, width, height);
        g.setColor(Color.BLACK);
        g.drawOval(x, y, width, height);
    }
}