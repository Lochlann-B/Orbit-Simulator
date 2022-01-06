package gui;

import org.lwjgl.nanovg.NanoVGGL3;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NanoVG;

import java.lang.reflect.Field;
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import app.Planet;
import app.PlanetBehaviour;
import app.SimulationSpeed;
import maths.Vector4f;
import opengl.Window;
import util.LoadSystem;
import util.SaveSystem;


public class Gui {
	
	public static final long vg = NanoVGGL3.nvgCreate(NanoVGGL3.NVG_ANTIALIAS | NanoVGGL3.NVG_STENCIL_STROKES);;
	private int font;
	
	private ArrayList<Panel> panels;
	
	private NVGColor VgColour;
	
	private Text infoText;
	private Text addRemoveText;
	private Text speedText;
	
	private Panel planetInfo;
	private Panel addRemove;
	private Panel speed;
	private Panel loadSave;
	private Panel textBox;
	private Button removeAdd;
	private Button infoButton;
	private Button playPause;
	private Button fastForward;
	private Button slowDown;
	private Button save;
	private Button load;
	private Button saveLoad;
	
	private Button selectedButton;
	
	private float speedVal = 1.0f;
	
	private ArrayList<Planet> planetList;
	private PlanetBehaviour planetBehaviour;
	
	public Gui() {
	}
	
	public void init(Window window, ArrayList<Planet> planetList, PlanetBehaviour planetBehaviour) throws Exception {
		panels = new ArrayList<Panel>();
		this.planetBehaviour = planetBehaviour;
		
		if(vg == MemoryUtil.NULL) {
			throw new Exception("Could not create nanoVG context!");
		}
		
		font = NanoVG.nvgCreateFont(vg, "roboto", "./resources/fonts/RobotoMono-Regular.ttf");
		
		String[] planetInfoArray = {"Mass: ", "Eccentricity: ", "Aphelion: ", "Perihelion: ", "Velocity: ", "Scale: ", "Angle: ", "Colour (RGB): "};
		char[] planetButtonArray = {'m', 'e', 'a', 'p', 'v', 'c', 'z', 'r', 'g', 'b'};
		
		VgColour = NVGColor.create();
		VgColour.r(0.584f);
		VgColour.g(0f);
		VgColour.b(1.0f);
		VgColour.a(0.8f);
		
		this.planetList = planetList; 
		
		setupPlanetInfo(window, planetInfoArray, planetButtonArray);
		setupAddRemovePanel(window);
		setupSpeedPanel(window);
		setupSystemSavePanel(window);
		setupTextBoxPanel(window);
		
	}
	
	private void setupTextBoxPanel(Window window) {
		textBox = new Panel(0.4f, 0.4f, 0.2f, 0.2f, new Vector4f(0.584f, 0f, 1.0f, 0.8f), window);
		textBox.setHidden(true);
		TextInputButton textInput = new TextInputButton(0,0, 0.8f, 0.3f, new Vector4f(1.0f, 1.0f, 1.0f, 0.5f), textBox, () -> {});
		textInput.sethCentered(true);
		textInput.setvCentered(true);
		textInput.setDivisions(5);
		textInput.setDefaultText("Enter system name: ");
		textInput.setText(new Text(0,0,font, new Vector4f(1.0f, 0.769f, 0.863f, 1.0f), 1, textBox, 0.2f, 0));
		textInput.resize();
		textInput.setNumericOnly(false);
		textBox.addButton(textInput);
		
		Button cancel = new Button((float)(textInput.getxPos()-textBox.getxPos())/(float)textBox.getWidth(), 0.7f, 0.3f, 0.15f, new Vector4f(1.0f, 1.0f, 1.0f, 0.5f), textBox, 
				() -> textBox.setHidden(true)
				);
		cancel.setText(new Text(0,0,font, new Vector4f(1.0f, 0.769f, 0.863f, 1.0f), 1, textBox, 0.2f, 0));
		cancel.setTextToButton(true);
		cancel.setDefaultText("cancel");
		cancel.centerText(true);
		cancel.resize();
		textBox.addButton(cancel);
		
		saveLoad = new Button((float)(textInput.getxPos()-textBox.getxPos())/(float)textBox.getWidth(), 0.7f, 0.3f, 0.15f, new Vector4f(1.0f, 1.0f, 1.0f, 0.5f), textBox, () -> {});
		saveLoad.setText(new Text(0,0,font, new Vector4f(1.0f, 0.769f, 0.863f, 1.0f), 1, textBox, 0.2f, 0));
		saveLoad.setTextToButton(true);
		saveLoad.setDefaultText(" ");
		saveLoad.fromhEdge(true);
		saveLoad.centerText(true);
		saveLoad.resize();
		saveLoad.setClickEvent(() -> {
			if(saveLoad.getText().getText().contentEquals("save")) {
				String system = textBox.getTextInputButtons().get(0).getEnteredText();
				if(system.length() > 0) {
					SaveSystem.saveSystem(system, planetList);
				}
			}
			else {
				int status = loadSystem(planetList);
				hideTextBox(status == 0 ? true : false);
				SimulationSpeed.setPauseStatus(true);
			}
		});
		textBox.addButton(saveLoad);
		
		panels.add(textBox);
	}
	
