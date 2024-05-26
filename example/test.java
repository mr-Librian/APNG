import com.librian.lib.APNGCollector;
import com.librian.lib.APNGSeparator;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

void main() throws Exception {
    int v = 0;

    APNGCollector b = new APNGCollector(ImageIO.read(new File("./src/1.png")), 0);
    b.addFrame(ImageIO.read(new File("./src/2.png")), 40, 20, (short) 100, (short) 100, (byte) 1, (byte) 1);
    b.addFrame(ImageIO.read(new File("./src/3.png")), 20, 50, (short) 100, (short) 100, (byte) 2, (byte) 1);
    b.addFrame(ImageIO.read(new File("./src/4.png")), 50, 40, (short) 100, (short) 100, (byte) 0, (byte) 0);

    FileOutputStream f = new FileOutputStream ("./test.png");
    f.write(b.build());
    f.close();

    for (BufferedImage img : new APNGSeparator(new FileInputStream("./test.png")).getFrames(true))
        ImageIO.write(img, "PNG", new File(STR."./\{++v}.png"));
}
