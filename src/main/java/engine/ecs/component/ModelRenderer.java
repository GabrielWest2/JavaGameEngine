package engine.ecs.component;

import editor.util.Range;
import engine.ecs.Component;
import engine.rendering.model.Model;

/**
 * @author gabed
 * @Date 7/23/2022
 */
public class ModelRenderer extends Component {

    public Model model;

    public boolean cullBack = true;

    public float shineDamper = 20;

    @Range(max = 2)
    public float reflectivity = 0.5f;


    public ModelRenderer(Model model) {
        this.model = model;
    }

    public Model getModel() {
        return model;
    }

    public Component setModel(Model model) {
        this.model = model;
        return this;
    }

    public static <T extends Component> Class<ModelRenderer> clazz(){
        return ModelRenderer.class;
    }
}
