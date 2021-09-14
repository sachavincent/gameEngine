package util.parsing.colladaParser.colladaLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import renderEngine.shaders.structs.Material;
import renderEngine.structures.AttributeData;
import renderEngine.structures.AttributeData.DataType;
import renderEngine.structures.IndexData;
import renderEngine.structures.MaterialIndicesAttribute;
import util.Utils;
import util.math.Matrix4f;
import util.math.Vector2f;
import util.math.Vector3f;
import util.math.Vector4f;
import util.parsing.ModelType;
import util.parsing.Vertex;
import util.parsing.colladaParser.dataStructures.MaterialData;
import util.parsing.colladaParser.dataStructures.VertexSkinData;
import util.parsing.colladaParser.xmlParser.XmlNode;

public class GeometryLoader {

    private static final Matrix4f CORRECTION = new Matrix4f()
            .rotate((float) Math.toRadians(-90), new Vector3f(1, 0, 0));

    private final XmlNode              meshDataNode;
    private final List<VertexSkinData> vertexWeights;
    private final List<MaterialData>   materialsData;

    private Float[]   verticesArray;
    private Float[]   normalsArray;
    private Float[]   texturesArray;
    private Integer[] jointIdsArray;
    private Float[]   weightsArray;

    private final Map<Material, Integer[]> materialIndices = new HashMap<>();

    private final List<Vertex>   vertices = new ArrayList<>();
    private final List<Vector2f> textures = new ArrayList<>();
    private final List<Vector3f> normals  = new ArrayList<>();

    public GeometryLoader(XmlNode geometryNode, List<VertexSkinData> vertexWeights, List<MaterialData> materialsData) {
        this.vertexWeights = vertexWeights;
        this.meshDataNode = geometryNode.getChild("geometry").getChild("mesh");
        this.materialsData = materialsData;
    }

    public IndexData extractModelData(ModelType modelType) {
        readRawData();
        assembleVertices();
        Utils.removeUnusedVertices(this.vertices);
        initArrays();
        convertDataToArrays();

        List<AttributeData<?>> attributes = new ArrayList<>();
        attributes.add(new AttributeData<>(0, 3, this.verticesArray, DataType.FLOAT));
        attributes.add(new AttributeData<>(1, 2, this.texturesArray, DataType.FLOAT));
        attributes.add(new AttributeData<>(2, 3, this.normalsArray, DataType.FLOAT));
        if (modelType.isNormalMap()) {
            //TODO
        }
        for (var entry : this.materialIndices.entrySet())
            attributes.add(new MaterialIndicesAttribute(entry.getKey(), entry.getValue()));
        attributes.add(new AttributeData<>(4, 3, this.jointIdsArray, DataType.INT));
        attributes.add(new AttributeData<>(5, 3, this.weightsArray, DataType.FLOAT));

        return IndexData.createData(attributes);
    }

    private void readRawData() {
        readPositions();
        readNormals();
        readTextureCoords();
    }

    private void readPositions() {
        String positionsId = this.meshDataNode.getChild("vertices").getChild("input").getAttribute("source")
                .substring(1);
        XmlNode positionsData = this.meshDataNode.getChildWithAttribute("source", "id", positionsId)
                .getChild("float_array");
        int count = Integer.parseInt(positionsData.getAttribute("count"));
        String[] posData = positionsData.getData().split(" ");
        for (int i = 0; i < count / 3; i++) {
            float x = Float.parseFloat(posData[i * 3]);
            float y = Float.parseFloat(posData[i * 3 + 1]);
            float z = Float.parseFloat(posData[i * 3 + 2]);
            Vector4f position = new Vector4f(x, y, z, 1);
            Matrix4f.transform(CORRECTION, position, position);
            this.vertices.add(
                    new Vertex(this.vertices.size(), new Vector3f(position.getX(), position.getY(), position.getZ()),
                            this.vertexWeights.get(this.vertices.size())));
        }
    }

    private void readNormals() {
        this.meshDataNode.getChildren("triangles").forEach(triangleNode -> {
            String normalsId = triangleNode.getChildWithAttribute("input", "semantic", "NORMAL")
                    .getAttribute("source").substring(1);
            XmlNode normalsData = this.meshDataNode.getChildWithAttribute("source", "id", normalsId)
                    .getChild("float_array");
            int count = Integer.parseInt(normalsData.getAttribute("count"));
            String[] normData = normalsData.getData().split(" ");
            for (int i = 0; i < count / 3; i++) {
                float x = Float.parseFloat(normData[i * 3]);
                float y = Float.parseFloat(normData[i * 3 + 1]);
                float z = Float.parseFloat(normData[i * 3 + 2]);
                Vector4f norm = new Vector4f(x, y, z, 0f);
                Matrix4f.transform(CORRECTION, norm, norm);
                this.normals.add(new Vector3f(norm.getX(), norm.getY(), norm.getZ()));
            }
        });
    }

    private void readTextureCoords() {
        List<XmlNode> triangles = this.meshDataNode.getChildren("triangles");
        triangles.forEach(triangleNode -> {
            String texCoordsId = triangleNode.getChildWithAttribute("input", "semantic", "TEXCOORD")
                    .getAttribute("source").substring(1);
            XmlNode texCoordsData = this.meshDataNode.getChildWithAttribute("source", "id", texCoordsId)
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
        this.materialsData.forEach(materialData -> {
            XmlNode materialNode = this.meshDataNode.getChildWithAttribute("triangles", "material",
                    materialData.getId());
            if (materialNode == null)
                return;
            List<Integer> indices = new ArrayList<>();
            Material material = new Material(materialData);
            int typeCount = materialNode.getChildren("input").size();
            String[] indexData = materialNode.getChild("p").getData().split(" ");
            for (int i = 0; i < indexData.length / typeCount; i++) {
                int positionIndex = Integer.parseInt(indexData[i * typeCount]);
                int normalIndex = Integer.parseInt(indexData[i * typeCount + 1]);
                int texCoordIndex = Integer.parseInt(indexData[i * typeCount + 2]);
                Utils.processVertex(this.vertices, indices, positionIndex, normalIndex, texCoordIndex);
            }
            this.materialIndices.put(material, indices.toArray(new Integer[0]));
        });
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
            this.verticesArray[i * 3] = position.getX();
            this.verticesArray[i * 3 + 1] = position.getY();
            this.verticesArray[i * 3 + 2] = position.getZ();
            this.texturesArray[i * 2] = textureCoord.getX();
            this.texturesArray[i * 2 + 1] = 1 - textureCoord.getY();
            this.normalsArray[i * 3] = normalVector.getX();
            this.normalsArray[i * 3 + 1] = normalVector.getY();
            this.normalsArray[i * 3 + 2] = normalVector.getZ();
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

    private void initArrays() {
        this.verticesArray = new Float[this.vertices.size() * 3];
        this.texturesArray = new Float[this.vertices.size() * 2];
        this.normalsArray = new Float[this.vertices.size() * 3];
        this.jointIdsArray = new Integer[this.vertices.size() * 3];
        this.weightsArray = new Float[this.vertices.size() * 3];
    }

    public List<Material> getMaterials() {
        return new ArrayList<>(this.materialIndices.keySet());
    }
}