package jjwork.tools;


public class ButtonTimeout {
	private static final long TIMEOUT_MS = 500;
	
	private static long lastTime;
	public static boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();
		if(time - lastTime < TIMEOUT_MS) {
			return true;
		}
		lastTime = time;
		return false;
	}
}
