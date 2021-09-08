package display;

import java.io.PrintStream;
import util.math.Maths;

public class DisplayManager {

    public static int FRAMERATE_LIMIT = 300, CURRENT_FPS = FRAMERATE_LIMIT, TPS = 20;
    public static  double MSPT;
    private static double FRAMERATE_LIMIT_NS = 1000000000 / (double) FRAMERATE_LIMIT;

    public static PrintStream outStream;
    public static PrintStream errStream;

    public static final int    MIN_FRAMERATE      = 30;
    public static final int    MAX_FRAMERATE      = 300;
    public static final String FRAMERATE_INFINITE = "Inf.";

    public static final boolean IS_DEBUG = java.lang.management.ManagementFactory.
            getRuntimeMXBean().
            getInputArguments().toString().indexOf("jdwp") >= 0;

    private static void setWindow() {

//        GLFWImage image = GLFWImage.malloc();
//        ByteBuffer buf = null;
//        GLFWImage.Buffer images = null;
//        try {
//            PNGDecoder dec = new PNGDecoder(new FileInputStream(RES_PATH + "/insula_preview.png"));
//            int width = dec.getWidth();
//            int height = dec.getHeight();
//            buf = BufferUtils.createByteBuffer(width * height * 4);
//            dec.decode(buf, width * 4, PNGDecoder.Format.RGBA);
//            buf.flip();
//            image.set(width, height, buf);
//            images = GLFWImage.malloc(1);
//            images.put(0, image);
//
//            glfwSetWindowIcon(window, images);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (buf != null)
//                buf.clear();
//            if (images != null)
//                images.free();
//            image.free();
//        }
    }

    public static void setCurrentFps(int fps) {
        Display.limitFramerate = fps < Integer.MAX_VALUE;

        FRAMERATE_LIMIT = fps;
        FRAMERATE_LIMIT_NS = 1000000000 / (double) fps;
    }

    public static void setFPS(String fps) {
        if (fps.equalsIgnoreCase(FRAMERATE_INFINITE))
            setCurrentFps(Integer.MAX_VALUE);
        else {
            try {
                int fpsValue = Integer.parseInt(fps);
                setCurrentFps(Maths.clamp(fpsValue, MIN_FRAMERATE, MAX_FRAMERATE));
            } catch (NumberFormatException e) {

            }
        }
    }

    public static double getFramerateLimitNS() {
        return FRAMERATE_LIMIT_NS;
    }
}