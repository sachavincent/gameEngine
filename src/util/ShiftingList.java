package util;

import java.util.List;

public class ShiftingList<P> {

    private final List<P> parameters;

    private int currentIndex;

    public ShiftingList(List<P> parameters) {
        this.parameters = parameters;
        this.currentIndex = 0;
    }

    public P shiftAndGet() {
        this.currentIndex = (this.currentIndex + 1) % this.parameters.size();
        return get();
    }

    public P getAndshift() {
        P value = get();
        this.currentIndex = (this.currentIndex + 1) % this.parameters.size();

        return value;
    }

    public void shift() {
        this.currentIndex = (this.currentIndex + 1) % this.parameters.size();
    }

    public P get() {
        if (this.currentIndex >= this.parameters.size())
            throw new IndexOutOfBoundsException();

        return this.parameters.get(this.currentIndex);
    }
}