package models;

import util.math.Vector3f;

public class InstancedRawModel extends RawModel {

    private final int nbInstances;
    private final int vboID;

    public InstancedRawModel(int vboID, int vaoID, int vertexCount, int nbInstances) {
        super(vaoID, vertexCount);

        this.vboID = vboID;
        this.nbInstances = nbInstances;
    }

    public InstancedRawModel(int vboID, int vaoID, int vertexCount, Vector3f min, Vector3f max, int nbInstances) {
        super(vaoID, vertexCount, min, max);

        this.vboID = vboID;
        this.nbInstances = nbInstances;
    }

    public int getNbInstances() {
        return this.nbInstances;
    }

    public int getVboID() {
        return this.vboID;
    }
}
