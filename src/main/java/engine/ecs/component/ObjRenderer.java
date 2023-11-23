package engine.ecs.component;

import editor.util.NoHudRender;
import engine.ecs.Component;
import engine.rendering.model.Mesh;
import engine.rendering.model.Model;
import engine.rendering.Material;
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

    @NoHudRender
    public String modelPath = "";

    public transient Model model;

    public ObjRenderer() {

    }

    @Override
    public void onVariableChanged() {
        try {
            if (new File("res/" + modelPath).exists() && !new File("res/" + modelPath).isDirectory()) {
                this.model = new Model(modelPath);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Model getModel() {
        return model;
    }

    public ObjRenderer setPaths(String modelPath){
        this.modelPath = modelPath;
        onVariableChanged();
        return this;
    }

    @Override
    public void GUI() {
        super.GUI();

        char c = '\uf07c';

        ImGui.pushID("yaybuttoncool");
        boolean a = ImGui.button(c + "");
        ImGui.popID();
        if(a){
            JFrame parentFrame = new JFrame();
            FileFilter filter = new FileNameExtensionFilter("Model file", new String[] {"obj", "fbx", "dae", "gltf"});

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

        ImString string = new ImString(100);
        string.set(modelPath);
        if (ImGui.inputText("Model Path", string)) {
            this.modelPath = string.get();
            onVariableChanged();
        }

        if(model == null)
            return;
        int i =0;
        for(Mesh m : model.getMeshes()){
            if(ImGui.collapsingHeader("Mesh " + i)){
                Material mat = m.getMaterial();
                ImGui.text("Texture Path: " + mat.getTexturePath());
                ImGui.text("Texture ID: " + (mat.getLoadedTexture() == null ? "unloaded" : Integer.toString(mat.getLoadedTexture().textureID())));

                float[] cols  = new float[] {
                    mat.getDiffuseColor().x,
                    mat.getDiffuseColor().y,
                    mat.getDiffuseColor().z
                };
                if(ImGui.colorEdit3("Diffuse Color", cols)){
                    mat.getDiffuseColor().x = cols[0];
                    mat.getDiffuseColor().y = cols[1];
                    mat.getDiffuseColor().z = cols[2];
                }

                cols  = new float[] {
                    mat.getAmbientColor().x,
                    mat.getAmbientColor().y,
                    mat.getAmbientColor().z
                };
                if(ImGui.colorEdit3("Ambient Color", cols)){
                    mat.getAmbientColor().x = cols[0];
                    mat.getAmbientColor().y = cols[1];
                    mat.getAmbientColor().z = cols[2];
                }

                cols  = new float[] {
                    mat.getSpecularColor().x,
                    mat.getSpecularColor().y,
                    mat.getSpecularColor().z
                };
                if(ImGui.colorEdit3("Specular Color", cols)){
                    mat.getSpecularColor().x = cols[0];
                    mat.getSpecularColor().y = cols[1];
                    mat.getSpecularColor().z = cols[2];
                }
            }
            i++;
        }
    }
}
