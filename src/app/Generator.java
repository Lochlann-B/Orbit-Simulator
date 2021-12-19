package app;

//import org.joml.Vector2f;
//import org.joml.Vector3f;

import maths.Vector3f;

import opengl.Mesh;
import opengl.Particle;
import opengl.ParticleMesh;

public class Generator {
	
	private static Planet[] planets;
	private static Particle[] particles;
	
	public static Particle[] generateParticleList(ParticleMesh pMesh, float avgNumber, float avgSpeed, float avgLifeSpan, Vector3f parentPos) {
		particles = new Particle[(int)probabilityFunc(0.5*avgNumber, avgNumber*1.5, avgNumber, 5)];
		float speed = (float) probabilityFunc(avgSpeed/1.2, avgSpeed*1.2, avgSpeed, 3);
		for(int i = 0; i < particles.length; i++) {
			particles[i] = new Particle(pMesh);
			Vector3f speedVector = new Vector3f((float)(Math.random()*2 - 1), (float)(Math.random()*2 - 1),(float)(Math.random()*2 - 1));
			speedVector = speedVector.normalise();
			particles[i].setDirection(speedVector.x*speed, speedVector.y*speed, speedVector.z*speed);
			particles[i].setLifeSpan((float) probabilityFunc(avgLifeSpan/1.5, 1.5*avgLifeSpan, avgLifeSpan, 1));
			
			float time = 0.6f*particles[i].getTotalLife()*(float)(Math.random());
			particles[i].setPosition(2*time*particles[i].getDirection().x + parentPos.x, 2*time*particles[i].getDirection().y + parentPos.y, parentPos.z);
			particles[i].setCurrentLifeSpan((float)(particles[i].getTotalLife()*Math.random()));
			//int time = (int) (0.6f*particles[i].getTotalLife()*(Math.random()));
			//for(int j = 0; j < time; i++) {
			//	particles[i].move();
			//}
		}
		return particles;
	}
	
	public static Planet[] generateSystem(Mesh mesh) {
		planets = new Planet[getPlanetNumber()+1];
		planets[0] = new Planet(new AppItem(mesh));
		planets[0].item.setScale(10);
		planets[0].item.setColour(3.0f, 3.0f, 0.0f);
		planets[0].item.setPosition(0, 0, 0);
		planets[0].setID(0);
		for(int i = 1; i < planets.length; i++) {
			double semiMajor = getSemiMajor();
			float eccentricity = getEccentricity();
			float scale = (float) probabilityFunc(0.7, 5, 1, 1) + (float) (0.01*semiMajor);
			//Check if planet will intersect with Sun and correct semi-major axis
			if(semiMajor*(1-eccentricity) <= planets[0].item.getScale() + scale + 5) {
				semiMajor += (planets[0].item.getScale() + scale - semiMajor*(1-eccentricity) + 30)/(1-eccentricity);
			}
			
			planets[i] = new Planet(new AppItem(mesh));
			planets[i].setMass(getMass());
			planets[i].setSemiMajorDiameter(semiMajor);
			planets[i].setEccentricity(eccentricity);
			planets[i].setDeltaRot(getDeltaRot());
			planets[i].item.setColour((float) Math.random(), (float) Math.random(), (float) Math.random());
			planets[i].item.setScale(scale);
			planets[i].setTheta(Math.random() * 2 * Math.PI);
			planets[i].setID(i);
			planets[i].setAngle(Math.random() * 2 * Math.PI);
			//planets[i].setAngle(0);
			planets[i].setParent(planets[0]);
		}
		return planets;
	}
	
	private static double getMass() {
		return probabilityFunc(100000, 100000000, 50000000, 5);
	}
	
	private static float getDeltaRot() {
		return (float) probabilityFunc(0.01,1, 0.1, 3);
	}
	
	private static float getEccentricity() {
		return (float) probabilityFunc(0,0.9,0.3, 3);
	}
	
	private static double getSemiMajor() {
		return probabilityFunc(20,1000,200,1);
	}
	
	private static int getPlanetNumber() {
		int num = (int) probabilityFunc(10,100,20,5);
		return num;
	}
	
	private static double probabilityFunc(double min, double max, double mean, double dist) {
		double seed = Math.random();
		double a = (min - mean)/Math.pow(-0.5, dist);
		double b = Math.log((max-mean)/(a*Math.pow(0.5, dist)));
		return a*Math.pow(Math.E, b*seed)*Math.pow(seed - 0.5, dist) + mean;
	}
	
}