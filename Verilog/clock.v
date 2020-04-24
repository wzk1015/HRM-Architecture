`timescale 1ns / 1ps

module clock(
		output clk
    );

	reg r;
	
	initial r = 0;
	
	always #50 r = ~r;
	
	assign clk = r;

endmodule
