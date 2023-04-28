package scripting;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

public class LuaScriptingManager {
    private static LuaValue updateFunction;
    private static LuaValue leftClickFunction;

    public static void Update() {
        try {
            if (updateFunction != null)
                updateFunction.call();
        } catch (LuaError error) {
            System.out.println("[ERROR]: " + error.getMessage());
        }
    }

    public static void LeftClick() {
        try {
            if (leftClickFunction != null)
                leftClickFunction.call();
        } catch (LuaError error) {
            System.out.println("[ERROR]: " + error.getMessage());
        }
    }

    public void loadCode() {
        Thread thread = new Thread(() -> {
            Globals globals = JsePlatform.standardGlobals();
            try {
                LuaValue chunk = globals.loadfile("scripts/test.lua");
                chunk.call();
                updateFunction = globals.get("Update");
                leftClickFunction = globals.get("LeftClick");
            } catch (LuaError error) {
                System.out.println("[ERROR]: " + error.getMessage());
            }
        });
        thread.start();
    }
}
