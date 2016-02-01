package jjwork.modbus;

import java.util.LinkedList;

import jjwork.tools.TestRecordService;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class ModbusMasterThread extends Thread {
	private final static String TAG = "Modbus Thread";
	public final static int EXECUTE_SUCCESS = 1;
	public final static int EXECUTE_FALISE = 0;
	
	private Handler handler, serviceHandler;
	private Master master;
	private LinkedList<ModbusParams> params = new LinkedList<ModbusParams>();
	private boolean isRun = false;
	
	
	public ModbusMasterThread(Handler serviceHandler){
		this.serviceHandler = serviceHandler;
		master = new Master(1, 19200, 2000);
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				ModbusParams pag = (ModbusParams) msg.obj;
				params.offer(pag);
			}
		};
	}
	@Override
	public synchronized void start() {
		isRun = true;
		super.start();
	}
	@Override
	public void interrupt() {
		isRun = false;		
		super.interrupt();
		master.close();
		params.clear();
	}	
	@Override
	public void run() {
		while(isRun){
			if(!params.isEmpty()){
				ModbusParams pag = params.poll();
				try {
					byte[] data = null;
					if(pag.data == null)
						data = master.execute(pag.slave, pag.funcode, pag.startAddress, pag.quantity);
					else 
						data = master.execute(pag.slave, pag.funcode, pag.startAddress, pag.data);
		
					serviceHandler.sendMessage(serviceHandler.obtainMessage(EXECUTE_SUCCESS, data));
		
				} catch (Exception ex) {
					Log.d(TAG, ex.toString());
					TestRecordService.recordError(master.getSendData(), ex, master.getRecvData());
					
					serviceHandler.sendEmptyMessage(EXECUTE_FALISE);
				}
			}
		}
	}

	public Handler getHandler() {
		return handler;
	}
	public void setBaudRate(int baud){
		master.setBaudRate(baud);
	}
}