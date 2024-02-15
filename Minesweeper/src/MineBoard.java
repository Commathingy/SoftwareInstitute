
import java.util.ArrayList;
import java.util.Optional;

public class MineBoard {
    private ArrayList<Tile> tiles;
    private int width;
    private int height;
    private int num_mines;


    private void ResetTiles(){
        tiles.clear();

        //spawn mines using reservoir method
        int left_to_spawn = num_mines;

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
        //j is a row, i is a column
        for (int j = 0; j<height; j++){
            for (int i = 0; i<width; i++){
                builder.append(tiles.get(width * j + i).AsciiDisplay());
            }
            builder.append('\n');
        }
        return builder.toString();
    }

    public Optional<Tile> TryAccess(int i, int j){
        if (!isValidCoord(i, j)) {
            return Optional.empty();
        } else {
            return Optional.of(tiles.get(width * j + i));
        }
    }

    public boolean isValidCoord(int i, int j){
        return (i>=0 && i<width && j>=0 && j<height);
    }

    public void RevealTile(int i, int j){
        //should be valid since we check earlier that it is valid
        Tile tile = TryAccess(i,j).get();
        tile.is_hidden = false;
        int mine_neighbours = 0;
        if (!tile.is_mine){
            for (int delj = -1; delj<2; delj++){
                for (int deli = -1; deli<2; deli++){
                    Optional<Tile> neighbour = TryAccess(i+deli, j+delj);
                    if (neighbour.isPresent()){
                        mine_neighbours += neighbour.get().is_mine ? 1 : 0;
                    }
                }
            }
            tile.mine_neighbours = Optional.of(mine_neighbours);
        }
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
