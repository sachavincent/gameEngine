package util.parsing.objParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import renderEngine.shaders.structs.Material;
import renderEngine.structures.AttributeData;
import renderEngine.structures.AttributeData.DataType;
import renderEngine.structures.IndexData;
import renderEngine.structures.InstancedAttribute;
import renderEngine.structures.MaterialIndicesAttribute;
import util.Utils;
import util.exceptions.MissingFileException;
import util.math.Vector2f;
import util.math.Vector3f;
import util.parsing.ModelFileData;
import util.parsing.ModelType;
import util.parsing.Vertex;

public class OBJFile extends File {

    private final String name;

    private MTLFile mtlFile;

    private ModelFileData data;

    public OBJFile(File parent, String name) {
        super(parent, name);

        this.name = name;
    }

    public OBJFile(File file) {
        this(file.getParentFile(), file.getName());
    }

    public String getName() {
        return this.name;
    }

    public MTLFile getMTLFile() {
        return this.mtlFile;
    }

    public void setMTLFile(MTLFile mtlFile) {
        this.mtlFile = mtlFile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;
        OBJFile objFile = (OBJFile) o;
        return Objects.equals(this.name, objFile.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name);
    }

    public static OBJFile parseOBJFile(File file, ModelType modelType) {
        OBJFile objFile = new OBJFile(file);
        MTLFile mtlFile = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            List<Vertex> vertices = new ArrayList<>();
            List<Vector2f> textures = new ArrayList<>();
            List<Vector3f> normals = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("# ") || line.startsWith("o ") || line.startsWith("s "))
                    continue;

                if (line.startsWith("mtllib")) {
                    String MTLFileName = line.substring(7);
                    mtlFile = MTLFile.parseMTLFile(new File(file.getParentFile(), MTLFileName));
                    continue;
                }
                String[] currentLine = line.split(" ");
                if (!line.matches("^v([nt]|) (.*)$"))
                    break;