	private void setupSystemSavePanel(Window window) {
		loadSave = new Panel(0f, 0f, 1f, 0.05f, new Vector4f(0.584f, 0f, 1.0f, 0.8f), window);
		loadSave.setHidden(false);
		save = new Button(0.017f,0.1f,0.05f,0.8f, new Vector4f(1.0f, 1.0f, 1.0f, 0.5f), loadSave, 
				() -> {	
					setSaveLoadText("save");
					textBox.setHidden(false);
				});
		save.setvCentered(true);
		save.setDefaultText("Save");
		save.setText(new Text(0.1f,0.9f,font, new Vector4f(1.0f, 0.769f, 0.863f, 1.0f), 32, loadSave, 0f, 0f)); 
		load = new Button(save);
		load.setClickEvent(() -> 
		{
			setSaveLoadText("load");
			textBox.setHidden(false);
		}
				);
		load.setText(new Text(0.1f,0.9f,font, new Vector4f(1.0f, 0.769f, 0.863f, 1.0f), 32, loadSave, 0f, 0f));
		load.setvCentered(true);
		load.setDefaultText("Load");
		load.setCopyButton(save);
		load.relativePos(true);
		load.relativeDimensions(true);
		load.setxProp(0.067f);
		load.setyProp(0);
		load.setWidthProp(0);
		load.setHeightProp(0);
		load.centerText(true);
		save.centerText(true);
		load.setTextToButton(true);
		save.setTextToButton(true);
		load.setTextPos();
		save.setTextPos();
		save.resize();
		load.resize();
		loadSave.addButton(save);
		loadSave.addButton(load);
		panels.add(loadSave);
	}
	
	private void setupSpeedPanel(Window window) {
		speed = new Panel(0.85f, 0.05f, 0.15f, 0.15f, new Vector4f(0.584f, 0f, 1.0f, 0.6f), window);
		speed.setHidden(false);
		speedText = new Text(0.1f,0.9f,font, new Vector4f(1.0f, 0.769f, 0.863f, 1.0f), 32, speed, 0.05f, 0f);
		playPause = new Button(0,0.2f,0.2f,0.4f, new Vector4f(1.0f, 1.0f, 1.0f, 0.5f), speed, () -> {SimulationSpeed.togglePause();});
		playPause.sethCentered(true);
		playPause.centerText(true);
		playPause.setSquare(true);
		playPause.setTextToButton(true);
		Text playPauseText = new Text(speedText);
		playPause.setText(playPauseText);
		playPause.setDefaultText(">");
		playPause.resize();
		speed.addButton(playPause);
		
		fastForward = new Button(0.1f, 0.2f, 0.2f, 0.4f, new Vector4f(1.0f, 1.0f, 1.0f, 0.5f), speed, () -> 
		{
			SimulationSpeed.setSpeed(SimulationSpeed.getSpeed() + 0.1f);
			setSpeed(SimulationSpeed.getSpeed());
		}
		);
		fastForward.setSquare(true);
		fastForward.fromhEdge(true);
		Text ffText = new Text(speedText);
		fastForward.setText(ffText);
		fastForward.setDefaultText(">>");
		fastForward.setTextToButton(true);
		fastForward.centerText(true);
		fastForward.resize();
		speed.addButton(fastForward);
		
		slowDown = new Button(0.1f, 0.2f, 0.2f, 0.4f, new Vector4f(1.0f, 1.0f, 1.0f, 0.5f), speed, () -> {
			SimulationSpeed.setSpeed(SimulationSpeed.getSpeed() - 0.1f); setSpeed(SimulationSpeed.getSpeed());
			});
		slowDown.setSquare(true);
		Text ssText = new Text(speedText);
		slowDown.setText(ssText);
		slowDown.setDefaultText("<<");
		slowDown.setTextToButton(true);
		slowDown.centerText(true);
		slowDown.resize();
		
		speedText.setText("Simulation Speed: " + Float.toString(speedVal));
		speedText.setDivisions(1);
		speedText.resize();
		speed.addText(speedText);
		speed.addButton(slowDown);
		panels.add(speed);
	}
	
