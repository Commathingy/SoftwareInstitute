
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Optional;

public class MineBoard extends JComponent {
    private ArrayList<Tile> tiles;
    private int width;
    private int window_width;
    private int height;
    private int num_mines;
    private int unflagged;

    private boolean reset_held = false;

    private Optional<BoardCoord> last_held = Optional.empty();

    private void ResetTiles(){
        unflagged = num_mines;
        tiles.clear();

        //spawn mines using reservoir method
        int left_to_spawn = num_mines;

        for (int i=width*height; i>0; i--){
            //chance to spawn mine tile is left_to_spawn/i
            //since i = number of tiles left to create
            //only have is_mine true if left_to_spawn is actually above 0 still
            boolean is_mine = Math.random() < ((float) left_to_spawn / (float) i) && (left_to_spawn > 0);
            left_to_spawn -= is_mine ? 1 : 0;
            tiles.add(new Tile(is_mine));
        }


        //Todo: proper error
        if (left_to_spawn > 0) {throw new RuntimeException();}
    }


    private BoardCoord ScreenToTile(MouseEvent e){
        int i = e.getX() / 30;
        int j = (e.getY() / 30) - 2;
        return BoardCoord.FromCoord(i,j);
    }

    private boolean IsReset(MouseEvent e){
        int x = e.getX();
        int y = e.getY();

        return y>15 && y<45 && x<window_width/2 + 15 && x>window_width/2 - 15;
    }

    private void ReleaseMouse(MouseEvent e){

        if (!(e.getButton() == MouseEvent.BUTTON1)){
            //we don't ever care about releasing a m2
            return;
        }

        if (IsReset(e) && reset_held) {
            reset_held = false;
            ResetTiles();
        }

        if (last_held.isPresent()){
            //stop pressing the last held
            tiles.get(last_held.get().ToIndex(width)).is_held = false;

            //get position of the release

            BoardCoord pos = ScreenToTile(e);

            //check if release is in same place as press
            if (pos.ToIndex(width) == last_held.get().ToIndex(width)){
                //if so do the reveal logic
                StartCascade(pos);
            }
            last_held = Optional.empty();
        }
        repaint();
    }
    private void PressMouse(MouseEvent e){

        //in this case we pressed the reset button
        if (e.getButton() == MouseEvent.BUTTON1) {
            if (IsReset(e)) {
                reset_held = true;
            }
        }


        BoardCoord pos = ScreenToTile(e);

        //check for left or right click
        if (e.getButton() == MouseEvent.BUTTON1){
            Optional<Tile> tile = TryAccess(pos);
            if (tile.isPresent()){
                last_held = Optional.of(pos);
                tile.get().is_held = true;
            }
        } else if (e.getButton() == MouseEvent.BUTTON3 && last_held.isEmpty()){
            //we check for empty last held since m1 has precedence over m2
            Optional<Tile> op_tile = TryAccess(pos);
            //check if the tile is still hidden
            if (op_tile.isPresent() && op_tile.get().is_hidden){
                //flip the flagged status
                op_tile.get().is_flagged ^= true;
                unflagged -= op_tile.get().is_flagged ? 1 : -1;
            }
        }

        repaint();
    }

    public Optional<Tile> TryAccess(BoardCoord pos){
        if (!isValidCoord(pos)) {
            return Optional.empty();
        } else {
            return Optional.of(tiles.get(width * pos.j + pos.i));
        }
    }

    public boolean isValidCoord(BoardCoord pos){
        return (pos.i>=0 && pos.i<width && pos.j>=0 && pos.j<height);
    }

    private void HitMine(){
        int i = 0;
        for (Tile tile : tiles){
            tile.is_hidden = false;

            int mine_neighbours = 0;
            BoardCoord coord = BoardCoord.FromIndex(i, width);
            for (int delj = -1; delj < 2; delj++) {
                for (int deli = -1; deli < 2; deli++) {

                    Optional<Tile> neighbour = TryAccess(BoardCoord.FromCoord(coord.i + deli, coord.j + delj));
                    if (neighbour.isPresent()) {
                        mine_neighbours += neighbour.get().is_mine ? 1 : 0;
                    }
                }
            }
            tile.mine_neighbours = Optional.of(mine_neighbours);
            i++;
        }
    }

