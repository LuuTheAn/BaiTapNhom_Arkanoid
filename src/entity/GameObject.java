package entity;

import java.awt.*;

/**
 * Lớp trừu tượng {@code GameObject} đại diện cho mọi đối tượng trong trò chơi
 * có vị trí, kích thước và khả năng được vẽ (render) trên màn hình.
 * <p>
 * Các lớp con như {@link Brick}, {@link Ball}, {@link Paddle} sẽ kế thừa lớp này
 * để định nghĩa hành vi cụ thể.
 * </p>
 *
 * @author An
 * @version 1.0
 */
public abstract class GameObject {

    /** Tọa độ góc trên bên trái của đối tượng (theo pixel) */
    protected int x, y;

    /** Chiều rộng và chiều cao của đối tượng (theo pixel) */
    protected int width, height;

    /**
     * Khởi tạo một {@code GameObject} mới với vị trí và kích thước xác định.
     *
     * @param x hoành độ của đối tượng
     * @param y tung độ của đối tượng
     * @param width chiều rộng của đối tượng
     * @param height chiều cao của đối tượng
     */
    public GameObject(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Phương thức trừu tượng để vẽ đối tượng lên màn hình.
     * Mỗi lớp con phải tự định nghĩa cách vẽ riêng.
     *
     * @param g đối tượng {@link Graphics2D} dùng để vẽ
     */
    public abstract void render(Graphics2D g);

    /**
     * Cập nhật trạng thái của đối tượng (nếu có).
     * Mặc định không làm gì — lớp con có thể override.
     */
    public void update() {}

    /**
     * Trả về hình chữ nhật đại diện cho vùng chiếm chỗ (bounding box) của đối tượng.
     * Dùng để kiểm tra va chạm.
     *
     * @return {@link Rectangle} biểu diễn vùng bao quanh đối tượng
     */
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    // 🔹 Getter và Setter — giúp truy cập an toàn đến thuộc tính
    /** @return hoành độ của đối tượng */
    public int getX() { return x; }

    /** @return tung độ của đối tượng */
    public int getY() { return y; }

    /** @return chiều rộng của đối tượng */
    public int getWidth() { return width; }

    /** @return chiều cao của đối tượng */
    public int getHeight() { return height; }
}
