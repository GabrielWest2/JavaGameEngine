package editor;

import engine.GameEngine;
import engine.TerrainManager;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImInt;
import scripting.LuaScriptingManager;

public class DebugWindow {

    private static final LuaScriptingManager scriptingManager;
    static {
        scriptingManager = new LuaScriptingManager();
    }

    private static int lod = 1;

    public static void render() {
        ImGui.begin(FAIcons.ICON_BUG + " Debug Window", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse);

        ImInt i = new ImInt(lod);
        if(ImGui.inputInt("Terrain Lod", i) && i.get() > 0 && i.get() < 11){
            lod =  i.get();
            TerrainManager.regenerateChunks(lod);
        }


        ImGui.text("Lua Settings");
        if (ImGui.button("LOAD LUA CODE")) {
            scriptingManager.loadCode();
        }

        ImGui.image(GameEngine.refractionBuffer.getColorTexture(), 1920/10f, 1080/10f, 0, 1, 1, 0);
        ImGui.image(GameEngine.reflectionBuffer.getColorTexture(), 1920/10f, 1080/10f, 0, 1, 1, 0);

        ImGui.end();
    }

}
