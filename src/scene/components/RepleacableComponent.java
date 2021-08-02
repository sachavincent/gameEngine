package scene.components;

public class RepleacableComponent extends Component {

    private final boolean repleacable;

    public RepleacableComponent() {
        this(true);
    }

    public RepleacableComponent(boolean repleacable) {
        this.repleacable = repleacable;
    }

    public boolean isRepleacable() {
        return this.repleacable;
    }

}
