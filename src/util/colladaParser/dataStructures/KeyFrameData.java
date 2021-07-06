package util.colladaParser.dataStructures;

import java.util.ArrayList;
import java.util.List;

public class KeyFrameData {

    private final float                    time;
    private final List<JointTransformData> jointTransforms = new ArrayList<>();

    public KeyFrameData(float time) {
        this.time = time;
    }

    public void addJointTransform(JointTransformData transform) {
        jointTransforms.add(transform);
    }

	public float getTime() {
		return this.time;
	}

	public List<JointTransformData> getJointTransforms() {
		return this.jointTransforms;
	}
}
