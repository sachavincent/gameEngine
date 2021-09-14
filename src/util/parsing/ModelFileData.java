package util.parsing;

import java.util.List;
import util.math.Vector2f;
import util.math.Vector3f;

public class ModelFileData {

    private final List<Vertex>   vertices;
    private final List<Vector2f> textures;
    private final List<Vector3f> normals;

    private final List<Integer> indices;

    public ModelFileData(List<Vertex> vertices, List<Vector2f> textures, List<Vector3f> normals,
            List<Integer> indices) {
        this.vertices = vertices;
        this.textures = textures;
        this.normals = normals;
        this.indices = indices;
    }

    public List<Vertex> getVertices() {
        return this.vertices;
    }

    public List<Vector2f> getTextures() {
        return this.textures;
    }

    public List<Vector3f> getNormals() {
        return this.normals;
    }

    public List<Integer> getIndices() {
        return this.indices;
    }
}
