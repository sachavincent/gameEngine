package util.parsing.objParser;

import models.AnimatedModel;
import models.BoundingBox;
import models.Model;
import renderEngine.AnimatedModelLoader;
import renderEngine.Vao;
import scene.gameObjects.GameObjectData;
import util.Utils;
import util.exceptions.MTLFileException;
import util.exceptions.MissingFileException;
import util.math.Vector2f;
import util.math.Vector3f;
import util.parsing.*;
import util.parsing.colladaParser.colladaLoader.AnimationLoader;
import util.parsing.colladaParser.dataStructures.MeshData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static util.Utils.MODELS_PATH;

public class ModelLoader {

    public static MeshData loadRoadModel() {
        float[] vertices = new float[]{-1, 0, 1, 1, 0, 1, -1, 0, -1, 1, 0, -1};
        float[] textureCoords = new float[]{0, 0, 1, 0, 0, 1, 1, 1};
        float[] normals = new float[]{0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0};
//        float[] tangents = new float[]{0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0};
        int[] indices = new int[]{1, 2, 0, 1, 3, 2};
        Vector3f min = new Vector3f(-1, 0, -1);
        Vector3f max = new Vector3f(1, 0, 1);

        return new MeshData(vertices, textureCoords, normals, indices);
    }

    public static GameObjectData loadModel(String folder, ModelType modelType) {
        return loadModel(folder, "vertices", modelType);
    }

    public static GameObjectData loadModel(String folder, String modelFileName, ModelType modelType) {
        GameObjectData gameObjectData = new GameObjectData();
        String extension = modelType.getExtension();
        File modelFile = new File(
                MODELS_PATH + (folder == null ? "/" : ("/" + folder + "/")) + modelFileName + extension);
        File boundingBoxFile = new File(
                MODELS_PATH + (folder == null ? "/" : ("/" + folder + "/")) + "boundingbox.obj");
        if (!modelFile.exists())
            throw new MissingFileException(modelFile);

        if (modelType == ModelType.ANIMATED || modelType == ModelType.ANIMATED_INSTANCED ||
                modelType == ModelType.ANIMATED_WITH_NORMAL_MAP ||
                modelType == ModelType.ANIMATED_INSTANCED_WITH_NORMAL_MAP) {
            AnimatedModel animatedTexturedModel = AnimatedModelLoader.loadAnimatedModel(modelFile, modelType);
            animatedTexturedModel.doAnimation(AnimationLoader.loadAnimation(modelFile));
//            animatedTexturedModel.getModelTexture().setUseFakeLighting(true);
            gameObjectData.setTexture(animatedTexturedModel);
            gameObjectData.setPreviewTexture(animatedTexturedModel);
            if (boundingBoxFile.exists())
                gameObjectData.setBoundingBox(loadBoundingBox(boundingBoxFile));
            return gameObjectData;
        }

        OBJFile OBJFile = parseOBJFile(modelFile);
        MTLFile MTLFile = OBJFile.getMTLFile();
        if (MTLFile != null) {
            Vao vao = Vao.createVao(MTLFile.getMeshData(), modelType);

            gameObjectData.setTexture(new Model(vao, OBJFile));
        }
        gameObjectData.setPreviewTexture(gameObjectData.getTexture());
        if (boundingBoxFile.exists())
            gameObjectData.setBoundingBox(loadBoundingBox(boundingBoxFile));

        return gameObjectData;
    }

    private static BoundingBox loadBoundingBox(File file) {
        BoundingBox boundingBox = null;
        String line;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            List<Vertex> vertices = new ArrayList<>();
            List<Integer> indices = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                String[] currentLine = line.split(" ");

                if (line.startsWith("v ")) {
                    Vertex vertex = new Vertex(vertices.size(), new Vector3f(Float.parseFloat(currentLine[1]),
                            Float.parseFloat(currentLine[2]), Float.parseFloat(currentLine[3])));
                    vertices.add(vertex);
                } else if (line.startsWith("f "))
                    break;
            }
            do {
                String[] currentLine = line.split(" ");

                indices.add(Integer.parseInt(currentLine[1]) - 1);
                indices.add(Integer.parseInt(currentLine[2]) - 1);
                indices.add(Integer.parseInt(currentLine[3]) - 1);

                line = reader.readLine();
            } while (line != null);

