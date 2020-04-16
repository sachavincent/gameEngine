package models;

import java.util.ArrayList;
import java.util.List;
import renderEngine.Loader;
import textures.ModelTexture;
import util.math.Vector3f;

public class TexturedModel {

    private RawModel     rawModel;
    private ModelTexture modelTexture;

    public TexturedModel(RawModel rawModel, ModelTexture modelTexture) {
        this.rawModel = rawModel;
        this.modelTexture = modelTexture;
    }

    public TexturedModel(RawModel rawModel) {
        this.rawModel = rawModel;

        this.modelTexture = ModelTexture.DEFAULT_MODEL;
    }

    @Deprecated
    public TexturedModel(RawModel model, TypeModel typeModel) {
        this.rawModel = model;
        this.modelTexture = ModelTexture.DEFAULT_MODEL;

        switch (typeModel) {
            case BOUNDING_BOX:
                this.rawModel = model;
//                createBoundingBox(model);
                break;
            case SELECTION_BOX:
                this.rawModel = model;
//                createSelection(model);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    @Deprecated
    private void createSelection(RawModel model) {
        Vector3f min = model.getMin();
        Vector3f max = model.getMax();

        float minX = min.x;
        float minY = min.y;
        float minZ = min.z;

        float maxX = max.x;
        float maxY = max.y;
        float maxZ = max.z;

        float[] verticesArray = new float[12];
        int[] indicesArray;

        List<Integer> indices = new ArrayList<>();
        List<Float> vertices = new ArrayList<>();


        Vector3f fll = new Vector3f(minX, minY, minZ);
        Vector3f flr = new Vector3f(minX, minY, maxZ);
        Vector3f ful = new Vector3f(minX, maxY, minZ);
        Vector3f fur = new Vector3f(minX, maxY, maxZ);

        Vector3f bll = new Vector3f(maxX, minY, minZ);
        Vector3f blr = new Vector3f(maxX, minY, maxZ);
        Vector3f bul = new Vector3f(maxX, maxY, minZ);
        Vector3f bur = new Vector3f(maxX, maxY, maxZ);

        addVertexToVerticesList(vertices, fll);
        addVertexToVerticesList(vertices, flr);
        addVertexToVerticesList(vertices, bll);
        addVertexToVerticesList(vertices, blr);


//        addVertexToVerticesList(vertices, fll);
//        addVertexToVerticesList(vertices, flr);
//        addVertexToVerticesList(vertices, ful);
//        addVertexToVerticesList(vertices, fur);

        indices.add(0);
        indices.add(1);
        indices.add(2);
        indices.add(1);
        indices.add(2);
        indices.add(3);

        indicesArray = indices.stream().mapToInt(i -> i).toArray();

        int i = 0;
        for (float v : vertices)
            verticesArray[i++] = v;

        this.rawModel = Loader.getInstance().loadToVAO(verticesArray, new float[12], new float[12], indicesArray);
    }

    @Deprecated
    private void createBoundingBox(RawModel model) {
        Vector3f min = model.getMin();
        Vector3f max = model.getMax();

        float minX = min.x;
        float minY = min.y;
        float minZ = min.z;

        float maxX = max.x;
        float maxY = max.y;
        float maxZ = max.z;

        float[] verticesArray = new float[24];
        int[] indicesArray;

        List<Integer> indices = new ArrayList<>();
        List<Float> vertices = new ArrayList<>();


        Vector3f fll = new Vector3f(minX, minY, minZ);
        Vector3f flr = new Vector3f(minX, minY, maxZ);
        Vector3f ful = new Vector3f(minX, maxY, minZ);
        Vector3f fur = new Vector3f(minX, maxY, maxZ);

        Vector3f bll = new Vector3f(maxX, minY, minZ);
        Vector3f blr = new Vector3f(maxX, minY, maxZ);
        Vector3f bul = new Vector3f(maxX, maxY, minZ);
        Vector3f bur = new Vector3f(maxX, maxY, maxZ);

        addVertexToVerticesList(vertices, fll);
        addVertexToVerticesList(vertices, flr);
        addVertexToVerticesList(vertices, ful);
        addVertexToVerticesList(vertices, fur);
        addVertexToVerticesList(vertices, bll);
        addVertexToVerticesList(vertices, blr);
        addVertexToVerticesList(vertices, bul);
        addVertexToVerticesList(vertices, bur);


        indices.add(0);
        indices.add(1);
        indices.add(2);
        indices.add(1);
        indices.add(2);
        indices.add(3);


        indices.add(4);
        indices.add(5);
        indices.add(6);
        indices.add(5);
        indices.add(6);
        indices.add(7);


        indices.add(0);
        indices.add(2);
        indices.add(4);
        indices.add(2);
        indices.add(4);
        indices.add(6);


        indices.add(1);
        indices.add(3);
        indices.add(5);
        indices.add(3);
        indices.add(5);
        indices.add(7);


        indices.add(2);
        indices.add(3);
        indices.add(6);
        indices.add(3);
        indices.add(6);
        indices.add(7);


        indices.add(0);
        indices.add(1);
        indices.add(4);
        indices.add(1);
        indices.add(4);
        indices.add(5);

        indicesArray = indices.stream().mapToInt(i -> i).toArray();

        int i = 0;
        for (float v : vertices)
            verticesArray[i++] = v;


        this.rawModel = Loader.getInstance().loadToVAO(verticesArray, new float[24], new float[24], indicesArray);
    }

    private void addVertexToVerticesList(List<Float> vertices, Vector3f point) {
        vertices.add(point.x);
        vertices.add(point.y);
        vertices.add(point.z);
    }


    public RawModel getRawModel() {
        return this.rawModel;
    }

    public ModelTexture getModelTexture() {
        return this.modelTexture;
    }

    public enum TypeModel {
        BOUNDING_BOX,
        SELECTION_BOX;
    }
}
