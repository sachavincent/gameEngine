package renderEngine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import models.RawModel;
import util.math.Vector2f;
import util.math.Vector3f;

public class OBJLoader {

    public static RawModel loadObjModel(String fileName, Loader loader) {
        FileReader fr = null;
        try {
            fr = new FileReader(new File("res/" + fileName + ".obj"));
        } catch (FileNotFoundException e) {
            System.err.println("Couldn't load obj file!");
            e.printStackTrace();
        }

        BufferedReader reader = new BufferedReader(Objects.requireNonNull(fr));
        String line;

        List<Vector3f> vertices = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        float[] verticesArray = null;
        float[] normalsArray = null;
        float[] textureArray = null;
        int[] indicesArray = null;

        try {
            while (true) {
                line = reader.readLine();
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
                if (!line.startsWith("f ")) {
                    line = reader.readLine();
                    continue;
                }
                String[] currentLine = line.split(" ");
                String[] vertex1 = currentLine[1].split("/");
                String[] vertex2 = currentLine[2].split("/");
                String[] vertex3 = currentLine[3].split("/");

                processVerrtex(vertex1, indices, textures, normals, textureArray, normalsArray);
                processVerrtex(vertex2, indices, textures, normals, textureArray, normalsArray);
                processVerrtex(vertex3, indices, textures, normals, textureArray, normalsArray);

                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            System.err.println("Oops");
            e.printStackTrace();
        }

        verticesArray = new float[vertices.size() * 3];
        indicesArray = new int[indices.size()];

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

        float width = Math.abs(maxX - minX);
        float depth = Math.abs(maxZ - minZ);
        float height = Math.abs(maxY - minY);

        for (int i = 0; i < indices.size(); i++)
            indicesArray[i] = indices.get(i);

        return loader.loadToVAO(verticesArray, textureArray, normalsArray, indicesArray);
    }


    private static void processVerrtex(String[] vertexData, List<Integer> indices, List<Vector2f> textures,
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

        }
    }
}
