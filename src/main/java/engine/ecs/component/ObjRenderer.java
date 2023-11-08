package engine.ecs.component;

import engine.ecs.Component;
import engine.model.Model;
import engine.model.OBJLoader;
import engine.texture.TextureLoader;

import java.io.File;

/**
 * @author gabed
 * @Date 7/23/2022
 */
public class ObjRenderer extends Component {
    public boolean cullBack = true;
    public float shineDamper = 20;
    public float reflectivity = 0.5f;
    public String texturePath = "";
    public String filePath = "";
    public transient Model model;

    public ObjRenderer() {

    }

    @Override
    public void onVariableChanged() {
        try {
            if (new File("res/" + filePath).exists() && !new File("res/" + filePath).isDirectory() && new File("res/" + texturePath).exists() && !new File("res/" + texturePath).isDirectory())
                this.model = OBJLoader.loadUsingAssimp(filePath, TextureLoader.loadTexture(texturePath));

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Model getModel() {
        return model;
    }

    public ObjRenderer setPaths(String filePath, String texturePath){
        this.filePath = filePath;
        this.texturePath = texturePath;
        onVariableChanged();
        return this;
    }

}
