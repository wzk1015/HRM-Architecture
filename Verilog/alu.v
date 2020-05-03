`timescale 1ns / 1ps

`include "define.v"

module alu(
		input [15:0] reg_value,
		input [15:0] mem_value,
		input [2:0] op_sel,
		input EXL,
		output [15:0] result,
		output overflow
    );
	 
	wire [16:0] wsum;
	wire [16:0] wdiff;
	wire [16:0] wbup;
	wire [16:0] wbdn;
	
	assign wsum = reg_value + mem_value;
	assign wdiff = reg_value - mem_value;
	assign wbup = mem_value + 1;
	assign wbdn = mem_value - 1;

	reg ov;
	reg [15:0] r;
	
	initial begin
		ov = 1'b0;
		r = 16'b0;
	end
	
	always@(*) begin
		if (op_sel == `ALU_ADD) begin
			r = wsum[15:0];
			ov = (wsum[16] != wsum[15]) ? 1 : 0;
		end
		else if (op_sel == `ALU_SUB) begin
			r = wdiff[15:0];
			ov = (wdiff[16] != wdiff[15]) ? 1 : 0;
		end
		else if (op_sel == `ALU_BUP) begin
			r = wbup[15:0];
			ov = (wbup[16] != wbup[15]) ? 1 : 0;
		end
		else if (op_sel == `ALU_BDN) begin
			r = wbdn[15:0];
			ov = (wbdn[16] != wbdn[15]) ? 1 : 0;
		end
		else if (op_sel == `ALU_CPY) begin
			r = mem_value;
			ov = 0;
		end
	end
	
	assign result = r;
	assign overflow = (EXL == 0) ? ov : 1'b0;

endmodule
