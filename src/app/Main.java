package app;

public class Main {
	
	public static void main(String[] args) {
		OrbitApp app = new OrbitApp();
		Engine engine = new Engine("Elliptical Orbit Simulator", 720, 480, true, app);
		engine.run();
	}

}