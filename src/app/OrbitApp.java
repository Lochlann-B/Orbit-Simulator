package app;

import java.util.ArrayList;
import maths.Vector2f;
import maths.Vector3f;

import util.SaveSystem;

import org.lwjgl.glfw.GLFW;

import gui.Button;
import gui.Gui;
import gui.Panel;
import gui.TextInputButton;
import input.Camera;
import input.KeyboardInput;
import input.MouseInput;
import opengl.*;

public class OrbitApp implements AppInterface {
	
	private final Renderer renderer;
	private Vector3f cameraInc;
	private float[] clear = {0f,0f,0f,0f};
	
	//Camera sensitivity
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
	private Vector3f cameraAcceleration = new Vector3f();
	private Vector2f viewMousePos = new Vector2f();
	
	private PlanetBehaviour planetBehaviour;
	
	private Camera camera;
	private MouseInput mouseInput;
	private Window window;
	
	private ParticleGenerator particleGenerator;
	private ArrayList<ParticleGenerator> pGenList;
	
	private Gui gui;
	
	private KeyboardInput keyboardInput;

	private boolean mKeyPress = false;
	private boolean mKeyRelease = true;
	private boolean gKeyRelease = true;
	private boolean spaceBarRelease = true;
	private boolean enterPress = true;
	private boolean enterRelease = true;
	private boolean leftClicked = false;
	private boolean textButtonPressed = false;
	
	public OrbitApp() {
		renderer = new Renderer();
		cameraInc = new Vector3f(0,0,0);
		planetBehaviour = new PlanetBehaviour();
		keyboardInput = new KeyboardInput();
	}
	
	@Override
	public void init(Window window, Camera camera, MouseInput mouseInput) throws Exception {
		
		this.camera = camera;
		this.mouseInput = mouseInput;
		this.window = window;
		window.init();
		renderer.init(window, mouseInput, camera);

		
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
		
		planetBehaviour.setPlanetList(Generator.generateSystem(planetMesh));
		
		//Particles used for the sun
		pGenList = new ArrayList<ParticleGenerator>();
		particleGenerator = new ParticleGenerator(particleMesh, 2000f, 0.1f, 150f, 0, 0, 0);
		particleGenerator.setInitialColour(2.0f, 2.0f, 0f);
		particleGenerator.setFinalColour(2.0f, 0f, 0f);
		particleGenerator.setPosition(0, 0, 0);
		particleGenerator.move(SimulationSpeed.getSpeed());
		pGenList.add(particleGenerator);
		
		gui = new Gui();
		gui.init(window, planetBehaviour.getPlanetList(), planetBehaviour);
		mouseInput.init(window, gui);
		SaveSystem.init();
		
		keyboardInput.init(window, gui);
	}
	
	public void genSystem(Mesh mesh) {
		planetBehaviour.setSelectedPlanet(null);
		planetBehaviour.setPlanetList(Generator.generateSystem(planetMesh));
		int size = pGenList.size();
		for(int i = 1; i < size; i++) {
			pGenList.remove(1);
		}
		//No planet is being moved
		mKeyPress = false;
	}
	
	@Override
	public void render() throws Exception {
		window.setClearColour(clear[0], clear[1], clear[2], 1.0f);
		renderer.render(planetBehaviour.getPlanetList(), light, texture, pGenList, gui);
		gui.render(window);
	}
	
