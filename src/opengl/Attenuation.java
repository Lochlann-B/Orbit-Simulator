package opengl;

public class Attenuation {
	
	private float linear;
	private float constant;
	private float exponent;
	
	public Attenuation(float linear, float constant, float exponent) {
		this.linear = linear;
		this.constant = constant;
		this.exponent = exponent;
	}
	
	public void setLinear(float lin) {
		linear = lin;
	}
	
	public void setConstant(float cons) {
		constant = cons;
	}
	
	public void setExpoenent(float exp) {
		exponent = exp;
	}
	
	public float getLinear() {
		return linear;
	}
	
	public float getConstant() {
		return constant;
	}
	
	public float getExponent() {
		return exponent;
	}
}
