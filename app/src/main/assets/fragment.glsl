#version 300 es

precision mediump float;

uniform vec2 resolution;
uniform float time;
uniform vec2 touchPos;
in vec2 textureCoord;
out vec4 fragColor;

const float gridThickness = 0.01;
const vec3 circleColor = vec3(1.0, 2.0, 3.0);

void main() {
    vec2 uv = 2.0 * textureCoord - 1.0; // (0 to 1) -> (-1 to 1)
    uv = (uv * resolution) / resolution.y;

    vec2 uvTouch = 2.0 * (touchPos / resolution) - 1.0;
    uvTouch = (uvTouch * resolution) / resolution.y;

    float circleRadius = cos(time * 2.0) * 0.4 + 0.6;
    float grid = step(mod(uv.x, 0.1), gridThickness) + step(mod(uv.y, 0.1), gridThickness);
    float circle = smoothstep(circleRadius, 0.0, distance(uv, uvTouch));
    vec3 color = mix(vec3(0.1), circleColor, circle);

    fragColor = vec4(color * grid, 1.0);
}