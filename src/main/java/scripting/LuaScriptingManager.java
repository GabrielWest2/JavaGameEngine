package scripting;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

public class LuaScriptingManager {
    private static LuaValue updateFunction;
    private static LuaValue startFunction;
    private static LuaValue awakeFunction;

    public static void update() {
        try {
            if (updateFunction != null)
                updateFunction.call();
        } catch (LuaError error) {
            System.out.println("[ERROR]: " + error.getMessage());
        }
    }

    public static void awake() {
        try {
            if (awakeFunction != null)
                awakeFunction.call();
        } catch (LuaError error) {
            System.out.println("[ERROR]: " + error.getMessage());
        }
    }

    public static void start() {
        try {
            if (startFunction != null) {
                System.out.println("start function");
                startFunction.call();
            }
        } catch (LuaError error) {
            System.out.println("[ERROR]: " + error.getMessage());
        }
    }

    public static void loadCode() {
        Thread vmThread = new Thread(() -> {
            Globals globals = JsePlatform.standardGlobals();
            try {
                LuaValue chunk = globals.loadfile("scripts/test.lua");
                chunk.call();
                updateFunction = globals.get("Update");
                awakeFunction = globals.get("Awake");
                startFunction = globals.get("Start");
            } catch (LuaError error) {
                System.out.println("[ERROR]: " + error.getMessage());
            }
        });
        vmThread.start();
    }

}
