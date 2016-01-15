package jjwork.modbus;


public class InvalidRequestError extends Exception {
	public InvalidRequestError(String message) {
		super(message);
	}
}