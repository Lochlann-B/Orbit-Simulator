package gui;

import maths.Vector4f;

public class TextInputButton extends Button {
	
	private String enteredText = "";
	private boolean numericOnly = true;

	public TextInputButton(float xProp, float yProp, float widthProp, float heightProp, Vector4f colour, Panel parent,
			ClickEvent e) {
		super(xProp, yProp, widthProp, heightProp, colour, parent, e);
	}

	public TextInputButton(Button b) {
		super(b);
	}
	
	//Method for checking whether the button has been clicked (mouse click && mouse coords are within button) - called when glfw callback
	public void checkClicked(int mouseX, int mouseY) {
		
		if(mouseX >= xPos && mouseX <= xPos + width && mouseY >= yPos && mouseY <= yPos + height) {
			//Change this into something less cringe please
			isPressed = true;
			enteredText = "";
			setTextPos();
		} else {
			isPressed = false;
		}
	}
	
	public void setTextPos() {
		String string = isPressed ? enteredText : defaultText;
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
	
	String numeric = "1234567890.";
	String file = "/:.*?<>|\\";
	boolean containsDecimal;
	private String inputString;
	
	public void setString(char key) {
		char inputChar = key;
		inputString = inputString + key;
		if(isPressed) {
			if(numericOnly && stringContains(numeric, key))
				updateText(inputChar);
			else if (!numericOnly() && !stringContains(file, key)){
				updateText(inputChar);
			}
		}
	}
	
	public void removeChar() {
		if(isPressed) {
			inputString = enteredText;
			if(inputString.length() > 0)
				inputString = inputString.substring(0, inputString.length()-1);
			enteredText = inputString;
		}
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
	
	public void doClickEvent() {
		event.clickEvent();
	}
	
	public void updateText(char character) {
		enteredText = enteredText + character;
		setTextPos();
	}
	
	public void setEnteredText(String s) {
		enteredText = s;
		setTextPos();
	}
	
	public String getEnteredText() {
		return enteredText;
	}
	
	public void setContainsDecimal(boolean b) {
		containsDecimal = b;
	}
	
	public boolean numericOnly() {
		return numericOnly;
	}
	
	public void setNumericOnly(boolean b) {
		numericOnly = b;
	}
	

}
