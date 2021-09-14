package renderEngine.structures;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import renderEngine.structures.AttributeData.DataType;
import util.math.Vector3f;

public abstract class Vao {

    protected final int       id;
    protected final List<Vbo> dataVbos;

    protected       boolean                instanced;
    protected       Vbo                    instanceVbo;
    protected final List<AttributeData<?>> attributes;

    protected Vao() {
        this.id = GL30.glGenVertexArrays();

        this.dataVbos = new ArrayList<>();
        this.attributes = new ArrayList<>();
    }

    public final void bind() {
        GL30.glBindVertexArray(this.id);

        this.attributes.stream().map(AttributeData::getAttributeNumber).
                forEach(GL20::glEnableVertexAttribArray);
    }

    public final void unbind() {
        this.attributes.stream().map(AttributeData::getAttributeNumber).
                forEach(GL20::glDisableVertexAttribArray);

        GL30.glBindVertexArray(0);
    }

    @Deprecated
    public void createFloatAttribute(AttributeData<Float> attributeData) {
        if (attributeData == null || attributeData.getData().length == 0)
            return;
        Vbo dataVbo = createAttributeData(attributeData);
        dataVbo.bind();
        dataVbo.storeData(attributeData.getData());
        int attributeSize = attributeData.getAttributeSize();
        GL20.glVertexAttribPointer(attributeData.getAttributeNumber(),
                attributeSize, GL11.GL_FLOAT, false, attributeSize * 4, 0);
        dataVbo.unbind();
        this.dataVbos.add(dataVbo);
    }

    @Deprecated
    public void createIntAttribute(AttributeData<Integer> attributeData) {
        if (attributeData == null || attributeData.getData().length == 0)
            return;
        Vbo dataVbo = createAttributeData(attributeData);
        dataVbo.bind();
        dataVbo.storeData(attributeData.getData());
        int attributeSize = attributeData.getAttributeSize();
        GL30.glVertexAttribIPointer(attributeData.getAttributeNumber(),
                attributeSize, GL11.GL_INT, attributeSize * 4, 0);
        dataVbo.unbind();
        this.dataVbos.add(dataVbo);
    }

    private Vbo createAttributeData(AttributeData<?> attributeData) {
        this.attributes.add(attributeData);

        return new Vbo(GL15.GL_ARRAY_BUFFER);
    }

    void createAttribute(AttributeData<?> attributeData) {
        if (attributeData == null || attributeData.getData().length == 0)
            return;

        Vbo dataVbo = createAttributeData(attributeData);
        dataVbo.bind();
        dataVbo.storeData(attributeData.getData());
        int attributeSize = attributeData.getAttributeSize();
        DataType dataType = attributeData.getDataType();
        dataType.point(attributeData.getAttributeNumber(), attributeSize, 0);
        dataVbo.unbind();
        this.dataVbos.add(dataVbo);
    }

    public void delete() {
        GL30.glDeleteVertexArrays(this.id);
        this.dataVbos.forEach(Vbo::delete);
    }

    public static <V extends Vao, DataType extends AbstractData<?>> V createVao(DataType data, Class<V> type) {
        if (data == null || data.isEmpty())
            return null;

        V vao;
        try {
            vao = type.cast(data.getVaoType().getConstructor().newInstance());
        } catch (ReflectiveOperationException ignored) {
            return null;
        }

        vao.bind();

        for (AttributeData<?> attributeData : data.getAttributesData())
            attributeData.create(vao);

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

    public void setInstanceVbo(Vbo instanceVbo) {
        this.instanceVbo = instanceVbo;
        this.instanced = true;
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