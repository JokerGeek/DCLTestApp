package jjwork.modbus;

import android.util.Log;

public class Utils {
	public static byte[] pack(String format, int... datas) throws Exception {
		int pduIndex = 0;
		int datasIndex = 0;
		int pduLength = 0;
		for (char c : format.toCharArray()) {
			if (c == 'B')
				pduLength += 1;
			else if (c == 'H')
				pduLength += 2;
			else
				throw new Exception();
		}

		byte[] pduData = new byte[pduLength];
		for (char c : format.toCharArray()) {
			if (c == 'B') {
				pduData[pduIndex] = (byte) datas[datasIndex];
				pduIndex += 1;
				datasIndex += 1;
			} else if (c == 'H') {
				pduData[pduIndex] = (byte) ((datas[datasIndex] >> 8) & 0xff);
				pduData[pduIndex + 1] = (byte) (datas[datasIndex] & 0xff);
				pduIndex += 2;
				datasIndex += 1;
			}
		}
		return pduData;
	}

	public static int calculateCRC(byte[] datas) {
		return calculateCRC(datas, datas.length);
	}

	public static int calculateCRC(byte[] datas, int length) {
		int crc = 0xffff;
		for (int i = 0; i < length; i++) {
			crc = crc ^ datas[i];
			for (int j = 0; j < 8; j++) {
				int tmp = crc & 1;
				crc = crc >> 1;
				if (tmp != 0)
					crc = crc ^ 0xa001;
			}
		}
		return swapBytes(crc);
	}

	static int swapBytes(int wordValue) {
		int msb = wordValue >> 8;
		int lsb = wordValue % 256;
		return (lsb << 8) | msb;
	}

	static byte[] concat(byte[] a, byte[] b) {
		byte[] c = new byte[a.length + b.length];
		System.arraycopy(a, 0, c, 0, a.length);
		System.arraycopy(b, 0, c, a.length, b.length);
		return c;
	}


	public static byte[] splitArray(byte[] source, int startIndex, int endIndex) {
		byte[] result = new byte[endIndex - startIndex];
		System.arraycopy(source, startIndex, result, 0, endIndex - startIndex);
		return result;
	}
	
	public static String printBytes(String head, byte[] bytes){
		StringBuilder sb = new StringBuilder();
		sb.append(head);
		for (byte b : bytes) {
			sb.append(String.format("%02x ", b));
		}
		return sb.toString();
	}
	public static String printBytes(byte[] bytes){
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			sb.append(String.format("%02x ", b));
		}
		return sb.toString();
	}
}
