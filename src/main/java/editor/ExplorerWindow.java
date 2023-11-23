package editor;

import editor.util.FAIcons;
import engine.ecs.Entity;
import engine.scene.SceneManager;
import imgui.ImGui;

/**
 * @author gabed
 * @Date 7/22/2022
 */
public class ExplorerWindow {
    public static Entity selectedEntity = null;

    public static void render() {
        ImGui.begin(FAIcons.ICON_SITEMAP + " Explorer");
        int i = 0;
        for (Entity entity : SceneManager.loadedScene.getEntities()) {
            if(!entity.getName().startsWith("Tile")) {
                ImGui.pushID(i);
                if (ImGui.button(entity.getName())) {
                    selectedEntity = entity;
                    LightingWindow.selectedPointLight = null;
                    LightingWindow.selectedSpotLight = null;
                }
                ImGui.popID();
            }
            i++;
        }
        ImGui.end();
    }
}
