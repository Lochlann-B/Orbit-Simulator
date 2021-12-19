package input;

import maths.Vector2f;
import org.lwjgl.glfw.*;

import gui.Gui;
import opengl.Window;

public class MouseInput {
	
	private Vector2f previousPos;
	private Vector2f currentPos;
	private Vector2f displacement;
	
	private boolean middlePressed = false;
	private boolean leftPressed = false;
	private boolean middleReleased = false;
	private double scrollOffsetCurr = 0;
	private double scrollDisp = 0;
	
	private char characterKey = '.';
	
	public MouseInput() {
		previousPos = new Vector2f(-1,-1);
		currentPos = new Vector2f();
		displacement = new Vector2f();
	}
	
	public void init(Window window, Gui gui) {
		GLFW.glfwSetCursorPosCallback(window.getWindowId(), (windowHandle, xPos, yPos) -> {currentPos.x = (float) xPos; currentPos.y = (float) yPos;});
		//GLFW.glfwSetCursorEnterCallback(window.getWindowId(), (windowHandle, entered) -> {inWindow = entered;});
		GLFW.glfwSetScrollCallback(window.getWindowId(), (windowHandle, xOffset, yOffset) -> {scrollOffsetCurr = yOffset;});
		GLFW.glfwSetMouseButtonCallback(window.getWindowId(), (windowHandle, button, action, mode) -> {leftPressed = button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS;
																									   middlePressed = button == GLFW.GLFW_MOUSE_BUTTON_3 && action == GLFW.GLFW_PRESS; 
																									   middleReleased = button == GLFW.GLFW_MOUSE_BUTTON_3 && action == GLFW.GLFW_RELEASE;});
	}
	
	public void input(Window window) {
		
		displacement.x = 0;
		displacement.y = 0;
		double deltax = 0;
		double deltay = 0;
		if(previousPos.x >= 0 && previousPos.y >= 0) {
			deltax = currentPos.x - previousPos.x;
			deltay = currentPos.y - previousPos.y;
		}
		previousPos.x = currentPos.x;
		previousPos.y = currentPos.y;
		displacement.x = (float) deltax;
		displacement.y = (float) deltay;
	}
	
	public void scrollInput() {
		scrollDisp = scrollOffsetCurr;
		scrollOffsetCurr = 0;
	}
	
	public void setPrev() {
		previousPos.x = currentPos.x;
		previousPos.y = currentPos.y;
	}
	
	public boolean middleButtonPressed() {
		return middlePressed;
	}
	
	public boolean middleButtonReleased() {
		return middleReleased;
	}
	
	public Vector2f getDisplacement() {
		return displacement;
	}
	
	public Vector2f getCurrentPos() {
		return currentPos;
	}
	
	public double getScrollOffset() {
		return scrollDisp;
	}
	
	public boolean leftButtonPressed() {
		if(leftPressed) {
			leftPressed = false;
			return true;
		}
		return false;
	}
	
	public char getCharKey() {
		return characterKey;
	}
	
	
	
}