	private void setupAddRemovePanel(Window window) {
		addRemove = new Panel(0.85f, 0.92f, 0.15f, 0.08f, new Vector4f(0.584f, 0f, 1.0f, 0.6f), window);
		addRemove.setHidden(false);
		
		removeAdd = new Button(0,0,0,0, new Vector4f(1.0f, 1.0f, 1.0f, 0.5f), addRemove, () -> {});
		addRemoveText = new Text(0,0,font, new Vector4f(1.0f, 0.769f, 0.863f, 1.0f), 1, addRemove, 0.2f, 0);
		
		Button remove = new Button(removeAdd);
		remove.setxProp(0.6f);
		remove.setWidthProp(0.3f);
		remove.setyProp(0.25f);
		remove.setHeightProp(0.3f);
		Text txt1 = new Text(addRemoveText);
		txt1.setText(" ");
		remove.setText(txt1);
		remove.setDefaultText("Remove");
		remove.setTextPos();
		remove.setvCentered(true);
		remove.centerText(true);
		remove.resize();
		remove.setClickEvent(() -> {
			planetBehaviour.remove();
		});
		addRemove.addButton(remove);
		
		Button add = new Button(remove);
		add.setCopyButton(remove);
		add.relativePos(false);
		add.relativeDimensions(true);
		add.setxProp(0.1f);
		add.setyProp(0.1f);
		add.setWidthProp(0);
		add.setHeightProp(0);
		Text txt2 = new Text(addRemoveText);
		txt2.setText(" ");
		add.setText(txt2);
		add.setDefaultText("Add");
		add.setTextPos();
		add.setvCentered(true);
		add.centerText(true);
		add.setClickEvent(() -> {
			planetBehaviour.addNewPlanet();
		});
		add.setTextToButton(true);
		add.resize();
		addRemove.addButton(add);

		panels.add(addRemove);
	}
		
