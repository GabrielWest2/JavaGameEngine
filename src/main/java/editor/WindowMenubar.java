package editor;

import engine.GameEngine;
import engine.ecs.Entity;
import engine.ecs.component.ObjRenderer;
import engine.WaterManger;
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
                        GameEngine.getInstance().loadedScene.save(fileToSave.getAbsolutePath());
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
                        GameEngine.getInstance().loadedScene.load(fileToSave.getAbsolutePath());
                    }

                }

                ImGui.endMenu();
            }
            if (ImGui.beginMenu("Themes")) {
                if(ImGui.menuItem("Dark Theme")){
                    ImGuiThemer.NewDarkTheme();
                }
                if (ImGui.menuItem("Default Theme")) {
                    ImGuiThemer.DarkTheme();
                }
                if (ImGui.menuItem("VGUI Theme")) {
                    ImGuiThemer.VGUITheme();
                }
                if (ImGui.menuItem("Gold Theme")) {
                    ImGuiThemer.GoldTheme();
                }
                if (ImGui.menuItem("Red Theme")) {
                    ImGuiThemer.RedTheme();
                }
                ImGui.endMenu();
            }

            if (ImGui.beginMenu("Game Object")) {
                if (ImGui.menuItem("Add Empty Component")) {
                    GameEngine.getInstance().loadedScene.addEntity(new Entity());
                }
                if (ImGui.menuItem("Add Grass Block")) {
                    GameEngine.getInstance().loadedScene.addEntity(new Entity().addComponent(new ObjRenderer().setPaths("models/grass.obj", "models/ColorPaletteBLUE.png")));
                }

                ImGui.endMenu();
            }

            ImGui.endMainMenuBar();
        }

    }
}
