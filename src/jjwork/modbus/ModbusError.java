package jjwork.modbus;


public class ModbusError extends Exception {
	int exceptionCode = 0;

	public int code() {
		return exceptionCode;
	}

	public ModbusError(int exceptionCode) {
		super("Modbus error : exception code=" + exceptionCode);
		this.exceptionCode = exceptionCode;
	}
}