
package entity;

import java.awt.*;

public abstract class Brick extends GameObject {
    protected int hitPoints;
    public Brick(int x, int y, int width, int height, int hitPoints) {
        super(x, y, width, height);
        this.hitPoints = hitPoints;
    }
    public void takeHit() { hitPoints--; }
    public boolean isDestroyed() { return hitPoints <= 0; }
}
