package renderEngine.shaders;

import util.math.Matrix4f;

public class SunShader extends ShaderProgram {

    private static final String VERTEX_SHADER = "sunVertexShader.glsl";
    private static final String FRAGMENT_SHADER = "sunFragmentShader.glsl";

    private int location_sunTexture;
    private int location_MVPMatrix;

    public SunShader() {
        super(VERTEX_SHADER, FRAGMENT_SHADER);
    }

    @Override
    protected void getAllUniformLocations() {
        this.location_sunTexture = getUniformLocation("sunTexture");
        this.location_MVPMatrix = getUniformLocation("MVPMatrix");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }

    public void connectTextureUnits() {
        loadInt(this.location_sunTexture, 0);
    }

    public void loadMVPMatrix(Matrix4f MVPMatrix) {
        loadMatrix(this.location_MVPMatrix, MVPMatrix);
    }
}
