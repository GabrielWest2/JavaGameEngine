package engine.rendering.lighting;


import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class SceneLights {

    private AmbientLight ambientLight;
    private DirectionalLight dirLight;
    private List<PointLight> pointLights;
    private List<SpotLight> spotLights;

    public SceneLights() {
        ambientLight = new AmbientLight(new Vector3f(1, 1, 1), 0.1f);
        pointLights = new ArrayList<>();
        spotLights = new ArrayList<>();
        dirLight = new DirectionalLight(new Vector3f(1, 1, 1), new Vector3f(0, 1, 0), 0.6f);
        pointLights.add(new PointLight(new Vector3f(1, 0.5f, 0.5f), new Vector3f(0, 5, 0), 0.5f));
    }

    public AmbientLight getAmbientLight() {
        return ambientLight;
    }

    public DirectionalLight getDirLight() {
        return dirLight;
    }

    public List<PointLight> getPointLights() {
        return pointLights;
    }

    public List<SpotLight> getSpotLights() {
        return spotLights;
    }

    public void setSpotLights(List<SpotLight> spotLights) {
        this.spotLights = spotLights;
    }
}
