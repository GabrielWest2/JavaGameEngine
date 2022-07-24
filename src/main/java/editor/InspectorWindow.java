package editor;

import engine.ecs.component.Component;
import imgui.ImGui;

/**
 * @author gabed
 * @Date 7/22/2022
 */
public class InspectorWindow {

    public static void render() {
        ImGui.showDemoWindow();
        ImGui.begin("Inspector");
        if (ExplorerWindow.selectedEntity != null) {
            for (Component component : ExplorerWindow.selectedEntity.getComponents()) {
                if (ImGui.collapsingHeader(component.getClass().getSimpleName())) {
                    component.GUI();
                    ImGui.separator();
                }
            }
        } else {
            ImGui.text("There are no objects selected.");
        }
        ImGui.end();
    }
}
