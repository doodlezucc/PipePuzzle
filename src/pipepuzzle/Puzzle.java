package pipepuzzle;

import pipepuzzle.tiles.EndTile;
import pipepuzzle.tiles.StartTile;
import pipepuzzle.tiles.Tile;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class Puzzle {
    private Tile[][] tiles;
    private Random r = new Random();
    private Point start;
    private ArrayList<Integer> waterPath = new ArrayList<>();
    private int traveled = -100;
    private double speed = 1.0;
    private GameState state;
    private int lvlScoreStart = 0;
    private int score = 0;

    public GameState getState() {
        return state;
    }

    public static class GameState {
        String name;
        boolean paused;
        boolean interactive;

        GameState(String name, boolean paused, boolean interactive) {
            this.name = name;
            this.paused = paused;
            this.interactive = interactive;
        }

        public static final GameState PAUSED = new GameState("Paused",true,false);
        public static final GameState PLAYING = new GameState("Playing",false,true);
        public static final GameState GAME_OVER = new GameState("Game over",true,false);
        public static final GameState WINNING = new GameState("Winning",false,false);
    }

    public Puzzle(int width, int height) {
        tiles = new Tile[width][height];
        randomize();
    }

    public int getScore() {
        return score + lvlScoreStart;
    }

    public void randomize() {
        traveled = (int) (-100 * speed);
        if (state == GameState.GAME_OVER) {
            speed = 1;
            lvlScoreStart = 0;
        } else {
            speed += 0.1;
            lvlScoreStart = getScore();
        }
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                replaceRandom(x, y, false);
            }
        }

        score = 0;
        start = buildStartEndRandom();
        recalculateWaterPath();
        state = GameState.PLAYING;
    }

    public Point2D.Float[] getCleanArray() {
        ArrayList<Point2D.Float> path = new ArrayList<>();
        Point2D.Float p1 = new Point2D.Float(start.x, start.y);
        int i = 0;
        score = 0;
        for (int i1 = 0; i1 < waterPath.size(); i1++) {
            int w = waterPath.get(i1);
            int subd = 70;
            if (i1 < waterPath.size() - 1) {
                Point2D.Float p2 = plusDirection(p1, w, 1);
                for (int j = 0; j < subd; j++) {
                    if (j == subd / 2) {
                        score += 5;
                    }
                    if (i >= traveled) {
                        return path.toArray(new Point2D.Float[0]);
                    }
                    path.add(plusDirection(p1, w, j / (float) subd));
                    i++;
                }
                score += 5;
                p1 = p2;
            } else {
                for (int j = 0; j < subd / 2; j++) {
                    if (i >= traveled) {
                        return path.toArray(new Point2D.Float[0]);
                    }
                    path.add(plusDirection(p1, w, j / (float) subd));
                    i++;
                }
                if (w == 5) {
                    System.out.println("tädää");
                    score += 100;
                    randomize();
                    break;
                } else if (state != GameState.GAME_OVER) {
                    state = GameState.GAME_OVER;
                    try {
                        addFileEntry(false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("FUCK YOU");
                }
            }
        }
        return path.toArray(new Point2D.Float[0]);
    }

    public void addFileEntry(boolean closecall) throws IOException {
        String username = System.getProperty("user.name");
        System.out.println(username);
        Path p = Paths.get(".meta");
        if (!Files.exists(p)) {
            Files.createFile(p);
            Files.setAttribute(p,"dos:hidden", true, LinkOption.NOFOLLOW_LINKS);
        }

        List<String> lines = Files.readAllLines(p);
        for (String s : lines) {
            System.out.println(Obfuscate.unobfuscate(s));
        }
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        lines.add(Obfuscate.obfuscate(username + "#" + getScore() + "#" + now + "#" + closecall));
        Files.write(p, lines);
    }

    public void recalculateWaterPath() {
        waterPath.clear();
        Point p = start;
        int direction = ((StartTile)tiles[start.x][start.y]).getDirection();
        while (direction >= 0) {
            waterPath.add(direction);
            p = plusDirection(p, direction);
            Tile t = getTile(p.x, p.y);
            if (t == null) {
                direction = -1;
            } else if (t instanceof EndTile && ((EndTile) t).getDirection() == (direction + 2) % 4) {
                System.out.println("yass");
                state = GameState.WINNING;
                waterPath.add(5);
                break;
            } else {
                direction = t.findOutput((direction + 2) % 4);
            }
        }
    }

    public void togglePaused() {
        if (state == GameState.PLAYING) {
            state = GameState.PAUSED;
        } else if (state == GameState.PAUSED) {
            state = GameState.PLAYING;
        }
    }

    private static Point2D.Float plusDirection(Point2D.Float p, int direction, float d) {
        return new Point2D.Float(p.x + (direction == 1 ? 1 : (direction == 3 ? -1 : 0)) * d,
                p.y + (direction == 0 ? 1 : (direction == 2 ? -1 : 0)) * d);
    }

    private static Point plusDirection(Point p, int direction) {
        return new Point(p.x + (direction == 1 ? 1 : (direction == 3 ? -1 : 0)),
                p.y + (direction == 0 ? 1 : (direction == 2 ? -1 : 0)));
    }

    private Tile getTile(int x, int y) {
        if (x >= 0 && y >= 0 && x < getWidth() && y < getHeight()) {
            return tiles[x][y];
        }
        return null;
    }

    public Point buildStartEndNotRandom() {
        int x0 = 4;
        int y0 = 4;
        tiles[x0][y0] = new StartTile(1);
        int x1 = 5;
        int y1 = 4;
        tiles[x1][y1] = new EndTile(3);
        return new Point(x0, y0);
    }

    public Point buildStartEndRandom() {
        int x0 = r.nextInt(getWidth() - 2) + 1;
        int y0 = r.nextInt(getHeight() - 2) + 1;
        tiles[x0][y0] = new StartTile(r.nextInt(4));
        int x1 = r.nextInt(getWidth() - 2) + 1;
        int y1 = r.nextInt(getHeight() - 2) + 1;
        while (Math.abs(x0 - x1) < 2) {
            x1 = r.nextInt(getWidth() - 2) + 1;
        }
        while (Math.abs(y0 - y1) < 2) {
            y1 = r.nextInt(getHeight() - 2) + 1;
        }
        tiles[x1][y1] = new EndTile(r.nextInt(4));
        return new Point(x0, y0);
    }

    private Tile rndTile() {
        int rnd = r.nextInt(7);
        switch (rnd) {
            case 0: return new Tile(true,true,true,true);
            case 1: return new Tile(true,false,true,false);
            case 2: return new Tile(false,true,false,true);
            case 3: return new Tile(true,true,false,false);
            case 4: return new Tile(false,true,true,false);
            case 5: return new Tile(false,false,true,true);
            case 6: return new Tile(true,false,false,true);
        }
        return new Tile(false,false,false,false);
    }

    void nextStep() {
        if (!state.paused) {
            traveled += state == GameState.WINNING ? 15 : speed;
        }
    }

    Tile replaceRandom(int x, int y, boolean b) {
        Tile old = tiles[x][y];
        if (b && (old instanceof StartTile || old instanceof EndTile)) {
            return old;
        }
        Tile n = rndTile();
        while (n.equals(old)) {
            n = rndTile();
        }
        tiles[x][y] = n;
        return n;
    }

    public Tile[][] getTiles() {
        return tiles;
    }

    public int getWidth() {
        return tiles.length;
    }

    public int getHeight() {
        return tiles[0].length;
    }
}
