package renderEngine.structures;

import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;

import org.lwjgl.opengl.GL41;

public class InstancedAttribute extends AttributeData<Float> {

    private final int nbAttributes;

    public InstancedAttribute(int attributeNumber, int attributeSize, DataType dataType, int nbAttributes) {
        super(attributeNumber, attributeSize, new Float[0], dataType);

        this.nbAttributes = nbAttributes;
    }

    @Override
    public <V extends Vao> void create(V vao) {
        Vbo vbo = new Vbo(GL_ARRAY_BUFFER);
        vbo.bind();
//        vao.dataVbos.add(vbo);
        vao.setInstanceVbo(vbo);

        for (int i = 0; i < this.nbAttributes; i++) {
            int number = i + this.attributeNumber;
            this.dataType.point(number, this.attributeSize,
                    this.nbAttributes * this.attributeSize * this.dataType.getSize(),
                    i * this.attributeSize * this.dataType.getSize());
            GL41.glVertexAttribDivisor(number, 1);
            glEnableVertexAttribArray(number);
        }
        vbo.unbind();
    }

    public final int getNbAttributes() {
        return this.nbAttributes;
    }
}
