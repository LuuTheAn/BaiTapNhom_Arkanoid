package entity;

/**
 * Lớp trừu tượng {@code MovableObject} mở rộng {@link GameObject},
 * đại diện cho các đối tượng có khả năng di chuyển trong trò chơi.
 * <p>
 * Các lớp con như {@link Ball} hoặc {@link Paddle} sẽ kế thừa lớp này
 * để xử lý logic di chuyển dựa trên vận tốc.
 * </p>
 *
 * @author An
 * @version 1.0
 */
public abstract class MovableObject extends GameObject {

    /** Vận tốc theo trục X (đơn vị: pixel / frame) */
    protected double dx;

    /** Vận tốc theo trục Y (đơn vị: pixel / frame) */
    protected double dy;

    /**
     * Khởi tạo một {@code MovableObject} với vị trí, kích thước và vận tốc cho trước.
     *
     * @param x hoành độ ban đầu của đối tượng
     * @param y tung độ ban đầu của đối tượng
     * @param width chiều rộng của đối tượng
     * @param height chiều cao của đối tượng
     * @param dx vận tốc theo trục X
     * @param dy vận tốc theo trục Y
     */
    public MovableObject(int x, int y, int width, int height, double dx, double dy) {
        super(x, y, width, height);
        this.dx = dx;
        this.dy = dy;
    }

    /**
     * Di chuyển đối tượng dựa trên vận tốc hiện tại.
     * <p>
     * Phương thức này làm tròn giá trị vận tốc về pixel nguyên để đảm bảo
     * đối tượng hiển thị chính xác trên màn hình.
     * </p>
     */
    public void move() {
        x += (int) Math.round(dx);
        y += (int) Math.round(dy);
    }

    /** @return vận tốc theo trục X */
    public double getDx() { return dx; }

    /** @return vận tốc theo trục Y */
    public double getDy() { return dy; }

    /** @param dx thiết lập vận tốc theo trục X */
    public void setDx(double dx) { this.dx = dx; }

    /** @param dy thiết lập vận tốc theo trục Y */
    public void setDy(double dy) { this.dy = dy; }
}
