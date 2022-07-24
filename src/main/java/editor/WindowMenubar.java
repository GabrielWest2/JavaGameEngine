package editor;

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

            ImGui.endMainMenuBar();
        }

    }
}
