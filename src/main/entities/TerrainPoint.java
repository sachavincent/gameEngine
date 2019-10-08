package main.entities;

import main.terrains.Terrain;
import main.util.vector.Vector3f;

public class TerrainPoint extends Focusable {

    public TerrainPoint(Terrain terrain, Vector3f position, double height) {
        super(terrain, position, height);
    }
}
