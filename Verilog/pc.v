`timescale 1ns / 1ps

module pc(
		input [15:0] npc,
		input clk,
		input reset,
		input crash,
		output [15:0] pc
    );
	 
	reg [15:0] r;
	 
	initial r = 16'b0;
	 
	always@ (posedge clk) begin
		if (reset) r <= 16'b0;
		else if (!crash) r <= npc;
	end
	 
	assign pc = r;

endmodule
