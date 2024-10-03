#version 300 es

in vec3 inPosition;
in vec2 inTextureCoord;
out vec2 textureCoord;

void main() {
    textureCoord = inTextureCoord;
    gl_Position = vec4(inPosition.xyz, 1.0);
}