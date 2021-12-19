package app;

public class Planet extends Body {
	
	private boolean selected = false;
	
	public Planet(AppItem item) {
		this.item = item;
	}
	
	public AppItem getAppItem() {
		return item;
	}
	
	public void setSelected(boolean b) {
		selected = b;
	}
	
	public boolean isSelected() {
		return selected;
	}
	
	public double[] getProperties() {
		return new double[]{ID, hasParent ? parent.getID() : 0, mass, eccentricity, semiMajorDiameter, theta, deltaRot, angle, item.getColour().x, item.getColour().y, item.getColour().z, item.getScale()};
	}
	
	public void setProperties(double[] properties) {
		ID = (int) properties[0];
		mass = properties[2];
		eccentricity = (float) properties[3];
		semiMajorDiameter = (float) properties[4];
		theta = properties[5];
		deltaRot = (float) properties[6];
		angle = properties[7];
		item.setColour((float) properties[8], (float) properties[9], (float) properties[10]); 
		item.setScale((float) properties[11]);
	}
	
}
