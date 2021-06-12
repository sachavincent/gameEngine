package objConverter;

import java.util.ArrayList;
import java.util.List;
import util.math.Vector3f;

public class VertexNM {

    private static final int NO_INDEX = -1;

    private final Vector3f       position;
    private       int            textureIndex    = NO_INDEX;
    private       int            normalIndex     = NO_INDEX;
    private       VertexNM       duplicateVertex = null;
    private final int            index;
    private final float          length;
    private final List<Vector3f> tangents        = new ArrayList<>();
    private       Vector3f       averagedTangent = new Vector3f(0, 0, 0);

    public VertexNM(int index, Vector3f position) {
        this.index = index;
        this.position = position;
        this.length = position.length();
    }

    public void addTangent(Vector3f tangent) {
        this.tangents.add(tangent);
    }

    public void averageTangents() {
        if (this.tangents.isEmpty()) {
            return;
        }
        for (Vector3f tangent : this.tangents) {
            this.averagedTangent = Vector3f.add(this.averagedTangent, tangent, null);
        }
        this.averagedTangent.normalise();
    }

    public Vector3f getAverageTangent() {
        return this.averagedTangent;
    }

    public int getIndex() {
        return this.index;
    }

    public float getLength() {
        return this.length;
    }

    public boolean isSet() {
        return this.textureIndex != NO_INDEX && this.normalIndex != NO_INDEX;
    }

    public boolean hasSameTextureAndNormal(int textureIndexOther, int normalIndexOther) {
        return textureIndexOther == textureIndex && normalIndexOther == normalIndex;
    }

    public void setTextureIndex(int textureIndex) {
        this.textureIndex = textureIndex;
    }

    public void setNormalIndex(int normalIndex) {
        this.normalIndex = normalIndex;
    }

    public Vector3f getPosition() {
        return this.position;
    }

    public int getTextureIndex() {
        return this.textureIndex;
    }

    public int getNormalIndex() {
        return this.normalIndex;
    }

    public VertexNM getDuplicateVertex() {
        return this.duplicateVertex;
    }

    public void setDuplicateVertex(VertexNM duplicateVertex) {
        this.duplicateVertex = duplicateVertex;
    }
}