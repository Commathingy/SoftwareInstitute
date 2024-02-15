public class Tile {
    public boolean is_hidden = true;
    public boolean is_mine;
    public int mine_neighbours;

    public Tile(boolean is_mine){
        this.is_mine = is_mine;
    }

    public char AsciiDisplay(){
        if (is_hidden) {return '#';}
        if (is_mine) {return 'M';}
        //mine neighbours should never be above 8
        return (char) mine_neighbours;
    }
}
