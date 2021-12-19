package gui;

import java.util.ArrayList;

import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.opengl.*;

import maths.Vector4f;
import opengl.Window;

public class Panel {

	private ArrayList<Button> buttons;
	private ArrayList<Text> textBoxes;
	private ArrayList<Panel> subPanels;
	
	private boolean hidden = true;
	
	private int xPos;
	private int yPos;
	private int width;
	private int height;
	
	private float widthProportion;
	private float heightProportion;
	private float xOffsetProportion;
	private float yOffsetProportion;
	
	private long vg;
	
	private Vector4f colour;
	
	private NVGColor VgColour;
	
	
	//Constructor (xPos, yPos, colour)
	public Panel(float xProportion, float yProportion, float widthProportion, float heightProportion, Vector4f colour, long context, Window window) {
		xOffsetProportion = xProportion;
		yOffsetProportion = yProportion;
		this.widthProportion = widthProportion;
		this.heightProportion = heightProportion;
		this.xPos = (int) (xProportion*window.getWidth());
		this.yPos = (int) (yProportion*window.getHeight());
		this.width = (int) (widthProportion*window.getWidth());
		this.height = (int) (heightProportion*window.getHeight());
		this.colour = colour;
		vg = context;
		VgColour = NVGColor.create();
		VgColour.r(colour.x);
		VgColour.g(colour.y);
		VgColour.b(colour.z);
		VgColour.a(colour.w);
		buttons = new ArrayList<Button>();
		textBoxes = new ArrayList<Text>();
	}
	
	//Method for getting/setting colour
	public void setColour(Vector4f col) {
		colour = col;
	}
	
	public void setColour(float r, float g, float b, float a) {
		colour.x = r;
		colour.y = g;
		colour.z = b;
		colour.w = a;
		VgColour.r(colour.x);
		VgColour.g(colour.y);
		VgColour.b(colour.z);
		VgColour.a(colour.w);
	}
	
	public Vector4f getColour() {
		return colour;
	}
	
	//Method for adding/removing buttons, text boxes, panels
	public void addButton(Button b) {
		buttons.add(b);
	}
	public void addText(Text t) {
		textBoxes.add(t);
	}
	
	//Method for setting/getting hidden status
	public boolean isHidden() {
		return hidden;
	}
	
	public void setHidden(boolean b) {
		hidden = b;
	}
	
	//Method for rendering panel
	//Method for accessing all buttons and text and rendering them
	public void render(Window window) {
		
		NanoVG.nvgBeginFrame(vg, window.getWidth(), window.getHeight(), 1);
		NanoVG.nvgBeginPath(vg);
		NanoVG.nvgRect(vg, xPos, yPos, width, height);
		NanoVG.nvgFillColor(vg, VgColour);
		NanoVG.nvgFill(vg);
		NanoVG.nvgEndFrame(vg);
		
		NanoVG.nvgEndFrame(vg);
		
		for(int i = 0; i < textBoxes.size(); i++) {
			textBoxes.get(i).drawText();
		}
		
		for(int i = 0; i < buttons.size(); i++) {
			buttons.get(i).render(window);
		}
	}
	
	public int getxPos() {
		return xPos;
	}
	public int getyPos() {
		return yPos;
	}
	public int getWidth() {
		return width;
	}
	public int getHeight() {
		return height;
	}
	
	public ArrayList<Button> getButtons() {
		return buttons;
	}
	
	public ArrayList<Text> getText() {
		return textBoxes;
	}
	
	//Resize method (called when window is resized) - change all buttons and text as well
	public void resize(Window window) {
		xPos = (int) (window.getWidth()*xOffsetProportion);
		yPos = (int) (window.getHeight()*yOffsetProportion);
		width = (int) (window.getWidth()*widthProportion);
		height = (int) (window.getHeight()*heightProportion);
		for(int i = 0; i < textBoxes.size(); i++) {
			textBoxes.get(i).resize();
		}
		
		for(int i = 0; i < buttons.size(); i++) {
			buttons.get(i).resize();
		}
	}
	
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}
	public void setPosition(int xPos, int yPos) {
		this.xPos = xPos;
		this.yPos = yPos;
	}
}
