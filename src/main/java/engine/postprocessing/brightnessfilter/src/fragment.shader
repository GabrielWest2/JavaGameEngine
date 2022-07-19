#version 400 core

in vec2 textureCoords;

out vec4 out_Color;

uniform sampler2D textureSampler;

void main(void){

	vec4 color = texture(textureSampler, textureCoords);
	float brightness = ((color.r * 0.2126) + (color.g * 0.7152) + (color.b * 0.0722)) * 3;
    out_Color = color * brightness;

}