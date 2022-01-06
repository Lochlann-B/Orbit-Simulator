package input;

//import org.joml.Math;
//import org.joml.Vector3f;
import maths.Vector3f;

public class Camera {

	private Vector3f rotation;
	private Vector3f position;
	
	public Camera() {
		rotation = new Vector3f(0,0,0);
		position = new Vector3f(0,0,0);
	}
	
	public void setPosition(float x, float y, float z) {
		position.x = x;
		position.y = y;
		position.z = z;
	}
	
	public void setRotation(float x, float y, float z) {
		rotation.x = x;
		rotation.y = y;
		rotation.z = z;
	}
	
	public void offsetPosition(float x, float y, float z) {
		if(z != 0 ) {
			position.x += Math.sin(Math.toRadians(rotation.y))* -1f * z;
			position.z += Math.cos(Math.toRadians(rotation.y)) * z;
		}
		if(x != 0) {
			position.x += Math.cos(Math.toRadians(rotation.y))* -1f * x;
			position.z += Math.sin(Math.toRadians(rotation.y)) * x;
		}
		position.y += y;
	}
	
	public void offsetRotation(float x, float y, float z) {
		rotation.x += x;
		rotation.y += y;
		rotation.z += z;
	}
	
	public Vector3f getRotation() {
		return rotation;
	}
	
	public Vector3f getPosition() {
		return position;
	}
}
