package jjwork.controller;

public class InverterHW {
	public final static int SLAVE = 1;
	public final static int OPEN_ADDR = 1;
	public final static int CLOSE_ADDR = 2;
	public final static int SET_ADDR = 0;
	public final static int GET_ADDR = 0;
	public final static int GET_LEN = 4;
	
	public int[] args = new int[10];
	public boolean isOpen;
	public float outputWork;

	private InverterHW() {
	}

	private static InverterHW inverterHardwave = new InverterHW();

	public static InverterHW getInstance() {
		return inverterHardwave;
	}
}
