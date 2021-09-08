package util.parsing.objParser;

import java.io.File;
import java.util.List;
import java.util.Objects;
import renderEngine.shaders.structs.Material;
import renderEngine.structures.IndexData;

public class MTLFile extends File {

    private final String name;
    private final OBJFile objFile;
    private final int nbMaterials;

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
}
