package entity;

import java.awt.*;

/**
 * Lớp trừu tượng {@code Brick} đại diện cho một viên gạch trong trò chơi Arkanoid.
 * <p>
 * Mỗi viên gạch có:
 * <ul>
 *     <li>Vị trí (x, y)</li>
 *     <li>Kích thước (width, height)</li>
 *     <li>Số lần va chạm (hitPoints) – giảm dần khi bị bóng đập vào</li>
 * </ul>
 * Khi {@code hitPoints} giảm về 0, gạch được xem là bị phá hủy.
 *
 * <p>Đây là lớp cha cho các loại gạch cụ thể như {@code NormalBrick}, {@code StrongBrick}, v.v.</p>
 *
 * @author Lưu
 * @version 1.0
 */
public abstract class Brick extends GameObject {

    /**
     * Số lần va chạm còn lại trước khi viên gạch bị phá hủy.
     */
    protected int hitPoints;

    /**
     * Khởi tạo một viên gạch với vị trí, kích thước và số lần va chạm nhất định.
     *
     * @param x         Tọa độ X của gạch
     * @param y         Tọa độ Y của gạch
     * @param width     Chiều rộng của gạch
     * @param height    Chiều cao của gạch
     * @param hitPoints Số lần va chạm mà gạch có thể chịu được trước khi bị phá hủy
     */
    public Brick(int x, int y, int width, int height, int hitPoints) {
        super(x, y, width, height);
        this.hitPoints = hitPoints;
    }

    /**
     * Giảm {@code hitPoints} của gạch đi 1 đơn vị khi bị bóng đập vào.
     */
    public void takeHit() {
        hitPoints--;
    }

    /**
     * Kiểm tra xem gạch đã bị phá hủy chưa.
     *
     * @return {@code true} nếu {@code hitPoints <= 0}, ngược lại {@code false}
     */
    public boolean isDestroyed() {
        return hitPoints <= 0;
    }

    /**
     * Vẽ gạch lên màn hình.
     *
     * @param g Đối tượng {@link Graphics2D} dùng để vẽ
     */
    public abstract void render(Graphics2D g);
}
