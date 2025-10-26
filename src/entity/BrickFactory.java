package entity;

import java.util.ArrayList;
import java.util.List;

public class BrickFactory {
    public static List<Brick> createDefaultBricks() {
        List<Brick> bricks = new ArrayList<>();

        for (int row = 0; row < 7; row++) {
            for (int col = 0; col < 20; col++) {
                int x =  col * 40;
                int y = 50 + row * 20;

                if (row == 0) {
                    bricks.add(new UnbreakableBrick(x, y, 40, 20));
                } else if (row == 1) {
                    bricks.add(new StrongBrick(x, y, 40, 20));
                } else if (row == 3) {
                    bricks.add(new ExplosiveBrick(x, y, 40, 20));
                } else {
                    bricks.add(new NormalBrick(x, y, 40, 20));
                }
            }
        }
        return bricks;
    }
}