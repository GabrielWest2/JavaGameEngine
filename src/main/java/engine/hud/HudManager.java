package engine.hud;

import engine.display.DisplayManager;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGPaint;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.nanovg.NanoSVG.nsvgParse;
import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVGGL3.*;
import static org.lwjgl.system.MemoryUtil.*;

public class HudManager {
    static long vg;

    private static int red;
    private static int gray;
    private static int yellow;
    private static int dpad;
    private static int roundBombPlus;
    private static List<HudImage> images = new ArrayList<>();
    private static List<HudRectangle> rectangles = new ArrayList<>();


    public static void init(){
        vg = nvgCreate(NVG_ANTIALIAS | NVG_STENCIL_STROKES);
        if (vg == NULL) {
            System.out.println("Could not init nanovg");
        }

        //red = NanoVGImageUtils.loadImage("hud/red.png");
        //gray = NanoVGImageUtils.loadImage("hud/gray.png");
        //yellow = NanoVGImageUtils.loadImage("hud/yellow.png");
        dpad = NanoVGImageUtils.loadImage("hud/dpad.png");
        //roundBombPlus = NanoVGImageUtils.loadImage("hud/cubeBomb.png");
        for(int y = 0; y < 2; y ++) {
            for (int i = 0; i < 15; i++) {
                if((y*15 + i) > 22)
                    break;
                //images.add(new HudImage((y*15 + i) > 12 ? yellow : red, 25 + (33 * i), 17 + (29 * y), 30, 24));
            }
        }
        images.add(new HudImage(dpad,60, 130, 50, 50));
        //images.add(new HudImage(roundBombPlus,60, 80, 50, 50));
    }


    public static void renderHud(){
        nvgBeginFrame(vg, DisplayManager.getWidth(), DisplayManager.getHeight(), 1);

        for(HudRectangle rectangle : rectangles){
            rectangle.render();
        }
        for(HudImage image : images){
            image.render();
        }

        nvgEndFrame(vg);
    }
}
