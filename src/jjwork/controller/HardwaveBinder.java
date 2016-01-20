package jjwork.controller;

import java.util.LinkedList;

import jjwork.modbus.*;
import android.os.Binder;
import android.os.Handler;
import android.os.Message;

public class HardwaveBinder extends Binder {
	BoostHW boost = BoostHW.getInstance();
	InverterHW inverter = InverterHW.getInstance();	
	
	Handler mbHandler;
	LinkedList<HandlerCallback>  handlerCallbacks;
	public HardwaveBinder(Handler handler, LinkedList<HandlerCallback> cbs) {
		mbHandler = handler;
		handlerCallbacks = cbs;
	}
	public void openBoost() {
		handlerCallbacks.offer(new HandlerCallback(null, null));
		byte[] openData = {0,1};
		
		sendMessage(new ModbusParams(
				boost.SLAVE, 
				Function.write_single_register, 
				boost.OPEN_ADDR, 
				openData));

	}

	public void closeBoost() {
		handlerCallbacks.offer(new HandlerCallback(null, null));
		byte[] closeData = {0,1};
		
		sendMessage(new ModbusParams(
				boost.SLAVE, 
				Function.write_single_register, 
				boost.CLOSE_ADDR, 
				closeData));

	}

	public void getBoostParam(OnUICallback cb) {
		handlerCallbacks.offer(new HandlerCallback(cb, new DataTrans() {				
			@Override
			public Object translation(byte[] data) {
				for(int i = 0; i < data.length/2; i++)
					boost.args[i] = (data[i * 2 + 1] & 0xff) | ((data[i * 2] << 8) & 0xff00);
				return (Object) boost;
			}
		}));
		
		sendMessage(new ModbusParams(
				boost.SLAVE, 
				Function.read_holding_registers, 
				boost.GET_ADDR, 
				boost.GET_LEN));
	}

	public void setBoostParam(int arg1, int arg2, int arg3, OnUICallback cb) {
		handlerCallbacks.offer(new HandlerCallback(cb, null));
		
		byte[] data = new byte[6];
		data[0] = (byte)((arg1 >> 8) & 0xff);
		data[1] = (byte)(arg1 & 0xff);
		data[2] = (byte)((arg2 >> 8) & 0xff);
		data[3] = (byte)(arg2 & 0xff);
		data[4] = (byte)((arg3 >> 8) & 0xff);
		data[5] = (byte)(arg3 & 0xff);
		
		sendMessage(new ModbusParams(
				boost.SLAVE, 
				Function.write_multiple_registers, 
				boost.SET_ADDR, 
				data));
	}
	public void setBoostParam(int arg1, int arg2, int arg3) {
		setBoostParam(arg1, arg2, arg3, null);
	}

	public void openInverter() {
	}

	public void closeInverter() {
	}

	public void getInverterParam(OnUICallback cb) {
		handlerCallbacks.offer(new HandlerCallback(cb, new DataTrans() {				
			@Override
			public Object translation(byte[] data) {
				for(int i = 0; i < data.length/2; i++)
					inverter.args[i] = (data[i * 2 + 1] & 0xff) | ((data[i * 2] << 8) & 0xff00);
				return (Object) inverter;
			}
		}));
		
		sendMessage(new ModbusParams(
				inverter.SLAVE, 
				Function.read_holding_registers, 
				inverter.GET_ADDR, 
				inverter.GET_LEN));
	}

	public void setInverterParam(int arg1, int arg2, OnUICallback cb) {
		handlerCallbacks.offer(new HandlerCallback(cb, null));
		
		byte[] data = new byte[4];
		data[0] = (byte)((arg1 >> 8) & 0xff);
		data[1] = (byte)(arg1 & 0xff);
		data[2] = (byte)((arg2 >> 8) & 0xff);
		data[3] = (byte)(arg2 & 0xff);
		
		sendMessage(new ModbusParams(
				inverter.SLAVE, 
				Function.write_multiple_registers, 
				inverter.SET_ADDR, 
				data));
	}		
	public void setInverterParam(int arg1, int arg2) {
		setInverterParam(arg1, arg2, null);
	}

	private void sendMessage(Object obj){
		Message msg = mbHandler.obtainMessage();
		msg.obj = obj;
		mbHandler.sendMessage(msg);
	}
	
	public class HandlerCallback{
		OnUICallback cb = null;
		DataTrans trans = null;
		public HandlerCallback(OnUICallback cb, DataTrans trans) {
			this.cb = cb;
			this.trans = trans;
		}
		public void handlerCb(byte[] data){
			if(cb == null || trans == null) return;
			cb.callback(trans.translation(data));
		}
	}
	interface DataTrans{
		Object translation(byte[] data);
	}
	public interface OnUICallback{
		void callback(Object obj);
	}
}
