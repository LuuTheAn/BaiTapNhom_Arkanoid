package entity;

import java.util.ArrayList;
import java.util.List;

public class BrickFactory {

    // 🔹 Hàm tạo level mặc định (nếu không có level cụ thể)
    public static List<Brick> createDefaultBricks() {
        return createLevel(1);
    }

    // 🔹 Hàm chính để tạo bố cục gạch theo level
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

                        boolean isBorder = (row == 0 || row == rows - 1 || col == 0 || col == cols - 1);
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

            // 🌈 LEVEL 4 – Xen kẽ màu và loại ngẫu nhiên (vui mắt)
            case 4 -> {
                for (int row = 0; row < 8; row++) {
                    for (int col = 0; col < 20; col++) {
                        int x = col * brickWidth;
                        int y = startY + row * brickHeight;
                        int pattern = (row + col) % 4;

                        switch (pattern) {
                            case 0 -> bricks.add(new NormalBrick(x, y, brickWidth, brickHeight));
                            case 1 -> bricks.add(new StrongBrick(x, y, brickWidth, brickHeight));
                            case 2 -> bricks.add(new ExplosiveBrick(x, y, brickWidth, brickHeight));
                            case 3 -> bricks.add(new UnbreakableBrick(x, y, brickWidth, brickHeight));
                        }
                    }
                }
            }

            // 🔹 LEVEL 5 – Dạng sóng (zigzag)
            case 5 -> {
                for (int row = 0; row < 8; row++) {
                    int offsetX = (row % 2 == 0) ? 0 : brickWidth / 2;
                    for (int col = 0; col < 20; col++) {
                        int x = offsetX + col * brickWidth;
                        int y = startY + row * brickHeight;

                        if (row % 2 == 0)
                            bricks.add(new StrongBrick(x, y, brickWidth, brickHeight));
                        else
                            bricks.add(new NormalBrick(x, y, brickWidth, brickHeight));
                    }
                }
            }

            // Mặc định nếu không khớp level
            default -> {
                return createDefaultBricks();
            }
        }

        return bricks;
    }
}
