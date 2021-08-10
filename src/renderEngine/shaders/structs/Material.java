package renderEngine.shaders.structs;

import textures.ModelTexture;
import util.math.Vector3f;
import util.parsing.MaterialColor;
import util.parsing.SimpleMaterialColor;
import util.parsing.colladaParser.dataStructures.MaterialData;

import java.awt.*;
import java.io.File;
import java.util.Objects;

public class Material implements StructElement {

    public final static Material DEFAULT = new Material("DEFAULT");

    private final String name;

    private float shininessExponent;
    private MaterialColor emission;
    private MaterialColor ambient;
    private MaterialColor diffuse;
    private MaterialColor specular;
    private float opticalDensity;
    private float dissolve;
    private float illumination;

    private ModelTexture ambientMap;
    private ModelTexture diffuseMap;
    private ModelTexture normalMap;
    private ModelTexture specularMap;

    protected Material() {
        this.name = null;
    }

    public Material(String name) {
        this.name = name;
    }

    public Material(MaterialData materialData) {
        this(materialData.getName());

        setAmbient(new SimpleMaterialColor(new Vector3f(0, 0, 0)));
        setDiffuse(materialData.getDiffuse());
        if (materialData.getDiffuseTexture() != null)
            setDiffuseMap(materialData.getDiffuseTexture());
        setSpecular(new SimpleMaterialColor(new Vector3f(materialData.getSpecular(),
                materialData.getSpecular(), materialData.getSpecular())));
        if (materialData.getSpecularTexture() != null)
            setSpecularMap(materialData.getSpecularTexture());
        setEmission(materialData.getEmission());
    }

    public final void setShininessExponent(float shininessExponent) {
        this.shininessExponent = shininessExponent;
    }

    public final void setAmbient(MaterialColor ambient) {
        this.ambient = ambient;
    }

    public final void setDiffuse(MaterialColor diffuse) {
        this.diffuse = diffuse;
    }

    public final void setSpecular(MaterialColor specular) {
        this.specular = specular;
    }

    public final void setEmission(MaterialColor emission) {
        this.emission = emission;
    }

    public final void setOpticalDensity(float opticalDensity) {
        this.opticalDensity = opticalDensity;
    }

    public final void setDissolve(float dissolve) {
        this.dissolve = dissolve;
    }

    public final void setIllumination(float illumination) {
        this.illumination = illumination;
    }

    public final void setAmbientMap(File ambientMap) {
        this.ambientMap = ModelTexture.createTexture(ambientMap);
    }

    public final void setDiffuseMap(File diffuseMap) {
        this.diffuseMap = ModelTexture.createTexture(diffuseMap);
    }

    public final void setNormalMap(File normalMap) {
        this.normalMap = ModelTexture.createTexture(normalMap);
    }

    public final void setSpecularMap(File specularMap) {
        this.specularMap = ModelTexture.createTexture(specularMap);
    }

    public final void setAmbientMap(ModelTexture ambientMap) {
        this.ambientMap = ambientMap;
    }

    public final void setDiffuseMap(ModelTexture diffuseMap) {
        this.diffuseMap = diffuseMap;
    }

    public final void setNormalMap(ModelTexture normalMap) {
        this.normalMap = normalMap;
    }

    public final void setSpecularMap(ModelTexture specularMap) {
        this.specularMap = specularMap;
    }

    public final String getName() {
        return this.name;
    }

    public final float getShininessExponent() {
        return this.shininessExponent;
    }

    public final MaterialColor getAmbient() {
        return this.ambient == null ? new SimpleMaterialColor(Color.BLACK) : this.ambient;
    }

    public final MaterialColor getDiffuse() {
        return this.diffuse == null ? new SimpleMaterialColor(Color.BLACK) : this.diffuse;
    }

    public final MaterialColor getSpecular() {
        return this.specular == null ? new SimpleMaterialColor(Color.BLACK) : this.specular;
    }

    public final MaterialColor getEmission() {
        return this.emission == null ? new SimpleMaterialColor(Color.BLACK) : this.emission;
    }

    public final float getOpticalDensity() {
        return this.opticalDensity;
    }

    public final float getDissolve() {
        return this.dissolve;
    }

    public final float getIllumination() {
        return this.illumination;
    }

    public final ModelTexture getAmbientMap() {
        return this.ambientMap == null ? ModelTexture.NONE : this.ambientMap;
    }

    public final ModelTexture getDiffuseMap() {
        return this.diffuseMap == null ? ModelTexture.NONE : this.diffuseMap;
    }

    public final ModelTexture getNormalMap() {
        return this.normalMap == null ? ModelTexture.NONE : this.normalMap;
    }

    public final ModelTexture getSpecularMap() {
        return this.specularMap == null ? ModelTexture.NONE : this.specularMap;
    }

    public final boolean hasAmbientMap() {
        return !getAmbientMap().equals(ModelTexture.NONE);
    }

    public final boolean hasDiffuseMap() {
        return !getDiffuseMap().equals(ModelTexture.NONE);
    }

    public final boolean hasNormalMap() {
        return !getNormalMap().equals(ModelTexture.NONE);
    }

    public final boolean hasSpecularMap() {
        return !getSpecularMap().equals(ModelTexture.NONE);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Material material = (Material) o;
        return this.name.equals(material.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name);
    }

    @Override
    public String toString() {
        return "Material{name='" + this.name + '}';
    }

    public Material createVariant(int numVariant) {
        Material material = new Material(this.name + "::" + numVariant);
//                "_" + UUID.randomUUID());
        if (this.ambient != null)
            material.setAmbient(this.ambient.copy());
        material.setAmbientMap(this.ambientMap);
        if (this.diffuse != null)
            material.setDiffuse(this.diffuse.copy());
        material.setDiffuseMap(this.diffuseMap);
        if (this.emission != null)
            material.setEmission(this.emission.copy());
        if (this.specular != null)
            material.setSpecular(this.specular.copy());
        material.setSpecularMap(this.specularMap);
        material.setOpticalDensity(this.opticalDensity);
        material.setShininessExponent(this.shininessExponent);
        material.setDissolve(this.dissolve);
        material.setIllumination(this.illumination);

        return material;
    }

    @Override
    public Class<? extends StructLocation> getStructure() {
        return MaterialStruct.class;
    }

    @Override
    public Object[] getValues() {
        return new Object[]{getEmission(), getAmbient(), getDiffuse(), getSpecular(),
                getShininessExponent(), hasAmbientMap(), hasDiffuseMap(),
                hasNormalMap(), hasSpecularMap(), getAmbientMap(), getDiffuseMap(),
                getNormalMap(), getSpecularMap()};
    }
}
