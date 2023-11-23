package engine.rendering.hud;

import engine.rendering.DisplayManager;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.nanovg.NanoVG.nvgBeginFrame;
import static org.lwjgl.nanovg.NanoVG.nvgEndFrame;
import static org.lwjgl.nanovg.NanoVGGL3.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class HudManager {

    static long vg;

    private static List<HudImage> images = new ArrayList<>();

    private static List<HudRectangle> rectangles = new ArrayList<>();


    public static void init(){
        vg = nvgCreate(NVG_ANTIALIAS | NVG_STENCIL_STROKES);
        if (vg == NULL) {
            System.out.println("Could not init nanovg");
        }
    }


    public static void renderHud(){
        nvgBeginFrame(
                vg,
                DisplayManager.getWidth(),
                DisplayManager.getHeight(),
                1);

        for(HudRectangle rectangle : rectangles){
            rectangle.render();
        }
        for(HudImage image : images){
            image.render();
        }

        nvgEndFrame(vg);
    }
}
