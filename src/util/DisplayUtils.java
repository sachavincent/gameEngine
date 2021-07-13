package util;

import static org.lwjgl.opengl.GL11.GL_DONT_CARE;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGetInteger;
import static org.lwjgl.opengl.GL30.GL_CONTEXT_FLAGS;
import static org.lwjgl.opengl.GL43.GL_CONTEXT_FLAG_DEBUG_BIT;
import static org.lwjgl.opengl.GL43.GL_DEBUG_OUTPUT;
import static org.lwjgl.opengl.GL43.GL_DEBUG_OUTPUT_SYNCHRONOUS;
import static org.lwjgl.opengl.GL43.glDebugMessageControl;

import java.nio.IntBuffer;
import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.GL44;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.APIUtil;

public class DisplayUtils {

    public static void enableDebugging() {
        glEnable(GL_DEBUG_OUTPUT);
        glEnable(GL_DEBUG_OUTPUT_SYNCHRONOUS);
        GLUtil.setupDebugMessageCallback(APIUtil.DEBUG_STREAM);
    }

    public static boolean isDebuggingEnabled() {
        if ((glGetInteger(GL_CONTEXT_FLAGS) & GL_CONTEXT_FLAG_DEBUG_BIT) == 0)
            return false;

        return GL44.glIsEnabled(GL_DEBUG_OUTPUT_SYNCHRONOUS) || GL44.glIsEnabled(GL_DEBUG_OUTPUT);
    }

    /**
     * Disable message for specific type
     *
     * @param type among
     * {@link GL43#GL_DEBUG_TYPE_ERROR}
     * {@link GL43#GL_DEBUG_TYPE_DEPRECATED_BEHAVIOR} {@link GL43#GL_DEBUG_TYPE_UNDEFINED_BEHAVIOR}
     * {@link GL43#GL_DEBUG_TYPE_PORTABILITY} {@link GL43#GL_DEBUG_TYPE_PERFORMANCE}
     * {@link GL43#GL_DEBUG_TYPE_OTHER} {@link GL43#GL_DEBUG_TYPE_MARKER}
     */
    public static void disableDebugMessage(int type) {
        if (isDebuggingEnabled()) {
            glDebugMessageControl(GL_DONT_CARE, GL_DONT_CARE, type, (IntBuffer) null, false);
        }
    }
}
