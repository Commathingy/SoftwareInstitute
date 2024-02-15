import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Optional;

public class Tile {

    public boolean is_flagged = false;
    public boolean is_hidden = true;
    public boolean is_mine;
    public Optional<Integer> mine_neighbours;

    public Tile(boolean is_mine){
        this.is_mine = is_mine;
    }

    public char AsciiDisplay(){
        if (is_flagged) {return 'F';}
        if (is_hidden) {return '#';}
        if (is_mine) {return 'M';}
        //mine neighbours should never be above 8
        //and we should be in the case where the tile has been revealed and assigned a value
        //or it was a mine
        return (char) (mine_neighbours.get().intValue() + '0');
    }

    public void draw(Graphics2D g2d, int i, int j){
        float position_x = (float) i * 30.0f;
        float position_y = (float) j * 30.0f;
        Rectangle2D.Float light_rect = new Rectangle2D.Float(position_x, position_y, 30.0f, 30.0f);
        Rectangle2D.Float dark_rect = new Rectangle2D.Float(position_x + 2.0f, position_y + 2.0f, 28.0f, 28.0f);
        Rectangle2D.Float main_rect = new Rectangle2D.Float(position_x + 2.0f, position_y + 2.0f, 26.0f, 26.0f);

        g2d.setColor(new Color(200, 200, 200));
        g2d.fill(light_rect);
        g2d.setColor(new Color(50, 50, 50));
        g2d.fill(dark_rect);
        g2d.setColor(new Color(100, 100, 100));
        g2d.fill(main_rect);
    }
}
