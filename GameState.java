package Game;

import java.awt.Graphics2D;

public interface GameState {
    void update(GameManager gm);
    void render(Graphics2D g, GameManager gm);
}
