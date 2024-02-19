import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Optional;

public class Tile {
    public boolean hit = false;
    public boolean is_held = false;
    public boolean is_flagged = false;
    public boolean is_hidden = true;
    public boolean is_mine;
    public Optional<Integer> mine_neighbours;

    public Tile(boolean is_mine){
        this.is_mine = is_mine;
    }

    public void ResetTile(){
           this.hit = false;
           this.is_held = false;
           this.is_flagged = false;
           this.is_hidden = true;
           this.is_mine = false;
           this.mine_neighbours = Optional.empty();
    }
    public void draw(Graphics2D g2d, int i, int j, DrawInfo draw_info){
        int position_x = i * 30;
        int position_y = j * 30;

        if (is_hidden && is_flagged){
            DrawTools.draw_flagged(g2d, position_x, position_y, draw_info.flag_sprite, false);
        } else if (is_hidden && is_held) {
            DrawTools.draw_revealed(g2d, position_x, position_y);
        } else if (is_hidden) {
            DrawTools.draw_hidden(g2d, position_x, position_y);
        } else if (is_flagged && !is_mine){ //this case should only happen at the very end when we force reveal tiles
            DrawTools.draw_flagged(g2d, position_x, position_y, draw_info.flag_sprite, true);
        } else if (is_mine) {
            DrawTools.draw_mine(g2d, position_x, position_y, draw_info.mine_sprite, hit);
        } else if (mine_neighbours.isPresent() && mine_neighbours.get()>0) {
            DrawTools.draw_number(g2d, position_x, position_y, mine_neighbours.get());
        } else {
            DrawTools.draw_revealed(g2d, position_x, position_y);
        }
    }
}
