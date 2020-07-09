package pipepuzzle.tiles;

public class EndTile extends Tile {
    private int direction;

    public int getDirection() {
        return direction;
    }

    public EndTile(int direction) {
        super(direction);
        this.direction = direction;
    }
}
