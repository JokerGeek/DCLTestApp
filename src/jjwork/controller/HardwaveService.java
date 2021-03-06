package jjwork.controller;

import java.util.LinkedList;

import jjwork.controller.HardwaveBinder.HandlerCallback;
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
	HardwaveBinder hwbinder;
	ModbusMasterThread mbThread;
	Handler serviceHandler, mbHandler;
	LinkedList<HandlerCallback> handlerCallbacks = new LinkedList<HandlerCallback>();

	final String TAG = "Modbus HW Service";
	
	@Override
	public void onCreate() {
		super.onCreate();

		serviceHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if(handlerCallbacks.isEmpty()) return;
				HandlerCallback cb = handlerCallbacks.poll();
				if(msg.what == ModbusMasterThread.EXECUTE_FALISE) return;
				cb.handlerCb((byte[])msg.obj);
			}
		};
		mbThread = new ModbusMasterThread(serviceHandler);
		mbThread.start();
		
		mbHandler = mbThread.getHandler();
		
		hwbinder = new HardwaveBinder(mbHandler, handlerCallbacks);
		
		Log.d(TAG, "oncreate");
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

}
