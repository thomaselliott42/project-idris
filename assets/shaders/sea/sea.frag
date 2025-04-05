#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoord;
uniform sampler2D u_texture;
uniform float u_time;

void main() {
    float frequency = 10.0;
    float amplitude = 0.02;
    float speed = 2.0;

    // Center fade: fades at edges, strongest in center
    vec2 centerDist = abs(v_texCoord - 0.5) * 2.0; // 0 to 1
    float centerFade = smoothstep(1.0, 0.7, max(centerDist.x, centerDist.y));

    // Top-right corner fade (1.0, 1.0 is top-right in UV space)
    vec2 cornerDist = (1.0 - v_texCoord); // Distance from top-right
    float cornerFade = smoothstep(1.0, 0.7, max(cornerDist.x, cornerDist.y));

    // Combine the two fades (multiplying gives sharper pinning)
    float combinedFade = centerFade * cornerFade;

    // Apply wave distortion with combined fade
    vec2 wave = vec2(
        sin(v_texCoord.y * frequency + u_time * speed),
        cos(v_texCoord.x * frequency + u_time * speed)
    ) * amplitude * combinedFade;

    vec4 texColor = texture2D(u_texture, v_texCoord + wave);

    gl_FragColor = texColor;
}
