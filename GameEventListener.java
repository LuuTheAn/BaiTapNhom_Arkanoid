package Game;

import entity.Brick;

public interface GameEventListener {
    void onBrickDestroyed(Brick brick);
}
