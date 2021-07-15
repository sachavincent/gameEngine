package util;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL41;
import util.math.Vector3f;
import util.parsing.ModelType;
import util.parsing.colladaParser.dataStructures.MeshData;
import util.parsing.objParser.Material;

public class Vao {

    private static final int                  BYTES_PER_FLOAT = 4;
    private static final int                  BYTES_PER_INT   = 4;
    public               Map<Material, int[]> materialIndices;

    private final int       id;
    private final List<Vbo> dataVbos;
    private       Vbo       indexVbo;

    private boolean   instanced;
    private Vbo       instanceVbo;
    private ModelType modelType;

    public static Vao create() {
        int id = GL30.glGenVertexArrays();
        return new Vao(id);
    }

    private Vao(int id) {
        this.id = id;
        this.dataVbos = new ArrayList<>();
    }

    public Vbo getIndexVbo() {
        return this.indexVbo;
    }

    public int getIndexCount() {
        return this.getIndexVbo().getDataLength();
    }

    public final void bind(int... attributes) {
        bind();

        IntStream.of(attributes).forEachOrdered(GL20::glEnableVertexAttribArray);
    }

    public final void unbind(int... attributes) {
        IntStream.of(attributes).forEachOrdered(GL20::glDisableVertexAttribArray);

        unbind();
    }

    public void createIndexBuffer(int[] indices) {
        Vbo indexVbo = Vbo.create(GL15.GL_ELEMENT_ARRAY_BUFFER);
        indexVbo.bind();
        indexVbo.storeData(indices);
        indexVbo.unbind();
        this.indexVbo = indexVbo;
    }
    public Vbo createIndexBufferTMP(int[] indices) {
        Vbo indexVbo = Vbo.create(GL15.GL_ELEMENT_ARRAY_BUFFER);
        indexVbo.bind();
        indexVbo.storeData(indices);
        indexVbo.unbind();
        return indexVbo;
    }

    public void createAttribute(int attribute, float[] data, int attrSize) {
        Vbo dataVbo = Vbo.create(GL15.GL_ARRAY_BUFFER);
        dataVbo.bind();
        dataVbo.storeData(data);
        GL20.glVertexAttribPointer(attribute, attrSize, GL11.GL_FLOAT, false, attrSize * BYTES_PER_FLOAT, 0);
        dataVbo.unbind();
        this.dataVbos.add(dataVbo);
    }

    public void createIntAttribute(int attribute, int[] data, int attrSize) {
        Vbo dataVbo = Vbo.create(GL15.GL_ARRAY_BUFFER);
        dataVbo.bind();
        dataVbo.storeData(data);
        GL30.glVertexAttribIPointer(attribute, attrSize, GL11.GL_INT, attrSize * BYTES_PER_INT, 0);
        dataVbo.unbind();
        this.dataVbos.add(dataVbo);
    }

    public void delete() {
        GL30.glDeleteVertexArrays(this.id);
        this.dataVbos.forEach(Vbo::delete);
        this.indexVbo.delete();
    }

    private void bind() {
        GL30.glBindVertexArray(this.id);
    }

    private void unbind() {
        GL30.glBindVertexArray(0);
    }

