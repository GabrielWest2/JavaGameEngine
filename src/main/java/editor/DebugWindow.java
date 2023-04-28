package editor;
import engine.GameEngine;
import engine.Renderer;
import engine.postprocessing.PostProcessing;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImFloat;
import scripting.LuaScriptingManager;

public class DebugWindow {

    private static final LuaScriptingManager scriptingManager;
    public static float waterMovement;
    static {
        scriptingManager = new LuaScriptingManager();
    }

    public static void render() {
        ImGui.begin("Debug Window", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse);

        ImGui.text("Lua Settings");
        if (ImGui.button("LOAD LUA CODE")) {
            scriptingManager.loadCode();
        }
        float[] floats = new float[]{
                waterMovement
        };
        if(ImGui.dragFloat("water", floats, 0.005f)){
            waterMovement = floats[0];
        }

        ImGui.image(GameEngine.refractionBuffer.getDepthTexture(), 1920/10f, 1080/10f, 0, 1, 1, 0);
        ImGui.image(GameEngine.reflectionBuffer.getDepthTexture(), 1920/10f, 1080/10f, 0, 1, 1, 0);

        ImGui.end();
    }

}
