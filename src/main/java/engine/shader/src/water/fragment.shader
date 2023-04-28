#version 400 core

in vec4 clipSpace;
in vec2 pass_textureCoords;
in vec3 toCamera;
in vec3 fromLightVector;


out vec4 out_Color;

uniform sampler2D reflectionTexture;
uniform sampler2D refractionTexture;
uniform sampler2D dudv;
uniform sampler2D normalMap;
uniform sampler2D refractionDepthTexture;
uniform float moveFactor;
uniform vec3 lightColor;


const float waveStrength = 0.04;
const float shineDamper = 20.0;
const float reflectivity = 0.5;

void main(void){
    vec2 ndc = (clipSpace.xy/clipSpace.w)/2.0 + 0.5;
    vec2 refractTextureCoords = vec2(ndc.xy);
    vec2 reflectTextureCoords = (vec2(clipSpace.x, -clipSpace.y)/clipSpace.w)/2.0 + 0.5;

    float near = 0.1;
    float far = 1500;

    float depth = texture(refractionDepthTexture, refractTextureCoords).r;
    float floorDistance = 2.0 * near * far / (far + near - (2.0 * depth - 1.0) * (far - near));
    depth = gl_FragCoord.z;
    float waterDistance = 2.0 * near * far / (far + near - (2.0 * depth - 1.0) * (far - near));
    float waterDepth = floorDistance - waterDistance;



    vec2 distortedTexCoords = texture(dudv, vec2(pass_textureCoords.x + moveFactor, pass_textureCoords.y)).rg*0.1;
    distortedTexCoords = pass_textureCoords + vec2(distortedTexCoords.x, distortedTexCoords.y+moveFactor);
    vec2 totalDistortion = (texture(dudv, distortedTexCoords).rg * 2.0 - 1.0) * waveStrength * clamp(waterDepth / 20.0, 0.0, 1.0);

    refractTextureCoords += totalDistortion;
    reflectTextureCoords += totalDistortion;

    vec4 reflectColor = texture(reflectionTexture, reflectTextureCoords);
    vec4 refractColor = texture(refractionTexture, refractTextureCoords);

    vec4 normalMapColor = texture(normalMap, distortedTexCoords);
    vec3 normal = vec3(normalMapColor.r * 2.0 - 1.0, normalMapColor.b * 3.0, normalMapColor.g * 2.0 - 1.0);
    normal = normalize(normal);

    vec3 viewVector = normalize(toCamera);
    float fresnelFactor = dot(viewVector, normal);
    fresnelFactor = clamp(fresnelFactor, 0.2, 0.75);



    vec3 reflectedLight = reflect(normalize(fromLightVector), normal);
    float specular = max(dot(reflectedLight, viewVector), 0.0);
    specular = pow(specular, shineDamper);
    vec3 specularHighlights = lightColor * specular * reflectivity;

    out_Color = mix(reflectColor, refractColor, fresnelFactor);
    out_Color = mix(out_Color, vec4(0.411764, 0.713725, 0.941176, 1), 0.15) + vec4(specularHighlights.xyz, 0.0);
    out_Color.a = clamp(waterDepth / 10.0, 0.0, 1.0);
}