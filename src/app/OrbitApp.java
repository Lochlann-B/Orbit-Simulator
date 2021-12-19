package app;

import java.io.File;
import java.util.ArrayList;

/*
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
*/
import maths.Vector2f;
import maths.Vector3f;
import maths.Vector4f;
import util.LoadSystem;
import util.SaveSystem;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import gui.Button;
import gui.Gui;
import gui.Panel;
import input.Camera;
import input.KeyboardInput;
import input.MouseInput;
import maths.Matrix3f;
import maths.Matrix4f;
import opengl.*;
import org.lwjgl.nanovg.*;

public class OrbitApp implements AppInterface {
	
	private final Renderer renderer;
	private Vector3f cameraInc;
	private float[] clear = {0f,0f,0f,0f};
	private float sensitivityX = 0.1f;
	private float sensitivityY = 0.1f;
	private float sensitivityZ = 1f;
	
	private Mesh planetMesh;
	private Model planetModel;
	private Material planetMaterial;
	private Attenuation att;
	private PointLight light;
	private Texture texture;
	private Texture particleTexture;
	private ParticleMesh particleMesh;
	private boolean mKeyPress = false;
	private boolean mKeyRelease = true;
	private boolean gKeyRelease = true;
	private boolean spaceBarRelease = true;
	private boolean enterPress = true;
	private boolean enterRelease = true;
	private boolean pause = false;
	private boolean leftClicked = false;
	private boolean textButtonPressed = false;
	private Vector3f cameraAcceleration = new Vector3f();
	private Vector2f viewMousePos = new Vector2f();
	private Planet selectedPlanet = null;
	private float speed = 1f;
	
	private Camera camera;
	private MouseInput mouseInput;
	private Window window;
	
	private ParticleGenerator particleGenerator;
	private ArrayList<ParticleGenerator> pGenList;
	
	private Planet[] planets;
	private ArrayList<Planet> planetList;
	
	private Gui gui;
	
	private KeyboardInput keyboardInput;
	
	public OrbitApp() {
		renderer = new Renderer();
		cameraInc = new Vector3f(0,0,0);
		planetList = new ArrayList<Planet>();
		keyboardInput = new KeyboardInput();
	}
	
	@Override
	public void init(Window window, Camera camera, MouseInput mouseInput) throws Exception {
		this.camera = camera;
		this.mouseInput = mouseInput;
		this.window = window;
		window.init();
		renderer.init(window, mouseInput, camera);
		gui = new Gui();
		gui.init(window);
		mouseInput.init(window, gui);
		SaveSystem.init();
		
		
		
		keyboardInput.init(window, gui);
		
		att = new Attenuation(0.05f, 0f, 0.0005f);
		
		texture = new Texture("./resources/textures/bricks.png");
		
		particleTexture = new Texture("./resources/textures/particle.png");
		
		texture.createGlTexture();
		particleTexture.createGlTexture();
		particleMesh = new ParticleMesh(particleTexture);
		
		light = new PointLight(new Vector3f(1.0f,1.0f,0.0f), new Vector3f(0f, 0f, -5f), 1, att);
		planetModel = new Model("/models/sphere2.obj");
		planetMaterial = new Material("/models/sphere2.mtl");
		planetMesh = new Mesh(planetModel.getVertices(), planetModel.getIndices(), planetModel.getNormals(), planetModel.getTextures(), planetMaterial);
		
		planets = Generator.generateSystem(planetMesh);
		pGenList = new ArrayList<ParticleGenerator>();
		particleGenerator = new ParticleGenerator(particleMesh, 2000f, 0.1f, 150f, 0, 0, 0);
		particleGenerator.setInitialColour(2.0f, 2.0f, 0f);
		particleGenerator.setFinalColour(2.0f, 0f, 0f);
		particleGenerator.setPosition(0, 0, 0);
		particleGenerator.move(speed);
		pGenList.add(particleGenerator);

		for(int i = 0; i < planets.length; i++) {
			planetList.add(planets[i]);
			if(i != 0)
				planets[i].move(speed);
		}
	}
	
	public void genSystem(Mesh mesh) {
		selectedPlanet = null;
		planets = Generator.generateSystem(planetMesh);
		planetList.clear();
		for(int i = 0; i < planets.length; i++) {
			planetList.add(planets[i]);
			if(i != 0) {
				planets[i].move(speed);
			}
		}
		int size = pGenList.size();
		for(int i = 1; i < size; i++) {
			pGenList.remove(1);
		}
		mKeyPress = false;
	}
	
	@Override
	public void render() throws Exception {
		window.setClearColour(clear[0], clear[1], clear[2], 1.0f);
		renderer.render(planetList, light, texture, pGenList, gui);
		gui.render(window);
	}
	
	char characterKey;
	char tempKey = '.';
	
