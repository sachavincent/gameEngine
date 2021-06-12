package renderEngine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import models.BoundingBox;
import models.RawModel;
import models.TexturedModel;
import objConverter.VertexNM;
import scene.gameObjects.OBJGameObject;
import textures.ModelTexture;
import util.math.Vector2f;
import util.math.Vector3f;

public class OBJLoader {

    public static RawModel loadRoadModel() {
        float[] vertices = new float[]{-1, 0, 1, 1, 0, 1, -1, 0, -1, 1, 0, -1};
        float[] textureCoords = new float[]{0, 0, 1, 0, 0, 1, 1, 1};
        float[] normals = new float[]{0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0};
        float[] tangents = new float[]{0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0};
        int[] indices = new int[]{1, 2, 0, 1, 3, 2};
        Vector3f min = new Vector3f(-1, 0, -1);
        Vector3f max = new Vector3f(1, 0, 1);

        return Loader.getInstance().loadToVAO(vertices, textureCoords, normals, tangents, indices, min, max);
    }

    public static OBJGameObject loadOBJGameObject(String name, boolean instanced) {
        OBJGameObject objGameObject = new OBJGameObject();

        try (BufferedReader reader = new BufferedReader(
                new FileReader("res/" + name.toLowerCase() + ".obj"))) {
            String line;

            List<Vector3f> vertices = new ArrayList<>();

            RawModel rawModel = handleIndicesTexturesNormalsVertex(reader, "BoundingBox", instanced);

            ModelTexture modelTexture = new ModelTexture(name.toLowerCase() + ".png", false);
            modelTexture.setNormalMap(name + "Normal.png");
            objGameObject.setTexture(new TexturedModel(rawModel, modelTexture));
            objGameObject.setPreviewTexture(objGameObject.getTexture());

            line = handleVertices(reader, vertices);
            final int[] indicesArray = handleIndicesVertex(reader, line);

            BoundingBox boundingBox = new BoundingBox(vertices, indicesArray, name.toLowerCase());

            objGameObject.setBoundingBox(boundingBox);
        } catch (IOException e) {
            System.err.println("Oops");
            e.printStackTrace();
        }
        return objGameObject;
    }

    private static String handleVertices(BufferedReader reader, List<Vector3f> vertices) throws IOException {
        String line;

        while ((line = reader.readLine()) != null) {
            String[] currentLine = line.split(" ");

            if (line.startsWith("v ")) {
                Vector3f vertex = new Vector3f(Float.parseFloat(currentLine[1]),
                        Float.parseFloat(currentLine[2]), Float.parseFloat(currentLine[3]));
                vertices.add(vertex);
            } else if (line.startsWith("f "))
                return line;
        }
        return null;
    }

