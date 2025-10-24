package entity;

import java.util.ArrayList;
import java.util.List;

public class BrickFactory {
    public static List<Brick> createDefaultBricks() {
        List<Brick> bricks = new ArrayList<>();

        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 16; col++) {
                int x = 20 + col * 45;
                int y = 50 + row * 25;

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
