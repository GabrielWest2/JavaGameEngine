package engine.rendering.lighting;

import org.joml.Vector3f;

public class PointLight {
    private Vector3f position;

    private Vector3f color;

    private float intensity;

    private LightAttenuation attenuation;

    public PointLight(Vector3f color, Vector3f position, float intensity) {
        attenuation = new LightAttenuation(0, 0, 1);
        this.color = color;
        this.position = position;
        this.intensity = intensity;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getColor() {
        return color;
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public LightAttenuation getAttenuation() {
        return attenuation;
    }

    public void setAttenuation(LightAttenuation attenuation) {
        this.attenuation = attenuation;
    }
}
