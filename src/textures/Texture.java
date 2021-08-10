package textures;

import guis.presets.Background;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL41;
import org.lwjgl.stb.STBImage;
import util.exceptions.MissingFileException;

import java.awt.*;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.GL_TEXTURE_RECTANGLE;
import static org.lwjgl.opengl.GL32.GL_TEXTURE_2D_MULTISAMPLE;
import static org.lwjgl.opengl.GL41.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL41.glTexParameteri;

public abstract class Texture {

    protected int ID, width, height;

    private Color color;
    protected boolean keepAspectRatio;
    private int renderBuffer;
    private int intermediateFbo;
    private int multisampledID;
    private int frameBuffer;

    protected Texture() {
    }

    public Texture(Float[][] array) {
        int w = array.length;
        int h = array[0].length;
        instantiateWithArray(array, w, h, this);
    }

    public Texture(Background<?> background) {
        Object back = background.getBackground();
        if (back instanceof Color)
            instantiateWithColor((Color) back);
        else if (back instanceof String) {
            String backString = ((String) back);
            if (backString.startsWith("#"))
                instantiateWithHexColor(backString);
        } else if (back instanceof File) {
            instantiateWithFile((File) back, this);
        } else if (back instanceof Integer) {
            instantiateWithInteger((Integer) back);
        }
//        else
//            throw new IllegalArgumentException("Invalid type: " + background);
    }

    private void instantiateWithHexColor(String hexColor) {
        this.width = 8;
        this.height = 8;

        this.color = Color.decode(hexColor);
    }

    private void instantiateWithColor(Color color) {
        this.width = 8;
        this.height = 8;
        this.ID = GL41.glGenTextures();
        if (color == null)
            color = Color.WHITE;

        this.color = color;
        GL41.glBindTexture(GL_TEXTURE_2D, this.ID);
    }

    private void instantiateWithInteger(Integer integer) {
        this.ID = integer;
    }

    protected static Texture instantiateWithFile(File file, Class<? extends Texture> textureClass) {
        Texture texture = null;
        try {
            texture = textureClass.getDeclaredConstructor(File.class).newInstance(file);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }

        return instantiateWithFile(file, texture);
    }

    protected static Texture instantiateWithArray(Float[][] data, int width, int height, Texture texture) {
        if (texture == null || data == null || data.length != width || data[0].length != height)
            return texture;
        texture.width = width;
        texture.height = height;
        if (false) {
            texture.loadMSAATexture();
        } else {
            int textureID = GL41.glGenTextures();

            texture.ID = textureID;
            texture.keepAspectRatio = true;
            float[] texels = new float[data.length * data[0].length];
            for (int i = 0; i < data.length; ++i) {
                float[] arr = new float[data[i].length];
                for (int j = 0; j < arr.length; j++) {
                    arr[j] = data[i][j];
                }
                System.arraycopy(arr, 0, texels, i * data[0].length, data[i].length);
            }

//            float min = Float.MAX_VALUE;
//            float max = Float.MIN_VALUE;
//            for (float texel : texels) {
//                if (texel < min)
//                    min = texel;
//                if (texel > max)
//                    max = texel;
//            }
//            System.out.println("MIN TEXEL: " + min);
//            System.out.println("MAX TEXEL: " + max);
            GL41.glBindTexture(GL_TEXTURE_RECTANGLE, textureID);
            GL41.glTexImage2D(GL_TEXTURE_RECTANGLE, 0, GL_RED, width, height,
                    0, GL_RED, GL41.GL_FLOAT, texels);
        }
        return texture;
    }

    protected static Texture instantiateWithFile(File file, Texture texture) {
        if (texture == null || file == null)
            return texture;

        if (!file.exists())
            throw new MissingFileException(file);

        IntBuffer widthBuffer = BufferUtils.createIntBuffer(1);
        IntBuffer heightBuffer = BufferUtils.createIntBuffer(1);
        IntBuffer compBuffer = BufferUtils.createIntBuffer(1);
        ByteBuffer byteBuffer = STBImage.stbi_load(file.getPath(), widthBuffer, heightBuffer, compBuffer, 4);

        if (byteBuffer == null) {
            widthBuffer.clear();
            heightBuffer.clear();
            compBuffer.clear();

            return texture;
        }
        int width = widthBuffer.get();
        int height = heightBuffer.get();
        texture.width = width;
        texture.height = height;
        if (false) {
            texture.loadMSAATexture();
        } else {
            int textureID = GL41.glGenTextures();

            texture.ID = textureID;
            texture.keepAspectRatio = true;

            GL41.glBindTexture(GL_TEXTURE_2D, textureID);
            GL41.glTexParameterf(GL_TEXTURE_2D, GL41.GL_TEXTURE_MIN_FILTER, GL41.GL_NEAREST);
            GL41.glTexParameterf(GL_TEXTURE_2D, GL41.GL_TEXTURE_MAG_FILTER, GL41.GL_NEAREST);
            GL41.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL41.GL_UNSIGNED_BYTE,
                    byteBuffer);
            GL41.glGenerateMipmap(GL_TEXTURE_2D);
            glTexParameteri(GL_TEXTURE_2D, GL41.GL_TEXTURE_MIN_FILTER, GL41.GL_LINEAR_MIPMAP_LINEAR);
            GL41.glTexParameterf(GL_TEXTURE_2D, GL41.GL_TEXTURE_LOD_BIAS, 0f);

            if (GL.getCapabilities().GL_EXT_texture_filter_anisotropic) {
                float amount = Math
                        .min(4f, GL41.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
                GL41.glTexParameterf(GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT,
                        amount);
            } else {
                System.out.println("no anisotropic");
                // TODO
            }
        }
        widthBuffer.clear();
        heightBuffer.clear();
        compBuffer.clear();

        STBImage.stbi_image_free(byteBuffer);

        return texture;
    }

    public boolean dokeepAspectRatio() {
        return this.keepAspectRatio;
    }

    public Color getColor() {
        return this.color;
    }

    public int getID() {
        return this.ID;
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
        GL41.glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D_MULTISAMPLE, multisampledID,
                0);


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


        this.ID = GL41.glGenTextures();
        GL41.glBindTexture(GL_TEXTURE_2D, this.ID);
        GL41.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, this.width, this.height, 0, GL_RGBA, GL41.GL_UNSIGNED_BYTE, 0);
        GL41.glTexParameterf(GL_TEXTURE_2D, GL41.GL_TEXTURE_MIN_FILTER, GL41.GL_NEAREST);
        GL41.glTexParameterf(GL_TEXTURE_2D, GL41.GL_TEXTURE_MAG_FILTER, GL41.GL_NEAREST);
        GL41.glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, this.ID, 0);

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Texture texture = (Texture) o;
        return this.ID == texture.ID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.ID);
    }
}
