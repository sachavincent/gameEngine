package util.parsing.colladaParser.colladaLoader;

import java.util.ArrayList;
import java.util.List;
import util.math.Matrix4f;
import util.math.Vector2f;
import util.math.Vector3f;
import util.math.Vector4f;
import util.parsing.colladaParser.dataStructures.MeshData;
import util.parsing.colladaParser.dataStructures.Vertex;
import util.parsing.colladaParser.dataStructures.VertexSkinData;
import util.parsing.colladaParser.xmlParser.XmlNode;

public class GeometryLoader {

    private static final Matrix4f CORRECTION = new Matrix4f()
            .rotate((float) Math.toRadians(-90), new Vector3f(1, 0, 0));

    private final XmlNode meshData;

    private final List<VertexSkinData> vertexWeights;

    private float[] verticesArray;
    private float[] normalsArray;
    private float[] texturesArray;
    private int[]   indicesArray;
    private int[]   jointIdsArray;
    private float[] weightsArray;

    List<Vertex>   vertices = new ArrayList<>();
    List<Vector2f> textures = new ArrayList<>();
    List<Vector3f> normals  = new ArrayList<>();
    List<Integer>  indices  = new ArrayList<>();

    public GeometryLoader(XmlNode geometryNode, List<VertexSkinData> vertexWeights) {
        this.vertexWeights = vertexWeights;
        this.meshData = geometryNode.getChild("geometry").getChild("mesh");
    }

    public MeshData extractModelData() {
        readRawData();
        assembleVertices();
        removeUnusedVertices();
        initArrays();
        convertDataToArrays();
        convertIndicesListToArray();
        return new MeshData(this.verticesArray, this.texturesArray, this.normalsArray, this.indicesArray,
                this.jointIdsArray, this.weightsArray);
    }

    private void readRawData() {
        readPositions();
        readNormals();
        readTextureCoords();
    }

    private void readPositions() {
        String positionsId = this.meshData.getChild("vertices").getChild("input").getAttribute("source").substring(1);
        XmlNode positionsData = this.meshData.getChildWithAttribute("source", "id", positionsId)
                .getChild("float_array");
        int count = Integer.parseInt(positionsData.getAttribute("count"));
        String[] posData = positionsData.getData().split(" ");
        for (int i = 0; i < count / 3; i++) {
            float x = Float.parseFloat(posData[i * 3]);
            float y = Float.parseFloat(posData[i * 3 + 1]);
            float z = Float.parseFloat(posData[i * 3 + 2]);
            Vector4f position = new Vector4f(x, y, z, 1);
            Matrix4f.transform(CORRECTION, position, position);
            this.vertices.add(new Vertex(this.vertices.size(), new Vector3f(position.x, position.y, position.z),
                    this.vertexWeights.get(this.vertices.size())));
        }
    }

    private void readNormals() {
        this.meshData.getChildren("triangles").forEach(triangleNode -> {
            String normalsId = triangleNode.getChildWithAttribute("input", "semantic", "NORMAL")
                    .getAttribute("source").substring(1);
            XmlNode normalsData = this.meshData.getChildWithAttribute("source", "id", normalsId)
                    .getChild("float_array");
            int count = Integer.parseInt(normalsData.getAttribute("count"));
            String[] normData = normalsData.getData().split(" ");
            for (int i = 0; i < count / 3; i++) {
                float x = Float.parseFloat(normData[i * 3]);
                float y = Float.parseFloat(normData[i * 3 + 1]);
                float z = Float.parseFloat(normData[i * 3 + 2]);
                Vector4f norm = new Vector4f(x, y, z, 0f);
                Matrix4f.transform(CORRECTION, norm, norm);
                this.normals.add(new Vector3f(norm.x, norm.y, norm.z));
            }
        });
    }

    private void readTextureCoords() {
        List<XmlNode> triangles = this.meshData.getChildren("triangles");
        triangles.forEach(triangleNode -> {
            String texCoordsId = triangleNode.getChildWithAttribute("input", "semantic", "TEXCOORD")
                    .getAttribute("source").substring(1);
            XmlNode texCoordsData = this.meshData.getChildWithAttribute("source", "id", texCoordsId)
                    .getChild("float_array");
            int count = Integer.parseInt(texCoordsData.getAttribute("count"));
            String[] texData = texCoordsData.getData().split(" ");
            for (int i = 0; i < count / 2; i++) {
                float s = Float.parseFloat(texData[i * 2]);
                float t = Float.parseFloat(texData[i * 2 + 1]);
                this.textures.add(new Vector2f(s, t));
            }
        });
    }

