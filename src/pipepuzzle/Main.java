package pipepuzzle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            JFrame jFrame = new JFrame("Pipe Puzzle");
            Puzzle p = new Puzzle(8,8);
            PuzzleComponent pc = new PuzzleComponent(p);
            jFrame.add(pc);
            jFrame.addMouseListener(pc.getMouseListener());
            jFrame.addKeyListener(pc.getKeyListener());
            jFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    System.out.println("gotcha bitch");
                    try {
                        p.addFileEntry(true);
                        System.exit(420);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            jFrame.setSize(p.getWidth() * pc.getTileSize() + 16, p.getHeight() * pc.getTileSize() + 39);
            jFrame.setResizable(false);
            jFrame.setVisible(true);
        } else {
            System.out.println("I'm " + args[0] + "ing over here!");
            Path p = Paths.get(".meta");
            List<String> lines = Files.readAllLines(p);
            for (String s : lines) {
                System.out.println(Obfuscate.unobfuscate(s));
            }
        }
    }
}
