package Game;

import java.awt.Graphics2D;

public class WinState implements GameState {
    @Override
    public void update(GameManager gm) {
        if (gm.isRestartPressed()) {
            gm.resetGame();
            gm.setState(new MenuState());
        }
    }

    @Override
    public void render(Graphics2D g, GameManager gm) {
        g.drawString("YOU WIN! Press R to play again", 280, 300);
    }
}
