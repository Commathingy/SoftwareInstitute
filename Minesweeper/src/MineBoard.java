
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Optional;

public class MineBoard extends JComponent {
    private ArrayList<Tile> tiles;
    private final int width;
    private final int height;
    private final int num_mines;

    private Optional<Integer> last_held = Optional.empty();


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


    private int[] ScreenToTile(MouseEvent e){
        //todo: account for top bar
        int i = e.getX() / 30;
        int j = e.getY() / 30;
        return new int[]{i, j};
    }

    private void ReleaseMouse(MouseEvent e){
        if (last_held.isPresent()){
            //stop pressing the last held
            tiles.get(last_held.get()).is_held = false;

            //get position of the release
            int[] pos = ScreenToTile(e);
            int index = pos[0] + pos[1] * width;

            //check if release is in same place as press
            if (index == last_held.get()){
                //if so do the reveal logic
                Optional<Tile> tile = TryAccess(pos[0], pos[1]);
                if (tile.isPresent() && tile.get().is_hidden && !tile.get().is_flagged) {
                    RevealTile(pos[0], pos[1]);
                }
            }
        }
        repaint();
    }
    private void PressMouse(MouseEvent e){
        int[] pos = ScreenToTile(e);
        last_held = Optional.of(pos[0] + pos[1] * width);
        tiles.get(last_held.get()).is_held = true;
        repaint();
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
        //TODO: do cascading
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

        //draw the tiles
        for (int j = 0; j<height; j++){
            for (int i = 0; i<width; i++){
                //should be present since i and j are in valid range
                TryAccess(i, j).get().draw(g2d, i, j);
            }
        }
    }
}
