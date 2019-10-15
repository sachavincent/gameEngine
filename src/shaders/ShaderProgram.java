package shaders;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import util.vector.Matrix4f;
import util.vector.Vector2f;
import util.vector.Vector3f;
import util.vector.Vector4f;

public abstract class ShaderProgram {

    private static final String ROOT = "src/shaders/glsl/";

    private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

    private int programID, vertexShaderID, fragmentShaderID;

    ShaderProgram(String vertexFile, String fragmentFile) {
        vertexShaderID = loadShader(ROOT + vertexFile, GL20.GL_VERTEX_SHADER);
        fragmentShaderID = loadShader(ROOT + fragmentFile, GL20.GL_FRAGMENT_SHADER);

        programID = GL20.glCreateProgram();
        GL20.glAttachShader(programID, vertexShaderID);
        GL20.glAttachShader(programID, fragmentShaderID);

        bindAttributes();

        GL20.glLinkProgram(programID);
        GL20.glValidateProgram(programID);

        getAllUniformLocations();
    }

    protected abstract void getAllUniformLocations();

    int getUniformLocation(String uniformName) {
        return GL20.glGetUniformLocation(programID, uniformName);
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
    }

    public void start() {
        GL20.glUseProgram(programID);
    }

    public void stop() {
        GL20.glUseProgram(0);
    }

    public void cleanUp() {
        stop();

        GL20.glDetachShader(programID, vertexShaderID);
        GL20.glDetachShader(programID, fragmentShaderID);

        GL20.glDeleteShader(vertexShaderID);
        GL20.glDeleteShader(fragmentShaderID);

        GL20.glDeleteProgram(programID);
    }

    protected abstract void bindAttributes();

    protected void bindAttribute(int attribute, String variableName) {
        GL20.glBindAttribLocation(programID, attribute, variableName);
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
