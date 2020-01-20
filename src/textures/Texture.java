package textures;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;

import guis.presets.GuiBackground;
import java.awt.Color;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import util.math.Vector3f;

public abstract class Texture {

    private int textureID, width, height;

    private Vector3f color;

    public Texture(GuiBackground<?> background) {
        if (background.getBackground() instanceof Color)
            instantiateWithColor((Color) background.getBackground());
        else if (background.getBackground() instanceof String)
            instantiateWithFile((String) background.getBackground());
        else if (background.getBackground() instanceof Integer)
            instantiateWithInteger((Integer) background.getBackground());
        else
            throw new IllegalArgumentException("Type invalide.");

    }

    private void instantiateWithColor(Color color) {
        this.width = 8;
        this.height = 8;

        if (color == null)
            color = Color.WHITE;

        this.color = new Vector3f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);
    }

    private void instantiateWithInteger(Integer integer) {
        textureID = integer;
    }

    private void instantiateWithFile(String fileName) {
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer comp = BufferUtils.createIntBuffer(1);
        ByteBuffer byteBuffer = STBImage.stbi_load("res/" + fileName, width, height, comp, 4);

        if (byteBuffer == null)
            return;

        textureID = GL11.glGenTextures();

        this.width = width.get();
        this.height = height.get();

        GL11.glBindTexture(GL_TEXTURE_2D, textureID);

        GL11.glTexParameterf(GL_TEXTURE_2D, GL14.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameterf(GL_TEXTURE_2D, GL14.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexImage2D(GL_TEXTURE_2D, 0, GL11.GL_RGBA, this.width, this.height, 0, GL11.GL_RGBA,
                GL11.GL_UNSIGNED_BYTE, byteBuffer);

        GL30.glGenerateMipmap(GL_TEXTURE_2D);
        glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,
                GL11.GL_LINEAR_MIPMAP_LINEAR);
        GL11.glTexParameterf(GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0f);

        if (GL.getCapabilities().GL_EXT_texture_filter_anisotropic) {
            float amount = Math.min(4f, GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
            GL11.glTexParameterf(GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, amount);
        } else {
            // TODO
        }
        STBImage.stbi_image_free(byteBuffer);
    }

    public Vector3f getColor() {
        return this.color;
    }

    public int getTextureID() {
        return this.textureID;
    }
}
