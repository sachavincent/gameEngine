package util.parsing.objParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import renderEngine.shaders.structs.Material;
import renderEngine.structures.IndexData;
import util.Utils;
import util.exceptions.MTLFileException;
import util.exceptions.MissingFileException;
import util.math.Vector3f;
import util.parsing.MaterialColor;
import util.parsing.MixMaterialColor;
import util.parsing.SimpleMaterialColor;

public class MTLFile extends File {

    private final String  name;
    private final OBJFile objFile;
    private final int     nbMaterials;

    private final List<Material> materials;

    private IndexData meshData;

    public MTLFile(File parent, String name, String objFileName, int nbMaterials,
            List<Material> materials) {
        this(parent, name, new OBJFile(parent, objFileName), nbMaterials, materials);
    }

    public MTLFile(File file, String objFileName, int nbMaterials, List<Material> materials) {
        this(file.getParentFile(), file.getName(),
                new OBJFile(file.getParentFile(), objFileName), nbMaterials, materials);
    }

    public MTLFile(File parent, String name, OBJFile objFile, int nbMaterials, List<Material> materials) {
        super(parent, name);

        this.name = name;
        this.objFile = objFile;
        this.nbMaterials = nbMaterials;
        this.materials = materials;
    }

    public String getName() {
        return this.name;
    }

    public int getNbMaterials() {
        return this.nbMaterials;
    }

    public IndexData getMeshData() {
        return this.meshData;
    }

    public List<Material> getMaterials() {
        return this.materials;
    }

    public OBJFile getObjFile() {
        return this.objFile;
    }

    public void setMeshData(IndexData meshData) {
        this.meshData = meshData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        MTLFile mtlFile = (MTLFile) o;
        return this.nbMaterials == mtlFile.nbMaterials && this.name.equals(mtlFile.name) &&
                this.materials.equals(mtlFile.materials);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.nbMaterials, this.materials);
    }

    @Override
    public String toString() {
        return "MTLFile{name='" + this.name + '}';
    }

    public Material getMaterial(String materialName) {
        if (materialName.contains("::")) { // Material variant
            String[] split = materialName.split("::");
            String originalMaterialName = split[0];
            Material material = this.materials.stream().filter(m ->
                    m.getName().equals(originalMaterialName)).findFirst().orElse(null);
            int variantNumber = Integer.parseInt(split[1]);
            if (material != null)
                return material.createVariant(variantNumber);
            return null;
        }

        return this.materials.stream().filter(material ->
                material.getName().equals(materialName)).findFirst().orElse(null);
    }

    public static MTLFile parseMTLFile(File file) {
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