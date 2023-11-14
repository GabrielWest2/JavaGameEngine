package engine.util;

import org.joml.Vector3f;
import org.joml.Vector3i;

/**
 * @author gabed
 * @Date 7/23/2022
 */
public class Color {
    public float r, g, b;

    public Color(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public void set(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public Vector3f getRGB(){
        return new Vector3f(r, g, b);
    }

    public Vector3i getRGB256(){
        return new Vector3i(Math.round(r*256), Math.round(g*256), Math.round(b*256));
    }

    public void setRGB(float r, float g, float b){
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public void setRGB256(int r, int g, int b){
        this.r = r / 256.0f;
        this.g = g / 256.0f;
        this.b = b / 256.0f;
    }
}
