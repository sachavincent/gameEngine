package entities;

import terrains.Terrain;
import util.vector.Vector3f;

public class TerrainPoint extends Focusable {

    public TerrainPoint(Terrain terrain, Vector3f position, double height) {
        super(terrain, position, height);
    }
}
