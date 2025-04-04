#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoord;
uniform sampler2D u_texture;
uniform float u_time; // Time variable for animation

void main() {
    // Wave effect parameters
    float frequency = 10.0; // Number of waves
    float amplitude = 0.02; // Strength of distortion
    float speed = 2.0; // Speed of the wave motion

    // Apply sine wave distortion to the texture coordinates
    vec2 wave = vec2(
        sin(v_texCoord.y * frequency + u_time * speed) * amplitude,
        cos(v_texCoord.x * frequency + u_time * speed) * amplitude
    );

    // Keep the proportions but create a wave effect
    vec4 texColor = texture2D(u_texture, v_texCoord + wave);

    gl_FragColor = texColor; // Final color output
}
