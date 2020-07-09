package pipepuzzle;

import pipepuzzle.tiles.EndTile;
import pipepuzzle.tiles.StartTile;
import pipepuzzle.tiles.Tile;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;

public class PuzzleComponent extends JComponent {
    private Puzzle puzzle;
    private int tileSize = 50;
    private Timer timer;
    private int scoreBuffered = 0;

    public PuzzleComponent(Puzzle puzzle) {
        this.puzzle = puzzle;
        timer = new Timer(50, ae -> {
//                if (puzzle.isGameOver) {
//                    timer.stop();
//                    return;
//                }
            scoreBuffered = puzzle.getScore();
            puzzle.nextStep();

            repaint();
        });
        timer.start();
    }

    public int getTileSize() {
        return tileSize;
    }

    private int getPipeSize() {
        return (int) (tileSize * 0.3);
    }

    MouseAdapter getMouseListener() {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (puzzle.getState().interactive) {
                    int x = e.getX() - 16;
                    int y = puzzle.getHeight() * tileSize - e.getY() + tileSize / 2;

                    puzzle.replaceRandom(x / tileSize, y / tileSize, true);
                    puzzle.recalculateWaterPath();
                    repaint();
                } else if (puzzle.getState() == Puzzle.GameState.GAME_OVER) {
                    puzzle.randomize();
                }
            }
        };
    }

    @Override
    public void paint(Graphics g) {
        for (int x = 0; x < puzzle.getWidth(); x++) {
            for (int y = 0; y < puzzle.getHeight(); y++) {
                paintTile(g, puzzle.getTiles()[x][y], x, puzzle.getHeight() - y - 1);
            }
        }
        g.setColor(new Color(30,70,200));
        for (Point2D.Float p : puzzle.getCleanArray()) {
            g.fillOval(Math.round(p.x * tileSize + (tileSize - getPipeSize()) / 2.0f) - 1,
                    Math.round((puzzle.getHeight() - p.y - 1) * tileSize + (tileSize - getPipeSize()) / 2.0f) - 1,
                    getPipeSize() + 1,
                    getPipeSize() + 1);
        }

        if (puzzle.getState() == Puzzle.GameState.GAME_OVER) {
            g.setColor(new Color(230,200,60,100));
            g.fillRect(0, 0, getWidth() * tileSize, getHeight() * tileSize);
            try {
                Image gameover = ImageIO.read(Main.class.getResource("gameover.png"));
                g.drawImage(gameover, -30, 80, 250, 250, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (puzzle.getState() == Puzzle.GameState.PAUSED) {
            g.setColor(new Color(0,0,0,50));
            g.fillRect(0, 0, getWidth() * tileSize, getHeight() * tileSize);
            g.setColor(Color.WHITE);
            g.setFont(g.getFont().deriveFont(Font.BOLD, 50));
            g.drawString("PAUSIERT", 75, 215);
        }

        int score = puzzle.getScore();
        g.setColor(new Color(230, 200, 100));
        g.setFont(g.getFont().deriveFont(Font.BOLD, 15));
        g.drawString("SCORE: " + score, 5, 20);
    }

    private void paintTile(Graphics g, Tile tile, int x, int y) {
        g.setColor(Color.GRAY);
        g.fillRect(x * tileSize,y * tileSize, tileSize, tileSize);
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(x * tileSize,y * tileSize, tileSize - 1, tileSize - 1);
        g.setColor(Color.DARK_GRAY);
        g.fillRect(x * tileSize + 1,y * tileSize + 1, tileSize - 1, tileSize - 1);
        g.setColor(Color.GRAY);
        g.fillRect(x * tileSize + 1,y * tileSize + 1, tileSize - 2, tileSize - 2);

        if (!tile.isCompletelySolid()) {
            int x0 = x * tileSize;
            int y0 = y * tileSize;
            int center = tileSize / 2;
            int pipeStart = center - getPipeSize() / 2;

            if (tile instanceof StartTile) {
                g.setColor(Color.WHITE);
                g.fillRect(x0, y0, tileSize, tileSize);
            } else if (tile instanceof EndTile) {
                g.setColor(Color.BLACK);
                g.fillRect(x0, y0, tileSize, tileSize);
            }

            g.setColor(Color.DARK_GRAY);

            g.fillOval(x0 + pipeStart, y0 + pipeStart, getPipeSize(), getPipeSize());

            boolean[] open = tile.getOpen();
            if (open[0]) {
                g.fillRect(x0 + pipeStart, y0, getPipeSize(), center);
            }
            if (open[1]) {
                g.fillRect(x0 + center, y0 + pipeStart, center, getPipeSize());
            }
            if (open[2]) {
                g.fillRect(x0 + pipeStart, y0 + center, getPipeSize(), center);
            }
            if (open[3]) {
                g.fillRect(x0, y0 + pipeStart, center, getPipeSize());
            }
        }
    }

    public KeyAdapter getKeyListener() {
        return new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE || e.getKeyCode() == KeyEvent.VK_SPACE) {
                    puzzle.togglePaused();
                }
            }
        };
    }
}
