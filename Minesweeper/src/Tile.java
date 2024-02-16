import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
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


    public void draw(Graphics2D g2d, int i, int j){
        int position_x = i * 30;
        int position_y = j * 30;

        //todo: add cases for a red mine?
        //red mine goes as a flag for draw_mine
        //crossed flag is bool for flagged?
        if (is_hidden && is_flagged){
            DrawShapes.draw_flagged(g2d, position_x, position_y);
        } else if (is_hidden && is_held) {
            DrawShapes.draw_revealed(g2d, position_x, position_y);
        } else if (is_hidden) {
            DrawShapes.draw_hidden(g2d, position_x, position_y);
        } else if (is_flagged && !is_mine){ //this case should only happen at the very end when we force reveal tiles
            //todo: draw the crossed flag
        } else if (is_mine) {
            DrawShapes.draw_mine(g2d, position_x, position_y, hit);
        } else if (mine_neighbours.isPresent() && mine_neighbours.get()>0) {
            DrawShapes.draw_number(g2d, position_x, position_y, mine_neighbours.get());
        } else {
            DrawShapes.draw_revealed(g2d, position_x, position_y);
        }
    }
}


class DrawShapes{

    private static final Color dark_colour = new Color(128, 128, 128);
    private static final Color medium_colour = new Color(192, 192, 192);
    private static final Color light_colour = new Color(255, 255, 255);
    private static final Color colour_1 = new Color(0, 0, 255);
    private static final Color colour_2 = new Color(0, 128, 0);
    private static final Color colour_3 = new Color(255, 0, 0);
    private static final Color colour_4 = new Color(0, 0, 128);
    private static final Color colour_5 = new Color(128, 0, 0);
    private static final Color colour_6 = new Color(0, 128, 128);
    private static final Color colour_7 = new Color(0, 0, 0);
    private static final Color colour_8 = new Color(128, 0, 128);



    static void draw_hidden(Graphics2D g2d, int posx, int posy){
        //bevel width
        final int bw = 3;

        Rectangle2D.Float light_rect = new Rectangle2D.Float(posx, posy, 30, 30);
        Rectangle2D.Float dark_rect = new Rectangle2D.Float(posx + bw, posy + bw, 30 - bw, 30 - bw);
        Rectangle2D.Float main_rect = new Rectangle2D.Float(posx + bw, posy + bw, 30 - 2 * bw, 30 - 2 * bw);

        g2d.setColor(light_colour);
        g2d.fill(light_rect);
        g2d.setColor(dark_colour);
        g2d.fill(dark_rect);
        g2d.setColor(medium_colour);
        g2d.fill(main_rect);
    }
    static void draw_flagged(Graphics2D g2d, int posx, int posy){
        draw_hidden(g2d, posx, posy);
        //Todo: draw flag
        g2d.setColor(Color.BLUE);
        g2d.drawLine(posx, posy, posx+30, posy+30);
    }

    static void draw_revealed(Graphics2D g2d, int posx, int posy){
        Rectangle2D.Float main_rect = new Rectangle2D.Float(posx, posy, 30, 30);
        g2d.setColor(medium_colour);
        g2d.fill(main_rect);
        g2d.setColor(dark_colour);
        g2d.drawLine(posx, posy, posx + 30, posy);
        g2d.drawLine(posx, posy, posx, posy + 30);
    }
    static void draw_number(Graphics2D g2d, int posx, int posy, int mines){
        //todo: make this properly center
        draw_revealed(g2d, posx, posy);
        Color color = switch (mines){
            case 1 -> colour_1;
            case 2 -> colour_2;
            case 3 -> colour_3;
            case 4 -> colour_4;
            case 5 -> colour_5;
            case 6 -> colour_6;
            case 7 -> colour_7;
            case 8 -> colour_8;
            default -> Color.WHITE;
        };
        g2d.setColor(color);
        g2d.drawString(Integer.toString(mines), posx + 5, posy + 25);
    }

    static void draw_mine(Graphics2D g2d, int posx, int posy, boolean hit){
        draw_revealed(g2d, posx, posy);
        Ellipse2D.Float circle = new Ellipse2D.Float(posx+5, posy+5, 20, 20);
        g2d.setColor(hit ? Color.RED : Color.BLACK);
        //todo: improve mine graphic
        g2d.fill(circle);
    }
}
