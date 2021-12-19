package maths;

public class Vector4f {
	public float x;
	public float y;
	public float z;
	public float w;
	
	public Vector4f() {}
	
	public Vector4f(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	
	public Vector4f(Vector3f vec, float w) {
		x = vec.x;
		y = vec.y;
		z = vec.z;
		this.w = w;
	}
	
	public void set(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	
}
