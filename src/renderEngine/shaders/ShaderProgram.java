package renderEngine.shaders;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import util.math.Matrix4f;
import util.math.Vector2f;
import util.math.Vector3f;
import util.math.Vector4f;

public abstract class ShaderProgram {

    private static final String ROOT = "src/renderEngine/shaders/glsl/";

    private static final FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

    private final int programID;
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

    int getUniformLocation(String uniformName) {
        return GL20.glGetUniformLocation(this.programID, uniformName);
    }

    void loadFloat(int location, float value) {
        GL20.glUniform1f(location, value);
    }

    void loadVector(int location, Vector3f vector) {
        GL20.glUniform3f(location, vector.x, vector.y, vector.z);
    }

    void loadVector(int location, Vector4f vector) {
        GL20.glUniform4f(location, vector.x, vector.y, vector.z, vector.w);
    }

    void load2DVector(int location, Vector2f vector) {
        if (vector != null)
            GL20.glUniform2f(location, vector.x, vector.y);
    }

    void loadInt(int location, int value) {
        GL20.glUniform1i(location, value);
    }

    void loadBoolean(int location, boolean value) {
        GL20.glUniform1f(location, value ? 1 : 0);
    }

    void loadMatrix(int location, Matrix4f matrix) {
        matrix.store(matrixBuffer);
        matrixBuffer.flip();
        GL20.glUniformMatrix4fv(location, false, matrixBuffer);

        matrixBuffer.clear();
    }

    public void start() {
        GL20.glUseProgram(this.programID);
        this.started = true;
    }

    public void stop() {
        GL20.glUseProgram(0);
        this.started = false;
    }

    public void cleanUp() {
        stop();

        GL20.glDetachShader(this.programID, this.vertexShaderID);
        GL20.glDetachShader(this.programID, this.fragmentShaderID);

        GL20.glDeleteShader(this.vertexShaderID);
        GL20.glDeleteShader(this.fragmentShaderID);

        GL20.glDeleteProgram(this.programID);
    }

    protected abstract void bindAttributes();

    protected void bindAttribute(int attribute, String variableName) {
        GL20.glBindAttribLocation(this.programID, attribute, variableName);
    }

    public boolean isStarted() {
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
