package jjwork.modbus;

public class ModbusParams {
	public int slave;
	public Function funcode;
	public int startAddress;
	public int quantity;
	public byte[] data = null;

	public ModbusParams(int slave, Function fun, int startAddress, int quantity) {
		this.slave = slave;
		this.funcode = fun;
		this.startAddress = startAddress;
		this.quantity = quantity;
	}

	public ModbusParams(int slave, Function fun, int startAddress, byte[] data) {
		this.slave = slave;
		this.funcode = fun;
		this.startAddress = startAddress;
		this.data = data;
	}
}