    private static RawModel handleIndicesTexturesNormalsVertex(BufferedReader reader, String nextSegment,
            boolean instancedModel) throws IOException, IllegalArgumentException {
        List<VertexNM> vertices = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        String line;

        while ((line = reader.readLine()) != null) {
            if (line.startsWith("# ") || line.startsWith("o ") || line.startsWith("s "))
                continue;

            if (line.startsWith("f ")) {
                break;
            }

            String[] currentLine = line.split(" ");
            try {
                float arg1 = Float.parseFloat(currentLine[1].equalsIgnoreCase("nan") ? "0" : currentLine[1]);
                float arg2 = Float.parseFloat(currentLine[1].equalsIgnoreCase("nan") ? "0" : currentLine[2]);
                if (line.startsWith("v ")) {
                    float arg3 = Float.parseFloat(currentLine[3]);
                    vertices.add(new VertexNM(vertices.size(), new Vector3f(arg1, arg2, arg3)));
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

        while (line != null) {
            if (line.equalsIgnoreCase("o " + nextSegment))
                break;

            if (!line.startsWith("f ")) {
                line = reader.readLine();
                continue;
            }
            String[] currentLine = line.split(" ");
            try {
                String[] vertex1 = currentLine[1].split("/");
                String[] vertex2 = currentLine[2].split("/");
                String[] vertex3 = currentLine[3].split("/");
                VertexNM v0 = processVertex(vertex1, vertices, indices);
                VertexNM v1 = processVertex(vertex2, vertices, indices);
                VertexNM v2 = processVertex(vertex3, vertices, indices);
                calculateTangents(v0, v1, v2, textures);
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
                System.out.println(Arrays.toString(currentLine));
            }

            line = reader.readLine();
        }

        removeUnusedVertices(vertices);
        float[] verticesArray = new float[vertices.size() * 3];
        float[] texturesArray = new float[vertices.size() * 2];
        float[] normalsArray = new float[vertices.size() * 3];
        float[] tangentsArray = new float[vertices.size() * 3];
        Vector3f[] minMax = new Vector3f[2];
        float furthest = convertDataToArrays(vertices, textures, normals, verticesArray,
                texturesArray, normalsArray, tangentsArray, minMax);
        int[] indicesArray = indices.stream().mapToInt(i -> i).toArray();

        RawModel rawModel;
        if (instancedModel)
            rawModel = Loader.getInstance()
                    .loadInstancesToVAO(verticesArray, texturesArray, normalsArray, tangentsArray, indicesArray,
                            minMax[0], minMax[1]);
        else
            rawModel = Loader.getInstance()
                    .loadToVAO(verticesArray, texturesArray, normalsArray, tangentsArray, indicesArray, minMax[0],
                            minMax[1]);
        if (rawModel == null)
            throw new IllegalArgumentException("Model null");

        return rawModel;
    }

    private static int[] handleIndicesVertex(BufferedReader reader, String line) throws IOException {
        List<Integer> indices = new ArrayList<>();
        if (line == null)
            return new int[0];

        do {
            if (!line.startsWith("f ")) {
                line = reader.readLine();
                continue;
            }

            String[] currentLine = line.split(" ");

            indices.add(Integer.parseInt(currentLine[1]) - 1);
            indices.add(Integer.parseInt(currentLine[2]) - 1);
            indices.add(Integer.parseInt(currentLine[3]) - 1);

            line = reader.readLine();
        } while (line != null);

        return indices.stream().mapToInt(i -> i).toArray();
    }

    private static void calculateTangents(VertexNM v0, VertexNM v1, VertexNM v2,
            List<Vector2f> textures) {
        Vector3f delatPos1 = Vector3f.sub(v1.getPosition(), v0.getPosition(), null);
        Vector3f delatPos2 = Vector3f.sub(v2.getPosition(), v0.getPosition(), null);
        Vector2f uv0 = textures.get(v0.getTextureIndex());
        Vector2f uv1 = textures.get(v1.getTextureIndex());
        Vector2f uv2 = textures.get(v2.getTextureIndex());
        Vector2f deltaUv1 = Vector2f.sub(uv1, uv0, null);
        Vector2f deltaUv2 = Vector2f.sub(uv2, uv0, null);

        float r = 1.0f / (deltaUv1.x * deltaUv2.y - deltaUv1.y * deltaUv2.x);
        delatPos1.scale(deltaUv2.y);
        delatPos2.scale(deltaUv1.y);
        Vector3f tangent = Vector3f.sub(delatPos1, delatPos2, null);
        tangent.scale(r);
        v0.addTangent(tangent);
        v1.addTangent(tangent);
        v2.addTangent(tangent);
    }

    private static VertexNM processVertex(String[] vertex, List<VertexNM> vertices, List<Integer> indices) {
        int index = Integer.parseInt(vertex[0]) - 1;
        VertexNM currentVertex = vertices.get(index);
        int textureIndex = Integer.parseInt(vertex[1]) - 1;
        int normalIndex = Integer.parseInt(vertex[2]) - 1;
        if (!currentVertex.isSet()) {
            currentVertex.setTextureIndex(textureIndex);
            currentVertex.setNormalIndex(normalIndex);
            indices.add(index);
            return currentVertex;
        } else {
            return dealWithAlreadyProcessedVertex(currentVertex, textureIndex, normalIndex, indices, vertices);
        }
    }

    private static VertexNM dealWithAlreadyProcessedVertex(VertexNM previousVertex, int newTextureIndex,
            int newNormalIndex, List<Integer> indices, List<VertexNM> vertices) {
        if (previousVertex.hasSameTextureAndNormal(newTextureIndex, newNormalIndex)) {
            indices.add(previousVertex.getIndex());
            return previousVertex;
        } else {
            VertexNM anotherVertex = previousVertex.getDuplicateVertex();
            if (anotherVertex != null) {
                return dealWithAlreadyProcessedVertex(anotherVertex, newTextureIndex, newNormalIndex, indices,
                        vertices);
            } else {
                VertexNM duplicateVertex = new VertexNM(vertices.size(), previousVertex.getPosition());
                duplicateVertex.setTextureIndex(newTextureIndex);
                duplicateVertex.setNormalIndex(newNormalIndex);
                previousVertex.setDuplicateVertex(duplicateVertex);
                vertices.add(duplicateVertex);
                indices.add(duplicateVertex.getIndex());
                return duplicateVertex;
            }

        }
    }

    private static void removeUnusedVertices(List<VertexNM> vertices) {
        for (VertexNM vertex : vertices) {
            vertex.averageTangents();
            if (!vertex.isSet()) {
                vertex.setTextureIndex(0);
                vertex.setNormalIndex(0);
            }
        }
    }

    private static float convertDataToArrays(List<VertexNM> vertices, List<Vector2f> textures, List<Vector3f> normals,
            float[] verticesArray, float[] texturesArray, float[] normalsArray, float[] tangentsArray,
            Vector3f[] minMax) {
        float furthestPoint = 0;
        float minX = Integer.MAX_VALUE;
        float minY = Integer.MAX_VALUE;
        float minZ = Integer.MAX_VALUE;
        float maxX = 0;
        float maxY = 0;
        float maxZ = 0;
        for (int i = 0; i < vertices.size(); i++) {
            VertexNM currentVertex = vertices.get(i);
            if (currentVertex.getLength() > furthestPoint) {
                furthestPoint = currentVertex.getLength();
            }
            Vector3f position = currentVertex.getPosition();
            Vector2f textureCoord = textures.get(currentVertex.getTextureIndex());
            Vector3f normalVector = normals.get(currentVertex.getNormalIndex());
            Vector3f tangent = currentVertex.getAverageTangent();
            verticesArray[i * 3] = position.x;
            verticesArray[i * 3 + 1] = position.y;
            verticesArray[i * 3 + 2] = position.z;
            texturesArray[i * 2] = textureCoord.x;
            texturesArray[i * 2 + 1] = 1 - textureCoord.y;
            normalsArray[i * 3] = normalVector.x;
            normalsArray[i * 3 + 1] = normalVector.y;
            normalsArray[i * 3 + 2] = normalVector.z;
            tangentsArray[i * 3] = tangent.x;
            tangentsArray[i * 3 + 1] = tangent.y;
            tangentsArray[i * 3 + 2] = tangent.z;

            if (position.x < minX)
                minX = position.x;
            else if (position.x > maxX)
                maxX = position.x;

            if (position.y < minY)
                minY = position.y;
            else if (position.y > maxY)
                maxY = position.y;

            if (position.z < minZ)
                minZ = position.z;
            else if (position.z > maxZ)
                maxZ = position.z;
        }
        minMax[0] = new Vector3f(minX, minY, minZ);
        minMax[1] = new Vector3f(maxX, maxY, maxZ);

        return furthestPoint;
    }
}
