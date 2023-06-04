package engine.hud;

import org.joml.Vector4fc;
import org.lwjgl.nanovg.NVGColor;

import static engine.hud.HudManager.vg;
import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVG.nvgFill;

public class HudRectangle {
    private NVGColor color;
    private Vector4fc bgColor;
    private int x, y, w, h;

    public HudRectangle(int x, int y, int w, int h, Vector4fc bgColor){
        this.bgColor = bgColor;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        updateColor();
    }

    public void render(){
        nvgBeginPath(vg);
        nvgFillColor(vg, color);
        nvgRoundedRect(vg, x, y, w, h, 15);
        nvgFill(vg);
    }

    private void updateColor(){
        color = NVGColor.calloc();
        fillNvgColorWithRGBA(bgColor, color);
    }

    public void setPosition(int x, int y){
        this.x = x;
        this.y = y;
    }

    public void setSize(int w, int h){
        this.w = w;
        this.h = h;
    }

    public static void fillNvgColorWithRGBA(Vector4fc rgba, NVGColor color) {
        color.r(rgba.x());
        color.g(rgba.y());
        color.b(rgba.z());
        color.a(rgba.w());
    }
}
