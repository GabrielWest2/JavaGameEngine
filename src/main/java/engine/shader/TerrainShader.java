package engine.shader;

import engine.rendering.lighting.*;
import engine.rendering.yaycoolnewmodels.Material;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.List;

import static engine.rendering.Renderer.MAX_POINT_LIGHTS;
import static engine.rendering.Renderer.MAX_SPOT_LIGHTS;

public class TerrainShader extends ShaderProgram {

    private static final String vertexPath = "src/main/java/engine/shader/src/terrain/vertex.shader";
    private static final String fragmentPath = "src/main/java/engine/shader/src/terrain/fragment.shader";

    public TerrainShader() {
        super(vertexPath, fragmentPath);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");
        super.bindAttribute(2, "normal");
    }

    public void loadTransformationMatrix(Matrix4f matrix) {
        super.loadUniform(super.getUniformLocation("transformationMatrix"), matrix);

    }

    @Override
    public void loadProjectionMatrix(Matrix4f matrix) {
        super.loadUniform(super.getUniformLocation("projectionMatrix"), matrix);
    }

    public void loadViewMatrix(Matrix4f matrix) {
        super.loadUniform(super.getUniformLocation("viewMatrix"), matrix);
    }

    public void loadMaterial(Material material) {
        super.loadUniform(super.getUniformLocation("material.ambient"), material.getAmbientColor());
        super.loadUniform(super.getUniformLocation("material.diffuse"), material.getDiffuseColor());
        super.loadUniform(super.getUniformLocation("material.specular"), material.getSpecularColor());
        super.loadUniform(super.getUniformLocation("material.reflectance"), material.getReflectance());
        super.loadUniform(super.getUniformLocation("textureSampler"), 0);
    }

    public void loadLights(SceneLights sceneLights, Matrix4f viewMatrix) {
        AmbientLight ambientLight = sceneLights.getAmbientLight();
        super.loadUniform(super.getUniformLocation("ambientLight.factor"), ambientLight.getIntensity());
        super.loadUniform(super.getUniformLocation("ambientLight.color"), ambientLight.getColor());

        DirectionalLight dirLight = sceneLights.getDirLight();
        Vector4f auxDir = new Vector4f(dirLight.getDirection(), 0);
        auxDir.mul(viewMatrix);
        Vector3f dir = new Vector3f(auxDir.x, auxDir.y, auxDir.z);
        super.loadUniform(super.getUniformLocation("dirLight.color"), dirLight.getColor());
        super.loadUniform(super.getUniformLocation("dirLight.direction"), dir);
        super.loadUniform(super.getUniformLocation("dirLight.intensity"), dirLight.getIntensity());



        List<PointLight> pointLights = sceneLights.getPointLights();
        int numPointLights = pointLights.size();
        PointLight pointLight;
        for (int i = 0; i < MAX_POINT_LIGHTS; i++) {
            if (i < numPointLights) {
                pointLight = pointLights.get(i);
            } else {
                pointLight = null;
            }
            String name = "pointLights[" + i + "]";
            updatePointLight(pointLight, name, viewMatrix);
        }


        List<SpotLight> spotLights = sceneLights.getSpotLights();
        int numSpotLights = spotLights.size();
        SpotLight spotLight;
        for (int i = 0; i < MAX_SPOT_LIGHTS; i++) {
            if (i < numSpotLights) {
                spotLight = spotLights.get(i);
            } else {
                spotLight = null;
            }
            String name = "spotLights[" + i + "]";
            updateSpotLight(spotLight, name, viewMatrix);
        }
    }

    private void updatePointLight(PointLight pointLight, String prefix, Matrix4f viewMatrix) {
        Vector4f aux = new Vector4f();
        Vector3f lightPosition = new Vector3f();
        Vector3f color = new Vector3f();
        float intensity = 0.0f;
        float constant = 0.0f;
        float linear = 0.0f;
        float exponent = 0.0f;
        if (pointLight != null) {
            aux.set(pointLight.getPosition(), 1);
            aux.mul(viewMatrix);
            lightPosition.set(aux.x, aux.y, aux.z);
            color.set(pointLight.getColor());
            intensity = pointLight.getIntensity();
            LightAttenuation attenuation = pointLight.getAttenuation();
            constant = attenuation.getConstant();
            linear = attenuation.getLinear();
            exponent = attenuation.getExponent();
        }
        super.loadUniform(super.getUniformLocation(prefix + ".position"), lightPosition);
        super.loadUniform(super.getUniformLocation(prefix + ".color"), color);
        super.loadUniform(super.getUniformLocation(prefix + ".intensity"), intensity);
        super.loadUniform(super.getUniformLocation(prefix + ".att.constant"), constant);
        super.loadUniform(super.getUniformLocation(prefix + ".att.linear"), linear);
        super.loadUniform(super.getUniformLocation(prefix + ".att.exponent"), exponent);
    }

    private void updateSpotLight(SpotLight spotLight, String prefix, Matrix4f viewMatrix) {
        PointLight pointLight = null;
        Vector3f coneDirection = new Vector3f();
        float cutoff = 0.0f;
        if (spotLight != null) {
            coneDirection = spotLight.getConeDirection();
            cutoff = spotLight.getCutOff();
            pointLight = spotLight.getPointLight();
        }

        super.loadUniform(super.getUniformLocation(prefix + ".conedir"), coneDirection);
        super.loadUniform(super.getUniformLocation(prefix + ".conedir"), cutoff);
        updatePointLight(pointLight, prefix + ".pl", viewMatrix);
    }


    public void setClipPlane(Vector4f plane) {
        super.loadUniform(super.getUniformLocation("plane"), plane);
    }

    public void connectTextures(){
        super.loadUniform(super.getUniformLocation("splatmap"), 0);
        super.loadUniform(super.getUniformLocation("tex1"), 1);
        super.loadUniform(super.getUniformLocation("tex2"), 2);
        super.loadUniform(super.getUniformLocation("tex3"), 3);
        super.loadUniform(super.getUniformLocation("tex4"), 4);
    }

    public void setTextureScales(float scale1, float scale2, float scale3, float scale4){
        super.loadUniform(super.getUniformLocation("textureScale1"), scale1);
        super.loadUniform(super.getUniformLocation("textureScale2"), scale2);
        super.loadUniform(super.getUniformLocation("textureScale3"), scale3);
        super.loadUniform(super.getUniformLocation("textureScale4"), scale4);
    }
}


