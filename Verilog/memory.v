`timescale 1ns / 1ps

module memory(
		input [15:0] pc,
		input [15:0] data_addr,
		input [15:0] data_addr_final,
		input [15:0] reg_value,
		input clk,
		input reset,
		input memWE,
		input exl,
		output [15:0] instr,
		output [15:0] data,
		output [15:0] data_final,
		output empty_mem,
		output permission_denied
    );


endmodule
