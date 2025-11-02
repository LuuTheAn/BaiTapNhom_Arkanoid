package Game;

import java.awt.Graphics2D;

public class PlayingState implements GameState {
    @Override
    public void update(GameManager gm) {
        // Cập nhật khi đang chơi
        gm.updateGamePlay(); // tí nữa chị nói cách tạo hàm này
    }

    @Override
    public void render(Graphics2D g, GameManager gm) {
        gm.renderGamePlay(g);
    }
}
