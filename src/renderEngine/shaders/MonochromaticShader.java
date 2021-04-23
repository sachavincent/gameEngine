package renderEngine.shaders;

public class MonochromaticShader extends ShaderProgram {

    private static final String VERTEX_FILE   = "grayscaleVertex.glsl";
    private static final String FRAGMENT_FILE = "grayscaleFragment.glsl";

    public MonochromaticShader() {
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
