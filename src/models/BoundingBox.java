package models;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import renderEngine.shaders.structs.Material;
import renderEngine.structures.Vao;
import textures.ModelTexture;
import util.math.Plane3D;
import util.math.Triangle3D;
import util.parsing.ModelFileData;
import util.parsing.SimpleMaterialColor;
import util.parsing.Vertex;

public class BoundingBox extends SimpleModel {

    public static final Material MATERIAL = new Material("BoundingBox");

    static {
        MATERIAL.setDiffuse(new SimpleMaterialColor(Color.decode("#D500F9")));
    }

    private final Set<Plane3D> planes;

    public BoundingBox(BoundingBox boundingBox) {
        super(boundingBox.vao);
        this.planes = boundingBox.planes.stream().map(Plane3D::new)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public BoundingBox(Vao vao, ModelFileData fileData, String name) {
        super(vao);
        List<Integer> indices = fileData.getIndices();
        List<Vertex> vertices = fileData.getVertices();

        this.planes = new LinkedHashSet<>();
        if (indices.size() % 3 != 0)
            throw new IllegalArgumentException("Wrong indices for " + name);
        try {
            List<Triangle3D> triangles = new ArrayList<>();
            for (int i = 0; i < indices.size(); i += 3)
                triangles.add(new Triangle3D(
                        vertices.get(indices.get(i)).getPosition(),
                        vertices.get(indices.get(i + 1)).getPosition(),
                        vertices.get(indices.get(i + 2)).getPosition()));

            List<Triangle3D> copyOfTriangles = new ArrayList<>(triangles);
            List<Triangle3D> usedTriangles = new ArrayList<>();
            for (Triangle3D triangle : triangles) {
                if (usedTriangles.contains(triangle))
                    continue;

                Plane3D plane;
                Triangle3D foundTriangle = null;
                for (Triangle3D sndTriange : copyOfTriangles) {
                    if (triangle.equals(sndTriange))
                        continue;

                    if ((plane = Plane3D.planeFromTriangles(triangle, sndTriange)) != null) {
                        this.planes.add(plane);
                        foundTriangle = sndTriange;

                        break;
                    }
                }
                if (foundTriangle != null) {
                    usedTriangles.add(foundTriangle);
                    copyOfTriangles.remove(foundTriangle);
                }
            }
            setModelTexture(ModelTexture.DEFAULT_MODEL);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            System.err.println("Incorrect file format for " + name + ".obj");
        }
    }

    public void addPlane(Plane3D plane3D) {
        this.planes.add(plane3D);
    }

    public Set<Plane3D> getPlanes() {
        return this.planes;
    }

    @Override
    public String toString() {
        return "BoundingBox{" +
                "planes=" + this.planes +
                ", vao=" + this.vao +
                '}';
    }
//
//    public static BoundingBox parseBoundingBox(File file) {
//        BoundingBox boundingBox = null;
//        String line;
//        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
//            List<Vertex> vertices = new ArrayList<>();
//            List<Integer> indices = new ArrayList<>();
//            List<Vector3f> normals = new ArrayList<>();
//            List<Vector2f> textures = new ArrayList<>();
//
//            while ((line = reader.readLine()) != null) {
//                String[] currentLine = line.split(" ");
//
//                if (line.startsWith("v ")) {
//                    Vertex vertex = new Vertex(vertices.size(), new Vector3f(Float.parseFloat(currentLine[1]),
//                            Float.parseFloat(currentLine[2]), Float.parseFloat(currentLine[3])));
//                    vertices.add(vertex);
//                } else if (line.startsWith("vt ")) {
//                    textures.add(new Vector2f(Float.parseFloat(currentLine[1]),
//                            Float.parseFloat(currentLine[2])));
//                } else if (line.startsWith("vn ")) {
//                    Vector3f normal = new Vector3f(new Vector3f(Float.parseFloat(currentLine[1]),
//                            Float.parseFloat(currentLine[2]), Float.parseFloat(currentLine[3])));
//                    normals.add(normal);
//                } else if (line.startsWith("f "))
//                    break;
//            }
//            do {
//                String[] currentLine = line.split(" ");
//                int nb = currentLine.length;
//                String[] vertex1 = currentLine[nb - 3].split("/");
//                String[] vertex2 = currentLine[nb - 2].split("/");
//                String[] vertex3 = currentLine[nb - 1].split("/");
//                Vertex v0 = toVertex(vertex1, vertices, indices);
//                Vertex v1 = toVertex(vertex2, vertices, indices);
//                Vertex v2 = toVertex(vertex3, vertices, indices);
//                calculateTangents(v0, v1, v2, textures);
//            } while ((line = reader.readLine()) != null);
//
//            Utils.removeUnusedVertices(vertices);
//            Float[] verticesArray = new Float[vertices.size() * 3];
//            Float[] texturesArray = new Float[vertices.size() * 2];
//            Float[] normalsArray = new Float[vertices.size() * 3];
//            OBJFile.convertDataToArrays(vertices, textures, normals, verticesArray,
//                    texturesArray, normalsArray, new Float[vertices.size() * 3]);
//            Integer[] indicesArray = indices.toArray(new Integer[0]);
//
//            AttributeData<Float> verticesAttribute = new AttributeData<>(0, 3, verticesArray, DataType.FLOAT);
//            AttributeData<Float> texturesAttribute = new AttributeData<>(1, 2, texturesArray, DataType.FLOAT);
//            AttributeData<Float> normalsAttribute = new AttributeData<>(2, 3, normalsArray, DataType.FLOAT);
//            MaterialIndicesAttribute indicesAttribute = new MaterialIndicesAttribute(indicesArray);
//            IndexData data = IndexData.createData(verticesAttribute, indicesAttribute, texturesAttribute,
//                    normalsAttribute);
//            IndexBufferVao vao = Vao.createVao(data, data.getVaoType());
//            boundingBox = new BoundingBox(vao, vertices, indices, file.getName().toLowerCase());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return boundingBox;
//    }
}