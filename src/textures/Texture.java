package textures;

import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_DEPTH24_STENCIL8;
import static org.lwjgl.opengl.GL30.GL_DEPTH_STENCIL_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_COMPLETE;
import static org.lwjgl.opengl.GL30.GL_RENDERBUFFER;
import static org.lwjgl.opengl.GL32.GL_TEXTURE_2D_MULTISAMPLE;
import static org.lwjgl.opengl.GL41.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL41.glTexParameteri;

import guis.presets.Background;
import java.awt.Color;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL41;
import org.lwjgl.stb.STBImage;

public abstract class Texture {

    protected int textureID, width, height;

    private   Color   color;
    protected boolean keepAspectRatio;
    private   int     renderBuffer;
    private   int     intermediateFbo;
    private   int     multisampledID;
    private   int     frameBuffer;

    public Texture(Background<?> background) {
        Object back = background.getBackground();
        if (back instanceof Color)
            instantiateWithColor((Color) back);
        else if (back instanceof String) {
            String backString = ((String) back);
            if (backString.startsWith("#"))
                instantiateWithHexColor(backString);
            else
                instantiateWithFile(backString);
        } else if (back instanceof Integer)
            instantiateWithInteger((Integer) back);
        else
            throw new IllegalArgumentException("Invalid type: " + background);

    }

    private void instantiateWithHexColor(String hexColor) {
        this.width = 8;
        this.height = 8;

        this.color = Color.decode(hexColor);
    }

    private void instantiateWithColor(Color color) {
        this.width = 8;
        this.height = 8;

        if (color == null)
            color = Color.WHITE;

        this.color = color;
    }

    private void instantiateWithInteger(Integer integer) {
        this.textureID = integer;
    }

    private void instantiateWithFile(String fileName) {
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer comp = BufferUtils.createIntBuffer(1);
        ByteBuffer byteBuffer = STBImage.stbi_load("res/" + fileName, width, height, comp, 4);

        if (byteBuffer == null) {
            width.clear();
            height.clear();
            comp.clear();

            return;
        }

        this.width = width.get();
        this.height = height.get();

        if (false) {
            loadMSAATexture();
        } else {
            this.textureID = GL41.glGenTextures();
            this.keepAspectRatio = true;

            GL41.glBindTexture(GL_TEXTURE_2D, textureID);
            GL41.glTexParameterf(GL_TEXTURE_2D, GL41.GL_TEXTURE_MIN_FILTER, GL41.GL_NEAREST);
            GL41.glTexParameterf(GL_TEXTURE_2D, GL41.GL_TEXTURE_MAG_FILTER, GL41.GL_NEAREST);
            GL41.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, this.width, this.height, 0, GL_RGBA,
                    GL41.GL_UNSIGNED_BYTE, byteBuffer);

            GL41.glGenerateMipmap(GL_TEXTURE_2D);
            glTexParameteri(GL_TEXTURE_2D, GL41.GL_TEXTURE_MIN_FILTER, GL41.GL_LINEAR_MIPMAP_LINEAR);
            GL41.glTexParameterf(GL_TEXTURE_2D, GL41.GL_TEXTURE_LOD_BIAS, 0f);

            if (GL.getCapabilities().GL_EXT_texture_filter_anisotropic) {
                float amount = Math
                        .min(4f, GL41.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
                GL41.glTexParameterf(GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, amount);
            } else {
                // TODO
            }
        }
        width.clear();
        height.clear();
        comp.clear();

        STBImage.stbi_image_free(byteBuffer);
    }

    public boolean dokeepAspectRatio() {
        return this.keepAspectRatio;
    }

    public Color getColor() {
        return this.color;
    }

    public int getTextureID() {
        return this.textureID;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }


    public void loadMSAATexture() {
        frameBuffer = GL41.glGenFramebuffers();
        GL41.glBindFramebuffer(GL41.GL_FRAMEBUFFER, frameBuffer);

        multisampledID = GL41.glGenTextures();
        GL41.glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, multisampledID);
        GL41.glTexImage2DMultisample(GL_TEXTURE_2D_MULTISAMPLE, 4, GL_RGBA, this.width, this.height, true);
        GL41.glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, 0);
        GL41.glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D_MULTISAMPLE, multisampledID, 0);


        renderBuffer = GL41.glGenRenderbuffers();
        GL41.glBindRenderbuffer(GL_RENDERBUFFER, renderBuffer);
        GL41.glRenderbufferStorageMultisample(GL_RENDERBUFFER, 4, GL_DEPTH24_STENCIL8, this.width, this.height);
        GL41.glBindRenderbuffer(GL_RENDERBUFFER, 0);
        GL41.glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, renderBuffer);

        if (GL41.glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
            System.err.println("Error renderBuffer");

        GL41.glBindFramebuffer(GL_FRAMEBUFFER, 0);

        intermediateFbo = GL41.glGenFramebuffers();
        GL41.glBindFramebuffer(GL_FRAMEBUFFER, intermediateFbo);


        this.textureID = GL41.glGenTextures();
        GL41.glBindTexture(GL_TEXTURE_2D, this.textureID);
        GL41.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, this.width, this.height, 0, GL_RGBA, GL41.GL_UNSIGNED_BYTE, 0);
        GL41.glTexParameterf(GL_TEXTURE_2D, GL41.GL_TEXTURE_MIN_FILTER, GL41.GL_NEAREST);
        GL41.glTexParameterf(GL_TEXTURE_2D, GL41.GL_TEXTURE_MAG_FILTER, GL41.GL_NEAREST);
        GL41.glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, this.textureID, 0);

        if (GL41.glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
            System.err.println("Error renderBuffer");

        GL41.glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public int getRenderBuffer() {
        return this.renderBuffer;
    }

    public int getIntermediateFbo() {
        return this.intermediateFbo;
    }

    public int getMultisampledID() {
        return this.multisampledID;
    }

    public int getFrameBuffer() {
        return this.frameBuffer;
    }
}
