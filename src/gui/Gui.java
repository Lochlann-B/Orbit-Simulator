package gui;

import org.lwjgl.nanovg.NanoVGGL3;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NanoVG;
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import app.Planet;
import maths.Vector4f;
import opengl.Window;
import util.LoadSystem;
import util.SaveSystem;


public class Gui {
	
	private long vg;
	private int font;
	
	private String inputString = "";
	private char inputChar;
	
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
	
	private float speedVal;
	
	public Gui() {
	}
	
	public void init(Window window) throws Exception {
		panels = new ArrayList<Panel>();
		
		this.vg = NanoVGGL3.nvgCreate(NanoVGGL3.NVG_ANTIALIAS | NanoVGGL3.NVG_STENCIL_STROKES);
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
		
		setupPlanetInfo(window, planetInfoArray, planetButtonArray);
		setupAddRemovePanel(window);
		setupSpeedPanel(window);
		setupSystemSavePanel(window);
		setupTextBoxPanel(window);
		
	}
	
	private void setupTextBoxPanel(Window window) {
		textBox = new Panel(0.4f, 0.4f, 0.2f, 0.2f, new Vector4f(0.584f, 0f, 1.0f, 0.8f), vg, window);
		textBox.setHidden(true);
		Button textInput = new Button(0,0, 0.8f, 0.3f, new Vector4f(1.0f, 1.0f, 1.0f, 0.5f), vg, textBox);
		textInput.sethCentered(true);
		textInput.setvCentered(true);
		textInput.setEnterTex(true);
		textInput.setDivisions(5);
		textInput.setDefaultText("Enter system name: ");
		textInput.setText(new Text(0,0,font, new Vector4f(1.0f, 0.769f, 0.863f, 1.0f), 1, vg, textBox, 0.2f, 0));
		textInput.resize();
		textInput.setNumericOnly(false);
		textBox.addButton(textInput);
		
		Button cancel = new Button((float)(textInput.getxPos()-textBox.getxPos())/(float)textBox.getWidth(), 0.7f, 0.3f, 0.15f, new Vector4f(1.0f, 1.0f, 1.0f, 0.5f), vg, textBox);
		cancel.setText(new Text(0,0,font, new Vector4f(1.0f, 0.769f, 0.863f, 1.0f), 1, vg, textBox, 0.2f, 0));
		cancel.setTextToButton(true);
		cancel.setDefaultText("cancel");
		cancel.centerText(true);
		cancel.resize();
		cancel.setPurpose('c');
		textBox.addButton(cancel);
		
		saveLoad = new Button((float)(textInput.getxPos()-textBox.getxPos())/(float)textBox.getWidth(), 0.7f, 0.3f, 0.15f, new Vector4f(1.0f, 1.0f, 1.0f, 0.5f), vg, textBox);
		saveLoad.setText(new Text(0,0,font, new Vector4f(1.0f, 0.769f, 0.863f, 1.0f), 1, vg, textBox, 0.2f, 0));
		saveLoad.setTextToButton(true);
		saveLoad.setDefaultText(" ");
		saveLoad.fromhEdge(true);
		saveLoad.centerText(true);
		saveLoad.resize();
		textBox.addButton(saveLoad);
		
		panels.add(textBox);
	}
	
