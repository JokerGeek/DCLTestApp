package jjwork.modbus;

import java.util.LinkedList;
import java.util.Queue;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import jjwork.modbus.Master.*;
import jjwork.modbus.*;

public class ModbusService extends Service {
	MbBinder modbusBinder = new MbBinder();
	Master master = Master.getInstance();
	Handler masterHandler, serviceHandler;
	Queue<OnModbusRecvDataListener> modbusListeners = new LinkedList<Master.OnModbusRecvDataListener>();
	MasterThread thread;
	final String TAG = "ModbusService";

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "oncreate");
		
		
		try {
			serviceHandler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					if (modbusListeners.isEmpty())
						return;
					OnModbusRecvDataListener listener = modbusListeners.poll();
					if (msg.obj != null)
						listener.modbusRecvDataListener((byte[]) msg.obj);
				}
			};
			thread = new MasterThread();
			thread.start();
		} catch (Exception ex) {
			Log.d(TAG, ex.toString());
		}
	}

	@Override
	public void onDestroy() {
		thread.interrupt();
		master.close();
		Log.d(TAG, "ondestroy");
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return modbusBinder;
	}

	public class MbBinder extends Binder {
		public void execute(int slave, Function funCode, int startAddress,
				int quantity) {
			if (masterHandler != null) {
				Message masterMsg = masterHandler.obtainMessage();
				masterMsg.what = slave;
				masterMsg.obj = funCode;
				masterMsg.arg1 = startAddress;
				masterMsg.arg2 = quantity;
				masterHandler.sendMessage(masterMsg);
			}
		}

		public void setOnModbusRecvData(OnModbusRecvDataListener listener) {
			modbusListeners.offer(listener);
		}
	}

	class MasterThread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();

			this.setName("ModbusMasterThread");

			Looper.prepare();

			masterHandler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					try {
						byte[] data = master.execute(msg.what,
								(Function) msg.obj, msg.arg1, msg.arg2);

						Message serviceMsg = serviceHandler.obtainMessage();
						serviceMsg.obj = data;
						serviceHandler.sendMessage(serviceMsg);

					} catch (Exception ex) {
						Log.d(TAG, ex.toString());
						serviceHandler.sendEmptyMessage(0);
					}
				}
			};

			Looper.loop();
		}
	}
}
