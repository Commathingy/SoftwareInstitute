import java.util.Optional;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        MineBoard board = new MineBoard();
        boolean is_running = true;
        System.out.println(board.DisplayBoard());

        Scanner scanner = new Scanner(System.in);

        while (is_running){

            int i;
            int j;

            //get input and check its a valid coordinate form
            while (true) {
                try {
                    String[] parts = scanner.next("(\\d+,\\d+)").split(",");
                    i = Integer.parseInt(parts[0]);
                    j = Integer.parseInt(parts[1]);
                    break;
                } catch (Exception ignored) {
                    System.out.println("Not a valid coordinate - should be of form: integer,integer");
                    continue;
                }
            }

            Optional<Tile> op_tile = board.TryAccess(i, j);


            //check for bad tile, flagged tile, already revealed tile
            if (op_tile.isEmpty()){
                System.out.println("That coordinate is out of bounds");
                continue;
            }
            Tile tile = op_tile.get();
            if (tile.is_flagged){
                System.out.println("That tile is flagged");
                continue;
            }
            if (!tile.is_hidden){
                System.out.println("That tile is already revealed");
                continue;
            }

            board.RevealTile(i, j);





            //reveal tile and do cascading
            //cascade on mine or a 0

            //display new board
            System.out.println(board.DisplayBoard());
        }

    }
}