package pipepuzzle.tiles;

public class StartTile extends Tile {
    private int direction;

    public int getDirection() {
        return direction;
    }

    public StartTile(int direction) {
        super(direction);
        this.direction = direction;
    }
}
