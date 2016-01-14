package jjwork.modbus;

public enum Excep {
	illegal_function(1), illegal_data_address(2), illegal_data_value(3), slave_device_failure(
			4), command_acknowledge(5), slave_device_busy(6), memory_parity_error(
			8);

	private int code = 0;

	private Excep(int code) {
		this.code = code;
	}

}
