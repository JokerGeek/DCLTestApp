package jjwork.controller;

import android.util.Log;

public class InverterHW {
	public class IHWState{
		public boolean isOpen;
		public float outputWork;
		public IHWState(boolean isOpen, float outputWork) { this.isOpen = isOpen; this.outputWork = outputWork; }
	}
	public void open(){ }
	public void close(){ }
	public void setInputVoltage(int vol){}
	public IHWState getState() { return new IHWState(false, 2000); }
	

	private InverterHW(){}	
	private static InverterHW inverterHardwave = new InverterHW();
	public static InverterHW getInstance() { return inverterHardwave; }
}