	private void setupPlanetInfo(Window window, String[] planetInfoArray, char[] planetButtonArray) {
		planetInfo = new Panel(0f, 0.05f, 0.15f, 0.95f, new Vector4f(0.584f, 0f, 1.0f, 0.6f), window);
		planetInfo.setHidden(true);
		infoButton = new TextInputButton(0,0, 0, 0, new Vector4f(1.0f, 1.0f, 1.0f, 0.5f), planetInfo, () -> {});
		infoText = new Text(0,0,font, new Vector4f(1.0f, 0.769f, 0.863f, 1.0f), 1, planetInfo, 0.2f, 0);

		//Add button descriptors next to button
		for(int i = 0; i < planetInfoArray.length; i++) {
			Text temp = new Text(infoText);
			temp.setxProp(0.1f);
			temp.setyProp((float) (i+0.3) / 9f);
			temp.sethProp(0f);
			temp.setDivisions(24);
			temp.setText(planetInfoArray[i]);
			planetInfo.addText(temp);
		}
		
		// Place planet attribute buttons next to text
		for(int i = 0; i < planetButtonArray.length; i++) {
			int k = (i > 7) ? 7 : i;
			TextInputButton tempButton = new TextInputButton(infoButton);
			if(i < 8) {
				tempButton.setxProp(planetInfo.getText().get(k).getxProp());
				tempButton.setyProp(planetInfo.getText().get(k).getyProp() + 0.02f); //+ (float) mass.getSize()/ (float) planetInfo.getHeight());
				tempButton.setWidthProp(0.7f);
				tempButton.setHeightProp(0.1f);
				if(i == 4)
					tempButton.setClickEvent(() -> {});
			}
			else {
				//Place colour buttons beneath the first one
				tempButton.setCopyButton(planetInfo.getButtons().get(7));
				tempButton.relativeDimensions(true);
				tempButton.relativePos(true);
				tempButton.setyProp(0.05f*(i-7));
			}
			
			Text tmp1 = new Text(infoText);
			tempButton.setDivisions(24);
			tmp1.setText("test");
			tempButton.setText(tmp1);
			tempButton.setNumericOnly(true);
			tempButton.resize();
			
			//Change the property descriptor text so it matches the names of the fields of the body class - used to set values later
			String planetAttribute = planetInfoArray[k].substring(0, planetInfoArray[k].length() - 2).toLowerCase();
			
			//Set callback events - change properties of planet
			if(i != 4 && i < 7) {
				tempButton.setClickEvent(() -> 
				{
					String value = tempButton.getEnteredText();
					value = sanitiseInputText(value);
					float val = Float.parseFloat(value);
					Planet selectedPlanet = planetBehaviour.getSelectedPlanet();
					Class<?> body = (selectedPlanet.getClass()).getSuperclass();
					try {
						Field field = body.getDeclaredField(planetAttribute);
						field.setAccessible(true);
						field.set(selectedPlanet, val == -1 ? field.get(selectedPlanet) :  val);
					} catch (NoSuchFieldException e) {
						if(planetAttribute.charAt(0) == 's') {
							selectedPlanet.getAppItem().setScale(val == -1 ? selectedPlanet.getAppItem().getScale() : val);
						}
						else {
							e.printStackTrace();
						}
						
					} catch (SecurityException | IllegalAccessException e) {
						e.printStackTrace();
					}	
				});
			}
			
			//Change the colour of the planet - it's ugly, but no other way to do it other than changing body and appitem!
			switch(i) {
			case 7:
				tempButton.setClickEvent(() -> {
					String value = tempButton.getEnteredText();
					value = sanitiseInputText(value);
					float val = Float.parseFloat(value);
					Planet selectedPlanet = planetBehaviour.getSelectedPlanet();
					selectedPlanet.getAppItem().setRed(val == -1 ? selectedPlanet.getAppItem().getRed() : val);
				});
				break;
			case 8:
				tempButton.setClickEvent(() -> {
					String value = tempButton.getEnteredText();
					value = sanitiseInputText(value);
					float val = Float.parseFloat(value);
					Planet selectedPlanet = planetBehaviour.getSelectedPlanet();
					selectedPlanet.getAppItem().setGreen(val == -1 ? selectedPlanet.getAppItem().getGreen() : val);
				});
				break;
			case 9:
				tempButton.setClickEvent(() -> {
					String value = tempButton.getEnteredText();
					value = sanitiseInputText(value);
					float val = Float.parseFloat(value);
					Planet selectedPlanet = planetBehaviour.getSelectedPlanet();
					selectedPlanet.getAppItem().setBlue(val == -1 ? selectedPlanet.getAppItem().getBlue() : val);
				});
				break;
			}
			
			planetInfo.addButton(tempButton);
		}
		panels.add(planetInfo);
	}
	
	private String sanitiseInputText(String value) {
		//If no text is input, revert to original value
		if(value.isEmpty())
			value = "-1";
		else if(value.charAt(value.length()-1) == '.') {
			value = value + '0';
		}
		else if(value.contentEquals(" "))
			value = "0";
		return value;
	}
	
