#ifdef GL_ES
#define LOWP lowp
    precision mediump float;
#else
    #define LOWP
#endif

varying LOWP vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform vec2 u_circle_center;
uniform float u_circle_radius;

void main()
{
	float d = distance(gl_FragCoord.xy, u_circle_center);
	if (abs(d - u_circle_radius) <= 8.0){
		float pct = 1.0 - (abs(d - u_circle_radius) / 8.0);
		gl_FragColor = vec4(1.0, 0.0, 0.0, pct);
	}
	else{
		gl_FragColor = v_color * texture2D(u_texture, v_texCoords);
	}
}