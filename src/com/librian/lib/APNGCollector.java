package com.librian.lib;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * A class to create an animated PNG.
 */
public class APNGCollector {
    public final static byte APNG_DISPOSE_OP_NONE = 0,
                             APNG_DISPOSE_OP_BACKGROUND = 1,
                             APNG_DISPOSE_OP_PREVIOUS = 2;
    public final static byte APNG_BLEND_OP_SOURCE = 0,
                             APNG_BLEND_OP_OVER = 1;

    private final ByteArrayOutputStream result = new ByteArrayOutputStream();
    private final ByteArrayOutputStream chunks = new ByteArrayOutputStream();
    private final int plays;
    private int frames, index = 0;

    /**
     * Constructs an instance of an object that creates an APNG in which a static image is NOT part of the animation.
     *
     * @param sImg  a static image that displays in a non-animated PNG decoder.
     * @param plays a number of times that this animation should play.
     *              If it is 0, the animation should play indefinitely.
     * @throws IOException if an error occurs during writing.
     */
    public APNGCollector(BufferedImage sImg, int plays) throws IOException {
        this.plays = plays;

        ByteArrayInputStream in = createPNGStream(sImg);
        result.write(in.readNBytes(33));

        chunks.write(in.readNBytes(in.available() - 12));
    }

    /**
     * Constructs an instance of an object that creates an APNG in which a static image is part of the animation.
     *
     * @param sImg        a static image that displays in a non-animated PNG decoder.
     * @param plays       a number of times that this animation should play.
     *                    If it is 0, the animation should play indefinitely.
     * @param numerator   define the numerator of the delay fraction
     * @param denominator define the denominator of the delay fraction.
     * @param dispose     defines the type of frame area disposal to be done after rendering this frame.
     *  <pre>
     *  {@summary
     *  APNG_DISPOSE_OP_NONE
     *    no disposal is done on this frame before rendering the next; the contents of the output buffer are left as is.
     *
     *  APNG_DISPOSE_OP_BACKGROUND
     *    the frame's region of the output buffer is to be cleared to fully transparent black before rendering the next frame.
     *
     *  APNG_DISPOSE_OP_PREVIOUS
     *    the frame's region of the output buffer is to be reverted to the previous contents before rendering the next frame.}
     *  </pre>
     * @param blend       specifies whether the frame is to be alpha blended into the current output buffer content,
     *                    or whether it should completely replace its region in the output buffer.
     *  <pre>
     *  {@summary
     *  APNG_BLEND_OP_SOURCE
     *    all color components of the frame, including alpha, overwrite the current contents of the frame's output buffer region.
     *
     *  APNG_BLEND_OP_OVER
     *    the frame should be composited onto the output buffer based on its alpha.}
     *  </pre>
     * @throws IOException if an error occurs during writing or reading a static image.
     */
    public APNGCollector(BufferedImage sImg,
                         int plays,
                         short numerator,
                         short denominator,
                         byte dispose,
                         byte blend) throws IOException {
        this.plays = plays;

        ByteArrayInputStream in = createPNGStream(sImg);
        result.write(in.readNBytes(33));

        chunks.write(Chunk.createFcTL(index++, sImg.getWidth(), sImg.getHeight(), 0, 0, numerator, denominator, dispose, blend));
        chunks.write(in.readNBytes(in.available() - 12));
        ++frames;
    }

    /**
     * Add frame in animation.
     *
     * @param frame       animation frame.
     * @param x           define the x position of the following frame.
     * @param y           define the y position of the following frame.
     * @param numerator   define the denominator of the delay fraction.
     * @param denominator defines the type of frame area disposal to be done after rendering this frame.
     * @param dispose     defines the type of frame area disposal to be done after rendering this frame.
     *  <pre>
     *  {@summary
     *  APNG_DISPOSE_OP_NONE
     *    no disposal is done on this frame before rendering the next; the contents of the output buffer are left as is.
     *
     *  APNG_DISPOSE_OP_BACKGROUND
     *    the frame's region of the output buffer is to be cleared to fully transparent black before rendering the next frame.
     *
     *  APNG_DISPOSE_OP_PREVIOUS
     *    the frame's region of the output buffer is to be reverted to the previous contents before rendering the next frame.}
     *  </pre>
     * @param blend       specifies whether the frame is to be alpha blended into the current output buffer content,
     *                    or whether it should completely replace its region in the output buffer.
     *  <pre>
     *  {@summary
     *  APNG_BLEND_OP_SOURCE
     *    all color components of the frame, including alpha, overwrite the current contents of the frame's output buffer region.
     *
     *  APNG_BLEND_OP_OVER
     *    the frame should be composited onto the output buffer based on its alpha.}
     *  </pre>
     * @throws IOException if an error occurs during writing or reading a frame.
     */
    public void addFrame(BufferedImage frame,
                         int x,
                         int y,
                         short numerator,
                         short denominator,
                         byte dispose,
                         byte blend) throws IOException {
        ByteArrayInputStream in = createPNGStream(frame);
        in.skipNBytes(33);

        chunks.write(Chunk.createFcTL(index++, frame.getWidth(), frame.getHeight(), x, y, numerator, denominator, dispose, blend));
        chunks.write(Chunk.createFdAT(index++, in.readNBytes(in.available() - 12)));
        ++frames;
    }

    /**
     * @return APNG stream in byte array.
     * @throws IOException if an error occurs during writing.
     */
    public byte[] build() throws IOException {
        result.write(Chunk.createAcTL(frames, plays));
        result.write(chunks.toByteArray());
        result.write(Chunk.IEND);
        return result.toByteArray();
    }

    /**
     * Writes the BufferedImage to the byte array stream as a PNG, simplifies PNG stream.
     * <pre>
     * ┏━━━━━━┳━━━━━━┳━━━━━━┳━━━━━━┳━━━━━━┳━━━━━━┓
     * ┃ IHDR ┃ PLTE ┃ gAMA ┃ .... ┃ IDAT ┃ IEND ┃
     * ┗━━━━━━┻━━━━━━┻━━━━━━┻━━━━━━┻━━━━━━┻━━━━━━┛
     *          ↓ ImageIO
     * ┏━━━━━━┳━━━━━━┳━━━━━━┓
     * ┃ IHDR ┃ IDAT ┃ IEND ┃
     * ┗━━━━━━┻━━━━━━┻━━━━━━┛
     * </pre>
     *
     * @param frame image to be recorded.
     * @return PNG stream in {@code ByteArrayInputStream}.
     */
    private ByteArrayInputStream createPNGStream(BufferedImage frame) throws IOException {
        BufferedImage n = new BufferedImage(frame.getWidth(), frame.getHeight(), 2);
        n.getGraphics().drawImage(frame, 0, 0, null);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(n, "PNG", out);
        return new ByteArrayInputStream(out.toByteArray());
    }
}