package input;

import org.lwjgl.glfw.GLFW;

import gui.Gui;
import gui.TextInputButton;
import opengl.Window;

public class KeyboardInput {
	
	private char inputKey = ' ';
	private boolean enter = false;
	private boolean enterPressed = false;
	
	public KeyboardInput() {}
	
	public void init(Window window, Gui gui) {
		GLFW.glfwSetCharCallback(window.getWindowId(), (windowHandle, key) -> {
			
			if(key != 32 && gui.getSelectedButton() != null && gui.getSelectedButton().getClass() == TextInputButton.class) {
				System.out.println("le");
				((TextInputButton) gui.getSelectedButton()).setString((char) key);
			}
			});
		GLFW.glfwSetKeyCallback(window.getWindowId(), (windowHandle, key, scancode, action, mods) -> {if(key == 259 && action > 0 
				&& gui.getSelectedButton() != null && gui.getSelectedButton().getClass() == TextInputButton.class) 
			((TextInputButton) gui.getSelectedButton()).removeChar();
		});
		
	}
	
	public boolean getEnterPressed() {
		return enter;
	}
	
}
