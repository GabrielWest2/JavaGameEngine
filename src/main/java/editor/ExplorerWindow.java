package editor;

import engine.GameEngine;
import engine.ecs.Entity;
import imgui.ImGui;

/**
 * @author gabed
 * @Date 7/22/2022
 */
public class ExplorerWindow {
    public static Entity selectedEntity = null;

    public static void render() {
        ImGui.begin("Explorer");
        int i = 0;
        for (Entity entity : GameEngine.getInstance().loadedScene.getEntities()) {
            ImGui.pushID(i);
            if (ImGui.button(entity.getName())) {
                selectedEntity = entity;
            }
            ImGui.popID();
            i++;
        }
        ImGui.end();
    }
}
