import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Optional;

public class Tile {
    public boolean is_held = false;
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
        int position_x = i * 30;
        int position_y = j * 30;

        if (is_hidden && is_flagged){
            DrawShapes.draw_flagged(g2d, position_x, position_y);
        } else if (is_hidden && is_held) {
            DrawShapes.draw_depressed(g2d, position_x, position_y);
        } else if (is_hidden) {
            DrawShapes.draw_hidden(g2d, position_x, position_y);
        }
        //todo: addd the other cases (ones for revealed tiles)
    }
}


class DrawShapes{
    static void draw_hidden(Graphics2D g2d, int posx, int posy){
        //bevel width
        final int bw = 3;

        Rectangle2D.Float light_rect = new Rectangle2D.Float(posx, posy, 30, 30);
        Rectangle2D.Float dark_rect = new Rectangle2D.Float(posx + bw, posy + bw, 30 - bw, 30 - bw);
        Rectangle2D.Float main_rect = new Rectangle2D.Float(posx + bw, posy + bw, 30 - 2 * bw, 30 - 2 * bw);

        g2d.setColor(new Color(200, 200, 200));
        g2d.fill(light_rect);
        g2d.setColor(new Color(50, 50, 50));
        g2d.fill(dark_rect);
        g2d.setColor(new Color(100, 100, 100));
        g2d.fill(main_rect);
    }
    static void draw_flagged(Graphics2D g2d, int posx, int posy){
        draw_hidden(g2d, posx, posy);
        //Todo: draw flag
    }
    static void draw_depressed(Graphics2D g2d, int posx, int posy){
        Rectangle2D.Float rect = new Rectangle2D.Float(posx, posy, 30, 30);
        g2d.setColor(new Color(100, 100, 100));
        g2d.fill(rect);
    }
    static void draw_revealed(Graphics2D g2d, int posx, int posy){
        //for a revealed tile


        Rectangle2D.Float main_rect = new Rectangle2D.Float(posx, posy, 30, 30);
        g2d.setColor(new Color(100, 100, 100));
        g2d.fill(main_rect);
        g2d.setColor(new Color(50, 50, 50));
        g2d.drawLine(posx, posy, posx + 30, posy);
        g2d.drawLine(posx, posy, posx, posy + 30);


        //Todo: also draw mines and numbers
        //todo: make a clicked mine red
        //todo: at end, cross flags that are wrong
    }
}
