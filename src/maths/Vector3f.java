package maths;

public class Vector3f {
	public float x;
	public float y;
	public float z;
	
	public Vector3f() {}
	
	public Vector3f(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector3f(Vector3f vec) {
		x = vec.x;
		y = vec.y;
		z = vec.z;
	}
	
	public Vector3f(float[] pos) {
		x = pos[0];
		y = pos[1];
		z = pos[2];
	}
	
	public Vector3f normalise() {
		float mag = (float) Math.sqrt(x*x + y*y + z*z);
		return new Vector3f(x/mag, y/mag, z/mag);
	}
	
}