	private void setupSystemSavePanel(Window window) {
		loadSave = new Panel(0f, 0f, 1f, 0.05f, new Vector4f(0.584f, 0f, 1.0f, 0.8f), vg, window);
		loadSave.setHidden(false);
		save = new Button(0.017f,0.1f,0.05f,0.8f, new Vector4f(1.0f, 1.0f, 1.0f, 0.5f), vg, loadSave);
		save.setvCentered(true);
		save.setDefaultText("Save");
		save.setPurpose('k');
		save.setText(new Text(0.1f,0.9f,font, new Vector4f(1.0f, 0.769f, 0.863f, 1.0f), 32, vg, loadSave, 0f, 0f));
		load = new Button(save);
		load.setText(new Text(0.1f,0.9f,font, new Vector4f(1.0f, 0.769f, 0.863f, 1.0f), 32, vg, loadSave, 0f, 0f));
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
		load.setPurpose('l');
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
		speed = new Panel(0.85f, 0.05f, 0.15f, 0.15f, new Vector4f(0.584f, 0f, 1.0f, 0.6f), vg, window);
		speed.setHidden(false);
		speedText = new Text(0.1f,0.9f,font, new Vector4f(1.0f, 0.769f, 0.863f, 1.0f), 32, vg, speed, 0.05f, 0f);
		playPause = new Button(0,0.2f,0.2f,0.4f, new Vector4f(1.0f, 1.0f, 1.0f, 0.5f), vg, speed);
		playPause.sethCentered(true);
		playPause.centerText(true);
		playPause.setSquare(true);
		playPause.setTextToButton(true);
		Text playPauseText = new Text(speedText);
		playPause.setText(playPauseText);
		playPause.setDefaultText(">");
		playPause.setPurpose(' ');
		playPause.resize();
		speed.addButton(playPause);
		
		fastForward = new Button(0.1f, 0.2f, 0.2f, 0.4f, new Vector4f(1.0f, 1.0f, 1.0f, 0.5f), vg, speed);
		fastForward.setSquare(true);
		fastForward.fromhEdge(true);
		Text ffText = new Text(speedText);
		fastForward.setText(ffText);
		fastForward.setDefaultText(">>");
		fastForward.setTextToButton(true);
		fastForward.centerText(true);
		fastForward.setPurpose('f');
		fastForward.resize();
		speed.addButton(fastForward);
		
		slowDown = new Button(0.1f, 0.2f, 0.2f, 0.4f, new Vector4f(1.0f, 1.0f, 1.0f, 0.5f), vg, speed);
		slowDown.setSquare(true);
		Text ssText = new Text(speedText);
		slowDown.setText(ssText);
		slowDown.setDefaultText("<<");
		slowDown.setTextToButton(true);
		slowDown.centerText(true);
		slowDown.setPurpose('s');
		slowDown.resize();
		
		speedText.setText("Simulation speed: " + Float.toString(speedVal));
		speedText.setDivisions(1);
		speedText.resize();
		speed.addText(speedText);
		speed.addButton(slowDown);
		panels.add(speed);
	}
	
	private void setupAddRemovePanel(Window window) {
		addRemove = new Panel(0.85f, 0.92f, 0.15f, 0.08f, new Vector4f(0.584f, 0f, 1.0f, 0.6f), vg, window);
		addRemove.setHidden(false);
		
		removeAdd = new Button(0,0,0,0, new Vector4f(1.0f, 1.0f, 1.0f, 0.5f), vg, addRemove);
		addRemoveText = new Text(0,0,font, new Vector4f(1.0f, 0.769f, 0.863f, 1.0f), 1, vg, addRemove, 0.2f, 0);
		
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
		remove.setPurpose('w');
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
		add.setPurpose('q');
		
		add.setTextToButton(true);
		add.resize();
		addRemove.addButton(add);
		

		panels.add(addRemove);
	}
	
	private void setupPlanetInfo(Window window, String[] planetInfoArray, char[] planetButtonArray) {
		planetInfo = new Panel(0f, 0.05f, 0.15f, 0.95f, new Vector4f(0.584f, 0f, 1.0f, 0.6f), vg, window);
		planetInfo.setHidden(true);
		infoButton = new Button(0,0, 0, 0, new Vector4f(1.0f, 1.0f, 1.0f, 0.5f), vg, planetInfo);
		infoText = new Text(0,0,font, new Vector4f(1.0f, 0.769f, 0.863f, 1.0f), 1, vg, planetInfo, 0.2f, 0);
		
		for(int i = 0; i < planetInfoArray.length; i++) {
			Text temp = new Text(infoText);
			temp.setxProp(0.1f);
			temp.setyProp((float) (i+0.3) / 9f);
			temp.sethProp(0f);
			temp.setDivisions(24);
			temp.setText(planetInfoArray[i]);
			planetInfo.addText(temp);
		}
		
		for(int i = 0; i < planetButtonArray.length; i++) {
			int k = (i > 7) ? 7 : i;
			Button tempButton = new Button(infoButton);
			if(i < 8) {
				tempButton.setxProp(planetInfo.getText().get(k).getxProp());
				tempButton.setyProp(planetInfo.getText().get(k).getyProp() + 0.02f); //+ (float) mass.getSize()/ (float) planetInfo.getHeight());
				tempButton.setWidthProp(0.7f);
				tempButton.setHeightProp(0.1f);
			}
			else {
				tempButton.setCopyButton(planetInfo.getButtons().get(7));
				tempButton.relativeDimensions(true);
				tempButton.relativePos(true);
				tempButton.setyProp(0.05f*(i-7));
			}
			
			Text tmp1 = new Text(infoText);
			tempButton.setDivisions(24);
			tmp1.setText("test");
			tempButton.setText(tmp1);
			if(i != 4)
				tempButton.setEnterTex(true);
			tempButton.setPurpose(planetButtonArray[i]);
			tempButton.setNumericOnly(true);
			tempButton.resize();
			planetInfo.addButton(tempButton);
		}
		
		
		panels.add(planetInfo);
	}
	
