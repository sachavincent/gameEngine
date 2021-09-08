package renderEngine.structures;

import org.lwjgl.opengl.GL15;

public class IndexVbo extends Vbo {

    private static final int TYPE = GL15.GL_ELEMENT_ARRAY_BUFFER;

    public IndexVbo() {
        super(TYPE);
    }
}
