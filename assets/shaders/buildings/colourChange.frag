#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoord;
varying vec4 v_color;
uniform sampler2D u_texture;
uniform vec4 u_tintColor;  // Tint color
uniform int u_ownership;   // 0 = unowned, 1 = red, 2 = blue

void main() {
    vec4 texColor = texture2D(u_texture, v_texCoord);

    // If unowned (u_ownership == 0), don't change color
    if (u_ownership == 0) {
        gl_FragColor = texColor;
    } else {
        gl_FragColor = texColor * u_tintColor; // Apply tint if owned
    }
}