            float[] verticesArray = new float[vertices.size() * 3];
            int[] indicesArray = indices.stream().mapToInt(i -> i).toArray();
            for (int i = 0; i < vertices.size(); i++) {
                Vertex vertex = vertices.get(i);
                verticesArray[i * 3] = vertex.getPosition().x;
                verticesArray[i * 3 + 1] = vertex.getPosition().y;
                verticesArray[i * 3 + 2] = vertex.getPosition().z;
            }
            MeshData meshData = new MeshData(verticesArray, indicesArray);
            Vao vao = Vao.createVao(meshData, ModelType.DEFAULT);
            boundingBox = new BoundingBox(vao, vertices, indices, file.getName().toLowerCase());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return boundingBox;
    }

    private static void calculateTangents(Vertex v0, Vertex v1, Vertex v2, List<Vector2f> textures) {
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

    private static Vertex processVertex(String[] vertex, List<Vertex> vertices, List<Integer> indices) {
        int index = Integer.parseInt(vertex[0]) - 1;
        Vertex currentVertex = vertices.get(index);
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

    private static Vertex dealWithAlreadyProcessedVertex(Vertex previousVertex, int newTextureIndex,
                                                         int newNormalIndex, List<Integer> indices, List<Vertex> vertices) {
        if (previousVertex.hasSameTextureAndNormal(newTextureIndex, newNormalIndex)) {
            indices.add(previousVertex.getIndex());
            return previousVertex;
        } else {
            Vertex anotherVertex = previousVertex.getDuplicateVertex();
            if (anotherVertex != null) {
                return dealWithAlreadyProcessedVertex(anotherVertex, newTextureIndex, newNormalIndex, indices,
                        vertices);
            } else {
                Vertex duplicateVertex = new Vertex(vertices.size(), previousVertex.getPosition());
                duplicateVertex.setTextureIndex(newTextureIndex);
                duplicateVertex.setNormalIndex(newNormalIndex);
                previousVertex.setDuplicateVertex(duplicateVertex);
                vertices.add(duplicateVertex);
                indices.add(duplicateVertex.getIndex());
                return duplicateVertex;
            }

        }
    }

    private static void removeUnusedVertices(List<Vertex> vertices) {
        for (Vertex vertex : vertices) {
            vertex.averageTangents();
            if (!vertex.isSet()) {
                vertex.setTextureIndex(0);
                vertex.setNormalIndex(0);
            }
        }
    }

    private static float convertDataToArrays(List<Vertex> vertices, List<Vector2f> textures, List<Vector3f> normals,
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
            Vertex currentVertex = vertices.get(i);
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

    private static MTLFile parseMTLFile(File file) {
        if (!file.exists())
            throw new MissingFileException(file);

        MTLFile MTLFile = null;
        File parentFile = file.getParentFile();
        int nbMaterials = Integer.MAX_VALUE;
        String objFileName = null;
        List<Material> materials = new ArrayList<>();
        Material material = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("# Blender MTL File:"))
                    objFileName = line.substring(21, line.length() - 1);
                else if (line.startsWith("# Material Count:"))
                    nbMaterials = Integer.parseInt(line.split(": ")[1]);
                if (line.startsWith("#"))
                    continue;

                if (line.startsWith("newmtl")) {
                    if (material != null)
                        materials.add(material);

                    material = new Material(line.substring(7));
                }

                if (line.startsWith("Ns"))
                    material.setShininessExponent(Float.parseFloat(line.substring(3)));

                if (line.startsWith("Ka")) {
                    material.setAmbient(handleMTLColor(line));
                }
                if (line.startsWith("Kd")) {
                    material.setDiffuse(handleMTLColor(line));
                }
                if (line.startsWith("Ks")) {
                    material.setSpecular(handleMTLColor(line));
                }
                if (line.startsWith("Ke")) {
                    material.setEmission(handleMTLColor(line));
                }
                if (line.startsWith("Ni"))
                    material.setOpticalDensity(Float.parseFloat(line.substring(3)));
                if (line.startsWith("d"))
                    material.setDissolve(Float.parseFloat(line.substring(2)));
                if (line.startsWith("illum"))
                    material.setIllumination(Float.parseFloat(line.substring(6)));

                if (line.startsWith("map_Ka")) {
                    String ambientFileName = line.substring(7);
                    File ambientFile = new File(parentFile, ambientFileName);
                    material.setAmbientMap(ambientFile);
                }
                if (line.startsWith("map_Kd")) {
                    String diffuseFileName = line.substring(7);
                    File diffuseFile = new File(parentFile, diffuseFileName);
                    material.setDiffuseMap(diffuseFile);
                }
                if (line.startsWith("map_Ks")) {
                    String specularFileName = line.substring(7);
                    File specularFile = new File(parentFile, specularFileName);
                    material.setSpecularMap(specularFile);
                }
                if (line.startsWith("map_Bump")) {
                    String[] lineParts = line.split(" ");
                    String bumpFileName = lineParts[lineParts.length - 1];
                    File bumpFile = new File(parentFile, bumpFileName);
                    material.setNormalMap(bumpFile);
                }
            }
            if (material != null) // Last Material
                materials.add(material);
            MTLFile = new MTLFile(file, objFileName, nbMaterials, materials);
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }

        return MTLFile;
    }

    private static OBJFile parseOBJFile(File file) {
        OBJFile OBJFile = new OBJFile(file);
        MTLFile MTLFile = null;
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
                    MTLFile = parseMTLFile(new File(file.getParentFile(), MTLFileName));
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
            if (MTLFile == null || !MTLFile.exists())
                throw new MissingFileException("MTLFile for " + file.getName());

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

                        material = MTLFile.getMaterial(line.substring(7).
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
                            Vertex v0 = processVertex(vertex1, vertices, indices);
                            Vertex v1 = processVertex(vertex2, vertices, indices);
                            Vertex v2 = processVertex(vertex3, vertices, indices);
                            calculateTangents(v0, v1, v2, textures);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            e.printStackTrace();
                            System.out.println(Arrays.toString(currentLine));
                        }
                    }
                } while ((line = reader.readLine()) != null);
                indicesList.get(material).addAll(new ArrayList<>(indices));

                removeUnusedVertices(vertices);
                float[] verticesArray = new float[vertices.size() * 3];
                float[] texturesArray = new float[vertices.size() * 2];
                float[] normalsArray = new float[vertices.size() * 3];
                float[] tangentsArray = new float[vertices.size() * 3];
                Vector3f[] minMax = new Vector3f[2];
                float furthest = convertDataToArrays(vertices, textures, normals, verticesArray, texturesArray,
                        normalsArray, tangentsArray, minMax);

                Map<Material, int[]> indicesArray = indicesList.entrySet().stream().collect(
                        Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream().mapToInt(i -> i).toArray()));

                MeshData meshData = new MeshData(verticesArray, texturesArray, normalsArray, indicesArray, tangentsArray);

                MTLFile.setMeshData(meshData);
                OBJFile.setMTLFile(MTLFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return OBJFile;
    }


    public static MaterialColor handleMTLColor(String line) {
        if (line == null)
            throw new MTLFileException("Line null");

        String[] RGB = line.split(" ");
        if (RGB.length < 4)
            throw new MTLFileException(line);

        if (RGB[1].equalsIgnoreCase("Mix")) { // Mix Color
            if (RGB.length != 9)
                throw new MTLFileException(line);

            String factor = RGB[2];
            String[] factorSplit = factor.split("=");
            String factorName = factorSplit[0];

            try {
                Vector3f firstColor = Utils.parseVector(Vector3f.class, Arrays.copyOfRange(RGB, 3, 6));
                Vector3f secondColor = Utils.parseVector(Vector3f.class, Arrays.copyOfRange(RGB, 6, 9));

                if (factorName.equals("F"))
                    return new MixMaterialColor(firstColor, secondColor, Double.parseDouble(factorSplit[1]));
                if (factorName.equals("SEED")) { // Random
                    Random random = new Random(Long.parseLong(factorSplit[1]));
                    return new MixMaterialColor(firstColor, secondColor, random);
                }
            } catch (ReflectiveOperationException e) {
                throw new MTLFileException(line);
            }
        }

        if (RGB.length != 4)
            throw new MTLFileException(line);

        try {
            return new SimpleMaterialColor(Utils.parseVector(Vector3f.class, Arrays.copyOfRange(RGB, 1, 4)));
        } catch (ReflectiveOperationException e) {
            throw new MTLFileException(line);
        }
    }
}
