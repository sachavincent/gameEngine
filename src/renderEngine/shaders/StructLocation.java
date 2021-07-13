package renderEngine.shaders;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.lwjgl.opengl.GL13;
import textures.ModelTexture;
import textures.Texture;
import util.math.Matrix4f;
import util.math.Vector;

public class StructLocation {

    private final List<Location> locations;

    private final List<Location> textureLocations;

    public StructLocation(int programID, String structName, Location... locations) {
        this.locations = new ArrayList<>();
        this.textureLocations = new ArrayList<>();
        for (Location location : locations) {
            String uniformName;
            if (location.clazz == Texture.class)
                uniformName = location.name; // Samplers are not allowed in struct
            else
                uniformName = structName + "." + location.name;
            int uniformLocation = ShaderProgram.getUniformLocation(programID, uniformName);
            location.setUniformLocation(uniformLocation);
            if (location.clazz == Texture.class)
                this.textureLocations.add(location);
            else
                this.locations.add(location);
        }
    }

    public void connectTextureUnits() {
        AtomicInteger idx = new AtomicInteger();
        for (int i = 0; i < this.textureLocations.size(); i++) {
            Location location = this.textureLocations.get(i);
            int idTexture = idx.getAndIncrement();
            Location.getDefaultCallback(Integer.class).onLoadUniform(location.uniformLocation, idTexture);
            location.uniformLocation = idTexture;
            this.textureLocations.set(i, location);
        }
    }

    public void loadTextures(ModelTexture... textures) {
        AtomicInteger idx = new AtomicInteger();
        this.textureLocations.forEach(location -> {
            ModelTexture texture = textures[idx.getAndIncrement()];
            if (texture != null)
                location.loadUniform(texture.getTextureID());
        });
    }

    public void load(Object... values) {
        if (values.length != this.locations.size())
            throw new IllegalArgumentException("Incorrect number of arguments!");

        AtomicInteger idx = new AtomicInteger();
        this.locations.forEach(location -> {
            Object value = values[idx.getAndIncrement()];
            if (value != null) {
                if (value.getClass() != location.clazz)
                    throw new IllegalArgumentException("Wrong argument type: " + location.name);

                location.loadUniform(value);
            }
        });
    }


    @FunctionalInterface
    public interface LoadUniformCallback {

        void onLoadUniform(int location, Object value);
    }


    static class Location {

        private final String              name;
        private final Class<?>            clazz;
        private final LoadUniformCallback loadUniformCallback;

        private int uniformLocation;

        public Location(String name, Class<?> clazz, LoadUniformCallback loadUniformCallback) {
            this.name = name;
            this.clazz = clazz;
            this.loadUniformCallback = loadUniformCallback;
        }

        public Location(String name, Class<?> clazz) {
            this.name = name;
            this.clazz = clazz;
            this.loadUniformCallback = getDefaultCallback(clazz);
        }

        public void setUniformLocation(int uniformLocation) {
            this.uniformLocation = uniformLocation;
        }

        public void loadUniform(Object value) {
            this.loadUniformCallback.onLoadUniform(this.uniformLocation, value);
        }

        public static LoadUniformCallback getDefaultCallback(Class<?> clazz) {
            return (location, value) -> {
                if (location < 0 || value == null)
                    return;

                if (Vector.class.isAssignableFrom(clazz))
                    ShaderProgram.loadVector(location, (Vector) value);
                else if (clazz.equals(Float.class))
                    ShaderProgram.loadFloat(location, (Float) value);
                else if (clazz.equals(Integer.class))
                    ShaderProgram.loadInt(location, (Integer) value);
                else if (clazz.equals(Matrix4f.class))
                    ShaderProgram.loadMatrix(location, (Matrix4f) value);
                else if (clazz.equals(Boolean.class))
                    ShaderProgram.loadBoolean(location, (Boolean) value);
                else if (clazz.equals(Texture.class)) {
                    GL13.glActiveTexture(GL13.GL_TEXTURE0 + location);
                    GL13.glBindTexture(GL_TEXTURE_2D, (Integer) value);
                }
            };
        }
    }
}
