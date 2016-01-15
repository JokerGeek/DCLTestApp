package jjwork.controller;

import java.util.LinkedList;

import jjwork.modbus.Function;
import jjwork.modbus.ModbusMasterThread;
import jjwork.modbus.ModbusParams;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class HardwaveService extends Service {
	HWBinder hwbinder = new HWBinder();
	ModbusMasterThread mbThread;
	Handler serviceHandler, mbHandler;
	LinkedList<HandlerCallback> handlerCallbacks = new LinkedList<HandlerCallback>();

	final String TAG = "Modbus HW Service";
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "oncreate");

		serviceHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if(msg.what == ModbusMasterThread.EXECUTE_FALISE) return;
				if(handlerCallbacks.isEmpty()) return;
				HandlerCallback cb = handlerCallbacks.poll();
				cb.handlerCb((byte[])msg.obj);
			}
		};
		mbThread = new ModbusMasterThread(serviceHandler);
		mbHandler = mbThread.getHandler();
		mbThread.start();
	}

	@Override
	public void onDestroy() {
		mbThread.interrupt();
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return hwbinder;
	}

	public class HWBinder extends Binder {
		
		
		
		public void openBoost() {

		}

		public void closeBoost() {

		}

		public void getBoostParam(OnUICallback cb) {
			handlerCallbacks.offer(new HandlerCallback(cb, new DataTrans() {				
				@Override
				public Object translation(byte[] data) {
					BoostHW boost = BoostHW.getInstance();
					for(int i = 0; i < 5; i++)
						boost.args[i] = (data[i * 2 + 1] & 0xff) | ((data[i * 2] << 8) & 0xff00);
					return (Object) boost;
				}
			}));
			Message msg = mbHandler.obtainMessage();
			msg.obj = new ModbusParams(1, Function.read_holding_registers, 0, 5);
			mbHandler.sendMessage(msg);
		}

		public void setBoostParam(int arg1, int arg2, OnUICallback cb) {
			handlerCallbacks.offer(new HandlerCallback(cb, null));
			byte[] data = new byte[20];
			for(int i = 0; i < 10; i++)
				data[i*2+1] = (byte) i;
			data[1] = (byte) arg1;
			data[3] = (byte) arg2;
			Message msg = mbHandler.obtainMessage();
			msg.obj = new ModbusParams(1, Function.write_multiple_registers, 0, data);
			mbHandler.sendMessage(msg);
		}

		public void openInverter() {

		}

		public void closeInverter() {

		}

		public void getInverterParam() {

		}

		public void setInverterParam(int arg1, int arg2) {

		}		

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
