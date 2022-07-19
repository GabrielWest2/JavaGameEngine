package scripting;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

public class LuaScriptingManager {
    public void init() {
        GameEngineAPI engineAPI = new GameEngineAPI();
        Thread thread = new Thread(() -> {
            Globals globals = JsePlatform.standardGlobals();
            LuaValue api = CoerceJavaToLua.coerce(engineAPI);
            globals.set("engine", api);
            LuaValue chunk = globals.loadfile("scripts/test.lua");
            try {
                chunk.call();
            } catch (LuaError error) {
                System.out.println("[ERROR]: " + error.getMessage());
                System.out.println("[ERROR]: " + error.getCause());
            }
        });
        thread.start();
    }
}
