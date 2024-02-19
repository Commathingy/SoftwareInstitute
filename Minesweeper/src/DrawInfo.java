import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;

public class DrawInfo {

    public BufferedImage mine_sprite;
    public BufferedImage flag_sprite;

    public DrawInfo(){
        AffineTransform afft = new AffineTransform();
        afft.scale(0.5, 0.5);
        AffineTransformOp afft_op = new AffineTransformOp(afft, AffineTransformOp.TYPE_BILINEAR);

        try{
            BufferedImage big_mine_sprite = ImageIO.read(new File("./src/Mine.png"));
            BufferedImage big_flag_sprite = ImageIO.read(new File("./src/Flag.png"));

            int w = big_mine_sprite.getWidth();
            int h = big_mine_sprite.getHeight();

            mine_sprite = new BufferedImage(w/2, h/2, big_mine_sprite.getType());
            flag_sprite = new BufferedImage(w/2, h/2, big_flag_sprite.getType());

            mine_sprite = afft_op.filter(big_mine_sprite, mine_sprite);
            flag_sprite = afft_op.filter(big_flag_sprite, flag_sprite);
        } catch (Exception ignored){}



    }
}
