package editor;

import engine.GameEngine;
import engine.terrain.Terrain;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import org.joml.Vector3f;
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

        ImGui.text("Terrain Settings");
        boolean z = ImGui.dragInt("Size", terrainSize);
        boolean r = ImGui.dragFloat("Resolution", terrainRez);
        boolean s = ImGui.sliderFloat("Scale", terrainScale, 0f, 10.0f);
        boolean a = ImGui.sliderFloat("Amplitude", terrainAmplitude, 0f, 10.0f);
        boolean x = ImGui.dragFloat("Offset X", terrainOffsetX);
        boolean y = ImGui.dragFloat("Offset Y", terrainOffsetY);
        boolean b = ImGui.colorEdit3("Base Color", coloredit1);
        boolean t = ImGui.colorEdit3("Top Color", coloredit2);
        if (s || a || x || y || b || t || z || r) {
            Terrain.TERRAIN_SIZE = terrainSize[0];
            Terrain.UNIT_SIZE = terrainRez[0];
            Terrain.scale = terrainScale[0];
            Terrain.amplitude = terrainAmplitude[0];
            Terrain.offsetX = terrainOffsetX[0];
            Terrain.offsetY = terrainOffsetY[0];
            Terrain.color1 = new Vector3f(coloredit1[0], coloredit1[1], coloredit1[2]);
            Terrain.color2 = new Vector3f(coloredit2[0], coloredit2[1], coloredit2[2]);
            GameEngine.terrain.UpdateModel();
        }

        ImGui.separator();
        ImGui.newLine();
        ImGui.text("Lua Settings");
        if (ImGui.button("LOAD LUA CODE")) {
            scriptingManager.loadCode();
        }

        ImGui.end();
    }

}
