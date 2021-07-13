package models;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import textures.ModelTexture;
import util.Vao;
import util.math.Plane3D;
import util.math.Triangle3D;
import util.math.Vector3f;
import util.parsing.ModelType;
import util.parsing.colladaParser.dataStructures.MeshData;

public class BoundingBox extends Model {

    private final Set<Plane3D> planes;

    public BoundingBox(BoundingBox boundingBox) {
        super(boundingBox.vao, boundingBox.modelTexture);
        this.planes = boundingBox.planes.stream().map(Plane3D::new)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public BoundingBox(Vao vao) {
        super(vao);
        this.planes = new LinkedHashSet<>();
    }

    public BoundingBox(List<Vector3f> vertices, List<Integer> indices, String name) {
        this.planes = new LinkedHashSet<>();

        if (indices.size() % 3 != 0)
            throw new IllegalArgumentException("Wrong indices for " + name);
        try {
            List<Triangle3D> triangles = new ArrayList<>();
            for (int i = 0; i < indices.size(); i += 3)
                triangles.add(new Triangle3D(vertices.get(indices.get(i)), vertices.get(indices.get(i + 1)),
                        vertices.get(indices.get(i + 2))));

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

            float[] verticesArray = new float[vertices.size() * 3];
            int i = 0;
            for (Vector3f vec : vertices) {
                verticesArray[i++] = vec.x;
                verticesArray[i++] = vec.y;
                verticesArray[i++] = vec.z;
            }

            float[] textureCoords = new float[vertices.size() * 2];
            float[] normals = new float[vertices.size() * 3];
//            planes.forEach(plane3D -> {
//                plane3D.setPointA(new Vector3f(plane3D.getPointA().x, -plane3D.getPointA().z, plane3D.getPointA().y));
//                plane3D.setPointB(new Vector3f(plane3D.getPointB().x, -plane3D.getPointB().z, plane3D.getPointB().y));
//                plane3D.setPointC(new Vector3f(plane3D.getPointC().x, -plane3D.getPointC().z, plane3D.getPointC().y));
//                plane3D.setPointD(new Vector3f(plane3D.getPointD().x, -plane3D.getPointD().z, plane3D.getPointD().y));
//            });
//            planes.forEach(System.out::println);
//            this.rawModel = Loader.getInstance().loadToVAO(verticesArray, textureCoords, normals, indices);
            int[] indicesArray = indices.stream().mapToInt(k -> k).toArray();
            this.vao = Vao.createVao(new MeshData(verticesArray, textureCoords, normals, indicesArray), ModelType.DEFAULT);
            this.modelTexture = ModelTexture.DEFAULT_MODEL;
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
