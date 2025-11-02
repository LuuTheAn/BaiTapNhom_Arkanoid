package Game;

import java.awt.Graphics2D;

public class GameOverState implements GameState {
    @Override
    public void update(GameManager gm) {
        if (gm.isRestartPressed()) {
            gm.resetGame();
            gm.setState(new PlayingState());
        }
    }

    @Override
    public void render(Graphics2D g, GameManager gm) {
        g.drawString("GAME OVER - Press R to restart", 280, 300);
    }
}
