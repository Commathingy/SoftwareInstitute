import javax.swing.*;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        MineBoard board = new MineBoard();


        //set up the JFrame (window)
        JFrame frame = new JFrame();
        frame.setSize(800, 600);
        frame.setTitle("Minisweeper");
        //TODO; is this necessary?
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        //add the board to the window
        frame.add(board);

    }
}