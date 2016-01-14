package jjwork.modbus;

public enum Function {
	read_coils(1),
	read_discrete_registers(2),
	read_holding_registers(3),
	read_input_registers(4),
	write_single_coil(5),
	write_single_register(6),
	read_exception_status(7),
	diagnostic(8),
	write_multiple_coils(15),
	write_multiple_registers(16),
	read_write_multiple_registers(23);
	
	private int code = 0;
	private Function(int code){
		this.code = code;
	}
	public int value(){return code;}
}
