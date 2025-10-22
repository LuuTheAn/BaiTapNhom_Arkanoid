package Powerup;

import entity.Paddle;
import java.awt.*;

public class ExpandPaddlePowerUp extends PowerUp {

    private final int expandAmount = 50; // final ok

    public ExpandPaddlePowerUp(int x, int y) {
        super(x, y, 20, 12);
        this.color = Color.GREEN;
    }

    @Override
    public void applyEffect(Paddle paddle) {
        int newWidth = paddle.getWidth() + expandAmount;
        if (newWidth > 300) newWidth = 300;
        paddle.setWidth(newWidth);
        active = true;
    }
}
