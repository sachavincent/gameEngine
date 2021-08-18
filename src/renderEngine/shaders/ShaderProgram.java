package renderEngine.shaders;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import util.math.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

public abstract class ShaderProgram {

    private static final String ROOT = "src/renderEngine/shaders/glsl/";

    private static final FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

    protected final int programID;
    private final int vertexShaderID;
    private final int fragmentShaderID;

    private boolean started;

    ShaderProgram(String vertexFile, String fragmentFile) {
        this.vertexShaderID = loadShader(ROOT + vertexFile, GL20.GL_VERTEX_SHADER);
        this.fragmentShaderID = loadShader(ROOT + fragmentFile, GL20.GL_FRAGMENT_SHADER);

        this.programID = GL20.glCreateProgram();
        GL20.glAttachShader(this.programID, this.vertexShaderID);
        GL20.glAttachShader(this.programID, this.fragmentShaderID);

        bindAttributes();

        GL20.glLinkProgram(this.programID);
        GL20.glValidateProgram(this.programID);

        getAllUniformLocations();
    }

    protected abstract void getAllUniformLocations();

    final int getUniformLocation(String uniformName) {
        return getUniformLocation(this.programID, uniformName);
    }

    public static int getUniformLocation(int programID, String uniformName) {
        return GL20.glGetUniformLocation(programID, uniformName);
    }

    public static void loadFloat(int location, Float value) {
        GL20.glUniform1f(location, value);
    }

    public static void loadVector(int location, Vector vector) {
        if (vector instanceof Vector2f)
            GL20.glUniform2f(location, ((Vector2f) vector).x, ((Vector2f) vector).y);
        else if (vector instanceof Vector3f)
            GL20.glUniform3f(location, ((Vector3f) vector).x, ((Vector3f) vector).y, ((Vector3f) vector).z);
        else if (vector instanceof Vector4f)
            GL20.glUniform4f(location, ((Vector4f) vector).x, ((Vector4f) vector).y, ((Vector4f) vector).z,
                    ((Vector4f) vector).w);
    }

    public static void loadIntVector(int location, Vector vector) {
        if (vector instanceof Vector2f)
            GL20.glUniform2i(location, (int) ((Vector2f) vector).x, (int) ((Vector2f) vector).y);
        else if (vector instanceof Vector3f)
            GL20.glUniform3i(location, (int) ((Vector3f) vector).x, (int) ((Vector3f) vector).y, (int) ((Vector3f) vector).z);
        else if (vector instanceof Vector4f)
            GL20.glUniform4i(location, (int) ((Vector4f) vector).x, (int) ((Vector4f) vector).y, (int) ((Vector4f) vector).z,
                    (int) ((Vector4f) vector).w);
    }

    public static void loadInt(int location, Integer value) {
        GL20.glUniform1i(location, value);
    }

    public static void loadBoolean(int location, boolean value) {
        GL20.glUniform1f(location, value ? 1 : 0);
    }

    public static void loadMatrix(int location, Matrix4f matrix) {
        matrix.store(matrixBuffer);
        matrixBuffer.flip();
        GL20.glUniformMatrix4fv(location, false, matrixBuffer);

        matrixBuffer.clear();
    }

    public final void start() {
        if (!this.started) {
            GL20.glUseProgram(this.programID);
            this.started = true;
        }
    }

    public final void stop() {
        if (this.started) {
            GL20.glUseProgram(0);
            this.started = false;
        }
    }

    public final void cleanUp() {
        stop();

        GL20.glDetachShader(this.programID, this.vertexShaderID);
        GL20.glDetachShader(this.programID, this.fragmentShaderID);

        GL20.glDeleteShader(this.vertexShaderID);
        GL20.glDeleteShader(this.fragmentShaderID);

        GL20.glDeleteProgram(this.programID);
    }

    protected abstract void bindAttributes();

    protected final void bindAttribute(int attribute, String variableName) {
        GL20.glBindAttribLocation(this.programID, attribute, variableName);
    }

    public final boolean isStarted() {
        return this.started;
    }

    private static int loadShader(String file, int type) {
        StringBuilder shaderSource = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;

            while ((line = reader.readLine()) != null)
                shaderSource.append(line).append("\n");

            reader.close();
        } catch (IOException e) {
            System.err.println("Could not read file!");
            e.printStackTrace();
            System.exit(-1);
        }

        int shaderID = GL20.glCreateShader(type);
        GL20.glShaderSource(shaderID, shaderSource);
        GL20.glCompileShader(shaderID);

        if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            System.out.println(GL20.glGetShaderInfoLog(shaderID, 500));
            System.err.println("Could not compile shader.");
            System.exit(-1);
        }

        return shaderID;
    }
}
