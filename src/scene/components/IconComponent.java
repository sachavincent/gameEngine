package scene.components;

import guis.presets.Background;

public class IconComponent extends Component {

    private final Background<?> background;

    public IconComponent(Background<?> background) {
        this.background = background;
    }

    public Background<?> getBackground() {
        return this.background;
    }
}
