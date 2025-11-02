package Game;

import java.awt.Graphics2D;

public class MenuState implements GameState {
    @Override
    public void update(GameManager gm) {
        // Nhận input để bắt đầu game
        // Ví dụ: nếu nhấn phím SPACE thì chuyển sang PlayingState
        if (gm.isStartPressed()) {
            gm.setState(new PlayingState());
        }
    }

    @Override
    public void render(Graphics2D g, GameManager gm) {
        g.drawString("Press SPACE to start!", 300, 300);
    }
}
