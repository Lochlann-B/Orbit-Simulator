package gui;

import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NanoVG;

import maths.Vector4f;
import opengl.Window;



public class Button {
	
	protected ClickEvent event;
	
	protected int xPos;
	protected int yPos;
	protected int width;
	protected int height;
	
	protected float xProp;
	protected float yProp;
	protected float widthProp;
	protected float heightProp;
	protected int vPadding = 0;
	protected int hPadding = 0;
	protected Panel parent;
	protected int divisions = 1;
	protected boolean vCentered = false;
	protected boolean hCentered = false;
	protected boolean textToButton = false;
	protected boolean centerText = false;
	protected boolean square = false;
	protected boolean sethPropFromEdge = false;
	protected boolean setvPropFromEdge = false;
	protected boolean relativePos = true;
	protected boolean relativeDimensions = true;
	
	protected String defaultText = " ";
	
	protected Button copyButton;
	
	protected Text text;
	
	protected boolean isPressed;
	
	protected Vector4f colour;
	
	protected NVGColor VgColour;
	
	public Button(float xProp, float yProp, float widthProp, float heightProp, Vector4f colour, Panel parent, ClickEvent e) {
		this.xProp = xProp;
		this.yProp = yProp;
		this.widthProp = widthProp;
		this.heightProp = heightProp;
		this.parent = parent;
		width = (int) (parent.getWidth()*widthProp);
		height = (int) (parent.getHeight()*heightProp);
		VgColour = NVGColor.create();
		VgColour.r(colour.x);
		VgColour.g(colour.y);
		VgColour.b(colour.z);
		VgColour.a(colour.w);
		event = e;
	}
	
	public Button(Button b) {
		xPos = b.getxPos();
		yPos = b.getyPos();
		width = b.getWidth();
		height = b.getHeight();
		VgColour = b.getVgColour();
		parent = b.getParent();
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
	
	public void resize() {
		if(!sethPropFromEdge)
			xPos = (int) (parent.getxPos() + xProp*parent.getWidth());
		if(!setvPropFromEdge)
			yPos = (int) (parent.getyPos() + yProp*parent.getHeight());
		

		
		if(copyButton != null) {
			if(relativeDimensions) {
				height = (int) (copyButton.getHeight() + heightProp*parent.getHeight());
				width = (int) (copyButton.getWidth() + widthProp*parent.getWidth());
			}
			if(relativePos) {
				xPos = (int) (copyButton.getxPos() + xProp*parent.getWidth());
				yPos = (int) (copyButton.getyPos() + yProp*parent.getHeight());
			}
		}
		else {
			width = (int) (widthProp*parent.getWidth());
			height = (int) (heightProp*parent.getHeight());
			if(square) {
				width = Math.min(width, height);
				height = width;
			}
		}
		
		if(sethPropFromEdge)
			xPos = (int) (parent.getxPos() + parent.getWidth()*(1-xProp) - width);
		
		if(setvPropFromEdge)
			yPos = (int) (parent.getyPos() + parent.getHeight()*(1-yProp) - height);
		
		if(hCentered) {
			xPos = (int) (parent.getxPos() + parent.getWidth()/2 - width/2);
			
		}
		if(vCentered && text == null) {
			yPos = (int) (parent.getyPos() + parent.getHeight()/2 - height/2);
		}
		if(text != null) {
			setTextPos();
		}
	}
	
	public void setText(Text t) {
		text = t;
	}
	
	public void setTextPos() {
		String string = defaultText;
		text.setText(string.length() == 0 ? " " : string);

		if(!textToButton) {
			text.setSize(Math.min(2*width/text.getText().length() - 1, parent.getHeight()/divisions));
			height = text.getSize();
		}
		else {
			text.setSize((int) Math.min(height, (float)1.5f*width/(float)(text.getText().length())));
		}
		
		if(vCentered) {
			yPos = parent.getyPos() + parent.getHeight()/2 - height/2;
		}
		if(centerText) {
			text.setPos(xPos + width/2 - 2*text.getSize()*text.getText().length()/9, yPos + height/2 + (text.getSize())/4);

		}
		else {
			text.setPos(xPos + width/16, yPos + 3*text.getSize()/4);
		}
	}
	
	public void setClickEvent(ClickEvent e) {
		event = e;
	}
	
	//Method for checking whether the button has been clicked (mouse click && mouse coords are within button) - called when glfw callback
	public void checkClicked(int mouseX, int mouseY) {
		
		if(mouseX >= xPos && mouseX <= xPos + width && mouseY >= yPos && mouseY <= yPos + height) {
			//Change this into something less cringe please
			isPressed = true;
			event.clickEvent();
			
		} else {
			isPressed = false;
			
		}
	}
	
	public void render(Window window) {
		if(text != null) {
			text.drawText();;
		}
		long vg = Gui.vg;
		NanoVG.nvgBeginFrame(vg, window.getWidth(), window.getHeight(), 1);
		NanoVG.nvgBeginPath(vg);
		NanoVG.nvgRect(vg, xPos, yPos, width, height);
		NanoVG.nvgFillColor(vg, VgColour);
		NanoVG.nvgFill(vg);
		NanoVG.nvgEndFrame(vg);
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
	public Vector4f getColour() {
		return colour;
	}
	public NVGColor getVgColour() {
		return VgColour;
	}
	public Text getText() {
		return text;
	}
	
	public void setxProp(float x) {
		xProp = x;
	}
	
	public void setyProp(float y) {
		yProp = y;
	}
	
	public void setWidthProp(float w) {
		widthProp = w;
	}
	
	public void setHeightProp(float h) {
		heightProp = h;
	}
	
	public Panel getParent() {
		return parent;
	}
	
	public void updateSize() {
		width = text.getSize()/2 * text.getText().length() + 4;
		height = text.getSize()/2 + 8;
	}
	
	public void setPosition(int x, int y) {
		xPos = x;
		yPos = y;
	}
	
	public int gethPadding() {
		return hPadding;
	}
	
	public int getvPadding() {
		return vPadding;
	}
	
	public void sethPadding(int h) {
		hPadding = h;
	}
	
	public void setvPadding(int v) {
		vPadding = v;
	}
	
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public void setDivisions(int div) {
		divisions = div;
	}
	
	public void setvCentered(boolean b) {
		vCentered = b;
	}
	
	public void sethCentered(boolean b) {
		hCentered = b;
	}
	
	public void setTextToButton(boolean b) {
		textToButton = b;
	}
	
	public void setCopyButton(Button b) {
		copyButton = b;
	}
	
	public boolean isPressed() {
		return isPressed;
	}
	
	public void setDefaultText(String s) {
		defaultText = s;
	}
	
	public String getDefaultText() { 
		return defaultText;
	}
	
	public void setSquare(boolean b) {
		square = b;
	}
	
	public void fromhEdge(boolean b) {
		sethPropFromEdge = b;
	}
	
	public void fromvEdge(boolean b) {
		setvPropFromEdge = b;
	}
	
	public void centerText(boolean b) {
		centerText = b;
	}
	
	public void relativePos(boolean b) {
		relativePos = b;
	}
	
	public void relativeDimensions(boolean b) {
		relativeDimensions = b;
	}
	
	public float getxProp() {
		return xProp;
	}
	
	public float getyProp() {
		return yProp;
	}
	
	public float getwidthProp() {
		return widthProp;
	}
	
	public float getheightProp() {
		return heightProp;
	}
	
}
