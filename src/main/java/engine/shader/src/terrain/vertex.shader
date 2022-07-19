#version 400 core

in vec3 position;
in vec3 triangleColor;

out float worldHeight;
out vec3 pass_triangleColor;

uniform mat4 transformationMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;


void main(void){
    vec4 worldPosition = transformationMatrix * vec4(position, 1.0);
    worldHeight = worldPosition.y;
    gl_Position = projectionMatrix * viewMatrix * worldPosition;
    pass_triangleColor = triangleColor;
}