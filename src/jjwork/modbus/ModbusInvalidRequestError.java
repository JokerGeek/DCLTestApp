package jjwork.modbus;


public class ModbusInvalidRequestError extends Exception {
	public ModbusInvalidRequestError(String message) {
		super(message);
	}
}