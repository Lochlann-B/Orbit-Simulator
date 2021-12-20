package opengl;

//import org.joml.Vector3f;
//import org.joml.Vector4f;
import maths.Vector3f;
import maths.Vector4f;

public class Particle {

	private float scale;
	private float lifeSpan;
	private float currentLifeSpan;
	private float decayConstant;
	private Vector3f position;
	private Vector3f direction;
	private Vector4f colour;
	private ParticleMesh mesh;
	private boolean born = false;
	
	public Particle(ParticleMesh mesh) {
		currentLifeSpan = lifeSpan;
		scale = 1.2f;
		decayConstant = 1f;
		position = new Vector3f(0f, 0, 0);
		colour = new Vector4f(1.0f, 1f, 1f, 1.0f);
		direction = new Vector3f(0f, 0f, 0f);
		this.mesh = mesh;
	}
	
	public void setPosition(float x, float y, float z) {
		position.x = x;
		position.y = y;
		position.z = z;
	}
	
	public void setDirection(float x, float y, float z) {
		direction.x = x;
		direction.y = y;
		direction.z = z;
	}
	
	public void setLifeSpan(float l) {
		lifeSpan = l;
	}
	
	public void setCurrentLifeSpan(float l) {
		currentLifeSpan = l;
	}
	
	public void setColour(float r, float g, float b, float a) {
		colour.x = r;
		colour.y = g;
		colour.z = b;
		colour.w = a;
	}
	
	public void setAlpha(float a) {
		colour.w = a;
	}
	
	public void move(float speed) {
		position.x += speed*direction.x;
		position.y += speed*direction.y;
		position.z += speed*direction.z;
		
		currentLifeSpan = (float) ((currentLifeSpan > 0) ? currentLifeSpan - Math.sqrt(speed*speed)*decayConstant : 0);
	}
	
	public ParticleMesh getMesh() {
		return mesh;
	}
	
	public Vector3f getPosition() {
		return position;
	}
	
	public float getScale() {
		return scale;
	}
	
	public Vector4f getColour() {
		return colour;
	}
	
	public float getLife() {
		return currentLifeSpan;
	}
	
	public float getTotalLife() {
		return lifeSpan;
	}
	
	public boolean getBornStatus() {
		return born;
	}
	
	public void setBornStatus(boolean b) {
		born = b;
	}
	
	public Vector3f getDirection() {
		return direction;
	}
	
	public float getDecayConstant() {
		return decayConstant;
	}
	
	public void setScale(float f) {
		scale = f;
	}
	
}
