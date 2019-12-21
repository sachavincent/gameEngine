package shaders;

public class VerticalBlurShader extends ShaderProgram {

    private static final String VERTEX_FILE   = "verticalBlurVertex.glsl";
    private static final String FRAGMENT_FILE = "blurFragment.glsl";

    private int location_targetHeight;

    public VerticalBlurShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    public void loadTargetHeight(float height) {
        super.loadFloat(location_targetHeight, height);
    }

    @Override
    protected void getAllUniformLocations() {
        location_targetHeight = super.getUniformLocation("targetHeight");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }
}
