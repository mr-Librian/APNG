## Documentation

[Portable Network Graphics Specification (Third Edition)](https://www.w3.org/TR/png-3/)

[Library Documentation](./doc/index.html)

## Examples
1.  ```java
    import javax.imageio.ImageIO;
    import java.awt.image.BufferedImage;
    import java.io.*;
    
    import com.librian.lib.APNGCollector;
    
    import static com.librian.lib.APNGCollector.*;
    
    void main() throws Exception {
        BufferedImage i1 = ImageIO.read(new File("./example/1.png")),
                      i2 = ImageIO.read(new File("./example/2.png")),
                      i3 = ImageIO.read(new File("./example/3.png")),
                      i4 = ImageIO.read(new File("./example/4.png"));

        APNGCollector apng = new APNGCollector(i1, 0, (short) 70, (short) 100, APNG_DISPOSE_OP_NONE, APNG_BLEND_OP_OVER);
        apng.addFrame(i2, 26, 40, (short) 70, (short) 100, APNG_DISPOSE_OP_NONE, APNG_BLEND_OP_OVER);
        apng.addFrame(i3, 26, 40, (short) 70, (short) 100, APNG_DISPOSE_OP_NONE, APNG_BLEND_OP_OVER);
        apng.addFrame(i2, 26, 40, (short) 70, (short) 100, APNG_DISPOSE_OP_PREVIOUS, APNG_BLEND_OP_OVER);
        apng.addFrame(i4, 40, 48, (short) 70, (short) 100, APNG_DISPOSE_OP_NONE, APNG_BLEND_OP_OVER);
        apng.addFrame(i2, 26, 40, (short) 70, (short) 100, APNG_DISPOSE_OP_NONE, APNG_BLEND_OP_OVER);
        apng.addFrame(i3, 26, 40, (short) 70, (short) 100, APNG_DISPOSE_OP_NONE, APNG_BLEND_OP_OVER);

        FileOutputStream f = new FileOutputStream("./test.png");
        f.write(apng.build());
        f.close();
    }
    ```
    input:<br>
    ![output](res/1/1.png)
    ![output](res/1/2.png)
    ![output](res/1/3.png)
    ![output](res/1/4.png)

    output:<br>
    ![input](res/1/test.png)<br>
2.  ```java
    import javax.imageio.ImageIO;
    import java.awt.image.BufferedImage;
    import java.io.*;
    
    import com.librian.lib.APNGCollector;
    
    import static com.librian.lib.APNGCollector.*;
    
    void main() throws Exception {
        BufferedImage i1 = ImageIO.read(new File("./example/1.png")),
                      i2 = ImageIO.read(new File("./example/2.png")),
                      i3 = ImageIO.read(new File("./example/3.png")),
                      i4 = ImageIO.read(new File("./example/4.png"));

        APNGCollector apng = new APNGCollector(i1, 0);
        apng.addFrame(i2, 26, 40, (short) 70, (short) 100, APNG_DISPOSE_OP_NONE, APNG_BLEND_OP_OVER);
        apng.addFrame(i3, 26, 40, (short) 70, (short) 100, APNG_DISPOSE_OP_NONE, APNG_BLEND_OP_OVER);
        apng.addFrame(i2, 26, 40, (short) 70, (short) 100, APNG_DISPOSE_OP_PREVIOUS, APNG_BLEND_OP_OVER);
        apng.addFrame(i4, 40, 48, (short) 70, (short) 100, APNG_DISPOSE_OP_NONE, APNG_BLEND_OP_OVER);
        apng.addFrame(i2, 26, 40, (short) 70, (short) 100, APNG_DISPOSE_OP_NONE, APNG_BLEND_OP_OVER);
        apng.addFrame(i3, 26, 40, (short) 70, (short) 100, APNG_DISPOSE_OP_NONE, APNG_BLEND_OP_OVER);

        FileOutputStream f = new FileOutputStream("./test.png");
        f.write(apng.build());
        f.close();
    }
    ```
    input:<br>
    ![output](res/2/1.png)
    ![output](res/2/2.png)
    ![output](res/2/3.png)
    ![output](res/2/4.png)

    output:<br>
    ![input](res/2/test.png)<br>
3.  ```java
    import javax.imageio.ImageIO;
    import java.awt.image.BufferedImage;
    import java.io.*;
    
    import com.librian.lib.APNGSeparator;

    void main() throws Exception {        
        FileInputStream stream = new FileInputStream("./test.png");
        APNGSeparator separator = new APNGSeparator(stream);
   
        // #1
        int i = 0;
        for (BufferedImage img : separator.getFrames(true))
            ImageIO.write(img, "PNG", new File(STR."./1_\{++i}.png"));
   
        // #2
        i = 0;
        for (BufferedImage img : separator.getFrames(false)) {
            // Bright border for frame with transparent background.
            var output = new BufferedImage(img.getWidth() + 2, img.getHeight() + 2, BufferedImage.TYPE_INT_ARGB);
            output.getGraphics().drawImage(img, 1, 1, null);
            output.getGraphics().drawRect(0, 0, output.getWidth() - 1, output.getHeight() - 1);

            ImageIO.write(output, "PNG", new File(STR."./2_\{++i}.png"));
        }
        stream.close();
    }
    ```
    input:<br>
    ![input](res/3/test.png)<br>
    output:<br>
    1. ![output](res/3/1_1.png) <- static image <br>
       ![output](res/3/1_2.png)
       ![output](res/3/1_3.png)
       ![output](res/3/1_4.png)
       ![output](res/3/1_5.png)
       ![output](res/3/1_6.png)
       ![output](res/3/1_7.png)
       ![output](res/3/1_8.png)
    2. ![output](res/3/2_1.png) <- static image <br>
       ![output](res/3/2_2.png)
       ![output](res/3/2_3.png)
       ![output](res/3/2_4.png)
       ![output](res/3/2_5.png)
       ![output](res/3/2_6.png)
       ![output](res/3/2_7.png)
4.  ```java
    import javax.imageio.ImageIO;
    import java.awt.image.BufferedImage;
    import java.io.*;
    
    import com.librian.lib.APNGSeparator;
    import com.librian.lib.Frame;
    
    void main() throws Exception {
        FileInputStream stream = new FileInputStream("./test.png");
        APNGSeparator separator = new APNGSeparator(stream);

        int i = 0;
        for (Frame img : separator.getRawFrames()) {
            // Bright border for frame with transparent background.
            var output = new BufferedImage(
                    img.image.getWidth() + 2,
                    img.image.getHeight() + 2,
                    BufferedImage.TYPE_INT_ARGB);
            output.getGraphics().drawImage(img.image, 1, 1, null);
            output.getGraphics().drawRect(0, 0, output.getWidth() - 1, output.getHeight() - 1);

            ImageIO.write(output, "PNG", new File(STR."./\{++i}.png"));
        }
   
        stream.close();
    }
    ```
    input:<br>
    ![input](res/4/test.png)<br>
    output:<br>
    ![output](res/4/1.png) <- static image <br>
    ![output](res/4/2.png)
    ![output](res/4/3.png)
    ![output](res/4/4.png)
    ![output](res/4/5.png)
    ![output](res/4/6.png)
    ![output](res/4/7.png)

   