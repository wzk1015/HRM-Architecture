`timescale 1ns / 1ps

module reset_controller(
		output reset
    );

	reg r;
	
	initial begin
		r = 1;
		#50;
		r = 0;
	end
	
	assign reset = r;

endmodule
