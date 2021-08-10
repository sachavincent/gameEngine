package terrain;

import textures.TerrainTexture;
import util.exceptions.MissingFileException;
import util.feedback.Failure;
import util.feedback.Feedback;
import util.feedback.Success;
import util.math.Vector3f;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;

public class HeightMapReader implements HeightMapSupplier<TerrainTexture, Exception> {

    private final File file;

    public HeightMapReader(File file) {
        this.file = file;
        if (!file.exists())
            throw new MissingFileException(file);
    }

    @Override
    public Feedback<TerrainTexture, Exception> create(int width, int depth) {
        Float[][] heights;
        BufferedImage image;
        try {
            if (width <= 0 || depth <= 0)
                throw new IllegalArgumentException("Incorrect width or height!");

            heights = new Float[width][depth];
            image = ImageIO.read(this.file);
            Raster data = image.getData();
            for (int x = 0; x < depth; x++) {
                for (int z = 0; z < width; z++) {
                    heights[x][z] = getHeight(x, z, data);
                }
            }
        } catch (Exception e) {
            return new Failure<>(e);
        }

        return new Success<>(new TerrainTexture(heights));
    }


    private float getHeight(int x, int z, Raster data) {
        float height = 0;
        try {
            height = data.getSample(x, z, 0);
        } catch (IndexOutOfBoundsException ignored) {
        }
        return height / 256.0f;
    }

    private Vector3f calculateNormal(int x, int z, Raster data) {
        double heightL = getHeight(x - 1, z, data);
        double heightR = getHeight(x + 1, z, data);
        double heightD = getHeight(x, z - 1, data);
        double heightU = getHeight(x, z + 1, data);
        Vector3f normal = new Vector3f(heightL - heightR, 2, heightD - heightU);

        normal.normalise();
        return normal;
    }
}
