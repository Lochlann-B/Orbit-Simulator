package app;

import java.util.ArrayList;

import input.Camera;
import maths.Vector3f;
import opengl.ParticleMesh;
import maths.Vector2f;

public class PlanetBehaviour {

	private ArrayList<Planet> planetList = new ArrayList<Planet>();
	private Planet selectedPlanet;
	private boolean planetMoveMode = false;
	
	public Planet getSelectedPlanet() {
		return selectedPlanet;
	}
	
	public ArrayList<Planet> getPlanetList() {
		return planetList;
	}
	
	public void setPlanetList(ArrayList<Planet> planets) {
		planetList = planets;
	}
	
	public void setPlanetList(Planet[] planets) {
		planetList.clear();
		for(Planet p : planets) {
			planetList.add(p);
		}
	}
	
	public void planetsColliding(ParticleMesh particleMesh, ArrayList<ParticleGenerator> pGenList) {
		for(Planet p : planetList) {
			for(Planet p2 : planetList) {
				if((p2 != p && !p.isExploding() && !p2.isExploding())) {
					float diffX = p.item.getPosition().x - p2.item.getPosition().x;
					float diffY = p.item.getPosition().y - p2.item.getPosition().y;
					//Collision check
					if(Math.sqrt(diffX*diffX + diffY*diffY) <= p2.item.getScale() + p.item.getScale() && p.getID() != 0 && p2.getID() != 0) {
						
						//Order planets by size
						int ID1 = p.item.getScale() > p2.item.getScale() ? p.getID() : p2.getID();
						int ID2 = p.item.getScale() < p2.item.getScale() ? p.getID() : p2.getID();
						float combinedScale = p.getAppItem().getScale() + p2.getAppItem().getScale();
						
						if(p == selectedPlanet || p2 == selectedPlanet)
							planetMoveMode = false;
						
						for(int i = 0; i < 2; i++) {
							//Add particle effects for each planets' explosion
							ParticleGenerator pCollision = new ParticleGenerator(particleMesh, combinedScale*30f, 0.07f, combinedScale*10f, p.getPosition().x, p.getPosition().y, p.getPosition().z);
							pCollision.setLife(10);
							pCollision.setLifeStatus(true);
							pCollision.setInitialColour(2.0f, 2.0f, 0f);
							pCollision.setFinalColour(2.0f, 0f, 0f);
							if(i == 0) {
								pCollision.setPosition(p.getPosition().x, p.getPosition().y, 0);
								p.setParticleGenerator(pCollision);
							}
							if(i == 1) {
								pCollision.setPosition(p2.getPosition().x, p2.getPosition().y, 0);
								p2.setParticleGenerator(pCollision);
							}
							pGenList.add(pCollision);
							pCollision.move(SimulationSpeed.getSpeed());
						}
						
						//Bigger planet is reset to a circular orbit
						setCircularOrbit(planetList.get(ID1));
						
						//Smaller planet is shrunk and set to be a moon of the first planet
						Vector3f position2 = p2.getPosition();
						double combinedMass = p.getMass() + p2.getMass();
						
						//Mass and size are set to be a random proportion of the sum of the planets'
						double proportion = 0.5*Math.random() + 0.1;
						
						double semiMajor2 = Math.sqrt(position2.x*position2.x + position2.y*position2.y);
						
						double newTheta2 = Math.atan(position2.y/position2.x);
						if(position2.y < 0 && position2.x < 0) {
							newTheta2 = -Math.PI + newTheta2;
						}
						else if(position2.y > 0  && position2.x < 0) {
							newTheta2 = Math.PI + newTheta2;
						}
						
						float eccentricity2 = (float) (Math.random()*0.3);
						float scale2 = combinedScale*((float) proportion);
						float scale1 = (float) ((1 - proportion)*combinedScale*0.9);

						planetList.get(ID1).setMass(combinedMass*(1-proportion));

						planetList.get(ID1).getAppItem().setScale(scale1);
						planetList.get(ID1).setID(ID1);
						
						
						planetList.get(ID2).setMass(combinedMass*proportion);
						if(planetList.get(ID1).getParent() != planetList.get(0))
							planetList.get(ID1).setParent(planetList.get(0));
						planetList.get(ID2).setParent(planetList.get(ID1));
						semiMajor2 = planetList.get(ID2).getParent() == planetList.get(0) ? semiMajor2 : semiMajor2*0.1;
						//Make sure that the new planets aren't intersecting
						if(semiMajor2*(1-eccentricity2) <= scale1 + scale2 + 2) {
							//Increase the moon's radius so its orbit doesn't intersect the bigger planet
							semiMajor2 += (scale1 + scale2 - semiMajor2*(1-eccentricity2) + 2)/(1-eccentricity2);
						}
						planetList.get(ID2).setSemiMajorDiameter(semiMajor2);
						planetList.get(ID2).setEccentricity(eccentricity2);
						planetList.get(ID2).getAppItem().setScale(scale2);
						planetList.get(ID2).setID(ID2);
						planetList.get(ID2).setAngle(0);
						planetList.get(ID2).setDeltaRot((float) Math.random()*10);
						planetList.get(ID2).setTheta(newTheta2);
						planetList.get(ID1).move(SimulationSpeed.getSpeed());
						planetList.get(ID2).move(SimulationSpeed.getSpeed());	
					}
				}
			}
		}
	}
	
