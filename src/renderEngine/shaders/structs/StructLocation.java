package renderEngine.shaders.structs;

import org.lwjgl.opengl.GL13;
import renderEngine.shaders.ShaderProgram;
import textures.ModelTexture;
import textures.Texture;
import util.math.Matrix4f;
import util.math.Vector;
import util.parsing.MaterialColor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;

public class StructLocation {

    private final int programID;

    private final List<Location> locations;

    private final List<Location> textureLocations;

    public StructLocation(int programID, String structName, Location... locations) {
        this.programID = programID;
        this.locations = new ArrayList<>();
        this.textureLocations = new ArrayList<>();
        for (Location location : locations) {
            String uniformName;
            if (location.clazz == Texture.class)
                uniformName = location.name; // Samplers are not allowed in struct
            else if (StructElement.class.isAssignableFrom(location.clazz)) { // Structure
                try {
                    StructElement structElement = (StructElement) location.clazz.
                            getDeclaredConstructor().newInstance();
                    StructLocation structLocation = structElement.getStructure().
                            getDeclaredConstructor(Integer.class, String.class).
                            newInstance(this.programID, structName + "." + location.name);

                    this.locations.addAll(structLocation.locations);
                    this.textureLocations.addAll(structLocation.textureLocations);
                } catch (ReflectiveOperationException e) {
                    e.printStackTrace();
                }
                continue;
            } else
                uniformName = structName + "." + location.name;
            int uniformLocation = ShaderProgram.getUniformLocation(programID, uniformName);
            location.setUniformLocation(uniformLocation);
            location.setProgramID(this.programID);
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
            Location.getDefaultCallback(Integer.class).onLoadUniform(this.programID, location.uniformLocation, idTexture);
            location.uniformLocation = idTexture;
            this.textureLocations.set(i, location);
        }
    }

    public void load(StructElement element) {
        load(element.getValues());
    }

    /**
     * Loads given values to the appropriate Shader Uniform Locations
     *
     * @param values must not contain NULL values
     */
    public void load(Object... values) {
        final Object[] toLoad = new Object[this.locations.size()];
        final ModelTexture[] texturesToLoad = new ModelTexture[this.textureLocations.size()];

        int i = 0;
        int j = 0;
        for (Object obj : values) {
            if (obj == null) throw new NullPointerException("Null value while loading struct");
            if (obj instanceof StructElement) {
                for (Object value : ((StructElement) obj).getValues()) {
                    if (value == null) throw new NullPointerException("Null value while loading struct");
                    if (value instanceof ModelTexture)
                        texturesToLoad[j++] = (ModelTexture) value;
                    else
                        toLoad[i++] = value;
                }
            } else if (obj instanceof ModelTexture)
                texturesToLoad[j++] = (ModelTexture) obj;
            else
                toLoad[i++] = obj;
        }
        AtomicInteger idx = new AtomicInteger();
        this.locations.forEach(location -> {
            Object value = toLoad[idx.getAndIncrement()];
            if (value != null) {
                if (value.getClass() != location.clazz &&
                        !location.clazz.isAssignableFrom(value.getClass()))
                    throw new IllegalArgumentException("Wrong argument type: " + location.name);

                location.loadUniform(value);
            }
        });
        idx.set(0);
        this.textureLocations.forEach(location -> {
            ModelTexture texture = texturesToLoad[idx.getAndIncrement()];
            if (!texture.equals(ModelTexture.NONE))
                location.loadUniform(texture.getID());
        });
    }


    @FunctionalInterface
    public interface LoadUniformCallback {

        void onLoadUniform(int programID, int location, Object value);
    }


    public static class Location {

        private final String name;
        private final Class<?> clazz;

        private int uniformLocation;
        private int programID;
        private final LoadUniformCallback loadUniformCallback;

        public Location(String name, Class<?> clazz, LoadUniformCallback loadUniformCallback) {
            this.name = name;
            this.clazz = clazz;
            this.loadUniformCallback = loadUniformCallback;
        }

        public Location(String name, Class<?> clazz) {
            this.name = name;
            this.clazz = clazz;
            this.loadUniformCallback = getDefaultCallback(this.clazz);
        }

        public void setProgramID(int programID) {
            this.programID = programID;
        }

        public void setUniformLocation(int uniformLocation) {
            this.uniformLocation = uniformLocation;
        }

        public void loadUniform(Object value) {
            this.loadUniformCallback.onLoadUniform(this.programID, this.uniformLocation, value);
        }

        public static LoadUniformCallback getDefaultCallback(Class<?> clazz) {
            return (programID, location, value) -> {
                if (value == null || location < 0)
                    return;

                if (MaterialColor.class.isAssignableFrom(clazz)) {
                    value = ((MaterialColor) value).getColor();
                    ShaderProgram.loadVector(location, (Vector) value);
                } else if (Vector.class.isAssignableFrom(clazz))
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
