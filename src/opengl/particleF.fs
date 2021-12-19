#version 330

out vec4 fragColour;
in vec2 outTexCoords;

uniform sampler2D texture_sampler;
uniform vec4 colour;

void main()
{
	fragColour = texture2D(texture_sampler, outTexCoords) * colour;
}