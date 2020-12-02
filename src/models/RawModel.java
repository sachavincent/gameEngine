package models;

import java.util.Objects;
import util.math.Vector3f;

public class RawModel {

    private final int vaoID;
    private final int vertexCount;

    private Vector3f min, max;

    public RawModel(int vaoID, int vertexCount) {
        this.vaoID = vaoID;
        this.vertexCount = vertexCount;
    }

    public RawModel(int vaoID, int vertexCount, Vector3f min, Vector3f max) {
        this(vaoID, vertexCount);

        this.min = min;
        this.max = max;
    }

    public int getVaoID() {
        return this.vaoID;
    }

    public int getVertexCount() {
        return this.vertexCount;
    }

    public Vector3f getMin() {
        return this.min;
    }

    public Vector3f getMax() {
        return this.max;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        RawModel rawModel = (RawModel) o;
        return vaoID == rawModel.vaoID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(vaoID);
    }
}
