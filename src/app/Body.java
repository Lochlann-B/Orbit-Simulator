package app;

//import org.joml.Vector3f;
import maths.Vector3f;

public class Body {
	protected int ID;
	protected AppItem item;
	protected Body parent;
	protected boolean hasParent = false;
	
	protected float eccentricity;
	protected float aphelion;
	protected float perihelion;
	protected float deltaRot;
	protected Vector3f selfRotation = new Vector3f(0,0,0);
	protected double mass;
	protected double velocity;
	protected double r;
	protected double deltaTheta;
	protected double theta = 0;
	protected double semiMajorDiameter;
	protected double semiMinorDiameter;
	protected double angle = 0;
	protected float xPrev;
	protected float yPrev;
	protected boolean isExploding = false;
	protected ParticleGenerator explosion;
	
	public static final double G = 6.673*Math.pow(10, -9);
	
	public void move(float speed) {
		xPrev = item.getPosition().x;
		yPrev = item.getPosition().y;
		r = semiMajorDiameter*(1-eccentricity*eccentricity)/(1+eccentricity*Math.cos(theta));
		velocity = speed*Math.sqrt(G*mass*(2/r - 1/(semiMajorDiameter)));
		deltaTheta = velocity/r;
		theta += deltaTheta;
		theta %= 2*Math.PI;
		
		float x = (float) ((float) r*Math.cos(theta));
		float y = (float) ((float) r*Math.sin(theta));
		if(hasParent) {
			x += parent.getPosition().x;
			y += parent.getPosition().y;
		}
		item.setPosition((float) (x*Math.cos(angle) - y*Math.sin(angle)), (float) (y*Math.cos(angle) + x*Math.sin(angle)), 0f);
	}
	
	public void rotate(float speed) {
		item.setRotation(selfRotation.x, selfRotation.y, selfRotation.z);
		selfRotation.y += speed*deltaRot;
	}
	
	public void setPerihelion(double p) {
		semiMajorDiameter = p/(1-eccentricity);
	}
	
	public void setAphelion(double p) {
		semiMajorDiameter = p/(1+eccentricity);
	}
	
	public void setParent(Body parent) {
		this.parent = parent;
		hasParent = true;
	}
	
	public void setTheta(double d) {
		theta = d;
	}
	
	public void setDeltaRot(float rot) {
		deltaRot = rot;
	}
	
	public void setSemiMajorDiameter(double val) {
		semiMajorDiameter = val;
	}
	
	public void setEccentricity(float val) {
		eccentricity = val;
	}
	
	public void setMass(double val) {
		mass = val;
	}
	
	public boolean getParentStatus() {
		return hasParent;
	}
	
	public double getMass() {
		return mass;
	}
	
	public Body getParent() {
		return parent;
	}
	
	public Vector3f getPosition() {
		return item.getPosition();
	}
	
	public Vector3f getRotation() {
		return item.getRotation();
	}
	
	public Vector3f getColour() {
		return item.getColour();
	}
	
	public float getPrevX() {
		return xPrev;
	}
	
	public float getPrevY() {
		return yPrev;
	}
	
	public double getSemiMajorDiameter() {
		return semiMajorDiameter;
	}
	
	public int getID() {
		return ID;
	}
	
	public void setID(int ID) {
		this.ID = ID;
	}
	
	public void setAngle(double ang) {
		angle = ang;
	}
	
	public float getEccentricity() {
		return eccentricity;
	}
	
	public double getAngle() {
		return angle;
	}
	
	public boolean isExploding() {
		return isExploding;
	}
	
	public void setExploding(boolean b) {
		isExploding = b;
	}
	
	public ParticleGenerator getParticleGenerator() {
		return explosion;
	}
	
	public void setParticleGenerator(ParticleGenerator p) {
		explosion = p;
		isExploding = true;
	}
	
	public double getPerihelion() {
		return semiMajorDiameter*(1-eccentricity);
	}
	
	public double getAphelion() {
		return semiMajorDiameter*(1+eccentricity);
	}
	
	public double getVelocity() {
		return velocity;
	}
	
}