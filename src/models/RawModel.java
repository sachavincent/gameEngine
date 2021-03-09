package models;

import java.util.Objects;
import util.math.Vector3f;

public class RawModel {

    private final int vaoID;
    private final int vboID;
    private final int vertexCount;
    private final boolean instanced;

    private Vector3f min, max;

    public RawModel(int vaoID, int vboID, int vertexCount, boolean instanced) {
        this.vaoID = vaoID;
        this.vboID = vboID;
        this.vertexCount = vertexCount;
        this.instanced = instanced;
    }

    public RawModel(int vaoID, int vboID, int vertexCount, Vector3f min, Vector3f max, boolean instanced) {
        this(vaoID, vboID, vertexCount, instanced);

        this.min = min;
        this.max = max;
    }

    public boolean isInstanced() {
        return this.instanced;
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

    public int getVboID() {
        return this.vboID;
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
