package scene.components;

public class RepleacableComponent implements Component {

    private final boolean repleacable;

    public RepleacableComponent() {
        this.repleacable = true;
    }

    public RepleacableComponent(boolean repleacable) {
        this.repleacable = repleacable;
    }

    public boolean isRepleacable() {
        return this.repleacable;
    }

}
