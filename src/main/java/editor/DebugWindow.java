package editor;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import scripting.LuaScriptingManager;

public class DebugWindow {

    private static final float[] contrast = new float[]{0};
    private static final float[] terrainScale = new float[]{2};

    public static void render() {
        ImGui.begin("Settings", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse);
        if (ImGui.button("LOAD LUA CODE")) {
            new LuaScriptingManager().init();
        }

        ImGui.text("Terrain Settings");
        ImGui.sliderFloat("Scale", terrainScale, -10f, 10.0f);
        ImGui.end();
    }

}
