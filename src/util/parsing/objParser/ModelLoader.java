package util.parsing.objParser;

import static util.Utils.MODELS_PATH;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import models.AnimatedModel;
import models.BoundingBox;
import models.Model;
import objConverter.VertexNM;
import renderEngine.AnimatedModelLoader;
import scene.gameObjects.OBJGameObject;
import util.Utils;
import util.Vao;
import util.exceptions.MissingFileException;
import util.math.Vector2f;
import util.math.Vector3f;
import util.parsing.ModelType;
import util.parsing.colladaParser.colladaLoader.AnimationLoader;
import util.parsing.colladaParser.dataStructures.MeshData;

public class ModelLoader {

    public static Vao loadRoadModel() {
        float[] vertices = new float[]{-1, 0, 1, 1, 0, 1, -1, 0, -1, 1, 0, -1};
        float[] textureCoords = new float[]{0, 0, 1, 0, 0, 1, 1, 1};
        float[] normals = new float[]{0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0};
//        float[] tangents = new float[]{0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0};
        int[] indices = new int[]{1, 2, 0, 1, 3, 2};
        Vector3f min = new Vector3f(-1, 0, -1);
        Vector3f max = new Vector3f(1, 0, 1);

        return Vao.createVao(new MeshData(vertices, textureCoords, normals, indices), ModelType.DEFAULT);
    }

    public static OBJGameObject loadModel(String folder, ModelType modelType) {
        return loadModel(folder, "vertices", "texture", modelType);
    }

    public static OBJGameObject loadModel(String folder, String fileName, ModelType modelType) {
        return loadModel(folder, fileName, fileName, modelType);
    }

    public static OBJGameObject loadModel(String folder, String modelFileName, String textureFileName,
            ModelType modelType) {
        OBJGameObject objGameObject = new OBJGameObject();
        String extension = modelType.getExtension();
        File modelFile = new File(
                MODELS_PATH + (folder == null ? "/" : ("/" + folder + "/")) + modelFileName + extension);
        File textureFile = new File(
                MODELS_PATH + (folder == null ? "/" : ("/" + folder + "/")) + textureFileName + ".png");
        File boundingBoxFile = new File(
                MODELS_PATH + (folder == null ? "/" : ("/" + folder + "/")) + "boundingbox.obj");
        File normalsFile = new File(MODELS_PATH + (folder == null ? "/" : ("/" + folder + "/")) + "normals.png");
        if (!modelFile.exists())
            throw new MissingFileException(modelFile);

        if (modelType == ModelType.ANIMATED || modelType == ModelType.ANIMATED_INSTANCED ||
                modelType == ModelType.ANIMATED_WITH_NORMAL_MAP ||
                modelType == ModelType.ANIMATED_INSTANCED_WITH_NORMAL_MAP) {
            AnimatedModel animatedTexturedModel = AnimatedModelLoader
                    .loadEntity(modelFile, textureFile, modelType);
            animatedTexturedModel.doAnimation(AnimationLoader.loadAnimation(modelFile));
//            animatedTexturedModel.getModelTexture().setUseFakeLighting(true);
            objGameObject.setTexture(animatedTexturedModel);
            objGameObject.setPreviewTexture(animatedTexturedModel);
            if (boundingBoxFile.exists())
                objGameObject.setBoundingBox(loadBoundingBox(boundingBoxFile));
            return objGameObject;
        }

        OBJFile OBJFile = parseOBJFile(modelFile);
        MTLFile MTLFile = OBJFile.getMTLFile();
        if (MTLFile != null) {
            if (MTLFile.getMaterials().stream().anyMatch(Material::hasDiffuseMap)) {
                if (!textureFile.exists())
                    throw new MissingFileException(textureFile);
            }
            Vao vao = Vao.createVao(MTLFile.getMeshData(), modelType);

            objGameObject.setTexture(new Model(vao, OBJFile));
        }
        objGameObject.setPreviewTexture(objGameObject.getTexture());
        if (boundingBoxFile.exists())
            objGameObject.setBoundingBox(loadBoundingBox(boundingBoxFile));

        return objGameObject;
    }