	@Override
	public void input() {
		
		enterPress = false;
		if(window.getKeyPress(GLFW.GLFW_KEY_ENTER) && enterRelease) {
			enterPress = true;
			enterRelease = false;
		}
		if(!window.getKeyPress(GLFW.GLFW_KEY_ENTER))
			enterRelease = true;
		
		characterKey = '.';
		if(window.getResizeStatus())
			gui.resize(window);
		
		leftClicked = mouseInput.leftButtonPressed();

		if(leftClicked || enterPress) {
			boolean selected = false;
			textButtonPressed = false;
			if(leftClicked) {
				
				for(Panel p : gui.getPanels()) {
					if(!p.isHidden()) {
						   for(Button b : p.getButtons()) {
							   
							   tempKey = b.checkClicked((int)mouseInput.getCurrentPos().x, (int)mouseInput.getCurrentPos().y);
							    characterKey = '.' == tempKey ? characterKey : tempKey;
							    
							    if(b.isPressed()) {
							    	
							    	gui.setSelectedButton(b);
							    	selected = true;
							    	if(b.enterText())
							    		textButtonPressed = true;
							    }
						   }
						}
				   }
				
			}
			if(((!selected) || enterPress) && gui.getSelectedButton() != null) {
				textButtonPressed = false;
				if(gui.getSelectedButton().enterText())
					gui.updateValues(selectedPlanet);
		    	gui.setSelectedButton(null);
			}
			gui.setContainsDecimal(false);
		}
		setSpeed(characterKey);
		
		showTextBox(characterKey);

		
		if(mouseInput.middleButtonPressed()) {
			sensitivityX = 0.003f * camera.getPosition().z;
			sensitivityY = 0.003f * camera.getPosition().z;
			mouseInput.input(window);
			cameraInc.x += mouseInput.getDisplacement().x*sensitivityX;
			cameraInc.y += mouseInput.getDisplacement().y*sensitivityY;
		}
		mouseInput.scrollInput();
		sensitivityZ = 0.03f * camera.getPosition().z;
		float diffX = viewMousePos.x - camera.getPosition().x;
		float diffY = -viewMousePos.y - camera.getPosition().y;
		//cameraInc.z += mouseInput.getScrollOffset()*sensitivityZ;
		if((cameraAcceleration.z + mouseInput.getScrollOffset()*sensitivityZ)/(1-1/1.2) < camera.getPosition().z)
			cameraAcceleration.z += mouseInput.getScrollOffset()*sensitivityZ;
		
		//if(mouseInput.getScrollOffset() > 0 && (cameraAcceleration.x + mouseInput.getScrollOffset()*sensitivityX*diff)/(1-1/1.2) < Math.abs(viewMousePos.x)) {
		if(mouseInput.getScrollOffset() > 0) {
			cameraAcceleration.x += 0.03*diffX;
			cameraAcceleration.y += 0.03*diffY;
		}
		//}
		mouseInput.setPrev();
		
		if(!textButtonPressed) {
			if(window.getKeyPress(GLFW.GLFW_KEY_W))
				cameraInc.z -= sensitivityZ;
			if(window.getKeyPress(GLFW.GLFW_KEY_S)) {
				cameraInc.z += sensitivityZ;
			}
			
			if(window.getKeyPress(GLFW.GLFW_KEY_G) && gKeyRelease) {
				genSystem(planetMesh);
				gKeyRelease = false;
			}
			if(window.getKeyPress(GLFW.GLFW_KEY_T)) {
				speed = 1.0f;
			}
			if(!window.getKeyPress(GLFW.GLFW_KEY_G)) {
				gKeyRelease = true;
			}
			if((window.getKeyPress(GLFW.GLFW_KEY_SPACE) || characterKey == ' ') && spaceBarRelease) {
				spaceBarRelease = false;
				pause = !pause;
			}
			if(!window.getKeyPress(GLFW.GLFW_KEY_SPACE)) {
				spaceBarRelease = true;
			}
			if(window.getKeyPress(GLFW.GLFW_KEY_M) && mKeyRelease && selectedPlanet != null) {
				mKeyPress = !mKeyPress;
				mKeyRelease = false;
			}
			if(!window.getKeyPress(GLFW.GLFW_KEY_M)) {
				mKeyRelease = true;
			}
			if((window.getKeyPress(GLFW.GLFW_KEY_R) || characterKey == 'w') && selectedPlanet != null) {
				//Removes the selected planet and sets any moons to a circular orbit
				planetList.remove(selectedPlanet.getID());
				mKeyPress = false;
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
			if(window.getKeyPress(GLFW.GLFW_KEY_L)) {
				//Either sets the camera to the selected planet's position or moves it back to the origin
				if(selectedPlanet != null)
					camera.setPosition(selectedPlanet.getPosition().x, selectedPlanet.getPosition().y, camera.getPosition().z);
				else
					camera.setPosition(0, 0, 400);
			}
			
			if((window.getKeyPress(GLFW.GLFW_KEY_A) || characterKey == 'q') && selectedPlanet == null) {
				addPlanet();
			}
		}
	}
	float speedInc;
	private void setSpeed(char characterKey) {
		if(characterKey == 'f') {
			if(speed < 1 && speed >= -1)
				speedInc = 0.1f;
			else
				speedInc = 1;
			speed += speedInc;
		}
		if(characterKey == 's') {
			if(speed > -1 && speed <= 1)
				speedInc = 0.1f;
			else
				speedInc = 1;
			speed -= speedInc;
		}
		gui.setSpeed(speed);
	}
	
	private void showTextBox(char characterKey) {
		if(characterKey == 'k' || characterKey == 'l')
			gui.hideTextBox(false);
		else if(characterKey == 'c')
			gui.hideTextBox(true);
		if(characterKey == 'k') 
			gui.setSaveLoadText("save", 'm');
		if(characterKey == 'l') {
			gui.setSaveLoadText("load", 'n');
		}
		if(characterKey == 'm') {
			gui.hideTextBox(true);
			gui.saveSystem(planetList);
		}
		if(characterKey == 'n') {
			int status = gui.loadSystem(planetList);
			gui.hideTextBox(status == 0 ? true : false);
			if(status == 0) 
				pause = true;
		}
	}
	
	public void addPlanet() {
		//Add a new planet and make it the currently selected planet on 'move' mode
		
		Planet p = new Planet(new AppItem(planetMesh));
		p.item.setPosition(viewMousePos.x, -viewMousePos.y, 0);
		p.setID(planetList.size());
		p.setParent(planetList.get(0));
		p.setMass(50000000);
		p.item.setScale(2f);
		p.setSelected(true);
		p.setDeltaRot(0.5f);
		p.setAngle(Math.PI/8);
		planetList.add(p);
		setCircularOrbit(p);
		selectedPlanet = p;
		mKeyPress = true;
	}
	
	double rad;
	float r;
	boolean nothingSelected;
	boolean inPanel;
	
	@Override
	public void update(float interval) {
		inPanel = gui.checkMouseCoords((int) mouseInput.getCurrentPos().x, (int) mouseInput.getCurrentPos().y);
		if(selectedPlanet != null)
			gui.setPlanetInfo(selectedPlanet);
		if(selectedPlanet != null && gui.getPanels().get(0).isHidden()) {
			gui.getPanels().get(0).setHidden(false);
		}
		else if(selectedPlanet == null) {
			gui.getPanels().get(0).setHidden(true);
		}
		//Glide camera smoothly
		camera.offsetPosition(cameraInc.x*0.5f - cameraAcceleration.x, cameraInc.y*0.5f + cameraAcceleration.y, (float) (cameraInc.z - cameraAcceleration.z));
		cameraInc.x = 0;
		cameraInc.y = 0;
		cameraInc.z = 0;

		cameraAcceleration.z /= 1.2;
		cameraAcceleration.y /= 1.2;
		cameraAcceleration.x /= 1.2;
		
		if(Math.abs(cameraAcceleration.z) < 0.001f) {
			cameraAcceleration.z = 0;
		}
		if(Math.abs(cameraAcceleration.y) < 0.001f) {
			cameraAcceleration.y = 0;
		}
		if(Math.abs(cameraAcceleration.x) < 0.001f) {
			cameraAcceleration.x = 0;
		}
		
		
		viewMousePos = renderer.getWorldMousePosition();
		//System.out.println(viewMousePos.x);
		nothingSelected = true;
		
		for(Planet p : planetList) {
			
			if(p.getParentStatus()) {
				if(p.getParent().getParentStatus()) {
					if(p.getParent().getParent() != planetList.get(0)) {
						setCircularOrbit(p);
					}
				}
			}
			
			if(!p.isExploding()) {
				float relativeX = viewMousePos.x - p.item.getPosition().x;
				float relativeY = viewMousePos.y + p.item.getPosition().y;
				if(leftClicked && !inPanel)  {
					gui.hideTextBox(true);
					if(Math.sqrt(((relativeX)*(relativeX)) + ((relativeY)*(relativeY))) <= camera.getPosition().z*Math.tan(Math.asin(p.item.getScale()/camera.getPosition().z))) {
						nothingSelected = false;
						if(selectedPlanet != p && selectedPlanet != null && p.getID() != 0) {
							selectedPlanet.setSelected(false);
							p.setSelected(true);
							selectedPlanet = p;
						}
						else if(selectedPlanet == null && p.getID() != 0) {
							selectedPlanet = p;
							p.setSelected(true);
						}
					}
					
				}
			}
			
			for(Planet p2 : planetList) {
				if(p2 != p && !p.isExploding() && !p2.isExploding()) {
					float diffX = p.item.getPosition().x - p2.item.getPosition().x;
					float diffY = p.item.getPosition().y - p2.item.getPosition().y;
					if(Math.sqrt(diffX*diffX + diffY*diffY) <= p2.item.getScale() + p.item.getScale() && p.getID() != 0 && p2.getID() != 0) {
						if(p == selectedPlanet || p2 == selectedPlanet)
							mKeyPress = false;
						int ID1 = p.item.getScale() > p2.item.getScale() ? p.getID() : p2.getID();
						int ID2 = p.item.getScale() < p2.item.getScale() ? p.getID() : p2.getID();
						float combinedScale = p.getAppItem().getScale() + p2.getAppItem().getScale();
						
						for(int i = 0; i < 2; i++) {
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
							pCollision.move(speed);
						}
						
						setCircularOrbit(planetList.get(ID1));
						
						
						Vector3f position2 = p2.getPosition();
						double combinedMass = p.getMass() + p2.getMass();
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

						
						//planetList.get(ID1).getAppItem().setColour((float)Math.sqrt(3*(1-proportion)*planetList.get(ID1).getColour().x*proportion*planets[ID2].getColour().x), (float)Math.sqrt(3*(1-proportion)*planets[ID1].getColour().y*proportion*planets[ID2].getColour().y), (float)Math.sqrt(3*(1-proportion)*planets[ID1].getColour().z*proportion*planets[ID2].getColour().z));
						
						planetList.get(ID1).getAppItem().setScale(scale1);
						planetList.get(ID1).setID(ID1);
						
					
						planetList.get(ID2).setMass(combinedMass*proportion);
						if(planetList.get(ID1).getParent() != planetList.get(0))
							planetList.get(ID1).setParent(planetList.get(0));
						planetList.get(ID2).setParent(planetList.get(ID1));
						semiMajor2 = planetList.get(ID2).getParent() == planetList.get(0) ? semiMajor2 : semiMajor2*0.1;
						if(semiMajor2*(1-eccentricity2) <= scale1 + scale2 + 2) {
							semiMajor2 += (scale1 + scale2 - semiMajor2*(1-eccentricity2) + 2)/(1-eccentricity2);
						}
						planetList.get(ID2).setSemiMajorDiameter(semiMajor2);
						planetList.get(ID2).setEccentricity(eccentricity2);
						planetList.get(ID2).getAppItem().setScale(scale2);
						planetList.get(ID2).setID(ID2);
						planetList.get(ID2).setAngle(0);
						planetList.get(ID2).setDeltaRot((float) Math.random()*10);
						planetList.get(ID2).setTheta(newTheta2);
						planetList.get(ID1).move(speed);
						planetList.get(ID2).move(speed);	
						//System.out.println(planetList.get(ID1).getEccentricity());
						//System.out.println("\n");
					}
				}
			}
			
			
			
			//if(selectedPlanet != null) {
			//	System.out.println("BELP");
			//}
			
			if(p.getID() != 0 && pause == false) {
				if(p.getParticleGenerator() != null) {
					if(p.getParticleGenerator().getLife() <= 0) {
						p.setExploding(false);
						p.setParticleGenerator(null);
					}
					
				}
				else if(p.isExploding) {
					p.setExploding(false);
				}
				if(!p.isExploding &&!(mKeyPress && p == selectedPlanet)) {
					p.move(speed);
					p.rotate(speed);
				}
			}
			
		}
		
		if(leftClicked && nothingSelected && selectedPlanet != null && !mKeyPress && !inPanel) {
			selectedPlanet.setSelected(false);
			selectedPlanet = null;
			mKeyPress = false;
		}
		
		if(!pause) {
			for(int i = 0; i < pGenList.size(); i++)
			{
				if(pGenList.get(i).getLifeStatus()) {
					pGenList.get(i).setLife((float) (pGenList.get(i).getLife() - Math.sqrt(speed*speed)*0.1f));
					pGenList.get(i).setDeathStatus((pGenList.get(i).getLife() > 0) ? false : true);
					if(pGenList.get(i).getDeceasedStatus())
						pGenList.remove(i);
					else {
						pGenList.get(i).move(speed);
					}
				}
				else
					pGenList.get(i).move(speed);
			}
		}
		
		if((mKeyPress) && (selectedPlanet != null)) {
			selectedPlanet.item.setPosition(viewMousePos.x, -viewMousePos.y, 0);
		}
		
		if(mKeyPress && leftClicked && selectedPlanet != null && characterKey != 'q') {
			setCircularOrbit(selectedPlanet);
			
			selectedPlanet.setSelected(false);
			
			selectedPlanet = null;
			
			mKeyPress = false;	
		}
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
	
	public void cleanup() {
		renderer.cleanup();
	}
}