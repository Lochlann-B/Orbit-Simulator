#version 330

out vec4 fragColour;
in vec2 outTexCoord;
in vec3 worldNormal;
in vec3 worldVertexPos;

struct Attenuation {
	float constant;
	float linear;
	float exponent;
};

struct PointLight {
	vec3 colour;
	vec3 position;
	float intensity;
	Attenuation attenuation;
};

struct Material {
	vec4 ambience;
	vec4 diffuse;
	vec4 specular;
	int hasTexture;
	float reflectance;
};

uniform vec3 ambientLight;

uniform float specularPower;
uniform Material material;
uniform PointLight pointLight;
uniform sampler2D texture_sampler;

vec4 ambientColour;
vec4 diffuseColour;
vec4 specularColour;
vec4 attenuatedColour;

void applyTextures(Material material, vec2 outTexCoord) {
	if (material.hasTexture == 1) {
		ambientColour = texture2D(texture_sampler, outTexCoord);
		diffuseColour = ambientColour;
		specularColour = ambientColour;
	}
	else {
		ambientColour = vec4(colour, 0.0);
		diffuseColour = material.diffuse;
		specularColour = material.specular;
	}	
}

vec4 calculateDiffuseColour(vec3 normal, vec3 lightDirection, PointLight pointLight) {
	vec3 lightDir = normalize(lightDirection);
	float diffuse = max(dot(norm,lightDir), 0);
	vec3 diffuseC = diffuse * pointLight.colour;
	return vec4(diffuseC, 0.0);
}

vec4 calculateSpecularColour(vec3 normal, vec3 vertexPos, PointLight pointLight, Material material) {
	//We are in view space so camera position is (0,0,0)
	vec3 fromVertex = normalize(-vertexPos);
	vec3 reflectedLight = normalize(reflect(-pointLight.position, normal));
	float specular = pow(max(dot(fromVertex, reflectedLight), 0.0), specularPower);
	vec4 specularC = vec4(specular * material.reflectance * pointLight.colour, 1.0);
	return specularC;
}

vec4 applyAttenuation(vec3 lightDirection, vec4 diffuseCol, vec4 specularCol, PointLight light) {
	float distance = length(lightDirection);
	float attenuationFactor = light.att.constant + light.att.linear * distance + light.att.exponent * distance * distance;
	return (diffuseCol + specularCol) / attenuationFactor;
}

void main() {
	vec3 lightDirection = pointLight.position - worldVertexPos;

	applyTextures(material, outTexCoord);
	vec3 norm = normalize(worldNormal);
	diffuseColour = diffuseColour * calculateDiffuseColour(norm, lightDirection, pointLight);
	specularColour = specularColour * calculateSpecularColour(norm, worldVertexPos, pointLight, material);
	attenuatedColour = applyAttenuation(lightDirection, diffuseColour, specularColour, pointLight);
	fragColour = attenuatedColour + ambientColour;
}