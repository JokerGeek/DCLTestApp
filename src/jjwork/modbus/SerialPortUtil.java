package jjwork.modbus;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.util.Log;

public class SerialPortUtil {
	private String TAG = "Modbus Serial";
	private SerialPort mSerialPort;
	private OutputStream mOutputStream;
	private InputStream mInputStream;
	private ReadThread mReadThread;
	private String path = "/dev/ttySAC1";
	private int baudrate = 115200;
	private static SerialPortUtil portUtil = null;
	private OnDataReceiveListener onDataReceiveListener = null;
	private boolean isStop = false;
	private int readTimeout = 10;

	private final int readDataInterval = 10;
	private byte[] recvBuffer = new byte[512];
	private int recvIndex = 0;

	public interface OnDataReceiveListener {
		public void onDataReceive(byte[] buffer, int size);
	}

	public void setOnDataReceiveListener(
			OnDataReceiveListener dataReceiveListener) {
		onDataReceiveListener = dataReceiveListener;
	}

	public static SerialPortUtil getInstance() {
		if (portUtil == null)
			portUtil = new SerialPortUtil();
		return portUtil;
	}

	public void setReadTimeout(int timeoutMs) {
		readTimeout = timeoutMs;
	}

	private SerialPortUtil() {
		try {
			mSerialPort = new SerialPort(new File(path), baudrate);
			mOutputStream = mSerialPort.getOutputStream();
			mInputStream = mSerialPort.getInputStream();

			mReadThread = new ReadThread();
			isStop = false;
			mReadThread.start();

		} catch (Exception e) {
			Log.d(TAG, e.toString());
		}
	}

	public boolean send(String cmd) {
		boolean result = true;
		byte[] mBuffer = (cmd + "\r\n").getBytes();
		try {
			if (mOutputStream != null) {
				mOutputStream.write(mBuffer);
			} else {
				result = false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			result = false;
		}
		return result;
	}

	public boolean send(byte[] mBuffer) throws InterruptedException {
		boolean result = true;
		try {
			if (mOutputStream != null) {
				mSerialPort.setSend();
				mOutputStream.write(mBuffer);
				Thread.sleep(5);
				mSerialPort.setRecv();
			} else {
				result = false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			result = false;
		}
		return result;
	}

	public byte[] recv() throws InterruptedException {
		Thread.sleep(readTimeout);
		if (recvIndex == 0)
			return null;
		byte[] data = new byte[recvIndex];
		System.arraycopy(recvBuffer, 0, data, 0, recvIndex);
		recvIndex = 0;
		return data;
	}

	private class ReadThread extends Thread {
		@Override
		public void run() {
			super.run();
			while (!isStop && !isInterrupted()) {
				int size;
				try {
					if (mInputStream == null)
						return;
					size = mInputStream.read(recvBuffer, recvIndex, 256);
					if (size > 0) {
						recvIndex += size;
					}
					Thread.sleep(readDataInterval);
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
			}
		}
	}

	public void close() {
		isStop = true;
		if (mReadThread != null)
			mReadThread.interrupt();

		if (mSerialPort != null)
			mSerialPort.close();

		portUtil = null;
	}

}
