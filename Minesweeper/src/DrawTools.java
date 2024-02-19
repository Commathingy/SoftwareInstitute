import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class DrawTools {
    private static final Color dark_colour = new Color(128, 128, 128);
    private static final Color medium_colour = new Color(192, 192, 192);
    private static final Color light_colour = new Color(255, 255, 255);
    private static final Color[] colours = new Color[]{
            new Color(0, 0, 255),
            new Color(0, 128, 0),
            new Color(255, 0, 0),
            new Color(0, 0, 128),
            new Color(128, 0, 0),
            new Color(0, 128, 128),
            new Color(0, 0, 0),
            new Color(128, 0, 128)
    };

    public static void DrawShadedRegion(int x, int y, int w, int h, int bw, boolean reversed, Graphics2D g2d){
        Color c1 = reversed ? dark_colour : light_colour;
        Color c2 = reversed ? light_colour : dark_colour;

        //start with bottom and right shade with a single rectangle
        Rectangle2D.Float main_rect = new Rectangle2D.Float(x, y, w, h);
        g2d.setColor(c2);
        g2d.fill(main_rect);

        //top and left shading
        Rectangle2D.Float top_rect = new Rectangle2D.Float(x, y, w - bw, bw);
        Rectangle2D.Float left_rect = new Rectangle2D.Float(x, y, bw, h - bw);
        g2d.setColor(c1);
        g2d.fill(top_rect);
        g2d.fill(left_rect);
        g2d.fillPolygon(new int[]{x+w-bw, x+w, x+w-bw}, new int[]{y, y, y+bw},3);
        g2d.fillPolygon(new int[]{x, x+bw, x}, new int[]{y+h-bw, y+h-bw, y+h},3);

        //inner region
        Rectangle2D.Float inner_rect = new Rectangle2D.Float(x+bw, y+bw, w - 2*bw, h - 2*bw);
        g2d.setColor(medium_colour);
        g2d.fill(inner_rect);
    }

    static void draw_hidden(Graphics2D g2d, int posx, int posy){
        DrawTools.DrawShadedRegion(posx, posy, 30, 30, 3, false, g2d);
    }
    static void draw_flagged(Graphics2D g2d, int posx, int posy, BufferedImage flag_sprite, boolean crossed){
        draw_hidden(g2d, posx, posy);
        g2d.drawImage(flag_sprite, posx + 5, posy + 5, null);
        if (crossed){
            g2d.setColor(Color.RED);
            g2d.drawLine(posx, posy, posx+30, posy+30);
        }
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
        draw_revealed(g2d, posx, posy);
        Color color = colours[mines-1];
        g2d.setColor(color);
        g2d.drawString(Integer.toString(mines), posx + 7, posy + 26);
    }

    static void draw_mine(Graphics2D g2d, int posx, int posy, BufferedImage mine_sprite, boolean hit){
        draw_revealed(g2d, posx, posy);
        if (hit) {
            g2d.setColor(Color.RED);
            Rectangle2D.Float main_rect = new Rectangle2D.Float(posx+1, posy+1, 29, 29);
            g2d.fill(main_rect);
        }
        g2d.drawImage(mine_sprite, posx + 5, posy + 5, null);
    }
}
