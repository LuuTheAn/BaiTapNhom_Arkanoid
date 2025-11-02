package entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BrickFactory {

    private static final Random random = new Random();

    // 🔹 Hàm tạo level mặc định
    public static List<Brick> createDefaultBricks() {
        return createLevel(1);
    }

    // 🔹 Hàm tạo ngẫu nhiên một viên gạch theo tỉ lệ
    private static Brick createRandomBrick(int x, int y, int width, int height) {
        double chance = random.nextDouble() * 100;

        if (chance < 60.0) {
            return new NormalBrick(x, y, width, height);
        } else if (chance < 90.0) {
            return new StrongBrick(x, y, width, height);
        } else if (chance < 99.9) {
            return new ExplosiveBrick(x, y, width, height);
        } else {
            return new UnbreakableBrick(x, y, width, height);
        }
    }

    // 🔹 Hàm tạo bricks theo level
    public static List<Brick> createLevel(int level) {
        List<Brick> bricks = new ArrayList<>();
        int brickWidth = 40;
        int brickHeight = 20;
        int startY = 50;

        switch (level) {

            // 🧱 LEVEL 1 – Cơ bản, xen kẽ các loại gạch
            case 1 -> {
                for (int row = 0; row < 7; row++) {
                    for (int col = 0; col < 20; col++) {
                        int x = col * brickWidth;
                        int y = startY + row * brickHeight;

                        if (row == 0) {
                            bricks.add(new UnbreakableBrick(x, y, brickWidth, brickHeight));
                        } else if (row == 2 || row == 4) {
                            bricks.add(new StrongBrick(x, y, brickWidth, brickHeight));
                        } else if (row == 3) {
                            bricks.add(new ExplosiveBrick(x, y, brickWidth, brickHeight));
                        } else {
                            bricks.add(new NormalBrick(x, y, brickWidth, brickHeight));
                        }
                    }
                }
            }

            // 🔥 LEVEL 2 – Hình tam giác (kim tự tháp)
            case 2 -> {
                int rows = 10;
                for (int row = 0; row < rows; row++) {
                    int bricksInRow = rows - row;
                    int offsetX = (20 - bricksInRow) * (brickWidth / 2);

                    for (int col = 0; col < bricksInRow; col++) {
                        int x = offsetX + col * brickWidth;
                        int y = startY + row * brickHeight;

                        if (row % 3 == 0)
                            bricks.add(new StrongBrick(x, y, brickWidth, brickHeight));
                        else if (row % 4 == 0)
                            bricks.add(new ExplosiveBrick(x, y, brickWidth, brickHeight));
                        else
                            bricks.add(new NormalBrick(x, y, brickWidth, brickHeight));
                    }
                }
            }

            // 💣 LEVEL 3 – Hình chữ nhật rỗng (viền ngoài)
            case 3 -> {
                int rows = 10;
                int cols = 20;
                for (int row = 0; row < rows; row++) {
                    for (int col = 0; col < cols; col++) {
                        int x = col * brickWidth;
                        int y = startY + row * brickHeight;

                        boolean isBorder = (row == 0 || col == 0 || col == cols - 1);
                        if (isBorder) {
                            bricks.add(new UnbreakableBrick(x, y, brickWidth, brickHeight));
                        } else if ((row + col) % 4 == 0) {
                            bricks.add(new ExplosiveBrick(x, y, brickWidth, brickHeight));
                        } else {
                            bricks.add(new NormalBrick(x, y, brickWidth, brickHeight));
                        }
                    }
                }
            }

            // 🌈 LEVEL 4 – Random theo tỉ lệ 60/30/9.9/0.1
            case 4 -> {
                for (int row = 0; row < 8; row++) {
                    for (int col = 0; col < 20; col++) {
                        int x = col * brickWidth;
                        int y = startY + row * brickHeight;
                        bricks.add(createRandomBrick(x, y, brickWidth, brickHeight));
                    }
                }
            }

            // 🔹 LEVEL 5 – Zigzag + random thêm cho vui mắt
            case 5 -> {
                for (int row = 0; row < 8; row++) {
                    int offsetX = (row % 2 == 0) ? 0 : brickWidth / 2;
                    for (int col = 0; col < 20; col++) {
                        int x = offsetX + col * brickWidth;
                        int y = startY + row * brickHeight;
                        bricks.add(createRandomBrick(x, y, brickWidth, brickHeight));
                    }
                }
            }

            // 🔸 LEVEL khác → random toàn bộ
            default -> {
                for (int row = 0; row < 8; row++) {
                    for (int col = 0; col < 20; col++) {
                        int x = col * brickWidth;
                        int y = startY + row * brickHeight;
                        bricks.add(createRandomBrick(x, y, brickWidth, brickHeight));
                    }
                }
            }
        }

        return bricks;
    }
}
