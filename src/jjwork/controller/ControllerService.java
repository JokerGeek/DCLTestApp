package jjwork.controller;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import jjwork.modbus.Function;
import jjwork.modbus.ModbusService;
import jjwork.modbus.Master.OnModbusRecvDataListener;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

public class ControllerService {
	private final String TAG = "Modbus ctrl";
	

	public interface OnCtrHWUpdateListener {
		void onDataUpdate(ControllerService hw);
	}
	private boolean serviceBinding = false;

	interface RunWhenBind {
		void fun();
	}

	private ModbusService.MbBinder mbBinder = null;
	ArrayList<RunWhenBind> bindRunings = new ArrayList<RunWhenBind>();
	ServiceConnection connection = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mbBinder = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.d(TAG, "onServiceConnected");
			mbBinder = (ModbusService.MbBinder) service;
			serviceBinding = false;
			if (!bindRunings.isEmpty()) {
				for (RunWhenBind run : bindRunings)
					run.fun();
				bindRunings.clear();
			}
		}
	};

	public void bind(Context packageContext) {
		serviceBinding = true;
		packageContext.bindService(new Intent(packageContext,
				ModbusService.class), connection, Context.BIND_AUTO_CREATE);
	}

	public void unbind(Context packageContext) {
		packageContext.unbindService(connection);
	}
	public void startService(Context packageContext){
		packageContext.startService(new Intent(packageContext, ModbusService.class));
	}
	public void stopService(Context packageContext) {
		packageContext.stopService(new Intent(packageContext,
				ModbusService.class));
	}

	public int[] args = new int[10];
	private static ControllerService myHW = new ControllerService();

	private Queue<OnCtrHWUpdateListener> hwListeners = new LinkedList<OnCtrHWUpdateListener>();

	private ControllerService() {
	}

	public static ControllerService getInstance() {
		return myHW;
	}

	public void setGetA3Listener(OnCtrHWUpdateListener listener) {
		if (serviceBinding == false && mbBinder == null) // 没绑定
			return;
		hwListeners.offer(listener);
		if (serviceBinding) {
			bindRunings.add(new RunWhenBind() {				
				@Override
				public void fun() {
					mbBinder.setOnModbusRecvData(getA3listener);
					mbBinder.execute(1, Function.read_holding_registers, 0, 5);
				}
			});
		} else {
			mbBinder.setOnModbusRecvData(getA3listener);
			mbBinder.execute(1, Function.read_holding_registers, 0, 5);
		}
	}

	private OnModbusRecvDataListener getA3listener = new OnModbusRecvDataListener() {
		@Override
		public void modbusRecvDataListener(byte[] retDatas) {
			for (int i = 0; i < 5; i++)
				args[i] = (retDatas[i * 2 + 1] & 0xff)
						| ((retDatas[i * 2] << 8) & 0xff00);
			if (!hwListeners.isEmpty())
				hwListeners.poll().onDataUpdate(myHW);
		}
	};

	public void setGetA4Listener(OnCtrHWUpdateListener listener) {
		if (serviceBinding == false && mbBinder == null) // 没绑定
			return;
		hwListeners.offer(listener);
		if (serviceBinding) {
			bindRunings.add(new RunWhenBind() {				
				@Override
				public void fun() {
					mbBinder.setOnModbusRecvData(getA4listener);
					mbBinder.execute(1, Function.read_holding_registers, 5, 5);
				}
			});
		} else {
			mbBinder.setOnModbusRecvData(getA4listener);
			mbBinder.execute(1, Function.read_holding_registers, 5, 5);
		}
	}

	private OnModbusRecvDataListener getA4listener = new OnModbusRecvDataListener() {
		@Override
		public void modbusRecvDataListener(byte[] retDatas) {
			for (int i = 0; i < 5; i++)
				args[i + 5] = (retDatas[i * 2 + 1] & 0xff)
						| ((retDatas[i * 2] << 8) & 0xff00);
			if (!hwListeners.isEmpty())
				hwListeners.poll().onDataUpdate(myHW);
		}
	};
}
