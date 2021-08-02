package renderEngine.shaders;

public class ContrastShader extends ShaderProgram {

    private static final String VERTEX_FILE   = "contrastVertex.glsl";
    private static final String FRAGMENT_FILE = "contrastFragment.glsl";

    public ContrastShader() {
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
