package jjwork.modbus;

import android.util.Log;

public class Master {
	private final static String TAG = "Modbus master";
	private SerialPortUtil serial;
	private boolean isOpen = false;
	private int baudrate = 115200;
	private int comPort = 0;
	private int resendcount = 1;


	public Master(int com, int baudrate, int readTimeout) {
		comPort = com;
		this.baudrate = baudrate;
		serial = new SerialPortUtil(SerialPortUtil.COM1, 19200);
		serial.setReadTimeout(readTimeout);
	}
	public void setBaudRate(int baud){
		this.baudrate = baud;
	}
	
	public void open() {
		if (!isOpen) {
			isOpen = true;	
		}
	}
	
	public void close() {
		serial.close();
		isOpen = false;
	}

	public byte[] execute(int slave, Function funCode, int startAddress,
			int quantity) throws Exception {
		return execute(slave, funCode, startAddress, quantity, null);
	}
	public byte[] execute(int slave, Function funCode, int startAddress, byte[] outputValues) throws Exception {
		return execute(slave, funCode, startAddress, outputValues.length, outputValues);
	}

	public byte[] execute(int slave, Function funCode, int startAddress,
			int quantity, byte[] outputValues) throws Exception {
		boolean isReadFunction = false;
		int expectedLength = 0;
		byte[] pdu = null;

		open();

		switch (funCode) {
		case read_coils:
			break;
		case read_holding_registers:
		case read_input_registers:
			isReadFunction = true;
			pdu = Utils.pack("BHH", funCode.value(), startAddress, quantity);
			expectedLength = 2 * quantity + 5;
			break;

		case write_multiple_registers:
			if (outputValues == null)
				throw new Exception();

			byte[] pduHead = Utils.pack("BHHB", funCode.value(), startAddress,
					outputValues.length / 2, outputValues.length);
			pdu = Utils.concat(pduHead, outputValues);
			expectedLength = 8;
			break;
		case write_single_register:
			if (outputValues == null)
				throw new Exception();
			pdu = Utils.pack("BHBB", funCode.value(), startAddress, outputValues[0], outputValues[1]);
			expectedLength = 8;
			break;
		default:
			pdu = new byte[1];
			break;
		}

		Query query = makeQuery();
		byte[] request = query.buildRequest(pdu, slave);

		byte[] response = null;
		// 接收失败重复发送
		for(int i = 0; i < resendcount; i++){
			send(request);
			_sendData = request;

			if (slave == 0) return null;
			
			response = recv();
			if(response != null) break;
		}
		if(response == null)
			throw new NotConnectedError("Recv error");
		
		_recvData = response;
		
		byte[] responsePDU = query.parseResponse(response);
		int returnCode = responsePDU[0];
		int byte2 = responsePDU[1];
		
		if (returnCode > 0x80) {
			throw new ModbusError(byte2);
		} 
		
		if (!isReadFunction)
			return Utils.splitArray(responsePDU, 1, responsePDU.length);
		
		// isReadFunction == true
		int byteCount = byte2;
		byte[] data = Utils.splitArray(responsePDU, 2, responsePDU.length);
		if (byteCount != data.length)
			throw new InvalidResponseError("byte count is " + byteCount
					+ " while actual number of bytes is " + data.length);
		return data;
		
		
	}

	private Query makeQuery() {
		return new Query();
	}

	public void send(byte[] data) throws InterruptedException {
		Log.d(TAG, Utils.printBytes("Send:", data));
		serial.send(data);
	}

	private byte[] recv() throws Exception {
		byte[] data = serial.recv();
		if(data == null) return null;
		Log.d(TAG, Utils.printBytes("Recv:", data));
		return data;
	}
	// test
	byte[] _sendData, _recvData;
	public byte[] getSendData() {
		return _sendData;
	}
	public byte[] getRecvData(){
		return _recvData;
	}
	public interface OnModbusRecvDataListener {
		void modbusRecvDataListener(byte[] retDatas);
	}

}
