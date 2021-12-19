#version 330

layout (location=0) in vec3 vertexPos;
layout (location=1) in vec2 texCoords;

out vec2 outTexCoords;

uniform vec3 position;
uniform mat4 projectionMatrix;
uniform mat4 worldMatrix;

void main() 
{
	outTexCoords = texCoords;
	vec4 worldPos = worldMatrix * vec4(vertexPos, 1.0);
	gl_Position = projectionMatrix * worldPos;
}