#version 330

layout (location=0) in vec3 position;
layout (location=1) in vec2 inTexCoord;
layout (location=2) in vec3 inNormal;

out vec2 outTexCoord;
out vec3 worldNormal;
out vec3 worldVertexPos;

uniform mat4 projectionMatrix;
uniform mat4 worldMatrix;

void main()
{
	vec4 worldPos = worldMatrix * vec4(position, 1.0);
	gl_Position = projectionMatrix * worldPos;
	outTexCoord = inTexCoord;
	worldNormal = (worldMatrix * vec4(inNormal, 0.0)).xyz;
	worldVertexPos = worldPos.xyz;
	//gl_Position = projectionMatrix*worldPos;
}