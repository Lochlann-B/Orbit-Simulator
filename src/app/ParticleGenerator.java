package app;

import java.util.ArrayList;

/*
import org.joml.Vector3f;
import org.joml.Vector4f;
*/
import maths.Vector3f;
import maths.Vector4f;

import opengl.Particle;
import opengl.ParticleMesh;

public class ParticleGenerator {
	
	private ArrayList<Particle> particleList;
	private Vector3f position;
	private Vector4f initialColour;
	private Vector4f finalColour;
	private boolean hasLife = false;
	private float lifeSpan = 0;
	private boolean dead = false;
	private boolean deceased = false;
	
	public ParticleGenerator(ParticleMesh pMesh, float avgNum, float avgSpeed, float avgLife, float x, float y, float z) {
		initialColour = new Vector4f();
		finalColour = new Vector4f();
		position = new Vector3f(x, y, z);
		particleList = new ArrayList<Particle>();
		Particle[] pList = Generator.generateParticleList(pMesh, avgNum, avgSpeed, avgLife, position);
		for(Particle p : pList) {
			p.setScale(3);
			particleList.add(p);
		}
	}
	
	public void move(float speed) {
		deceased = true;
		for(int i = 0; i < particleList.size(); i++) {
			Particle p = particleList.get(i);
			if(p.getLife() < 0) {
				if(!dead) {
					p.setPosition(position.x, position.y, position.z);
					p.setCurrentLifeSpan(p.getTotalLife());
				}
			}
			else {
				deceased = false;
				p.move(speed);
				float r = 1.33f*p.getLife()/p.getTotalLife() * initialColour.x + 0.75f*(p.getTotalLife()-p.getLife())/p.getTotalLife() * finalColour.x;
				float g = 1.33f*p.getLife()/p.getTotalLife() * initialColour.y + 0.75f*(p.getTotalLife()-p.getLife())/p.getTotalLife() * finalColour.y;
				float b = 1.33f*p.getLife()/p.getTotalLife() * initialColour.z + 0.75f*(p.getTotalLife()-p.getLife())/p.getTotalLife() * finalColour.z;
				float a = (p.getLife())/p.getTotalLife();
				//float a = 0.9f;
				p.setColour(r , g, b, a);
			}
		}
	}
	
	public void setInitialColour(float r, float g, float b) {
		initialColour.x = r;
		initialColour.y = g;
		initialColour.z = b;
	}
	
	public void setFinalColour(float r, float g, float b) {
		finalColour.x = r;
		finalColour.y = g;
		finalColour.z = b;
	}
	
	public void setPosition(float x, float y, float z) {
		position.x = x;
		position.y = y;
		position.z = z;
	}
	
	public ArrayList<Particle> getList() {
		return particleList;
	}
	
	public void setLife(float life) {
		lifeSpan = life;
	}
	
	public float getLife() {
		return lifeSpan;
	}
	
	public void setLifeStatus(boolean b) {
		hasLife = b;
	}
	
	public boolean getLifeStatus() {
		return hasLife;
	}
	
	public void setDeathStatus(boolean b) {
		dead = b;
	}
	
	public boolean getDeceasedStatus() {
		return deceased;
	}
	
}