    private void assembleVertices() {
        this.meshData.getChildren("triangles").forEach(triangleNode -> {
            int typeCount = triangleNode.getChildren("input").size();
            String[] indexData = triangleNode.getChild("p").getData().split(" ");
            for (int i = 0; i < indexData.length / typeCount; i++) {
                int positionIndex = Integer.parseInt(indexData[i * typeCount]);
                int normalIndex = Integer.parseInt(indexData[i * typeCount + 1]);
                int texCoordIndex = Integer.parseInt(indexData[i * typeCount + 2]);
                processVertex(positionIndex, normalIndex, texCoordIndex);
            }
        });
    }


    private Vertex processVertex(int posIndex, int normIndex, int texIndex) {
        Vertex currentVertex = this.vertices.get(posIndex);
        if (!currentVertex.isSet()) {
            currentVertex.setTextureIndex(texIndex);
            currentVertex.setNormalIndex(normIndex);
            this.indices.add(posIndex);
            return currentVertex;
        } else {
            return dealWithAlreadyProcessedVertex(currentVertex, texIndex, normIndex);
        }
    }

    private int[] convertIndicesListToArray() {
        this.indicesArray = new int[indices.size()];
        for (int i = 0; i < this.indicesArray.length; i++) {
            this.indicesArray[i] = this.indices.get(i);
        }
        return this.indicesArray;
    }

    private float convertDataToArrays() {
        float furthestPoint = 0;
        for (int i = 0; i < this.vertices.size(); i++) {
            Vertex currentVertex = this.vertices.get(i);
            if (currentVertex.getLength() > furthestPoint) {
                furthestPoint = currentVertex.getLength();
            }
            Vector3f position = currentVertex.getPosition();
            Vector2f textureCoord = this.textures.get(currentVertex.getTextureIndex());
            Vector3f normalVector = this.normals.get(currentVertex.getNormalIndex());
            this.verticesArray[i * 3] = position.x;
            this.verticesArray[i * 3 + 1] = position.y;
            this.verticesArray[i * 3 + 2] = position.z;
            this.texturesArray[i * 2] = textureCoord.x;
            this.texturesArray[i * 2 + 1] = 1 - textureCoord.y;
            this.normalsArray[i * 3] = normalVector.x;
            this.normalsArray[i * 3 + 1] = normalVector.y;
            this.normalsArray[i * 3 + 2] = normalVector.z;
            VertexSkinData weights = currentVertex.getWeightsData();
            this.jointIdsArray[i * 3] = weights.jointIds.get(0);
            this.jointIdsArray[i * 3 + 1] = weights.jointIds.get(1);
            this.jointIdsArray[i * 3 + 2] = weights.jointIds.get(2);
            this.weightsArray[i * 3] = weights.weights.get(0);
            this.weightsArray[i * 3 + 1] = weights.weights.get(1);
            this.weightsArray[i * 3 + 2] = weights.weights.get(2);

        }
        return furthestPoint;
    }

    private Vertex dealWithAlreadyProcessedVertex(Vertex previousVertex, int newTextureIndex, int newNormalIndex) {
        if (previousVertex.hasSameTextureAndNormal(newTextureIndex, newNormalIndex)) {
            this.indices.add(previousVertex.getIndex());
            return previousVertex;
        } else {
            Vertex anotherVertex = previousVertex.getDuplicateVertex();
            if (anotherVertex != null) {
                return dealWithAlreadyProcessedVertex(anotherVertex, newTextureIndex, newNormalIndex);
            } else {
                Vertex duplicateVertex = new Vertex(this.vertices.size(), previousVertex.getPosition(),
                        previousVertex.getWeightsData());
                duplicateVertex.setTextureIndex(newTextureIndex);
                duplicateVertex.setNormalIndex(newNormalIndex);
                previousVertex.setDuplicateVertex(duplicateVertex);
                this.vertices.add(duplicateVertex);
                this.indices.add(duplicateVertex.getIndex());
                return duplicateVertex;
            }

        }
    }

    private void initArrays() {
        this.verticesArray = new float[this.vertices.size() * 3];
        this.texturesArray = new float[this.vertices.size() * 2];
        this.normalsArray = new float[this.vertices.size() * 3];
        this.jointIdsArray = new int[this.vertices.size() * 3];
        this.weightsArray = new float[this.vertices.size() * 3];
    }

    private void removeUnusedVertices() {
        for (Vertex vertex : this.vertices) {
            vertex.averageTangents();
            if (!vertex.isSet()) {
                vertex.setTextureIndex(0);
                vertex.setNormalIndex(0);
            }
        }
    }
}