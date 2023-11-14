package editor;

import editor.util.FAIcons;
import engine.rendering.lighting.*;
import imgui.ImGui;
import org.joml.Vector3f;

public class LightingWindow {

    public static SpotLight selectedSpotLight = null;
    public static PointLight selectedPointLight = null;

    public static void render(SceneLights lights){
        ImGui.begin(FAIcons.ICON_LIGHTBULB + " Lighting");
        if(ImGui.collapsingHeader("Ambient Light")){
            AmbientLight light = lights.getAmbientLight();
            float[] data = new float[]{
                    light.getColor().x,
                    light.getColor().y,
                    light.getColor().z
            };
            ImGui.pushID("AmbientColorEdit");
            if(ImGui.colorEdit3("Color", data)){
                light.getColor().x = data[0];
                light.getColor().y = data[1];
                light.getColor().z = data[2];
            }
            ImGui.popID();

            ImGui.pushID("AmbientIntensityEdit");
            data = new float[]
            {
                    light.getIntensity()
            };
            if(ImGui.sliderFloat("Intensity", data, 0.0f, 1.0f)){
                light.setIntensity(data[0]);
            }
            ImGui.popID();

        }
        if(ImGui.collapsingHeader("Directional Light")){
            DirectionalLight light = lights.getDirLight();

            float[] data = new float[]{
                    light.getDirection().x,
                    light.getDirection().y,
                    light.getDirection().z
            };
            ImGui.pushID("DirectionalDirectionalEdit");
            if(ImGui.sliderFloat3("Direction", data, -1.0f, 1.0f)){
                light.getDirection().x = data[0];
                light.getDirection().y = data[1];
                light.getDirection().z = data[2];
            }
            ImGui.popID();

            data = new float[]{
                    light.getColor().x,
                    light.getColor().y,
                    light.getColor().z
            };
            ImGui.pushID("DirectionalColorEdit");
            if(ImGui.colorEdit3("Color", data)){
                light.getColor().x = data[0];
                light.getColor().y = data[1];
                light.getColor().z = data[2];
            }
            ImGui.popID();

            ImGui.pushID("DirectionalIntensityEdit");
            data = new float[]
                    {
                            light.getIntensity()
                    };
            if(ImGui.sliderFloat("Intensity", data, 0.0f, 1.0f)){
                light.setIntensity(data[0]);
            }
            ImGui.popID();

        }
        int numPointLights = lights.getPointLights().size();

        ImGui.pushID("PointLightsHeader");
        if(ImGui.collapsingHeader("Point Lights")){
            if(numPointLights == 0){
                ImGui.text("There are no point lights!");
            }else{
                int i = 0;
                for(PointLight light : lights.getPointLights()){
                    if(ImGui.treeNode("Point Light "+i)) {

                        if(ImGui.button("Position Gizmo")){
                            selectedPointLight = light;
                        }

                        float[] data = new float[]{
                                light.getPosition().x,
                                light.getPosition().y,
                                light.getPosition().z
                        };

                        ImGui.pushID("PointLight" + i + "Position");
                        if (ImGui.dragFloat3("Position", data, 0.5f)) {
                            light.getPosition().x = data[0];
                            light.getPosition().y = data[1];
                            light.getPosition().z = data[2];
                        }
                        ImGui.popID();

                        data = new float[]{
                                light.getColor().x,
                                light.getColor().y,
                                light.getColor().z
                        };
                        ImGui.pushID("PointLight" + i + "Color");
                        if (ImGui.colorEdit3("Color", data)) {
                            light.getColor().x = data[0];
                            light.getColor().y = data[1];
                            light.getColor().z = data[2];
                        }
                        ImGui.popID();

                        data = new float[]{light.getIntensity()};
                        ImGui.pushID("PointLight" + i + "Intensity");
                        if (ImGui.sliderFloat("Intensity", data, 0.0f, 1.0f)) {
                            light.setIntensity(data[0]);
                        }
                        ImGui.popID();

                        ImGui.text("Attenuation " + FAIcons.ICON_ANGLE_DOWN);


                        data = new float[]{light.getAttenuation().getConstant()};
                        ImGui.pushID("PointLight" + i + "Constant");
                        if (ImGui.sliderFloat("Constant", data, 0.0f, 1.0f)) {
                            light.getAttenuation().setConstant(data[0]);
                        }
                        ImGui.popID();


                        data = new float[]{light.getAttenuation().getExponent()};
                        ImGui.pushID("PointLight" + i + "Exponent");
                        if (ImGui.sliderFloat("Exponent", data, 0.0f, 5.0f)) {
                            light.getAttenuation().setExponent(data[0]);
                        }
                        ImGui.popID();

                        data = new float[]{light.getAttenuation().getLinear()};
                        ImGui.pushID("PointLight" + i + "Linear");
                        if (ImGui.sliderFloat("Linear", data, 0.0f, 5.0f)) {
                            light.getAttenuation().setLinear(data[0]);
                        }
                        ImGui.popID();



                        ImGui.treePop();

                    }
                    i++;
                }
                if(ImGui.button("Add Point Light " + FAIcons.ICON_PLUS_CIRCLE)) {
                    lights.getPointLights().add(new PointLight(new Vector3f(1, 1, 1), new Vector3f(), 1));
                }
                ImGui.sameLine();
                if(ImGui.button("Remove Point Light " + FAIcons.ICON_MINUS_CIRCLE)){
                    if(!lights.getPointLights().isEmpty()){
                        lights.getPointLights().remove(lights.getPointLights().size()-1);
                    }
                }
            }
        }
        ImGui.popID();

        ImGui.end();
    }
}
