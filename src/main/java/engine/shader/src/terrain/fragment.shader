#version 330

const int MAX_POINT_LIGHTS = 5;
const int MAX_SPOT_LIGHTS = 5;
const float SPECULAR_POWER = 10;

in vec3 outPosition;
in vec3 outNormal;
in vec2 outTextCoord;

out vec4 fragColor;

struct Attenuation
{
 float constant;
 float linear;
 float exponent;
};
struct Material
{
 vec4 ambient;
 vec4 diffuse;
 vec4 specular;
 float reflectance;
};
struct AmbientLight
{
 float factor;
 vec3 color;
};
struct PointLight {
 vec3 position;
 vec3 color;
 float intensity;
 Attenuation att;
};
struct SpotLight
{
 PointLight pl;
 vec3 conedir;
 float cutoff;
};
struct DirLight
{
 vec3 color;
 vec3 direction;
 float intensity;
};


uniform Material material;
uniform AmbientLight ambientLight;
uniform PointLight pointLights[MAX_POINT_LIGHTS];
uniform SpotLight spotLights[MAX_SPOT_LIGHTS];
uniform DirLight dirLight;


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

vec4 calcAmbient(AmbientLight ambientLight, vec4 ambient) {
 return vec4(ambientLight.factor * ambientLight.color, 1) * ambient;
}
vec4 calcLightColor(vec4 diffuse, vec4 specular, vec3 lightColor, float light_intensity, vec3 position, vec3 to_light_dir, vec3 normal) {
 vec4 diffuseColor = vec4(0, 0, 0, 1);
 vec4 specColor = vec4(0, 0, 0, 1);

 // Diffuse Light
 float diffuseFactor = max(dot(normal, to_light_dir), 0.0);
 diffuseColor = diffuse * vec4(lightColor, 1.0) * light_intensity * diffuseFactor;

 // Specular Light
 vec3 camera_direction = normalize(-position);
 vec3 from_light_dir = -to_light_dir;
 vec3 reflected_light = normalize(reflect(from_light_dir, normal));
 float specularFactor = max(dot(camera_direction, reflected_light), 0.0);
 specularFactor = pow(specularFactor, SPECULAR_POWER);
 specColor = specular * light_intensity  * specularFactor * material.reflectance * vec4(lightColor, 1.0);

 return (diffuseColor + specColor);
}
vec4 calcPointLight(vec4 diffuse, vec4 specular, PointLight light, vec3 position, vec3 normal) {
 vec3 light_direction = light.position - position;
 vec3 to_light_dir  = normalize(light_direction);
 vec4 light_color = calcLightColor(diffuse, specular, light.color, light.intensity, position, to_light_dir, normal);

 // Apply Attenuation
 float distance = length(light_direction);
 float attenuationInv = light.att.constant + light.att.linear * distance +
 light.att.exponent * distance * distance;
 return light_color / attenuationInv;
}
vec4 calcSpotLight(vec4 diffuse, vec4 specular, SpotLight light, vec3 position, vec3 normal) {
 vec3 light_direction = light.pl.position - position;
 vec3 to_light_dir  = normalize(light_direction);
 vec3 from_light_dir  = -to_light_dir;
 float spot_alfa = dot(from_light_dir, normalize(light.conedir));

 vec4 color = vec4(0, 0, 0, 0);

 if (spot_alfa > light.cutoff)
 {
  color = calcPointLight(diffuse, specular, light.pl, position, normal);
  color *= (1.0 - (1.0 - spot_alfa)/(1.0 - light.cutoff));
 }
 return color;
}
vec4 calcDirLight(vec4 diffuse, vec4 specular, DirLight light, vec3 position, vec3 normal) {
 return calcLightColor(diffuse, specular, light.color, light.intensity, position, normalize(light.direction), normal);
}

void main() {
 //vec4 text_color = texture(tex1, textureScale1 * vec2(outTextCoord.x, 1.0 - outTextCoord.y));

 vec2 tex_coords = vec2(outTextCoord.x, 1.0 - outTextCoord.y);
 vec4 textureMixes = texture(splatmap, vec2(1.0 - outTextCoord.y, 1.0 - outTextCoord.x));
 textureMixes = normalize(textureMixes);
 vec3 color1 = texture(tex1, tex_coords * textureScale1).rgb;
 vec3 color2 = texture(tex2, tex_coords * textureScale2).rgb;
 vec3 color3 = texture(tex3, tex_coords * textureScale3).rgb;
 vec3 color4 = texture(tex4, tex_coords * textureScale4).rgb;

 float finalR = (color1.r * textureMixes.r) + (color2.r * textureMixes.g) + (color3.r * textureMixes.b) + (color4.r * textureMixes.a);
 float finalG = (color1.g * textureMixes.r) + (color2.g * textureMixes.g) + (color3.g * textureMixes.b) + (color4.g * textureMixes.a);
 float finalB = (color1.b * textureMixes.r) + (color2.b * textureMixes.g) + (color3.b * textureMixes.b) + (color4.b * textureMixes.a);

 vec4 text_color = vec4(finalR, finalG, finalB, 1.0);

 vec4 ambient = calcAmbient(ambientLight, text_color + material.ambient);
 vec4 diffuse = text_color + material.diffuse;
 vec4 specular = text_color + material.specular;

 vec4 diffuseSpecularComp = calcDirLight(diffuse, specular, dirLight, outPosition, outNormal);

 for (int i=0; i<MAX_POINT_LIGHTS; i++) {
  if (pointLights[i].intensity > 0) {
   diffuseSpecularComp += calcPointLight(diffuse, specular, pointLights[i], outPosition, outNormal);
  }
 }

 for (int i=0; i<MAX_SPOT_LIGHTS; i++) {
  if (spotLights[i].pl.intensity > 0) {
   diffuseSpecularComp += calcSpotLight(diffuse, specular, spotLights[i], outPosition, outNormal);
  }
 }
 fragColor = ambient + diffuseSpecularComp;
}
