package util.parsing.colladaParser.dataStructures;

import util.math.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class JointData {

    private final int index;
    private final String id;
    private final Matrix4f bindLocalTransform;
    private final List<JointData> children = new ArrayList<>();

    public JointData(int index, String id, Matrix4f bindLocalTransform) {
        this.index = index;
        this.id = id;
        this.bindLocalTransform = bindLocalTransform;
    }

    public void addChild(JointData child) {
        this.children.add(child);
    }

    public List<JointData> getChildren() {
        return this.children;
    }

    public String getId() {
        return this.id;
    }

    public int getIndex() {
        return this.index;
    }

    public Matrix4f getBindLocalTransform() {
        return this.bindLocalTransform;
    }
}
