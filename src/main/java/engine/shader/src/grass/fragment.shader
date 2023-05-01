#version 400 core

in vec2 pass_textureCoords;
in vec3 pass_normal;
in vec3 pass_toLight;
in vec3 pass_toCamera;
out vec4 out_Color;


uniform sampler2D textureSampler;
uniform vec3 lightColor;
uniform float shineDamper;
uniform float reflectivity;
const vec4 color1 = vec4(17/255.0, 133/255.0, 12/255.0, 1.0);
const vec4 color2 = vec4(21/255.0, 199/255.0, 12/255.0, 1.0);
void main(void){

    vec4 sampled = texture(textureSampler, vec2(pass_textureCoords.x, 1.0f - pass_textureCoords.y));

    //Discard transparent pixels
    if(sampled.a < 0.75){
        discard;
    }


    sampled = mix(color1, color2, sampled.g *2 * pass_textureCoords.y);

 vec3 unitNormal = normalize(pass_normal);
 vec3 unitLightVector = normalize(pass_toLight);

 float nDot1 = dot(unitNormal, unitLightVector);
 float brightness = max(nDot1, 0.7);
 vec3 diffuse = brightness * lightColor;

 vec3 unitVectorToCamera = normalize(pass_toCamera);
 vec3 lightDirection = -unitLightVector;
 vec3 reflectLightDirection = reflect(lightDirection, unitNormal);

 float specularFactor = dot(reflectLightDirection, unitVectorToCamera);
 specularFactor = max(specularFactor, 0.0);
 float dampedFactor = pow(specularFactor, shineDamper);
 vec3 finalSpecular = dampedFactor * lightColor * reflectivity;


 out_Color = vec4(diffuse, 1.0) * sampled + vec4(finalSpecular, 1.0);
}