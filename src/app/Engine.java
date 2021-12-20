package app;

import org.lwjgl.Version;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;

import input.Camera;
import input.MouseInput;
import input.Timer;
import opengl.Window;

public class Engine implements Runnable {
	
	private final Window window;
	private final AppInterface app;
	
	private final Timer timer;
	private final MouseInput mouseInput;
	private final Camera camera;
	
	private float passedTime;
	private float totalTime;
	private float interval = 1/60f;
	
	
	public Engine(String windowTitle, int width, int height, boolean vSync, AppInterface app) {
		window = new Window(windowTitle, width, height, vSync);
		this.app = app;
		
		timer = new Timer();
		mouseInput = new MouseInput();
		camera = new Camera();
		camera.setPosition(0, 0, 400);
	}
	
	public void init() throws Exception {
		app.init(window, camera, mouseInput);
	}
	
	@Override
	public void run() {
		try {
			init();
			loop();
		}
		catch(Exception excp) {
			System.err.println(excp);
			System.exit(-1);
		}
		finally {
			cleanup();
			GLFW.glfwTerminate();
			GLFW.glfwSetErrorCallback(null).free();
		}
		
	}
	
	public void loop() throws Exception {
		timer.init();
		while(!GLFW.glfwWindowShouldClose(window.getWindowId()))
		{
			input();
			
			GLFW.glfwSwapBuffers(window.getWindowId());
			passedTime = timer.getElapsedTime();
			
			totalTime += passedTime;
			
			while(totalTime >= interval) {
				totalTime -= interval;
				update(interval, camera);
			}
			render();
			//Checks if anything is going on
			GLFW.glfwPollEvents();
		}
        //Let go of callbacks and destroy window after loop
  		Callbacks.glfwFreeCallbacks(window.getWindowId());
  		GLFW.glfwDestroyWindow(window.getWindowId());
	}
	
	public void input() {
		app.input();
	}
	
	public void render() throws Exception {
		app.render();
	}
	
	public void update(float interval, Camera camera) {
		app.update(interval);
	}
	
	public void cleanup() {
		app.cleanup();
	}
	
}
