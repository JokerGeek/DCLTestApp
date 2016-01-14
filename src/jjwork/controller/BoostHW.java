package jjwork.controller;

import android.util.Log;

public class BoostHW {
	public class BHWState
	{
		public float voltage;
		public float current;
		public boolean isOpen;
		public String warning;
		public BHWState(boolean isOpen, float vol, float cur, String warn){ this.isOpen = isOpen; voltage = vol; current = cur; warning = warn; }
	}
	public enum TestMode {
		workTest, smallObjTest, bigObjTest, volFulTest, highVolTest, lowVolTest,
	}
	public void open(){ Log.d("HW", "boost open"); }
	public void close(){ Log.d("HW", "boost close"); }
	public void setParam(TestMode testMode, int work) {Log.d("HW", "boost set");}
	public BHWState getState(){
		Log.d("HW", "boost get state");
		return new BHWState(false, 220, 5, "");
	}
	
	private BoostHW(){}	
	private static BoostHW boostHardwave = new BoostHW();
	public static BoostHW getInstance() { return boostHardwave; }
}
