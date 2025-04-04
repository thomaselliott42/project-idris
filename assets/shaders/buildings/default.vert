#ifdef GL_ES
precision mediump float;
#endif

// Attributes
attribute vec4 a_position;   // Position of the vertex
attribute vec2 a_texCoord0;  // Texture coordinates
attribute vec4 a_color;      // Color information

// Varying variables to pass to fragment shader
varying vec2 v_texCoord;
varying vec4 v_color;

// Uniforms
uniform mat4 u_projTrans; // The projection transformation matrix
uniform vec2 u_screenSize;
void main() {
    v_texCoord = a_texCoord0;
    v_color = a_color;

    // Apply the projection matrix to the vertex position
    gl_Position = u_projTrans * a_position; // Multiply by projection matrix
}
