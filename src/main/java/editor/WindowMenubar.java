package editor;

import engine.GameEngine;
import engine.ecs.Entity;
import engine.ecs.component.ObjRenderer;
import engine.WaterManger;
import imgui.ImGui;

/**
 * @author gabed
 * @Date 7/23/2022
 */
public class WindowMenubar {

    public static void render() {
        if (ImGui.beginMainMenuBar()) {
            if (ImGui.beginMenu("File")) {
                if (ImGui.menuItem("Save")) {
                    GameEngine.getInstance().loadedScene.save();
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