    /**
     * Stores the mesh data in a VAO.
     *
     * @param data - all the data about the mesh that needs to be stored in the
     * VAO.
     * @param modelType - type of model
     * @return The VAO containing all the mesh data for the model.
     */
    public static Vao createVao(MeshData data, ModelType modelType) {
        Vao vao = Vao.create();
        vao.modelType = modelType;
        vao.bind();

        vao.createIndexBuffer(data.getIndices());
        vao.createAttribute(0, data.getVertices(), 3);
        vao.createAttribute(1, data.getTextureCoords(), 2);
        vao.createAttribute(2, data.getNormals(), 3);
        vao.materialIndices = data.temp;
        int[] d = data.getMaterialIndices().values().stream().
                flatMapToInt(Arrays::stream).toArray();
//        vao.createIntAttribute(3, data1, 1);
//        GL41.glVertexAttribDivisor(3, 1);
        Vbo dataVbo = Vbo.create(GL15.GL_ARRAY_BUFFER);
        dataVbo.bind();
        dataVbo.storeData(d);
        GL30.glVertexAttribIPointer(3, 1, GL11.GL_INT, 1 * BYTES_PER_INT, 0);
        GL41.glVertexAttribDivisor(3, 1);
        dataVbo.unbind();
        vao.dataVbos.add(dataVbo);

        Vbo vbo;
        switch (modelType) {
            case DEFAULT:
                break;
            case ANIMATED:
                vao.createIntAttribute(4, data.getJointIds(), 3);
                vao.createAttribute(5, data.getVertexWeights(), 3);
                break;
            case INSTANCED:
                vbo = Vbo.create(GL_ARRAY_BUFFER);
                vbo.bind();
                vao.dataVbos.add(vbo);
                vao.instanced = true;
                vao.instanceVbo = vbo;

                for (int i = 0; i < 4; i++) {
                    GL20.glVertexAttribPointer(i + 5, 4, GL_FLOAT, false, 64, i * 16);
                    GL41.glVertexAttribDivisor(i + 5, 1);
                    glEnableVertexAttribArray(i + 5);
                }
                vbo.unbind();
                break;
            case WITH_NORMAL_MAP:
                vao.createAttribute(4, data.getTangents(), 3);
                break;
            case ANIMATED_INSTANCED:
                vao.createIntAttribute(4, data.getJointIds(), 3);
                vao.createAttribute(5, data.getVertexWeights(), 3);

                vbo = Vbo.create(GL_ARRAY_BUFFER);
                vbo.bind();
                vao.dataVbos.add(vbo);
                vao.instanced = true;
                vao.instanceVbo = vbo;

                for (int i = 0; i < 4; i++) {
                    GL20.glVertexAttribPointer(i + 6, 4, GL_FLOAT, false, 64, i * 16);
                    GL41.glVertexAttribDivisor(i + 6, 1);
                    glEnableVertexAttribArray(i + 6);
                }
                vbo.unbind();
                break;
            case ANIMATED_WITH_NORMAL_MAP:
                vao.createAttribute(3, data.getTangents(), 3);
                vao.createIntAttribute(4, data.getJointIds(), 3);
                vao.createAttribute(5, data.getVertexWeights(), 3);
                break;
            case INSTANCED_WITH_NORMAL_MAP:
                vao.createAttribute(4, data.getTangents(), 3);

                vbo = Vbo.create(GL_ARRAY_BUFFER);
                vbo.bind();
                vao.dataVbos.add(vbo);
                vao.instanced = true;
                vao.instanceVbo = vbo;

                for (int i = 0; i < 4; i++) {
                    GL20.glVertexAttribPointer(i + 5, 4, GL_FLOAT, false, 64, i * 16);
                    GL41.glVertexAttribDivisor(i + 5, 1);
                    glEnableVertexAttribArray(i + 5);
                }
                vbo.unbind();
                break;
            case ANIMATED_INSTANCED_WITH_NORMAL_MAP:
                vao.createAttribute(3, data.getTangents(), 3);
                vao.createIntAttribute(4, data.getJointIds(), 3);
                vao.createAttribute(5, data.getVertexWeights(), 3);

                vbo = Vbo.create(GL_ARRAY_BUFFER);
                vbo.bind();
                vao.dataVbos.add(vbo);//TODO: Do animated attributes number
                vao.instanced = true;
                vao.instanceVbo = vbo;

                for (int i = 0; i < 4; i++) {
                    GL20.glVertexAttribPointer(i + 6, 4, GL_FLOAT, false, 64, i * 16);
                    GL41.glVertexAttribDivisor(i + 6, 1);
                    glEnableVertexAttribArray(i + 6);
                }
                vbo.unbind();
                break;
        }
        vao.unbind();
        return vao;
    }

    public int getId() {
        return this.id;
    }

    public boolean isInstanced() {
        return this.instanced;
    }

    public Vbo getInstanceVbo() {
        return this.instanceVbo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Vao vao = (Vao) o;
        return this.id == vao.id;
    }

    public ModelType getModelType() {
        return this.modelType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    public Vector3f getMin() {
        return new Vector3f(-5, 0, -5);
    }

    public Vector3f getMax() {
        return new Vector3f(5, 5, 5);
    }
}