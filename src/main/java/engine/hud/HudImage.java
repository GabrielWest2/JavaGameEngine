package engine.hud;

import org.lwjgl.nanovg.NVGPaint;

import static engine.hud.HudManager.vg;
import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVG.nvgFill;

public class HudImage {

    private NVGPaint paint;

    private int texture;

    private int x;

    private int y;

    private int w;

    private int h;

    public HudImage(int texture, int x, int y, int w, int h){
        this.texture = texture;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        updatePaint();
    }

    public void render(){
        nvgBeginPath(vg);
        nvgRect(vg, x, y, w, h);
        nvgFillPaint(vg, paint);
        nvgFill(vg);
    }

    private void updatePaint(){
        paint = NanoVGImageUtils.getPaint(texture, x, y, w, h);
    }

    public void setPosition(int x, int y){
        this.x = x;
        this.y = y;
        updatePaint();
    }

    public void setSize(int w, int h){
        this.w = w;
        this.h = h;
        updatePaint();
    }

    public void setTexture(int texture){
        this.texture = texture;
        updatePaint();
    }
}
