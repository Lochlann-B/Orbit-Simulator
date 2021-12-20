package opengl;

//import org.joml.Vector3f;
import maths.Vector3f;

public class PointLight {
	
	private Vector3f colour;
	private Vector3f position;
	private float intensity;
	private Attenuation att;
	
	public PointLight(Vector3f colour, Vector3f position, float intensity, Attenuation att) {
		this.colour = colour;
		this.position = position;
		this.intensity = intensity;
		this.att = att;
	}
	
	PointLight(PointLight pointLight) {
		colour = pointLight.colour;
		position = pointLight.position;
		intensity = pointLight.intensity;
		att = pointLight.att;
	}
	
	public void setPosition(Vector3f pos) {
		position = pos;
	}
	
	public Vector3f getColour() {
		return colour;
	}
	
	public Vector3f getPosition() {
		return position;
	}
	
	public float getIntensity() {
		return intensity;
	}
	
	public Attenuation getAttenuation() {
		return att;
	}
	
}