package app;

//import org.joml.Vector3f;
import maths.Vector3f;

import opengl.Mesh;
import opengl.Texture;

public class AppItem {
	
	private final Mesh mesh;
	private Vector3f position;
	private Vector3f rotation;
	private Vector3f colour;
	private Texture texture;
	private int useColour = 1;
	private float scale;
	
	public AppItem(Mesh mesh) {
		this.mesh = mesh;
		scale = 1;
		position = new Vector3f();
		rotation = new Vector3f();
		colour = new Vector3f(1,1,1);
	}
	
	public void setPosition(float x, float y, float z) {
		position.x = x;
		position.y = y;
		position.z = z;
	}
	
	public void setRotation(float rX, float rY, float rZ) {
		rotation.x = rX;
		rotation.y = rY;
		rotation.z = rZ;
	}
	
	public void setColour(float r, float g, float b) {
		colour.x = r;
		colour.y = g;
		colour.z = b;
		useColour = 1;
	}
	
	public void setTexture(Texture texture) {
		mesh.setTexture(texture);
		useColour = 0;
	}
	
	public void setScale(float s) {
		scale = s > 50 ? 50 : s;
	}
	
	public int getColourStatus() {
		return useColour;
	}
	
	public Vector3f getPosition() {
		return position;
	}
	
	public Vector3f getRotation() {
		return rotation;
	}
	
	public Vector3f getColour() {
		return colour;
	}
	
	public float getScale() {
		return scale;
	}
	
	public Mesh getMesh() {
		return mesh;
	}
}