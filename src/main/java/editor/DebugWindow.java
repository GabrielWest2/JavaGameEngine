package editor;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import scripting.LuaScriptingManager;

public class DebugWindow {

    private static final int[] terrainSize = new int[]{80};
    private static final float[] terrainRez = new float[]{0.5f};
    private static final float[] terrainScale = new float[]{10};
    private static final float[] terrainAmplitude = new float[]{2};
    private static final float[] terrainOffsetX = new float[]{0};
    private static final float[] terrainOffsetY = new float[]{0};
    private static final float[] coloredit1 = new float[]{255f / 255f, 217f / 255f, 66f / 255f};
    private static final float[] coloredit2 = new float[]{102f / 255f, 204f / 255f, 71f / 255f};
    private static final LuaScriptingManager scriptingManager;

    static {
        scriptingManager = new LuaScriptingManager();
    }

    public static void render() {
        ImGui.begin("Debug Window", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse);

        ImGui.text("Lua Settings");
        if (ImGui.button("LOAD LUA CODE")) {
            scriptingManager.loadCode();
        }

        ImGui.end();
    }

}
