package shaders;

public class HorizontalBlurShader extends ShaderProgram {

    private static final String VERTEX_FILE   = "horizontalBlurVertex.glsl";
    private static final String FRAGMENT_FILE = "blurFragment.glsl";

    private int location_targetWidth;

    public HorizontalBlurShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    public void loadTargetWidth(float width) {
        super.loadFloat(location_targetWidth, width);
    }

    @Override
    protected void getAllUniformLocations() {
        location_targetWidth = super.getUniformLocation("targetWidth");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }

}
