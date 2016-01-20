package jjwork.modbus;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class ModbusMasterThread extends Thread {
	private final static String TAG = "Modbus Thread";
	public final static int EXECUTE_SUCCESS = 1;
	public final static int EXECUTE_FALISE = 0;
	private Handler handler;
	private Master master;
	
	public ModbusMasterThread(final Handler serviceHandler){
		master = Master.getInstance();
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				try {
					ModbusParams pag = (ModbusParams) msg.obj;
					byte[] data = null;
					if(pag.data == null)
						data = master.execute(pag.slave, pag.funcode, pag.startAddress, pag.quantity);
					else 
						data = master.execute(pag.slave, pag.funcode, pag.startAddress, pag.data.length, pag.data);

					serviceHandler.sendMessage(serviceHandler.obtainMessage(EXECUTE_SUCCESS, data));

				} catch (Exception ex) {
					Log.d(TAG, ex.toString());
					serviceHandler.sendEmptyMessage(EXECUTE_FALISE);
				}
			}
		};
	}
	@Override
	public void interrupt() {
		master.close();
		super.interrupt();
	}	
	@Override
	public void run() {
		Looper.prepare();
		Looper.loop();
		super.run();
	}

	public Handler getHandler() {
		return handler;
	}
}