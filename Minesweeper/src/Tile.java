import java.util.Optional;

public class Tile {
    public boolean is_hidden = true;
    public boolean is_mine;
    public Optional<Integer> mine_neighbours;

    public Tile(boolean is_mine){
        this.is_mine = is_mine;
    }

    public char AsciiDisplay(){
        if (is_hidden) {return '#';}
        if (is_mine) {return 'M';}
        //mine neighbours should never be above 8
        //and we should be in the case where the tile has been revealed and assigned a value
        //or it was a mine
        return (char) mine_neighbours.get().intValue();
    }
}
