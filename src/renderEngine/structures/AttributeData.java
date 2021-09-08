package renderEngine.structures;

import static org.lwjgl.opengl.GL11.*;

import java.util.Collection;
import org.lwjgl.opengl.GL33;
import renderEngine.structures.AttributeData.DataType.PointCallback;
import util.Utils;

public class AttributeData<Type extends Number> {

    protected final int attributeNumber;
    protected final int attributeSize;

    protected final Type[]   data;
    protected final DataType dataType;

    public AttributeData(int attributeNumber, int attributeSize, Type[] data, DataType dataType) {
        this.attributeNumber = attributeNumber;
        this.attributeSize = attributeSize;
        this.data = data;
        this.dataType = dataType;
    }

    public AttributeData(int attributeNumber, int attributeSize, Collection<Type> data, DataType dataType) {
        this(attributeNumber, attributeSize, Utils.toArray(data), dataType);
    }

    public final int getAttributeNumber() {
        return this.attributeNumber;
    }

    public final int getAttributeSize() {
        return this.attributeSize;
    }

    public final Type[] getData() {
        return this.data;
    }

    public <V extends Vao> void create(V vao) {
        vao.createAttribute(this);
    }

    public final DataType getDataType() {
        return this.dataType;
    }

    private static final PointCallback DEFAULT_CALLBACK = (nb, size, type, totalSize, ptr) ->
            GL33.glVertexAttribPointer(nb, size, type, false, totalSize, ptr);

    public enum DataType {
        BYTE(GL_BYTE, 1),
        UNSIGNED_BYTE(GL_UNSIGNED_BYTE, 1),
        SHORT(GL_SHORT, 2),
        UNSIGNED_SHORT(GL_UNSIGNED_SHORT, 2),
        INT(GL_INT, 4, GL33::glVertexAttribIPointer),
        UNSIGNED_INT(GL_UNSIGNED_INT, 4, GL33::glVertexAttribIPointer),
        FLOAT(GL_FLOAT, 4),
        TWO_BYTES(GL_2_BYTES, 2),
        THREE_BYTES(GL_3_BYTES, 3),
        FOUR_BYTES(GL_4_BYTES, 4),
        DOUBLE(GL_DOUBLE, 8);

        private final int           glType;
        private final int           size; // size in bytes
        private final PointCallback pointCallback;

        DataType(int glType, int size, PointCallback pointCallback) {
            this.glType = glType;
            this.size = size;
            this.pointCallback = pointCallback;
        }

        DataType(int glType, int size) {
            this(glType, size, DEFAULT_CALLBACK);
        }

        public int getGlType() {
            return this.glType;
        }

        public int getSize() {
            return this.size;
        }

        public void point(int attributeNumber, int attributeSize, int pointer) {
            point(attributeNumber, attributeSize, attributeSize * this.size, pointer);
        }

        public void point(int attributeNumber, int attributeSize, int totalSize, int pointer) {
            this.pointCallback.onPoint(attributeNumber, attributeSize, this.glType, totalSize, pointer);
        }

        @FunctionalInterface
        protected interface PointCallback {

            void onPoint(int attributeNumber, int attributeSize, int glType, int totalSize, int pointer);
        }
    }
}
