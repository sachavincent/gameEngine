package models;

import renderEngine.Vao;
import textures.ModelTexture;
import util.math.Plane3D;
import util.math.Triangle3D;
import util.parsing.Vertex;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BoundingBox extends SimpleModel {

    private final Set<Plane3D> planes;

    public BoundingBox(BoundingBox boundingBox) {
        super(boundingBox.vao);
        this.planes = boundingBox.planes.stream().map(Plane3D::new)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public BoundingBox(Vao vao) {
        super(vao);
        this.planes = new LinkedHashSet<>();
    }

    public BoundingBox(Vao vao, List<Vertex> vertices, List<Integer> indices, String name) {
        super(vao);

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
}
