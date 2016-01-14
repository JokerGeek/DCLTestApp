package jjwork.modbus;


public class ModbusInvalidResponseError extends Exception {
	public ModbusInvalidResponseError(String message) {
		super(message);
	}
}