package app;

import input.Camera;
import input.MouseInput;
import opengl.Window;

public interface AppInterface {
	void cleanup();
	void input();
	void init(Window window, Camera camera, MouseInput mouseInput) throws Exception;
	void render() throws Exception;
	void update(float interval);
}
