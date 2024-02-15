
import java.util.ArrayList;

public class MineBoard {
    private ArrayList<Tile> tiles;
    private int width;
    private int height;

    private int num_mines;


    private void ResetTiles(){

    }

    public MineBoard() {
        this(20, 10, 20);
    }

    public MineBoard(int width, int height, int num_mines) {
        this.num_mines = num_mines;
        this.width = width;
        this.height = height;
        tiles = new ArrayList<>(width*height);

        //spawn mines using reservoir method
        int left_to_spawn = num_mines;

        for (int i=width*height-1; i>=0; i--){
            //chance to spawn mine tile is left_to_spawn/i
            //since i = number of tiles left to create
            //only have is_mine true if left_to_spawn is actually above 0 still
            boolean is_mine = Math.random() < ((float) left_to_spawn / (float) i) || (left_to_spawn > 0);
            left_to_spawn -= is_mine ? 1 : 0;
            tiles.set(i, new Tile(is_mine));
        }

        //Todo: proper error
        if (left_to_spawn > 0) {throw new RuntimeException();}
    }
}
