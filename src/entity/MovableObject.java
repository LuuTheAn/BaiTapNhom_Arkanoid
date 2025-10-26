package entity;

public abstract class MovableObject extends GameObject {
    protected double dx;
    protected double dy;

    public MovableObject(int x, int y, int width, int height, double dx, double dy) {
        super(x, y, width, height);
        this.dx = dx;
        this.dy = dy;
    }

    // 🏃 Cập nhật vị trí dựa trên dx/dy
    public void move() {
        x += (int) Math.round(dx);
        y += (int) Math.round(dy);
    }

    // Getter/Setter cho vận tốc
    public double getDx() { return dx; }
    public double getDy() { return dy; }
    public void setDx(double dx) { this.dx = dx; }
    public void setDy(double dy) { this.dy = dy; }
}
