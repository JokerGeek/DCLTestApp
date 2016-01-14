package jjwork.modbus;

public class ModbusNotConnectedError extends Exception {
	public ModbusNotConnectedError(String message) {
		super(message);
	}
}