	public void setPlanetInfo(Planet p) {
		
		//All of the planet's properties are displayed on their respective buttons
		
		String[] planArray = {
				Double.toString((double) Math.round(p.getMass()*10000)/10000),
				Double.toString((double) Math.round(p.getEccentricity()*10000)/10000),
				Double.toString((double) Math.round(p.getAphelion()*10000)/10000),
				Double.toString((double) Math.round(p.getPerihelion()*10000)/10000),
				Double.toString((double) Math.round(p.getVelocity()*10000)/10000),
				Double.toString((double) Math.round(p.getAppItem().getScale()*10000)/10000),
				Double.toString((double) Math.round(Math.toDegrees(p.getAngle())*10000)/10000),
				Double.toString((double) Math.round(p.getAppItem().getColour().x*10000)/10000),
				Double.toString((double) Math.round(p.getAppItem().getColour().y*10000)/10000),
				Double.toString((double) Math.round(p.getAppItem().getColour().z*10000)/10000)
		};
		
		for(int i = 0; i < planArray.length; i++) {
			Button b = planetInfo.getButtons().get(i);
			b.setDefaultText(planArray[i]);
			b.setTextPos();
		}
		
	}
	
	public void render(Window window) {
		//Every item of text and every button is held in a panel, and the render method of each panel renders all of its items as well
		for(int i=0; i < panels.size(); i++) {
			if(!panels.get(i).isHidden())
				panels.get(i).render(window);
		}
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_STENCIL_TEST);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_BLEND);		
	}
	
	public boolean checkMouseCoords(int mouseX, int mouseY) {
		//Checks if the mouse is on a panel
		for(Panel p : panels) {
			if(mouseX >= p.getxPos() && mouseX <= p.getxPos() + p.getWidth() && mouseY >= p.getyPos() && mouseY <= p.getyPos() + p.getHeight() && !p.isHidden()) {
				return true;
			}
		}
		return false;
	}
	

	
	public ArrayList<Panel> getPanels() {
		return panels;
	}
	
	public void resize(Window window) {
		for(Panel p : panels) {
			p.resize(window);
		}
		
	}
	
	public void setSelectedButton(Button b) {
		selectedButton = b;
	}
	
	public Button getSelectedButton() {
		return selectedButton;
	}
	
	public void setSpeed(float f) {
		speedVal = f;
		speedText.setText("Simulation Speed: " + Float.toString((float) Math.round(f*10)/10));
	}
	
	public void hideTextBox(boolean b) {
		textBox.setHidden(b);
	}
	
	public void setSaveLoadText(String s) {
		saveLoad.setDefaultText(s);
		saveLoad.setTextPos();
	}
	
	public void saveSystem(ArrayList<Planet> pList) {
		String system = textBox.getTextInputButtons().get(0).getEnteredText();
		if(system.length() > 0) {
			SaveSystem.saveSystem(system, pList);
		}
	}
	
	public int loadSystem(ArrayList<Planet> pList) {
		//Status message - if -1, then then load failed, so do not exit the load window
		String system = textBox.getTextInputButtons().get(0).getEnteredText();
		if(system.length() > 0) {
			return LoadSystem.loadSystem(system, pList, pList.get(0).getAppItem().getMesh());
		}
		return -1;
	}
	
	public void hidePlanetInfo() {
		planetInfo.setHidden(true);
	}
	
	public void drawEllipse(float xAxis, float yAxis, float cx, float cy, float angle) {
		float sm = (float) (Math.sqrt(xAxis*xAxis - yAxis*yAxis));
		GL11.glEnable(GL11.GL_LINE);
		NanoVG.nvgBeginPath(vg);
		NanoVG.nvgTranslate(vg, cx + sm, cy);
		NanoVG.nvgRotate(vg, -angle);
		NanoVG.nvgTranslate(vg, -(cx+(float)Math.cos(-angle)*sm), -cy+sm*(float)Math.sin(-angle));
		
		NanoVG.nvgStrokeColor(vg, VgColour);
		NanoVG.nvgStrokeWidth(vg, 5);
		NanoVG.nvgEllipse(vg, cx, cy, xAxis, yAxis);
		
		NanoVG.nvgStroke(vg);
		
		NanoVG.nvgRotate(vg, angle);
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_STENCIL_TEST);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_BLEND);	
	}
		
}
