package scene.components;

import entities.Model;

public class SingleModelComponent extends Component {

    protected Model model;

    public SingleModelComponent(Model model) {
        this.model = model;
    }

    public Model getModel() {
        return this.model;
    }

    public void setModel(Model model) {
        this.model = model;
    }
}
