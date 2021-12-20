package opengl;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.glfw.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
	
	private int width;
	private int height;
	private String title;
	private long windowId;
	private boolean resize;
	private boolean vSync;
	
	public Window(String title, int width, int height, boolean vsync) {
		this.title = title;
		this.width = width;
		this.height = height;
		vSync = vsync;
	}
	
	public void init() {
		GLFWErrorCallback.createPrint(System.err).set();
		
		GLFW.glfwInit();
		if(!GLFW.glfwInit())
			throw new IllegalStateException("Unable to initialise GLFW!");
		
		GLFW.glfwDefaultWindowHints();
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GL11.GL_FALSE);
		
		windowId = GLFW.glfwCreateWindow(width, height, title, NULL, NULL);
		if(windowId == NULL)
			throw new RuntimeException("GLFW window creation failed!");
		
		GLFWVidMode mode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
		GLFW.glfwSetWindowPos(windowId, (mode.width()-width)/2, (mode.height() - height)/2);
		GLFW.glfwMakeContextCurrent(windowId);
		
		GL.createCapabilities();
		
		GLFW.glfwSetInputMode(windowId, GLFW.GLFW_STICKY_MOUSE_BUTTONS, 0);
		
		if(vSync)
			GLFW.glfwSwapInterval(1);
		
		GLFW.glfwShowWindow(windowId);
		
		//Set callback for window resizing (this window's width, height, resize bool <- the callback's width, height when w/h changes)
		GLFW.glfwSetFramebufferSizeCallback(windowId, (windowId, width, height) -> {this.width = width < 360 ? 360 : width; this.height = height < 360 ? 360 : height; this.resize = true;});
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_STENCIL_TEST);
		//GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK,  GL11.GL_LINE);
		
	}
	
	public boolean getKeyPress(int keyId) {
		boolean isPressed = GLFW.glfwGetKey(windowId, keyId) == GLFW.GLFW_PRESS;
		return isPressed;
	}
	
	public boolean getResizeStatus() {
		if(width <= 360)
			GLFW.glfwSetWindowSize(windowId, 360, height);
		if(height <= 360)
			GLFW.glfwSetWindowSize(windowId, width, 360);
		return resize;
	}
	
	public void setResizeStatus(boolean bool) {
		resize = bool;
	}
	
	public long getWindowId() {
		return windowId;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public void setClearColour(float r, float g, float b, float a) {
		GL11.glClearColor(r, g, b, a);
	}
	
}
