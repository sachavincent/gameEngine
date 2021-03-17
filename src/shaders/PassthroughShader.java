package shaders;

public class PassthroughShader extends ShaderProgram {

    private static final String VERTEX_FILE   = "passthroughVertex.glsl";
    private static final String FRAGMENT_FILE = "passthroughFragment.glsl";

    public PassthroughShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void getAllUniformLocations() {
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }
}
