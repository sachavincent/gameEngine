package display;

import java.util.Set;

public class Monitor {

    private final long            id;
    private final int             index;
    private final int             x;
    private final int             y;
    private final boolean         isPrimary;
    private final Resolution      resolution;
    private final Set<Resolution> resolutions;

    public Monitor(int index, long id, int x, int y, boolean isPrimary, Set<Resolution> resolutions) {
        this.index = index;
        this.id = id;
        this.x = x;
        this.y = y;
        this.isPrimary = isPrimary;
        this.resolutions = resolutions;
        this.resolution = resolutions.stream().findFirst().orElse(null);
    }

    @Override
    public String toString() {
        return "Screen{" +
                "id=" + this.id +
                ", x=" + this.x +
                ", y=" + this.y +
                ", isPrimary=" + this.isPrimary +
                ", resolution=" + this.resolution +
                ", resolutions=" + this.resolutions +
                '}';
    }

    public long getId() {
        return this.id;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public boolean isPrimary() {
        return this.isPrimary;
    }

    public Resolution getResolution() {
        return this.resolution;
    }

    public Set<Resolution> getResolutions() {
        return this.resolutions;
    }

    public int getIndex() {
        return this.index;
    }
}