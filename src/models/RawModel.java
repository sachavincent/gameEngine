package models;

import java.util.Objects;

public class RawModel {

    private final int     vaoID;
    private final int     vboID;
    private final int     vertexCount;

    public RawModel(int vaoID, int vboID, int vertexCount, boolean instanced) {
        this.vaoID = vaoID;
        this.vboID = vboID;
        this.vertexCount = vertexCount;
    }

    public int getVaoID() {
        return this.vaoID;
    }

    public int getVertexCount() {
        return this.vertexCount;
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
