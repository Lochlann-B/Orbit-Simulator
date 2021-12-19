package gui;

import org.lwjgl.nanovg.*;

import maths.Vector4f;

public class Text {
	
	private String string;
	private int font;
	private int size = 1;
	private int xPos;
	private int yPos;
	private long vg;
	private Vector4f colour;
	private NVGColor VgColour;
	private Panel parent;
	private int divisions = 1;
	
	private float horizontalPaddingProportion = 0;
	private float verticalPaddingProportion;
	private float xProp;
	private float yProp;
	
	//Init method - set up font
	public Text(int xPos, int yPos, int font, Vector4f colour, int size, long context) {
		this.font = font;
		this.size = size;
		vg = context;
		VgColour = NVGColor.create();
		VgColour.r(colour.x);
		VgColour.g(colour.y);
		VgColour.b(colour.z);
		VgColour.a(colour.w);
		string = " ";
		this.xPos = xPos;
		this.yPos = yPos;
	}
	
	public Text(float xProp, float yProp, int font, Vector4f colour, int size, long context, Panel parent, float hProp, float vProp) {
		this.xProp = xProp;
		this.yProp = yProp;
		this.parent = parent;
		xPos = (int) (xProp*parent.getWidth() + parent.getxPos());
		yPos = (int) (yProp*parent.getHeight() + parent.getyPos());
		horizontalPaddingProportion = hProp;
		verticalPaddingProportion = vProp;
		this.font = font;
		vg = context;
		VgColour = NVGColor.create();
		VgColour.r(colour.x);
		VgColour.g(colour.y);
		VgColour.b(colour.z);
		VgColour.a(colour.w);
		string = " ";
	}
	
	public Text(Text t) {
		font = t.getFont();
		size = t.getSize();
		VgColour = t.getVgColour();
		vg = t.getContext();
		string = "";
		xPos = t.getPos()[0];
		xPos = t.getPos()[1];
		parent = t.getParent();
	}
	
	//Method for setting/getting text
	public void setText(String s) {
		string = s;
		resize();
	}
	
	public String getText() {
		return string;
	}
	
	//Method for size of text
	public void setSize(int size) {
		this.size = size;
	}
	
	public void resize() {
		size = Math.min((int) (2*(parent.getWidth() - parent.getWidth()*horizontalPaddingProportion)/string.length()), parent.getHeight()/divisions);
		xPos = (int) (xProp*parent.getWidth() + parent.getxPos());
		yPos = (int) (yProp*parent.getHeight() + parent.getyPos());
	}
	
	public int getSize() {
		return size;
	}
	
	//Method for colour of text
	public void setColour(Vector4f col) {
		colour = col;
		VgColour.r(colour.x);
		VgColour.g(colour.y);
		VgColour.b(colour.z);
		VgColour.a(colour.w);
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
	
	//Method for drawing text
	public void drawText() {
		
		NanoVG.nvgFontFaceId(vg, font);
		NanoVG.nvgFillColor(vg, VgColour);
		NanoVG.nvgFontSize(vg, size);
		NanoVG.nvgText(vg, xPos, yPos, string);
	}
	
	public void setPos(int xPos, int yPos) {
		this.xPos = xPos;
		this.yPos = yPos;
	}
	
	public int[] getPos() {
		return new int[]{xPos, yPos};
	}
	
	public int getFont() {
		return font;
	}
	
	public NVGColor getVgColour() {
		return VgColour;
	}
	
	public long getContext() {
		return vg;
	}
	
	public void setxProp(float x) {
		xProp = x;
	}
	
	public void setyProp(float y) {
		yProp = y;
	}
	
	public float getxProp() {
		return xProp;
	}
	
	public float getyProp() {
		return yProp;
	}
	
	public void sethProp(float h) {
		horizontalPaddingProportion = h;
	}
	
	public void setParent(Panel p) {
		parent = p;
	}
	
	public Panel getParent() {
		return parent;
	}
	
	public void setDivisions(int num) {
		divisions = num;
	}
	
		//Resize method
}
