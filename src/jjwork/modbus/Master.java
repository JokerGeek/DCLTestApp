package jjwork.modbus;

import android.util.Log;

public class Master {
	String TAG = "Modbus master";
	SerialPortUtil serial = SerialPortUtil.getInstance();
	boolean isOpen = false;
	int baudrate = 0;
	int comPort = 0;

	private static Master modbusMaster = null;

	public static Master getInstance() {
		if(modbusMaster == null)
			modbusMaster = new Master();
		return modbusMaster;
	}

	private Master() {
	}
	
	public void open() {
		open(3, 9600, 1000);
	}

	public void open(int com, int baudrate, int readTimeout) {
		if (!isOpen) {
			isOpen = true;
			comPort = com;
			this.baudrate = baudrate;
			serial.setReadTimeout(readTimeout);
		}
	}

	public void close() {
		serial.close();
		isOpen = false;
		modbusMaster = null;
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

		send(request);

		if (slave != 0) {

			byte[] response = recv();

			byte[] responsePDU = query.parseResponse(response);
			int returnCode = responsePDU[0];
			int byte2 = responsePDU[1];
			if (returnCode > 0x80) {
				// error
			} else {
				byte[] data;
				if (isReadFunction) {
					int byteCount = byte2;
					data = Utils.splitArray(responsePDU, 2, responsePDU.length);
					if (byteCount != data.length)
						throw new InvalidResponseError("byte count is "
								+ byteCount
								+ " while actual number of bytes is "
								+ data.length + ".");
				} else {
					data = Utils.splitArray(responsePDU, 1, responsePDU.length);
				}
				return data;
			}
		}
		return null;
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
		if(data == null) throw new NotConnectedError("Recv error");
		Log.d(TAG, Utils.printBytes("Recv:", data));
		return data;
	}

	public interface OnModbusRecvDataListener {
		void modbusRecvDataListener(byte[] retDatas);
	}

}
