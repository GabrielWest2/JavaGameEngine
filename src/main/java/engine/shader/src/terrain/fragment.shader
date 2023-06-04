#version 400 core

in vec2 pass_textureCoords;
in vec3 pass_normal;
in vec3 pass_toLight;
in vec3 pass_toCamera;

out vec4 out_Color;


uniform sampler2D splatmap;
uniform sampler2D tex1;
uniform sampler2D tex2;
uniform sampler2D tex3;
uniform sampler2D tex4;
uniform vec3 lightColor;
uniform float shineDamper;
uniform float reflectivity;
uniform float textureScale1;
uniform float textureScale2;
uniform float textureScale3;
uniform float textureScale4;


void main(void){



 vec3 unitNormal = normalize(pass_normal);
 vec3 unitLightVector = normalize(pass_toLight);

 float nDot1 = dot(unitNormal, unitLightVector);
 float brightness = max(nDot1, 0.45);
 vec3 diffuse = brightness * lightColor;

 vec3 unitVectorToCamera = normalize(pass_toCamera);
 vec3 lightDirection = -unitLightVector;
 vec3 reflectLightDirection = reflect(lightDirection, unitNormal);

 float specularFactor = dot(reflectLightDirection, unitVectorToCamera);
 specularFactor = max(specularFactor, 0.0);
 float dampedFactor = pow(specularFactor, shineDamper);
 vec3 finalSpecular = dampedFactor * lightColor * reflectivity;
 //out_Color = vec4(diffuse, 1.0) * texture(textureSampler, pass_textureCoords * textureScale)  + vec4(finalSpecular, 1.0);

 vec4 textureMixes = texture(splatmap, -pass_textureCoords);
 textureMixes = normalize(textureMixes);
 vec3 color1 = texture(tex1, pass_textureCoords * textureScale1).rgb;
 vec3 color2 = texture(tex2, pass_textureCoords * textureScale2).rgb;
 vec3 color3 = texture(tex3, pass_textureCoords * textureScale3).rgb;
 vec3 color4 = texture(tex4, pass_textureCoords * textureScale4).rgb;

 float finalR = (color1.r * textureMixes.r) + (color2.r * textureMixes.g) + (color3.r * textureMixes.b) + (color4.r * textureMixes.a);
 float finalG = (color1.g * textureMixes.r) + (color2.g * textureMixes.g) + (color3.g * textureMixes.b) + (color4.g * textureMixes.a);
 float finalB = (color1.b * textureMixes.r) + (color2.b * textureMixes.g) + (color3.b * textureMixes.b) + (color4.b * textureMixes.a);

 out_Color = vec4(diffuse, 1.0) *  vec4(finalR, finalG, finalB, 1.0) + vec4(finalSpecular, 1.0);
}