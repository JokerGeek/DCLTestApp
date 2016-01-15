package jjwork.modbus;

public class NotConnectedError extends Exception {
	public NotConnectedError(String message) {
		super(message);
	}
}