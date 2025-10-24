package entity;

/**
 * MovableObject: lớp cha cho các đối tượng có vận tốc dx/dy.
 * - x,y vẫn là int (vị trí pixel)
 * - dx,dy là double (vận tốc, có phần thập phân)
 *
 * Lưu ý: nhiều lớp con hiện tại gọi super(x,y,width,height, 0, 0)
 * int literals sẽ tự động được widen sang double nên tương thích.
 */
public abstract class MovableObject extends GameObject {
    protected double dx;
    protected double dy;

    public MovableObject(int x, int y, int width, int height, double dx, double dy) {
        super(x, y, width, height);
        this.dx = dx;
        this.dy = dy;
    }

    // Cập nhật vị trí dựa trên dx/dy (ep ve int khi cập nhật x/y)
    public void move() {
        // Sử dụng Math.round để giảm jitter khi tích luỹ phần thập phân
        x += (int) Math.round(dx);
        y += (int) Math.round(dy);
    }
}
