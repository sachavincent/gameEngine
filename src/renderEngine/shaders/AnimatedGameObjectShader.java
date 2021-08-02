package renderEngine.shaders;

import util.math.Matrix4f;

import java.util.Iterator;
import java.util.List;

public class AnimatedGameObjectShader extends GameObjectShader implements IGameObjectShader {

    private static final int MAX_JOINTS = 50;

    private static final String VERTEX_FILE = "animatedGameObjectVertexShader.glsl";
    private static final String FRAGMENT_FILE = "gameObjectFragmentShader.glsl";

    private int[] location_jointTransforms;

    public AnimatedGameObjectShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");
        super.bindAttribute(2, "normal");
        super.bindAttribute(3, "jointIndices");
        super.bindAttribute(4, "weights");
        super.bindAttribute(5, "tangent");
        super.bindAttribute(6, "globalTransformationMatrix");
    }

    @Override
    protected void getAllUniformLocations() {
        super.getAllUniformLocations();

        this.location_jointTransforms = new int[MAX_JOINTS];
        for (int i = 0; i < MAX_JOINTS; i++) {
            this.location_jointTransforms[i] = getUniformLocation("jointTransforms[" + i + "]");
        }
    }

    public void loadJointTransforms(List<Matrix4f> jointTransforms) {
        Iterator<Matrix4f> iterator = jointTransforms.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            loadMatrix(this.location_jointTransforms[i], iterator.next());
            i++;
        }
        for (; i < MAX_JOINTS; i++) {
            loadMatrix(this.location_jointTransforms[i], new Matrix4f());
        }
    }
}
