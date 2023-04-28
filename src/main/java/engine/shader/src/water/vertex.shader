#version 400 core

in vec3 position;
in vec2 textureCoords;

out vec4 clipSpace;
out vec2 pass_textureCoords;
out vec3 toCamera;
out vec3 fromLightVector;

uniform mat4 transformationMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;
uniform vec3 cameraPos;
uniform vec3 lightPosition;

void main(void){
    vec4 worldPos = transformationMatrix * vec4(position.xyz, 1.0);
    clipSpace = projectionMatrix * viewMatrix * worldPos;
    gl_Position = clipSpace;
    pass_textureCoords = textureCoords * 6;
    toCamera = cameraPos - worldPos.xyz;

    fromLightVector = worldPos.xyz - lightPosition;
}