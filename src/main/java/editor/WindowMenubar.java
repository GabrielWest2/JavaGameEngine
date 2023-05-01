package editor;

import engine.GameEngine;
import engine.ecs.Entity;
import engine.ecs.component.ModelRenderer;
import engine.ecs.component.ObjRenderer;
import engine.ecs.component.Terrain;
import engine.ecs.component.Water;
import engine.model.MultiTexturedModel;
import engine.model.OBJLoader;
import engine.texture.TextureLoader;
import imgui.ImGui;

/**
 * @author gabed
 * @Date 7/23/2022
 */
public class WindowMenubar {

    public static void render() {
        if (ImGui.beginMainMenuBar()) {
            if (ImGui.beginMenu("Themes")) {
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
                if (ImGui.menuItem("Add Terrain Component")) {
                    GameEngine.getInstance().loadedScene.addEntity(new Entity().addComponent(new Terrain()));
                }
                if (ImGui.menuItem("Add Water Component")) {
                    GameEngine.getInstance().loadedScene.addEntity(new Entity().addComponent(new Water()));
                }
                if (ImGui.menuItem("Add OBJ Component")) {
                    GameEngine.getInstance().loadedScene.addEntity(new Entity().addComponent(new ObjRenderer()));
                }

                ImGui.endMenu();
            }

            ImGui.endMainMenuBar();
        }

    }
}
