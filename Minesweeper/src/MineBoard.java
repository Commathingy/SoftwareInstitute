
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Optional;

public class MineBoard extends JComponent {
    private ArrayList<Tile> tiles;
    private int width;
    private int window_width;
    private int height;
    private int num_mines;

    /**
     * The number of un-flagged mines, equal to (num_mines - no. flagged tiles)
     */
    private int unflagged;

    /**
     * Flag indicating if the current move is the first
     */
    private boolean first_flip = true;

    /**
     * stores the buffered images of the mine and flag
     */
    private final DrawInfo draw_info;
    
    /**
     * indicates whether the rest button is currently being held
     */
    private boolean reset_held = false;
    
    /**
     * The coordinate of the last pressed tile, if there is one
     */
    private Optional<BoardCoord> last_held = Optional.empty();


    /**
     * Create a standard size board, 20x10 with 20 mines
     */
    public MineBoard() {
        this(20, 10, 20);
    }

    /**
     * Create a board with a custom size and number of mines
     */
    public MineBoard(int width, int height, int num_mines) {
        this.draw_info = new DrawInfo();
        this.num_mines = num_mines;
        this.width = width;
        this.window_width = 30*width;
        this.height = height;
        this.unflagged = num_mines;
        tiles = new ArrayList<>(width*height);
        for (int i = 0; i<width*height; i++){
            tiles.add(new Tile(false));
        }
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

    /**
     * Moves the mines to be in different locations, but does not alter anything but the <code>is_mine</code>
     * property of each tile, so should only be used in situation where this is expected
     */
    private void ReplaceMines(){
        //spawn mines using reservoir method
        int left_to_spawn = num_mines;
        int i=width*height;
        for (Tile tile : tiles){
            //chance to spawn mine tile is left_to_spawn/i
            //since i = number of tiles left to create
            //only have is_mine true if left_to_spawn is actually above 0 still
            boolean is_mine = Math.random() < ((float) left_to_spawn / (float) i) && (left_to_spawn > 0);
            left_to_spawn -= is_mine ? 1 : 0;
            tile.is_mine = is_mine;
            i--;
        }

        if (left_to_spawn > 0) {throw new RuntimeException("Error: Failed to spawn all mines");}
    }

    /**
     * Resets the entire board, replacing the mines and resetting all relevant variables.
     */
    private void ResetTiles(){
        unflagged = num_mines;
        first_flip = true;
        for (Tile tile : tiles){
            tile.ResetTile();
        }
        ReplaceMines();
        repaint();
    }

    private void CheckGameEnd(){
        boolean game_won = true;
        for (Tile tile : tiles){
            if (tile.hit) {
                //hit a mine and so lost
                game_won = false;
            }
            if (tile.is_hidden && !tile.is_mine){
                //there is an uncleared non mine tile
                return;
            }
        }
        //if we won, flag all hidden tiles
        if (game_won){
            for (Tile tile : tiles) {
                if (tile.is_hidden){
                    tile.is_flagged = true;
                }
            }
        }

        String outcome = game_won? "You Won!" : "You Lost";
        this.paintComponent(getGraphics());
        int reply = JOptionPane.showConfirmDialog(null, new Object[]{"Try again?"}, outcome, JOptionPane.YES_NO_OPTION);
        if (reply == 0) {
            //yes was pressed, so reset

            ResetTiles();
        } else {
            SwingUtilities.getWindowAncestor(this).dispose();
        }
    }

    /**
     * Converts the coordinates of mouse event to a board coordinate.
     * <p>
     * If the events position was outside the board, this will result in an invalid <code>BoardCoord</code>
     * </p>
     * @param e The event containing position info
     * @return A <code>BoardCoord</code> that corresponds to this position
     */
    private BoardCoord ScreenToTile(MouseEvent e){
        int i = e.getX() / 30;
        int j = (e.getY() / 30) - 2;
        return BoardCoord.FromCoord(i,j);
    }

    private void ReleaseMouse(MouseEvent e){

        //check if button 1 was released
        if (!(e.getButton() == MouseEvent.BUTTON1)){
            //if not pressed just early return
            return;
        }

        //check for pressing reset
        if (e.getY()>15 && e.getY()<45 && e.getX()<window_width/2 + 15 && e.getX()>window_width/2 - 15 && reset_held) {
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
                //first check if this is first click and we should rearrange mines
                if (first_flip){
                    first_flip = false;
                    Tile tile = tiles.get(pos.ToIndex(width));
                    while (tile.is_mine){
                        ReplaceMines();
                    }
                }

                StartCascade(pos);
            }
            last_held = Optional.empty();

            //this should be the only point when we could have already won
            CheckGameEnd();
        }
        repaint();
    }
    private void PressMouse(MouseEvent e){

        //in this case we pressed the reset button
        if (e.getButton() == MouseEvent.BUTTON1) {
            if (e.getY()>15 && e.getY()<45 && e.getX()<window_width/2 + 15 && e.getX()>window_width/2 - 15) {
                reset_held = true;
                return;
            }
        }

        //check for pressing settings
        if (e.getY()>15 && e.getY()<45 && e.getX()<window_width - 20 && e.getX()>window_width - 50) {
            Component container = SwingUtilities.getWindowAncestor(this);
            container.setVisible(false);
            Main.run_once((JFrame) container);
            return;
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

    /**
     * Get the tile from the board with the given coordinate, if it is valid
     * @param pos The coordinate of the tile desired
     * @return An optional value that contains the tile if the coordinate was valid, otherwise empty
     */
    private Optional<Tile> TryAccess(BoardCoord pos){
        if (!isValidCoord(pos)) {
            return Optional.empty();
        } else {
            return Optional.of(tiles.get(width * pos.j + pos.i));
        }
    }

    /**
     * Returns true if the given coordinate is a valid coordinate on the board
     * @param pos The coordinate to check
     * @return <code>true</code> if coordinate is valid, <code>false</code> otherwise
     */
    private boolean isValidCoord(BoardCoord pos){
        return (pos.i>=0 && pos.i<width && pos.j>=0 && pos.j<height);
    }

    /**
     * Called when a tile with a mine is revealed by <code>CascadeFrom</code>.
     * Sets all tiles except flagged tiles to be revealed and calculates the appropriate value
     * for <code>mine_neighbours</code>
     */
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

    /**
     * Start a cascade from the current tile.
     * <p>
     * If a tile is hidden, start a cascade at the given position with <code>CascadeFrom</code>
     * If the tile is already revealed and the number on it is less than or equal to the number of adjacent flags, it
     * will call <code>CascadeFrom</code> on all the adjacent tiles
     * </p>
     *
     * @param pos The position at which the cascade is beginning
     */
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
            //if there are at least as many flags as the number, cascade
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

    /**
     * Cascading reveal of tiles on the board.
     * <p>
     * If the tile is hidden and not flagged, it will be revealed,
     * and if it is a 0, it will reveal all the tiles next to it via this method.
     * </p>
     *
     * @param pos The position of the tile the cascade is currently happening at
     */
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


    protected void paintComponent(Graphics g){
        Graphics2D g2d = (Graphics2D) g;

        //draw reset button
        Ellipse2D.Float reset_button = new Ellipse2D.Float(window_width/2 - 15, 15, 30, 30);
        g2d.setColor(Color.YELLOW);
        g2d.fill(reset_button);

        //draw settings button
        Ellipse2D.Float settings_button = new Ellipse2D.Float(window_width - 50, 15, 30, 30);
        g2d.setColor(Color.DARK_GRAY);
        g2d.fill(settings_button);

        //draw the mine counter
        g2d.setFont(new Font("SansSerif", Font.BOLD, 30));
        g2d.setColor(Color.BLACK);
        g2d.drawString(Integer.toString(unflagged), 55, 41);
        g2d.drawImage(draw_info.mine_sprite, 20, 20, null);

        //translate downwards to start drawing tiles
        g2d.translate(0, 60);
        //draw the tiles
        for (int j = 0; j<height; j++){
            for (int i = 0; i<width; i++){
                //should be present since i and j are in valid range
                TryAccess(BoardCoord.FromCoord(i, j)).get().draw(g2d, i, j, draw_info);
            }
        }
    }
}


class BoardCoord{
    public int i;
    public int j;

    /**
     * Converts this coordinate into the index in a list which corresponds to
     * this coordinate for a board with the given width
     * @param width The width of the board
     * @return The index in a 1D list that corresponds to this coordinate
     */
    public int ToIndex(int width){
        return i + j * width;
    }

    /**
     * @param i The horizontal position on the board
     * @param j The vertical position on the board
     */
    private BoardCoord(int i, int j){
        this.i = i;
        this.j = j;
    }

    /**
     * @param i The horizontal position on the board
     * @param j The vertical position on the board
     */
    public static BoardCoord FromCoord(int i, int j){
        return new BoardCoord(i,j);
    }

    /**
     * Converts from a index for a list to a 2D coordinate
     * @param index The index in the list for the tile
     * @param width The width of the board this coordinate corresponds to
     * @return The coordinate corresponding to the given index
     */
    public static BoardCoord FromIndex(int index, int width){
        int i = index % width;
        int j = index / width;
        return new BoardCoord(i,j);
    }

}
