package maths;

public class Vector2f {
	public float x;
	public float y;
	
	public Vector2f(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public Vector2f() {x = 0; y = 0;}
	
	public void set(float x, float y) {
		this.x = x;
		this.y = y;
	}
}
