package util.parsing.colladaParser.dataStructures;

import util.math.Matrix4f;

public class JointTransformData {

    private final String   jointId;
    private final Matrix4f jointLocalTransform;

    public JointTransformData(String jointId, Matrix4f jointLocalTransform) {
        this.jointId = jointId;
        this.jointLocalTransform = jointLocalTransform;
    }

    public String getJointId() {
        return this.jointId;
    }

    public Matrix4f getJointLocalTransform() {
        return this.jointLocalTransform;
    }
}
