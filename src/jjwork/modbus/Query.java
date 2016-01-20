package jjwork.modbus;

import android.util.Log;

public class Query {
	private final static int MIN_SLAVE_ADDRESS = 0;
	private final static int MAX_SLAVE_ADDRESS = 255;

	int requestAddress = 0;
	int responseAddress = 0;

	boolean isCorrectSlaveAddress(int slave) {
		return slave < MIN_SLAVE_ADDRESS || slave > MAX_SLAVE_ADDRESS;
	}

	public byte[] buildRequest(byte[] pdu, int slave) throws Exception {
		if (isCorrectSlaveAddress(slave))
			throw new Exception("Invalid address " + slave);
		requestAddress = slave;
		byte[] requestPDU = new byte[pdu.length + 3];
		requestPDU[0] = (byte) slave;
		System.arraycopy(pdu, 0, requestPDU, 1, pdu.length);
		int crc = Utils.calculateCRC(requestPDU, pdu.length + 1);
		System.arraycopy(Utils.pack("H", crc), 0, requestPDU, pdu.length + 1, 2);
		return requestPDU;
	}

	public byte[] parseResponse(byte[] response)
			throws InvalidResponseError {
		if (response.length < 3)
			throw new InvalidResponseError("Response length is invalid "
					+ response.length);
		responseAddress = response[0];
		if (responseAddress != requestAddress)
			throw new InvalidResponseError("Response address "
					+ responseAddress + " is different from request address "
					+ requestAddress);
		int crc = ((response[response.length-2] << 8) & 0xff00) | (response[response.length - 1] & 0xff);
		if (Utils.calculateCRC(response, response.length - 2) != crc)
			throw new InvalidResponseError("Invalid CRC in response. CRC=" + String.format("0x%04x", crc));
		return Utils.splitArray(response, 1, response.length - 2);
	}

	public byte[] buildResponse(byte[] responsePDU) {
		return new byte[0];
	}

	public byte[] parseRequest(byte[] request) {
		return new byte[0];
	}
}