	public void remove() {
		if(selectedPlanet != null) {
			//Removes the selected planet and sets any moons to a circular orbit
			planetList.remove(selectedPlanet.getID());
			planetMoveMode = false;
			for(int i = 0; i < planetList.size(); i++) {
				if(i >= selectedPlanet.getID())
					planetList.get(i).setID(planetList.get(i).getID() - 1);
				if(planetList.get(i).getParentStatus()) {
					if(planetList.get(i).getParent() == selectedPlanet) {
						planetList.get(i).setSemiMajorDiameter(Math.sqrt(planetList.get(i).getPosition().x*planetList.get(i).getPosition().x + planetList.get(i).getPosition().y*planetList.get(i).getPosition().y));
						setCircularOrbit(planetList.get(i));
						planetList.get(i).setParent(planetList.get(0));
					}
				}
			}
			selectedPlanet = null;
		}
	}
	
	public void update() {
		float speed = SimulationSpeed.getSpeed();
		for(Planet p : planetList) {
			//Make sure that no planet is a moon of a moon
			if(p.getParentStatus()) {
				if(p.getParent().getParentStatus()) {
					if(p.getParent().getParent() != planetList.get(0)) {
						setCircularOrbit(p);
					}
				}
			}
			
			//If the particle generator has 'died', planet is no longer exploding and is visible again
			if(p.getID() != 0 && !SimulationSpeed.isPaused()) {
				if(p.getParticleGenerator() != null) {
					if(p.getParticleGenerator().getLife() <= 0) {
						p.setExploding(false);
						p.setParticleGenerator(null);
					}
					
				}
				else if(p.isExploding) {
					p.setExploding(false);
				}
				if(!p.isExploding &&!(planetMoveMode && p == selectedPlanet)) {
					p.move(speed);
					p.rotate(speed);
				}
			}
			
		}
	
	}
	
	public void addNewPlanet() {
		//Add a new planet and make it the currently selected planet on 'move' mode
		Planet p = new Planet(new AppItem(planetList.get(0).item.getMesh()));
		//p.item.setPosition(viewMousePos.x, -viewMousePos.y, 0);

		p.setMass(50000000);
		p.item.setScale(2f);
		p.setSelected(true);
		p.setDeltaRot(0.5f);
		p.setAngle(Math.PI/8);
		p.setID(planetList.size());
		p.setParent(planetList.get(0));
		planetList.add(p);
		setCircularOrbit(p);
		selectedPlanet = p;
		planetMoveMode = true;
	}
	
	private void setCircularOrbit(Planet p) {
		int ID1 = p.getID();
		Vector3f position1 = p.getPosition();
		double semiMajor1 = Math.sqrt(position1.x*position1.x + position1.y*position1.y);
		double newTheta1 = Math.atan(position1.y/position1.x);
		
		//Place planet in the correct quadrant
		if(position1.y < 0 && position1.x < 0) {
			newTheta1 = -Math.PI + newTheta1;
		}
		else if(position1.y > 0  && position1.x < 0) {
			newTheta1 = Math.PI + newTheta1;
		}
		
		planetList.get(ID1).setSemiMajorDiameter(semiMajor1);
		planetList.get(ID1).setTheta(newTheta1);
		planetList.get(ID1).setAngle(0);
		planetList.get(ID1).setEccentricity(0f);
		planetList.get(ID1).setParent(planetList.get(0));
	}

	public void setSelectedPlanet(Planet p) {
		selectedPlanet = p;
	}
	
	public void moveOnce() {
		for(Planet p : planetList) {
			p.move(SimulationSpeed.getSpeed());
		}
	}
	
	public boolean moveMode() {
		return planetMoveMode;
	}
	
	public void setMoveMode(boolean b) {
		planetMoveMode = b;
	}
	
	public void customMovePlanet(float x, float y, float z) {
		selectedPlanet.item.setPosition(x, y, z);
	}
	
	public void setSelectedPlanetPos() {
		if(planetMoveMode && selectedPlanet != null) {
			
			setCircularOrbit(selectedPlanet);
			
			selectedPlanet.setSelected(false);
			
			selectedPlanet = null;
			
			planetMoveMode = false;
		}
	}

	public boolean planetMoveMode() {
		return planetMoveMode;
	}
	
}
