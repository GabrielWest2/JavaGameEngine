#version 400 core

in vec2 textureCoords;

out vec4 out_Color;

uniform sampler2D textureSampler;
uniform sampler2D textureSampler1;

uniform float contrast;

void main(void){
    vec4 original = texture(textureSampler, textureCoords);
    vec4 blur = texture(textureSampler1, textureCoords);

    out_Color = original + (blur*0.7);
}