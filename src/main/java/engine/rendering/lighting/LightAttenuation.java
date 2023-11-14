package engine.rendering.lighting;

public class LightAttenuation {

    private float constant;

    private float exponent;

    private float linear;

    public LightAttenuation(float constant, float linear, float exponent) {
        this.constant = constant;
        this.linear = linear;
        this.exponent = exponent;
    }

    public float getConstant() {
        return constant;
    }

    public float getExponent() {
        return exponent;
    }

    public float getLinear() {
        return linear;
    }

    public void setConstant(float constant) {
        this.constant = constant;
    }

    public void setExponent(float exponent) {
        this.exponent = exponent;
    }

    public void setLinear(float linear) {
        this.linear = linear;
    }
}