    private static BoundingBox loadBoundingBox(File file) {
        BoundingBox boundingBox = null;
        String line;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            List<Vector3f> vertices = new ArrayList<>();
            List<Integer> indices = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                String[] currentLine = line.split(" ");

                if (line.startsWith("v ")) {
                    Vector3f vertex = new Vector3f(Float.parseFloat(currentLine[1]),
                            Float.parseFloat(currentLine[2]), Float.parseFloat(currentLine[3]));
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

            boundingBox = new BoundingBox(vertices, indices, file.getName().toLowerCase());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return boundingBox;
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
            VertexNM currentVertex;
            Vector2f textureCoord;
            Vector3f normalVector;
            try {
                currentVertex = vertices.get(i);
                textureCoord = textures.get(currentVertex.getTextureIndex());
                normalVector = normals.get(currentVertex.getNormalIndex());
            } catch (IndexOutOfBoundsException e) {
                break;
            }
            if (currentVertex.getLength() > furthestPoint) {
                furthestPoint = currentVertex.getLength();
            }
            Vector3f position = currentVertex.getPosition();
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
                    String[] ambientRGB = line.substring(3).split(" ");
                    material.setAmbient(Utils.parseVector(Vector3f.class, ambientRGB));
                }
                if (line.startsWith("Kd")) {
                    String[] diffuseRGB = line.substring(3).split(" ");
                    material.setDiffuse(Utils.parseVector(Vector3f.class, diffuseRGB));
                }
                if (line.startsWith("Ks")) {
                    String[] specularRGB = line.substring(3).split(" ");
                    material.setSpecular(Utils.parseVector(Vector3f.class, specularRGB));
                }
                if (line.startsWith("Ke")) {
                    String[] emissionRGB = line.substring(3).split(" ");
                    material.setEmission(Utils.parseVector(Vector3f.class, emissionRGB));
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

            List<VertexNM> vertices = new ArrayList<>();
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
            if (MTLFile == null || !MTLFile.exists())
                throw new MissingFileException("MTLFile for " + file.getName());

            String group = null;
            Material material = null;
            Map<Material, int[]> materialsIndices = new LinkedHashMap<>();
            Map<Material, List<Integer>> tempLocalIndicesMap = new LinkedHashMap<>();
            List<Integer> indices = new ArrayList<>();
            List<Integer> localIndices = new ArrayList<>();
            List<Integer> tempLocalIndices = new ArrayList<>();

            do {
                if (line.startsWith("g ")) { // New group
                    if (group != null) {
                        materialsIndices.put(material, localIndices.stream().mapToInt(i -> i).toArray());
                        tempLocalIndicesMap.put(material, new ArrayList<>(tempLocalIndices));
                        localIndices.clear();
                        tempLocalIndices.clear();
                    }
                    group = line.substring(2);
                } else if (line.startsWith("f ")) {
                    String[] currentLine = line.split(" ");
                    try {
                        String[] vertex1 = currentLine[1].split("/");
                        String[] vertex2 = currentLine[2].split("/");
                        String[] vertex3 = currentLine[3].split("/");
                        List<VertexNM> vertices1 = new ArrayList<>(vertices);
//                        processVertex(vertex1, vertices1, indices);
//                         processVertex(vertex2, vertices1, indices);
//                        processVertex(vertex3, vertices1, indices);
                        VertexNM v1 = processVertex(vertex1, vertices, tempLocalIndices);
                        VertexNM v2 = processVertex(vertex2, vertices, tempLocalIndices);
                        VertexNM v3 = processVertex(vertex3, vertices, tempLocalIndices);
                        localIndices.add(materialsIndices.size());
//                        tempLocalIndices.add(Integer.parseInt(vertex1[0]) - 1);
//                        tempLocalIndices.add(Integer.parseInt(vertex2[0]) - 1);
//                        tempLocalIndices.add(Integer.parseInt(vertex3[0]) - 1);
//                        calculateTangents(v0, v1, v2, textures);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                        System.out.println(Arrays.toString(currentLine));
                    }
                } else if (line.startsWith("usemtl")) {
                    material = MTLFile.getMaterial(line.substring(7).trim());
                }
            } while ((line = reader.readLine()) != null);
            materialsIndices.put(material, localIndices.stream().mapToInt(i -> i).toArray());
            tempLocalIndicesMap.put(material, tempLocalIndices);

            removeUnusedVertices(vertices);
            float[] verticesArray = new float[vertices.size() * 3];
            float[] texturesArray = new float[vertices.size() * 2];
            float[] normalsArray = new float[vertices.size() * 3];
            float[] tangentsArray = new float[vertices.size() * 3];
            Vector3f[] minMax = new Vector3f[2];
            float furthest = convertDataToArrays(vertices, textures, normals, verticesArray, texturesArray,
                    normalsArray, tangentsArray, minMax);

            indices = tempLocalIndicesMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
            int[] indicesArray = indices.stream().mapToInt(i -> i).toArray();
            MeshData meshData = new MeshData(verticesArray, texturesArray, normalsArray, indicesArray, materialsIndices,
                    tangentsArray);
            meshData.setTempValue(tempLocalIndicesMap);
            MTLFile.setMeshData(meshData);
            OBJFile.setMTLFile(MTLFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return OBJFile;
    }
}
