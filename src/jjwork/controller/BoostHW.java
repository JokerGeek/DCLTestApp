package jjwork.controller;

public class BoostHW {
	public final static int SLAVE = 1;
	public final static int OPEN_ADDR = 1;
	public final static int CLOSE_ADDR = 2;
	public final static int SET_ADDR = 0;
	public final static int GET_ADDR = 0;
	public final static int GET_LEN = 5;
	
	
	public int[] args = new int[10];
	public float voltage;
	public float current;
	public boolean isOpen;
	public String warning;

	public enum TestMode {
		workTest, smallObjTest, bigObjTest, volFulTest, highVolTest, lowVolTest,
	}

	private BoostHW() {
	}

	private static BoostHW boostHardwave = new BoostHW();

	public static BoostHW getInstance() {
		return boostHardwave;
	}
}
