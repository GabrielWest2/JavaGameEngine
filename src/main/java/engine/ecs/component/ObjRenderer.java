package engine.ecs.component;

import editor.NoHudRender;
import engine.ecs.Component;
import engine.model.Model;
import engine.model.ModelLoader;
import engine.texture.TextureLoader;
import imgui.ImGui;
import imgui.type.ImString;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.net.URI;

/**
 * @author gabed
 * @Date 7/23/2022
 */
public class ObjRenderer extends Component {

    public boolean cullBack = true;

    public float shineDamper = 20;

    public float reflectivity = 0.5f;

    @NoHudRender
    public String texturePath = "";

    @NoHudRender
    public String modelPath = "";

    public transient Model model;

    public ObjRenderer() {

    }

    @Override
    public void onVariableChanged() {
        try {
            if (new File("res/" + modelPath).exists() && !new File("res/" + modelPath).isDirectory() && new File("res/" + texturePath).exists() && !new File("res/" + texturePath).isDirectory())
                this.model = ModelLoader.loadUsingAssimp(modelPath, TextureLoader.loadTexture(texturePath));

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Model getModel() {
        return model;
    }

    public ObjRenderer setPaths(String modelPath, String texturePath){
        this.modelPath = modelPath;
        this.texturePath = texturePath;
        onVariableChanged();
        return this;
    }

    @Override
    public void GUI() {
        super.GUI();

        char c = '\uf07c';
        if(ImGui.button(c + "")){
            JFrame parentFrame = new JFrame();
            FileFilter filter = new FileNameExtensionFilter("Image file", new String[] {"jpg", "jpeg", "png", "tiff", "tif"});

            JFileChooser fileChooser = new JFileChooser("res/models/");
            fileChooser.addChoosableFileFilter(filter);
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.setDialogTitle("Select Texture");

            int userSelection = fileChooser.showOpenDialog(parentFrame);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                URI ogPath = fileToSave.toURI();
                URI resDirectory = new File("res/").toURI();
                URI relative = resDirectory.relativize(ogPath);
                this.texturePath = relative.getPath();
                onVariableChanged();
            }
        }
        ImGui.sameLine();

        ImString string = new ImString(100);
        string.set(texturePath);
        if (ImGui.inputText("Texture Path", string)) {
            this.texturePath = string.get();
            onVariableChanged();
        }


       // ImGui.pushID(ImGui.getID("secondButton"));
        ImGui.pushID("yaybuttoncool");
        boolean a = ImGui.button(c + "");
        ImGui.popID();
        if(a){
            JFrame parentFrame = new JFrame();
            FileFilter filter = new FileNameExtensionFilter("Model file", new String[] {"obj", "fbx"});

            JFileChooser fileChooser = new JFileChooser("res/models/");
            fileChooser.addChoosableFileFilter(filter);
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.setDialogTitle("Select Model");

            int userSelection = fileChooser.showOpenDialog(parentFrame);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                URI ogPath = fileToSave.toURI();
                URI resDirectory = new File("res/").toURI();
                URI relative = resDirectory.relativize(ogPath);
                this.modelPath = relative.getPath();
                onVariableChanged();
            }
        }
        //ImGui.popID();
        ImGui.sameLine();

        string = new ImString(100);
        string.set(modelPath);
        if (ImGui.inputText("Model Path", string)) {
            this.modelPath = string.get();
            onVariableChanged();
        }
    }
}
