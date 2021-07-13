package util.parsing.objParser;

import java.io.File;
import java.util.Objects;
import textures.ModelTexture;
import util.math.Vector3f;

public class Material {

    private final String name;

    private float    shininessExponent;
    private Vector3f ambient;
    private Vector3f diffuse;
    private Vector3f specular;
    private Vector3f emission;
    private float    opticalDensity;
    private float    dissolve;
    private float    illumination;

    private ModelTexture ambientMap;
    private ModelTexture diffuseMap;
    private ModelTexture normalMap;
    private ModelTexture specularMap;

    public Material(String name) {
        this.name = name;
    }

    public void setShininessExponent(float shininessExponent) {
        this.shininessExponent = shininessExponent;
    }

    public void setAmbient(Vector3f ambient) {
        this.ambient = ambient;
    }

    public void setDiffuse(Vector3f diffuse) {
        this.diffuse = diffuse;
    }

    public void setSpecular(Vector3f specular) {
        this.specular = specular;
    }

    public void setEmission(Vector3f emission) {
        this.emission = emission;
    }

    public void setOpticalDensity(float opticalDensity) {
        this.opticalDensity = opticalDensity;
    }

    public void setDissolve(float dissolve) {
        this.dissolve = dissolve;
    }

    public void setIllumination(float illumination) {
        this.illumination = illumination;
    }

    public void setAmbientMap(File ambientMap) {
        this.ambientMap = ModelTexture.createTexture(ambientMap);
    }

    public void setDiffuseMap(File diffuseMap) {
        this.diffuseMap = ModelTexture.createTexture(diffuseMap);
    }

    public void setNormalMap(File normalMap) {
        this.normalMap = ModelTexture.createTexture(normalMap);
    }

    public void setSpecularMap(File specularMap) {
        this.specularMap = ModelTexture.createTexture(specularMap);
    }

    public String getName() {
        return this.name;
    }

    public float getShininessExponent() {
        return this.shininessExponent;
    }

    public Vector3f getAmbient() {
        return this.ambient;
    }

    public Vector3f getDiffuse() {
        return this.diffuse;
    }

    public Vector3f getSpecular() {
        return this.specular;
    }

    public Vector3f getEmission() {
        return this.emission;
    }

    public float getOpticalDensity() {
        return this.opticalDensity;
    }

    public float getDissolve() {
        return this.dissolve;
    }

    public float getIllumination() {
        return this.illumination;
    }

    public ModelTexture getAmbientMap() {
        return this.ambientMap;
    }

    public ModelTexture getDiffuseMap() {
        return this.diffuseMap;
    }

    public ModelTexture getNormalMap() {
        return this.normalMap;
    }

    public ModelTexture getSpecularMap() {
        return this.specularMap;
    }

    public boolean hasAmbientMap() {
        return this.ambientMap != null;
    }

    public boolean hasDiffuseMap() {
        return this.diffuseMap != null;
    }

    public boolean hasNormalMap() {
        return this.normalMap != null;
    }

    public boolean hasSpecularMap() {
        return this.specularMap != null;
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
}