	public void setPlanetInfo(Planet p) {
		
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
		for(Panel p : panels) {
			if(mouseX >= p.getxPos() && mouseX <= p.getxPos() + p.getWidth() && mouseY >= p.getyPos() && mouseY <= p.getyPos() + p.getHeight() && !p.isHidden()) {
				return true;
			}
		}
		return false;
	}
	
	String numeric = "1234567890.";
	String file = "/:.*?<>|\\";
	boolean containsDecimal;
	
	public void setString(char key) {
		inputChar = key;
		inputString = inputString + key;
		if(selectedButton != null && selectedButton.enterText()) {
			if(selectedButton.numericOnly() && stringContains(numeric, key))
				selectedButton.updateText(inputChar);
			else if (!selectedButton.numericOnly() && !stringContains(file, key)){
				selectedButton.updateText(inputChar);
			}
		}
	}
	
	public void removeChar() {
		inputString = selectedButton.getEnteredText();
		if(inputString.length() > 0)
			inputString = inputString.substring(0, inputString.length()-1);
		if(selectedButton != null && selectedButton.enterText())
			selectedButton.setEnteredText(inputString);
	}
	
	private boolean stringContains(String s, char c) {

		for(int i = 0; i < s.length(); i++) {
			if(s.charAt(i) == c && (!(containsDecimal && c == '.') )) {
				if(c == '.')
					containsDecimal = true;
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
	
	public void setContainsDecimal(boolean b) {
		containsDecimal = b;
	}
	
	public void updateValues(Planet selectedPlanet) {
		char p = selectedButton.getPurpose();
		String value = selectedButton.getEnteredText();
		if(selectedPlanet != null) {
			if(selectedButton.numericOnly()) {
				if(value.isEmpty())
					value = "-1";
				else if(value.charAt(value.length()-1) == '.') {
					value = value + '0';
				}
				else if(value.contentEquals(" "))
					value = "0";
				
			}
			double val = Double.parseDouble(value);
			switch(p) {
				case(' '):
					break;
				case('m'):
					selectedPlanet.setMass(val == -1 ? selectedPlanet.getMass() : val);
					break;
				case('e'):
					selectedPlanet.setEccentricity((float) val == -1 ? selectedPlanet.getEccentricity() : ((float) val >= 0.9f ? 0.9f : (float) val));
					break;
				case('p'):
					selectedPlanet.setPerihelion(val == -1 ? selectedPlanet.getPerihelion() : (val <= 0 ? 10 :  val));
					break;
				case('a'):
					selectedPlanet.setAphelion(val == -1 ? selectedPlanet.getAphelion() : (val <= 0 ? 10 :  val));
					break;
				case('c'):
					selectedPlanet.getAppItem().setScale((float) (val == -1 ? selectedPlanet.getAppItem().getScale() : val));
					break;
				case('z'):
					selectedPlanet.setAngle(Math.toRadians((val == -1 ? Math.toDegrees(selectedPlanet.getAngle()) : val)));
					break;
				case('r'):
					selectedPlanet.getAppItem().setColour((float) (val == -1 ? selectedPlanet.getAppItem().getColour().x : val), selectedPlanet.getAppItem().getColour().y, selectedPlanet.getAppItem().getColour().z);
					break;
				case('g'):
					selectedPlanet.getAppItem().setColour(selectedPlanet.getAppItem().getColour().x, (float) (val == -1 ? selectedPlanet.getAppItem().getColour().y : val), selectedPlanet.getAppItem().getColour().z);
					break;
				case('b'):
					selectedPlanet.getAppItem().setColour(selectedPlanet.getAppItem().getColour().x, selectedPlanet.getAppItem().getColour().y, (float) (val == -1 ? selectedPlanet.getAppItem().getColour().z : val));
					break;
			}
		}
		inputString= "";
	}
	
	public void setSpeed(float f) {
		speedVal = f;
		speedText.setText("Simulation Speed: " + Float.toString((float) Math.round(f*10)/10));
	}
	
	public void hideTextBox(boolean b) {
		textBox.setHidden(b);
	}
	
	public void setSaveLoadText(String s, char p) {
		saveLoad.setDefaultText(s);
		saveLoad.setTextPos();
		saveLoad.setPurpose(p);
	}
	
	public void saveSystem(ArrayList<Planet> pList) {
		String system = textBox.getButtons().get(0).getEnteredText();
		if(system.length() > 0) {
			SaveSystem.saveSystem(system, pList);
		}
	}
	
	public int loadSystem(ArrayList<Planet> pList) {
		String system = textBox.getButtons().get(0).getEnteredText();
		if(system.length() > 0) {
			return LoadSystem.loadSystem(system, pList, pList.get(0).getAppItem().getMesh());
		}
		return -1;
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
