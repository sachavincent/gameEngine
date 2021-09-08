package display;

import java.util.Objects;

public class Resolution implements Comparable<Resolution> {

    private final int width;
    private final int height;

    public Resolution(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    @Override
    public String toString() {
        return this.width + " x " + this.height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Resolution that = (Resolution) o;
        return this.width == that.width &&
                this.height == that.height;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.width, this.height);
    }

    @Override
    public int compareTo(Resolution o) {
        int compare = Integer.compare(o.width, this.width);
        if (compare != 0)
            return compare;

        return Integer.compare(o.height, this.height);
    }
}