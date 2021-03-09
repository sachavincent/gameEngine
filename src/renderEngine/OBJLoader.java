package renderEngine;

import items.Item;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import models.BoundingBox;
import models.RawModel;
import models.TexturedModel;
import textures.ModelTexture;
import util.math.Vector2f;
import util.math.Vector3f;

public class OBJLoader {

    public static RawModel loadObjModel(String fileName) {
        FileReader fr;
        try {
            fr = new FileReader("res/" + fileName + ".obj");
        } catch (FileNotFoundException e) {
            System.err.println("Couldn't load obj file!");
            e.printStackTrace();

            return null;
        }

        RawModel rawModel = null;
        BufferedReader reader = new BufferedReader(fr);

        try {
            rawModel = handleIndicesTexturesNormalsVertex(reader, null, false);

        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Something went wrong");
            e.printStackTrace();
        } finally {
            try {
                reader.close();
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        return rawModel;
    }

    public static RawModel loadRoadModel() {
        float[] vertices = new float[]{-1, 0, 1, 1, 0, 1, -1, 0, -1, 1, 0, -1};
        float[] textureCoords = new float[]{0, 0, 1, 0, 0, 1, 1, 1};
        float[] normals = new float[]{0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0};
        int[] indices = new int[]{1, 2, 0, 1, 3, 2};
        Vector3f min = new Vector3f(-1, 0, -1);
        Vector3f max = new Vector3f(1, 0, 1);

        return Loader.getInstance().loadInstancesToVAO(vertices, textureCoords, normals, indices, min, max);
    }

    public static Item loadObjForItem(Item item, boolean doesUseDirectionalColor) {
        FileReader fr;
        try {
            fr = new FileReader("res/" + item.getName().toLowerCase() + ".obj");
        } catch (FileNotFoundException e) {
            System.err.println("Couldn't load obj file!");
            e.printStackTrace();

            return null;
        }

        BufferedReader reader = new BufferedReader(fr);
        String line;

        List<Vector3f> vertices = new ArrayList<>();

        try {
            RawModel rawModel = handleIndicesTexturesNormalsVertex(reader, "BoundingBox", true);

            item.setTexture(new TexturedModel(rawModel, new ModelTexture(item.getName() + ".png", false)));
//            item.setTexture(new TexturedModel(rawModel, new ModelTexture("white.png", true)));
            item.setPreviewTexture(item.getTexture());
            item.getTexture().getModelTexture().setDirectionalColor(doesUseDirectionalColor);
//
            line = handleVertices(reader, vertices);
            final float[] textureArray = new float[vertices.size() * 2];
            final float[] normalsArray = new float[vertices.size() * 3];

            final int[] indicesArray = handleIndicesVertex(reader, line, "SelectionBox");


            BoundingBox boundingBox = new BoundingBox(vertices, indicesArray, item.getName());

            item.setBoundingBox(boundingBox);

            Map<float[], float[]> verticesMapResult = floatArrayToFloatList(vertices, true);

//            verticesMapResult.forEach((key, minMaxArray) -> item.setBoundingBox(new TexturedModel(
//                    Loader.getInstance().loadToVAO(key, textureArray, normalsArray,
//                            indicesArray, new Vector3f(minMaxArray[0], minMaxArray[1], minMaxArray[2]),
//                            new Vector3f(minMaxArray[3], minMaxArray[4], minMaxArray[5])))));
            vertices.clear();

            line = handleVertices(reader, vertices);
            final float[] textureArray2 = new float[vertices.size() * 2];
            final float[] normalsArray2 = new float[vertices.size() * 3];

            final int[] indicesArray2 = handleIndicesVertex(reader, line, null);

            verticesMapResult = floatArrayToFloatList(vertices, false);
            verticesMapResult.forEach((key, minMaxArray) -> item.setSelectionBox(new TexturedModel(
                    Loader.getInstance().loadToVAO(key, textureArray2, normalsArray2, indicesArray2))));

        } catch (IOException e) {
            System.err.println("Oops");
            e.printStackTrace();
        } finally {
            try {
                reader.close();
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return item;
    }

    private static Map<float[], float[]> floatArrayToFloatList(List<Vector3f> vertices, boolean minMax) {
        int vertexPointer = 0;
        float[] verticesArray = new float[vertices.size() * 3];
        float[] minMaxArray = new float[minMax ? 6 : 0];

        float minX = Integer.MAX_VALUE;
        float minY = Integer.MAX_VALUE;
        float minZ = Integer.MAX_VALUE;
        float maxX = 0;
        float maxY = 0;
        float maxZ = 0;

        for (Vector3f vertex : vertices) {
            if (minMax) {
                if (vertex.x < minX)
                    minX = vertex.x;
                else if (vertex.x > maxX)
                    maxX = vertex.x;

                if (vertex.y < minY)
                    minY = vertex.y;
                else if (vertex.y > maxY)
                    maxY = vertex.y;

                if (vertex.z < minZ)
                    minZ = vertex.z;
                else if (vertex.z > maxZ)
                    maxZ = vertex.z;
            }
            verticesArray[vertexPointer++] = vertex.x;
            verticesArray[vertexPointer++] = vertex.y;
            verticesArray[vertexPointer++] = vertex.z;
        }

        Map<float[], float[]> map = new HashMap<>();
        if (minMax) {
            vertexPointer = 0;

            minMaxArray[vertexPointer++] = minX;
            minMaxArray[vertexPointer++] = minY;
            minMaxArray[vertexPointer++] = minZ;
            minMaxArray[vertexPointer++] = maxX;
            minMaxArray[vertexPointer++] = maxY;
            minMaxArray[vertexPointer] = maxZ;
        }

        map.put(verticesArray, minMaxArray);

        return map;
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
        List<Vector3f> vertices = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        float[] normalsArray = null;
        float[] textureArray = null;

        String line;

        while ((line = reader.readLine()) != null) {
            String[] currentLine = line.split(" ");

            if (line.startsWith("v ")) {
                Vector3f vertex = new Vector3f(Float.parseFloat(currentLine[1]),
                        Float.parseFloat(currentLine[2]), Float.parseFloat(currentLine[3]));
                vertices.add(vertex);
            } else if (line.startsWith("vt ")) {
                Vector2f texture = new Vector2f(Float.parseFloat(currentLine[1]),
                        Float.parseFloat(currentLine[2]));
                textures.add(texture);
            } else if (line.startsWith("vn ")) {
                Vector3f normal = new Vector3f(Float.parseFloat(currentLine[1]),
                        Float.parseFloat(currentLine[2]), Float.parseFloat(currentLine[3]));
                normals.add(normal);
            } else if (line.startsWith("f ")) {
                textureArray = new float[vertices.size() * 2];
                normalsArray = new float[vertices.size() * 3];

                break;
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
                processVertex(vertex1, indices, textures, normals, textureArray, normalsArray);
                processVertex(vertex2, indices, textures, normals, textureArray, normalsArray);
                processVertex(vertex3, indices, textures, normals, textureArray, normalsArray);
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
                System.out.println(Arrays.toString(currentLine));
            }

            line = reader.readLine();
        }

        int[] indicesArray;
        float[] verticesArray = new float[vertices.size() * 3];

        int vertexPointer = 0;

        float minX = Integer.MAX_VALUE;
        float minY = Integer.MAX_VALUE;
        float minZ = Integer.MAX_VALUE;
        float maxX = 0;
        float maxY = 0;
        float maxZ = 0;

        for (Vector3f vertex : vertices) {
            if (vertex.x < minX)
                minX = vertex.x;
            else if (vertex.x > maxX)
                maxX = vertex.x;

            if (vertex.y < minY)
                minY = vertex.y;
            else if (vertex.y > maxY)
                maxY = vertex.y;

            if (vertex.z < minZ)
                minZ = vertex.z;
            else if (vertex.z > maxZ)
                maxZ = vertex.z;

            verticesArray[vertexPointer++] = vertex.x;
            verticesArray[vertexPointer++] = vertex.y;
            verticesArray[vertexPointer++] = vertex.z;
        }

        indicesArray = indices.stream().mapToInt(i -> i).toArray();

        if (textureArray == null || normalsArray == null || indicesArray == null)
            throw new IllegalArgumentException("Incorrect OBJ file format");
        RawModel rawModel;
        if (instancedModel)
            rawModel = Loader.getInstance()
                    .loadInstancesToVAO(verticesArray, textureArray, normalsArray, indicesArray,
                            new Vector3f(minX, minY, minZ), new Vector3f(maxX, maxY, maxZ));
        else
            rawModel = Loader.getInstance()
                    .loadToVAO(verticesArray, textureArray, normalsArray, indicesArray,
                            new Vector3f(minX, minY, minZ), new Vector3f(maxX, maxY, maxZ));
        if (rawModel == null)
            throw new IllegalArgumentException("Model null");

        return rawModel;
    }

    private static int[] handleIndicesVertex(BufferedReader reader, String line, String nextSegment)
            throws IOException {
        List<Integer> indices = new ArrayList<>();
        do {
            if (nextSegment != null && line.equalsIgnoreCase("o " + nextSegment))
                break;

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

    private static void processVertex(String[] vertexData, List<Integer> indices, List<Vector2f> textures,
            List<Vector3f> normals, float[] textureArray, float[] normalsArray) {

        try {
            int currentVertexPointer = Integer.parseInt(vertexData[0]) - 1;
            indices.add(currentVertexPointer);

            Vector2f currentTex = textures.get(Integer.parseInt(vertexData[1]) - 1);
            textureArray[currentVertexPointer * 2] = currentTex.x;
            textureArray[currentVertexPointer * 2 + 1] = 1 - currentTex.y;

            Vector3f currentNorm = normals.get(Integer.parseInt(vertexData[2]) - 1);
            normalsArray[currentVertexPointer * 3] = currentNorm.x;
            normalsArray[currentVertexPointer * 3 + 1] = currentNorm.y;
            normalsArray[currentVertexPointer * 3 + 2] = currentNorm.z;
        } catch (IndexOutOfBoundsException e) {
            System.err.println("Error during vertex processing");
            e.printStackTrace();
        }
    }
}