    private void StartCascade(BoardCoord pos){
        Optional<Tile> tile = TryAccess(pos);
        //we start a cascade if the tile is hidden and a 0
        //or if it is revealed, and the number of flags around it is equal to the number of mines around it
        if (tile.isPresent() && !tile.get().is_flagged){
            if (tile.get().is_hidden){
                CascadeFrom(pos);
                return;
            }
            //calculate number of flags around
            int flag_neighbours = 0;
            for (int delj = -1; delj<2; delj++){
                for (int deli = -1; deli<2; deli++){
                    Optional<Tile> neighbour = TryAccess(BoardCoord.FromCoord(pos.i+deli, pos.j+delj));
                    if (neighbour.isPresent()){
                        flag_neighbours += neighbour.get().is_flagged ? 1 : 0;
                    }
                }
            }
            if (tile.get().mine_neighbours.isPresent() && flag_neighbours >= tile.get().mine_neighbours.get()){
                for (int deli = -1; deli < 2; deli++) {
                    for (int delj = -1; delj < 2; delj++) {
                        if (deli == 0 && delj == 0) {
                            continue;
                        }
                        CascadeFrom(BoardCoord.FromCoord(pos.i + deli, pos.j + delj));
                    }
                }
            }
        }
    }

    private void CascadeFrom(BoardCoord pos){
        Optional<Tile> op_tile = TryAccess(pos);
        if (op_tile.isPresent() && !op_tile.get().is_flagged && op_tile.get().is_hidden){
            Tile tile = op_tile.get();

            if (tile.is_mine){
                tile.hit = true;
                HitMine();
                return;
            }

            tile.is_hidden = false;
            //first check the number of neighbouring mines
            int mine_neighbours = 0;
            for (int delj = -1; delj < 2; delj++) {
                for (int deli = -1; deli < 2; deli++) {
                    Optional<Tile> neighbour = TryAccess(BoardCoord.FromCoord(pos.i + deli, pos.j + delj));
                    if (neighbour.isPresent()) {
                        mine_neighbours += neighbour.get().is_mine ? 1 : 0;
                    }
                }
            }
            tile.mine_neighbours = Optional.of(mine_neighbours);
            if (mine_neighbours == 0) {
                //now cascade is there were no mine neighbours
                for (int deli = -1; deli < 2; deli++) {
                    for (int delj = -1; delj < 2; delj++) {
                        if (deli == 0 && delj == 0) {
                            continue;
                        }
                        CascadeFrom(BoardCoord.FromCoord(pos.i + deli, pos.j + delj));
                    }
                }
            }
        }
    }

    public MineBoard() {
        this(20, 10, 20);
    }

    public MineBoard(int width, int height, int num_mines) {
        this.num_mines = num_mines;
        this.width = width;
        //todo: for small boards change
        this.window_width = 30*width;
        this.height = height;
        this.unflagged = num_mines;
        tiles = new ArrayList<>(width*height);
        this.ResetTiles();

        //add the mouse listener
        addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {}
            public void mousePressed(MouseEvent e) {PressMouse(e);}
            public void mouseReleased(MouseEvent e) {ReleaseMouse(e);}
            public void mouseEntered(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {}
        });
    }

    protected void paintComponent(Graphics g){
        Graphics2D g2d = (Graphics2D) g;

        //draw the top bar
        //TODO
        Ellipse2D.Float reset_button = new Ellipse2D.Float(window_width/2 - 15, 15, 30, 30);
        g2d.setColor(Color.YELLOW);
        g2d.fill(reset_button);

        //Todo: change this font to a better one
        g2d.setFont(new Font("Serif", Font.ITALIC, 36));

        //translate downwards to start drawing tiles
        g2d.translate(0, 60);
        //draw the tiles
        for (int j = 0; j<height; j++){
            for (int i = 0; i<width; i++){
                //should be present since i and j are in valid range
                TryAccess(BoardCoord.FromCoord(i, j)).get().draw(g2d, i, j);
            }
        }
    }
}


class BoardCoord{
    public int i;
    public int j;

    public int ToIndex(int width){
        return i + j * width;
    }

    private BoardCoord(int i, int j){
        this.i = i;
        this.j = j;
    }

    public static BoardCoord FromCoord(int i, int j){
        return new BoardCoord(i,j);
    }

    public static BoardCoord FromIndex(int index, int width){
        int i = index % width;
        int j = index / width;
        return new BoardCoord(i,j);
    }

}