                try {
                    float arg1 = Float.parseFloat(currentLine[1].equalsIgnoreCase("nan") ? "0" : currentLine[1]);
                    float arg2 = Float.parseFloat(currentLine[1].equalsIgnoreCase("nan") ? "0" : currentLine[2]);
                    if (line.startsWith("v ")) {
                        float arg3 = Float.parseFloat(currentLine[3]);
                        vertices.add(new Vertex(vertices.size(), new Vector3f(arg1, arg2, arg3)));
                    } else if (line.startsWith("vt ")) {
                        textures.add(new Vector2f(arg1, arg2));
                    } else if (line.startsWith("vn ")) {
                        float arg3 = Float.parseFloat(currentLine[3]);
                        normals.add(new Vector3f(arg1, arg2, arg3));
                    }
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    System.err.println("Error while loading OBJ File : (line=" + line + ")");
                    e.printStackTrace();
                }
            }
            if (mtlFile == null || !mtlFile.exists())
                throw new MissingFileException("mtlFile for " + file.getName());

            Material material = null;
            Map<Material, List<Integer>> indicesList = new HashMap<>();
            List<Integer> indices = new ArrayList<>();

            if (line != null) {
                do {
                    if (line.startsWith("usemtl ")) { // New material
                        if (indicesList.containsKey(material)) {
                            indicesList.get(material).addAll(new ArrayList<>(indices));
                            indices.clear();
                        }

                        material = mtlFile.getMaterial(line.substring(7).
//                                split("::")[0].
        trim());
                        if (!indicesList.containsKey(material))
                            indicesList.put(material, new ArrayList<>());
                    } else if (line.startsWith("f ")) {
                        String[] currentLine = line.split(" ");
                        int nb = currentLine.length;
                        try {
                            String[] vertex1 = currentLine[nb - 3].split("/");
                            String[] vertex2 = currentLine[nb - 2].split("/");
                            String[] vertex3 = currentLine[nb - 1].split("/");
                            Vertex v0 = toVertex(vertex1, vertices, indices);
                            Vertex v1 = toVertex(vertex2, vertices, indices);
                            Vertex v2 = toVertex(vertex3, vertices, indices);
                            calculateTangents(v0, v1, v2, textures);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            e.printStackTrace();
                            System.out.println(Arrays.toString(currentLine));
                        }
                    }
                } while ((line = reader.readLine()) != null);
                indicesList.get(material).addAll(new ArrayList<>(indices));

                Utils.removeUnusedVertices(vertices);
                Float[] verticesArray = new Float[vertices.size() * 3];
                Float[] texturesArray = new Float[vertices.size() * 2];
                Float[] normalsArray = new Float[vertices.size() * 3];
                Float[] tangentsArray = new Float[vertices.size() * 3];
                convertDataToArrays(vertices, textures, normals, verticesArray, texturesArray,
                        normalsArray, tangentsArray);

                ModelFileData fileData = new ModelFileData(vertices, textures, normals, indices);
                objFile.setData(fileData);
                Map<Material, Integer[]> indicesArray = indicesList.entrySet().stream().collect(
                        Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().toArray(new Integer[0])));

                List<AttributeData<?>> attributes = new ArrayList<>();
                attributes.add(new AttributeData<>(0, 3, verticesArray, DataType.FLOAT));
                attributes.add(new AttributeData<>(1, 2, texturesArray, DataType.FLOAT));
                attributes.add(new AttributeData<>(2, 3, normalsArray, DataType.FLOAT));
                if (modelType.isNormalMap())
                    attributes.add(new AttributeData<>(3, 3, tangentsArray, DataType.FLOAT));

                for (var entry : indicesArray.entrySet())
                    attributes.add(new MaterialIndicesAttribute(entry.getKey(), entry.getValue()));

                if (modelType.isInstanced())
                    attributes.add(new InstancedAttribute(6, 4, DataType.FLOAT, 4));
                IndexData data = IndexData.createData(attributes);
                mtlFile.setMeshData(data);
                objFile.setMTLFile(mtlFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return objFile;
    }

    public static Vertex toVertex(String[] vertexParts, List<Vertex> vertices, List<Integer> indices) {
        int index = Integer.parseInt(vertexParts[0]) - 1;
        int textureIndex = Integer.parseInt(vertexParts[1]) - 1;
        int normalIndex = Integer.parseInt(vertexParts[2]) - 1;
        return Utils.processVertex(vertices, indices, index, normalIndex, textureIndex);
    }

    public static void calculateTangents(Vertex v0, Vertex v1, Vertex v2, List<Vector2f> textures) {
        Vector3f delatPos1 = Vector3f.sub(v1.getPosition(), v0.getPosition(), null);
        Vector3f delatPos2 = Vector3f.sub(v2.getPosition(), v0.getPosition(), null);
        Vector2f uv0 = textures.get(v0.getTextureIndex());
        Vector2f uv1 = textures.get(v1.getTextureIndex());
        Vector2f uv2 = textures.get(v2.getTextureIndex());
        Vector2f deltaUv1 = Vector2f.sub(uv1, uv0, null);
        Vector2f deltaUv2 = Vector2f.sub(uv2, uv0, null);

        float r = 1.0f / (deltaUv1.getX() * deltaUv2.getY() - deltaUv1.getY() * deltaUv2.getX());
        delatPos1.scale(deltaUv2.getY());
        delatPos2.scale(deltaUv1.getY());
        Vector3f tangent = Vector3f.sub(delatPos1, delatPos2, null);
        tangent.scale(r);
        v0.addTangent(tangent);
        v1.addTangent(tangent);
        v2.addTangent(tangent);
    }

    public static float convertDataToArrays(List<Vertex> vertices, List<Vector2f> textures, List<Vector3f> normals,
            Float[] verticesArray, Float[] texturesArray, Float[] normalsArray, Float[] tangentsArray) {
        float furthestPoint = 0;
        float minX = Integer.MAX_VALUE;
        float minY = Integer.MAX_VALUE;
        float minZ = Integer.MAX_VALUE;
        float maxX = 0;
        float maxY = 0;
        float maxZ = 0;
        for (int i = 0; i < vertices.size(); i++) {
            Vertex currentVertex = vertices.get(i);
            if (currentVertex.getLength() > furthestPoint) {
                furthestPoint = currentVertex.getLength();
            }
            Vector3f position = currentVertex.getPosition();
            Vector2f textureCoord = textures.get(currentVertex.getTextureIndex());
            Vector3f normalVector = normals.get(currentVertex.getNormalIndex());
            Vector3f tangent = currentVertex.getAverageTangent();
            verticesArray[i * 3] = position.getX();
            verticesArray[i * 3 + 1] = position.getY();
            verticesArray[i * 3 + 2] = position.getZ();
            texturesArray[i * 2] = textureCoord.getX();
            texturesArray[i * 2 + 1] = 1 - textureCoord.getY();
            normalsArray[i * 3] = normalVector.getX();
            normalsArray[i * 3 + 1] = normalVector.getY();
            normalsArray[i * 3 + 2] = normalVector.getZ();
            tangentsArray[i * 3] = tangent.getX();
            tangentsArray[i * 3 + 1] = tangent.getY();
            tangentsArray[i * 3 + 2] = tangent.getZ();

            if (position.getX() < minX)
                minX = position.getX();
            else if (position.getX() > maxX)
                maxX = position.getX();

            if (position.getY() < minY)
                minY = position.getY();
            else if (position.getY() > maxY)
                maxY = position.getY();

            if (position.getZ() < minZ)
                minZ = position.getZ();
            else if (position.getZ() > maxZ)
                maxZ = position.getZ();
        }
//        minMax[0] = new Vector3f(minX, minY, minZ);
//        minMax[1] = new Vector3f(maxX, maxY, maxZ);

        return furthestPoint;
    }

    public void setData(ModelFileData data) {
        this.data = data;
    }

    public ModelFileData getData() {
        return this.data;
    }
}