	@Override
	public void input() {
		
		enterPress = false;
		if(window.getKeyPress(GLFW.GLFW_KEY_ENTER) && enterRelease) {
			enterPress = true;
			enterRelease = false;
		}
		if(!window.getKeyPress(GLFW.GLFW_KEY_ENTER))
			enterRelease = true;
		
		if(window.getResizeStatus())
			gui.resize(window);
		
		leftClicked = mouseInput.leftButtonPressed();

		if(leftClicked || enterPress) {
			//Check whether any buttons have been clicked
			boolean selected = false;
			textButtonPressed = false;
			if(leftClicked) {
				
				for(Panel p : gui.getPanels()) {
					if(!p.isHidden()) {
						   for(Button b : p.getButtons()) {
							   b.checkClicked((int)mouseInput.getCurrentPos().x, (int)mouseInput.getCurrentPos().y);
							   if(b.isPressed()) {
								   selected = true;
								   gui.setSelectedButton(b);
								   if(b.getClass() == TextInputButton.class) {
									   textButtonPressed = true;
								   }
							   }
						   }
						}
				   }
				
			}
			if(((!selected) || enterPress) && gui.getSelectedButton() != null) {
				if(gui.getSelectedButton().getClass() == TextInputButton.class) {
					TextInputButton button = (TextInputButton) gui.getSelectedButton();
					button.setContainsDecimal(false);
					button.doClickEvent();
				}
		    	gui.setSelectedButton(null);
			}
		}

		//Pan camera
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
		if((cameraAcceleration.z + mouseInput.getScrollOffset()*sensitivityZ)/(1-1/1.2) < camera.getPosition().z)
			cameraAcceleration.z += mouseInput.getScrollOffset()*sensitivityZ;
		
		if(mouseInput.getScrollOffset() > 0) {
			cameraAcceleration.x += 0.03*diffX;
			cameraAcceleration.y += 0.03*diffY;
		}

		mouseInput.setPrev();
		
		if(!textButtonPressed) {
			
			//Keyboard shortcuts
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
				SimulationSpeed.setSpeed(1.0f);
			}
			if(!window.getKeyPress(GLFW.GLFW_KEY_G)) {
				gKeyRelease = true;
			}
			if((window.getKeyPress(GLFW.GLFW_KEY_SPACE)) && spaceBarRelease) {
				spaceBarRelease = false;
				SimulationSpeed.togglePause();
			}
			if(!window.getKeyPress(GLFW.GLFW_KEY_SPACE)) {
				spaceBarRelease = true;
			}
			if(window.getKeyPress(GLFW.GLFW_KEY_M) && mKeyRelease && planetBehaviour.getSelectedPlanet() != null) {
				mKeyPress = !mKeyPress;
				mKeyRelease = false;
				planetBehaviour.setMoveMode(mKeyPress);
			}
			if(!window.getKeyPress(GLFW.GLFW_KEY_M)) {
				mKeyRelease = true;
			}
			if((window.getKeyPress(GLFW.GLFW_KEY_R)) && planetBehaviour.getSelectedPlanet() != null) {
				planetBehaviour.remove();
			}
			if(window.getKeyPress(GLFW.GLFW_KEY_L)) {
				//Either sets the camera to the selected planet's position or moves it back to the origin
				Planet selectedPlanet = planetBehaviour.getSelectedPlanet();
				if(selectedPlanet != null)
					camera.setPosition(selectedPlanet.getPosition().x, selectedPlanet.getPosition().y, camera.getPosition().z);
				else
					camera.setPosition(0, 0, 400);
			}
			
			if(window.getKeyPress(GLFW.GLFW_KEY_A) && planetBehaviour.getSelectedPlanet() == null) {
				planetBehaviour.addNewPlanet();
			}
		}
	}
	
	double rad;
	float r;
	boolean nothingSelected;
	boolean inPanel;
	
	@Override
	public void update(float interval) {
		Planet selectedPlanet = planetBehaviour.getSelectedPlanet();
		//System.out.println(selectedPlanet);
		float speed = SimulationSpeed.getSpeed();
		inPanel = gui.checkMouseCoords((int) mouseInput.getCurrentPos().x, (int) mouseInput.getCurrentPos().y);
		//Update planet info panel
		if(selectedPlanet != null)
			gui.setPlanetInfo(selectedPlanet);
		if(selectedPlanet != null && gui.getPanels().get(0).isHidden()) {
			gui.getPanels().get(0).setHidden(false);
		}
		else if(selectedPlanet == null) {
			//Hide planet info panel
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
		
		
		
		if(leftClicked && nothingSelected && selectedPlanet != null && !mKeyPress && !inPanel) {
			selectedPlanet.setSelected(false);
			selectedPlanet = null;
			mKeyPress = false;
		}
		
		planetBehaviour.update();
		planetBehaviour.planetsColliding(particleMesh, pGenList);
		
		if(leftClicked && !inPanel)  {
			checkPlanetSelect();
		}
		
		
		if(!SimulationSpeed.isPaused()) {
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
		
		if((planetBehaviour.planetMoveMode()) && (selectedPlanet != null)) {
			planetBehaviour.customMovePlanet(viewMousePos.x, -viewMousePos.y, 0);

		}
		
		if(leftClicked) {
			planetBehaviour.setSelectedPlanetPos();
		}
		
	}
	
	private void checkPlanetSelect() {
		//Planet cannot be selected if it's the sun (ID = 0), or if it is exploding
		Planet selectedPlanet = planetBehaviour.getSelectedPlanet();
		boolean planetSelected = false;
		for(Planet p : planetBehaviour.getPlanetList()) {
			if(!p.isExploding()) {
				float relativeX = viewMousePos.x - p.item.getPosition().x;
				float relativeY = viewMousePos.y + p.item.getPosition().y;
					gui.hideTextBox(true);
					if(p.getID() != 0 && Math.sqrt(((relativeX)*(relativeX)) + ((relativeY)*(relativeY))) <= camera.getPosition().z*Math.tan(Math.asin(p.item.getScale()/camera.getPosition().z))) {
						nothingSelected = false;
						
						if(selectedPlanet != p && selectedPlanet != null) {
							selectedPlanet.setSelected(false);
						}
						planetBehaviour.setSelectedPlanet(p);
						p.setSelected(true);
						
						planetSelected = true;
					}
					else {
						p.setSelected(false);
					}
			}
			else {
				p.setSelected(false);
			}
		}
		if(!planetSelected)
			planetBehaviour.setSelectedPlanet(null);
	}
	
	public void cleanup() {
		renderer.cleanup();
	}
}