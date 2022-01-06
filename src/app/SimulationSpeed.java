package app;

public class SimulationSpeed {
	
	private static boolean paused = false;
	private static float speed = 1f;
	
	public static boolean isPaused() {
		return paused;
	}
	
	public static void setPauseStatus(boolean b) {
		paused = b;
	}
	
	public static void togglePause() {
		paused = !paused;
	}
	
	public static float getSpeed() {
		return speed;
	}
	
	public static void setSpeed(float s) {
		speed = s;
	}

}
