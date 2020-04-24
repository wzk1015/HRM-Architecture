`timescale 1ns / 1ps

module top(
    );

	wire clk;
	wire reset;

	clock _CLOCK(.clk(clk));
	
	reset_controller _RSTCTRL(.reset(reset));

endmodule
