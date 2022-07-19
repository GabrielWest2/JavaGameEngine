#version 400 core


in float worldHeight;
out vec4 out_Color;
in vec3 pass_triangleColor;
uniform sampler2D textureSampler;


void main(void){
  out_Color = vec4(pass_triangleColor, 1.0);   //mix(sand, grass, (worldHeight*scale) + offset);//texture(textureSampler, pass_textureCoords * 10);
}