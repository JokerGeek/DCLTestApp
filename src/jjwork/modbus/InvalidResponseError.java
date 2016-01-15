package jjwork.modbus;


public class InvalidResponseError extends Exception {
	public InvalidResponseError(String message) {
		super(message);
	}
}