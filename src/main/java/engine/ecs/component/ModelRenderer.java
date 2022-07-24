package engine.ecs.component;

import engine.model.Model;

/**
 * @author gabed
 * @Date 7/23/2022
 */
public class ModelRenderer extends Component {
    public Model model;

    public ModelRenderer() {

    }

    public ModelRenderer(Model model) {
        this.model = model;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }
}
