package input;

public class Timer {
	
	public double currentTime;
	public double prevTime;
	public double elapsedTime;
	
	public Timer() {}
	
	public void init() {
		prevTime = getTime();
	}
	
	public float getElapsedTime() {
		currentTime = getTime();
		elapsedTime = currentTime - prevTime;
		prevTime = currentTime;
		return (float)elapsedTime;
	}
	
	public double getTime() {
		return (double)System.nanoTime() / 1000000000;
	}
	
}
