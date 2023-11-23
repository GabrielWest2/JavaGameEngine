package editor;

import editor.util.ImGuiThemer;
import engine.ecs.Entity;
import engine.ecs.component.ObjRenderer;
import engine.scene.SceneManager;
import imgui.ImGui;

import javax.swing.*;
import java.io.File;

/**
 * @author gabed
 * @Date 7/23/2022
 */
public class WindowMenubar {

    public static void render() {
        if (ImGui.beginMainMenuBar()) {
            if (ImGui.beginMenu("File")) {
                if (ImGui.menuItem("Save")) {
                    // parent component of the dialog
                    JFrame parentFrame = new JFrame();

                    JFileChooser fileChooser = new JFileChooser("scenes/");
                    fileChooser.setDialogTitle("Save Scene");

                    int userSelection = fileChooser.showSaveDialog(parentFrame);

                    if (userSelection == JFileChooser.APPROVE_OPTION) {
                        File fileToSave = fileChooser.getSelectedFile();
                        System.out.println("Save as file: " + fileToSave.getAbsolutePath());
                        SceneManager.save(fileToSave.getAbsolutePath());
                    }

                }

                if (ImGui.menuItem("Load")) {
                    // parent component of the dialog
                    JFrame parentFrame = new JFrame();

                    JFileChooser fileChooser = new JFileChooser("scenes/");
                    fileChooser.setDialogTitle("Save Scene");

                    int userSelection = fileChooser.showOpenDialog(parentFrame);

                    if (userSelection == JFileChooser.APPROVE_OPTION) {
                        File fileToSave = fileChooser.getSelectedFile();
                        SceneManager.load(fileToSave.getAbsolutePath());
                    }

                }

                ImGui.endMenu();
            }
            if (ImGui.beginMenu("Themes")) {
                if(ImGui.menuItem("Dark Theme")){
                    ImGuiThemer.newDarkTheme();
                }
                if (ImGui.menuItem("Default Theme")) {
                    ImGuiThemer.darkTheme();
                }
                if (ImGui.menuItem("VGUI Theme")) {
                    ImGuiThemer.vguiTheme();
                }
                if (ImGui.menuItem("Gold Theme")) {
                    ImGuiThemer.goldTheme();
                }
                if (ImGui.menuItem("Red Theme")) {
                    ImGuiThemer.redTheme();
                }
                ImGui.endMenu();
            }

            if (ImGui.beginMenu("Game Object")) {
                if (ImGui.menuItem("Add Empty Component")) {
                    SceneManager.loadedScene.addEntity(new Entity());
                }
                if (ImGui.menuItem("Add Grass Block")) {
                    SceneManager.loadedScene.addEntity(new Entity().addComponent(new ObjRenderer().setPaths("models/grass.obj")));
                }

                ImGui.endMenu();
            }

            ImGui.endMainMenuBar();
        }

    }
}
