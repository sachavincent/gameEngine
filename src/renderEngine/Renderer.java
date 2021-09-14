package renderEngine;

import renderEngine.shaders.ShaderProgram;

public abstract class Renderer<Shader extends ShaderProgram> {

    protected final Shader shader;

    protected Renderer(Shader shader) {
        this.shader = shader;
    }

    protected Renderer(Shader shader, GameObjectRenderer.LoadShaderCallback<Shader> loadShaderCallback) {
        this.shader = shader;

        loadShaderCallback.onLoadingShader(this.shader);
    }

    public abstract void render();

    protected void cleanUp() {
        this.shader.cleanUp();
    }

    public final ShaderProgram getShader() {
        return this.shader;
    }

    @FunctionalInterface
    public interface LoadShaderCallback<S extends ShaderProgram> {

        void on(S shader);

        default void onLoadingShader(S shader) {
            shader.start();

            on(shader);

            shader.stop();
        }
    }
}
