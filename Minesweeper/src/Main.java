import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    //todo: add number next to sliders
    //todo: settings button and reset button sprites

    public static MineBoard InputOptions(){
        //dialog to get dimensions
        JSlider width = new JSlider(10, 50, 20);
        JSlider height = new JSlider(5, 30, 10);
        JSlider mines = new JSlider(5, 40, 20);

        final int[] chosen_width = {20};
        final int[] chosen_height = {10};
        final int[] chosen_mines = {20};

        width.setMajorTickSpacing(5);
        width.setMinorTickSpacing(1);
        width.setSnapToTicks(true);
        width.setPaintTicks(true);
        width.setPaintLabels(true);
        width.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                chosen_width[0] = width.getValue();
            }
        });

        height.setMajorTickSpacing(5);
        height.setMinorTickSpacing(1);
        height.setSnapToTicks(true);
        height.setPaintTicks(true);
        height.setPaintLabels(true);
        height.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                chosen_height[0] = height.getValue();
            }
        });

        mines.setMajorTickSpacing(5);
        mines.setMinorTickSpacing(1);
        mines.setSnapToTicks(true);
        mines.setPaintTicks(true);
        mines.setPaintLabels(true);
        mines.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                chosen_mines[0] = mines.getValue();
            }
        });

        Object[] fields = {"Width", width, "Height", height, "Mine %", mines};
        JOptionPane.showConfirmDialog(null, fields, "Please choose your desired settings", JOptionPane.DEFAULT_OPTION);

        return new MineBoard(chosen_width[0], chosen_height[0], chosen_mines[0]*chosen_width[0]*chosen_height[0] / 100);
    }

    public static void run_once(JFrame frame){
        //clear any previous stuff
        frame.getContentPane().removeAll();



        //create the board
        MineBoard board = InputOptions();

        //set up the frame
        frame.setSize(board.width*30 + 66, 169 + board.height*30);
        frame.add(board);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setTitle("Minesweeper");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        run_once(frame);
    }
}