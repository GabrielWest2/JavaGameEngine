#version 400 core

in vec3 position;
in vec2 textureCoords;
in vec3 normal;

out vec3 outPosition;
out vec3 outNormal;
out vec2 outTextCoord;

uniform mat4 transformationMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

uniform vec4 plane;
void main()
{
    mat4 modelViewMatrix = viewMatrix * transformationMatrix;
    vec4 mvPosition =  modelViewMatrix * vec4(position, 1.0);
    gl_Position = projectionMatrix * mvPosition;
    gl_ClipDistance[0] = dot(transformationMatrix * vec4(position, 1.0), plane);
    outPosition = mvPosition.xyz;
    outNormal = normalize(modelViewMatrix * vec4(normal, 0.0)).xyz;
    outTextCoord = textureCoords;
}