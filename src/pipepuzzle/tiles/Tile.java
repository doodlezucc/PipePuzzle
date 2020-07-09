package pipepuzzle.tiles;

import java.awt.*;
import java.util.Arrays;

public class Tile {
    private boolean[] open;
    private boolean isFinal = false;

    public Tile(boolean up, boolean right, boolean down, boolean left) {
        open = new boolean[] {
                up,
                right,
                down,
                left
        };
    }

    protected Tile(int direction) {
        this(false,false,false,false);
        open[direction] = true;
    }

    public int findOutput(int localIn) {
        if (!open[localIn]) {
            return -1;
        }
        if (open[(localIn + 2) % 4]) {
            return (localIn + 2) % 4;
        }
        for (int x = 0; x < 4; x++) {
            if (x != localIn && open[x]) {
                return x;
            }
        }
        return -1;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public void setFinal(boolean v) {
        isFinal = v;
    }

    public int getOpenCount() {
        return boolToInt(open[0]) + boolToInt(open[1]) + boolToInt(open[2]) + boolToInt(open[3]);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Tile && Arrays.equals(((Tile) obj).open, this.open);
    }

    private int boolToInt(boolean b) {
        return b ? 1 : 0;
    }

    public boolean isCompletelySolid() {
        return !open[0] && !open[1] && !open[2] && !open[3];
    }

    public boolean[] getOpen() {
        return open;
    }
}
