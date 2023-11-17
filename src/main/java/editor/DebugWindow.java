package editor;

import editor.util.FAIcons;
import engine.GameEngine;
import engine.TerrainManager;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImInt;
import scripting.LuaScriptingManager;

public class DebugWindow {

    private static final LuaScriptingManager scriptingManager
            = new LuaScriptingManager();

    private static int lod = 1;

    public static void render() {
        int flags = ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse;
        ImGui.begin(FAIcons.ICON_BUG + " Debug Window", flags);

        ImInt i = new ImInt(lod);
        if(ImGui.inputInt("Terrain Lod", i) && i.get() > 0 && i.get() < 11){
            lod =  i.get();
            TerrainManager.regenerateChunks(lod);
        }


        ImGui.text("Lua Settings");
        if (ImGui.button("LOAD LUA CODE")) {
            scriptingManager.loadCode();
        }

        ImGui.image(GameEngine.refractionBuffer.getColorTexture(),
                1920/10f, 1080/10f, 0, 1, 1, 0);
        ImGui.image(GameEngine.reflectionBuffer.getColorTexture(),
                1920/10f, 1080/10f, 0, 1, 1, 0);

        float[] cols  = new float[] {
                GameEngine.loadedScene.getLights().getAmbientLight().getColor().x,
                GameEngine.loadedScene.getLights().getAmbientLight().getColor().y,
                GameEngine.loadedScene.getLights().getAmbientLight().getColor().z
        };
        if(ImGui.sliderFloat3("col", cols, 0.0f, 1.0f)){
            GameEngine.loadedScene.getLights().getAmbientLight().getColor().x = cols[0];
            GameEngine.loadedScene.getLights().getAmbientLight().getColor().y = cols[1];
            GameEngine.loadedScene.getLights().getAmbientLight().getColor().z = cols[2];
        }


        ImGui.end();
    }

}
