
import java.util.ArrayList;

public class MineBoard {
    private ArrayList<Tile> tiles;
    private int width;
    private int height;
    private int num_mines;


    private void ResetTiles(){
        tiles.clear();

        //spawn mines using reservoir method
        int left_to_spawn = num_mines;

        //also store where we added mines so we can easily set the number of neighbours for each Tile afterwards
        ArrayList<Integer> mine_positions = new ArrayList<>(num_mines);

        for (int i=width*height; i>0; i--){
            //chance to spawn mine tile is left_to_spawn/i
            //since i = number of tiles left to create
            //only have is_mine true if left_to_spawn is actually above 0 still
            boolean is_mine = Math.random() < ((float) left_to_spawn / (float) i) || (left_to_spawn > 0);
            left_to_spawn -= is_mine ? 1 : 0;
            tiles.add(new Tile(is_mine));

        }

        //Todo: proper error
        if (left_to_spawn > 0) {throw new RuntimeException();}
    }

    public String DisplayBoard(){
        StringBuilder builder = new StringBuilder(width*height);
        //i is a row, j is a column
        for (int i = 0; i<height; i++){
            for (int j = 0; j<width; j++){
                builder.append(tiles.get(width * i + j).AsciiDisplay());
            }
            builder.append('\n');
        }
        return builder.toString();
    }


    public MineBoard() {
        this(20, 10, 20);
    }

    public MineBoard(int width, int height, int num_mines) {
        this.num_mines = num_mines;
        this.width = width;
        this.height = height;
        tiles = new ArrayList<>(width*height);
        this.ResetTiles();
    }
}
