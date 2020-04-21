#Exceptions and CP0

HRM-CPU supports exception handling by utilizing CP0.  This document explains:

+ Various exceptions supported by HRM-Architecture
+ Design of CP0 registers and functions
+ Behaviors of exception handler
+ Examples of exception handler



## Exceptions Supported

| Exccode | Description              | Source Instructions              | Source Module |
| ------- | ------------------------ | -------------------------------- | ------------- |
| 0x1     | Algebraic Overflow       | add sub bumpup bumpdown          | ALU           |
| 0x2     | Empty Memory             | add sub bumpup bumpdown copyfrom | DM            |
| 0x3     | Empty Register           | add sub copyto mtcause mtepc     | RF            |
| 0x4     | Unrecgonized Instrcution | *after J-instructions or eret*   | CTRL          |
| 0x5     | Permission Denied*       | add sub bumpup bumpdown copyfrom | CTRL IM DM    |
| 0x6     | Inbox Trap               | inbox                            | CTRL          |
| 0x7     | Outbox Trap              | outbox                           | CTRL          |

*About *Permission Denied*: This can appear when:

* using `mfcause` `mtcause` `mfepc` `mtepc` `eret` `jumpr` in user mode (*EXL*=0)
* jumping out of  *text* area address in user mode
* writing *static data* area in user mode (reading *static data* is allowed so user can use constants)
* reading or writing kernel address or illegal address in user mode

Note that *Permission Denied* will not appear in kernel mode (In fact, **any** exceptions will be ignored in kernel mode).



## CP0 Design

### EPC 

EPC register stores the PC address. When exception encountered, EPC-writing is disabled, so that it stores the PC of the wrong instruction.

Exception handler may read EPC by `mfepc`, and then modify it by `mtepc`. For example, handler may add EPC by 2 to skip the wrong instruction.

When `eret`, PC is set to the address in EPC to return from exception.

Note that when *Unrecgonized Instrcution* encountered after J-instructions or `eret`, EPC is set to address of previous instruction's next instruction, i.e. `addr(jump/eret) + 2`, by hardware.

### Cause

| 15   | 14    | 13 - 4       | 3-0     |
| ---- | ----- | ------------ | ------- |
| EXL  | Crash | *(Reserved)* | Exccode |

The highest bit in Cause is *EXL*, which represents whether the CPU is at exception level. It is set to 1 when exception encountered, and set to 0 when `eret`.

The second highest bit is *Crash*. If it is set to 1 (e.g. by exception handler) , CPU will crash and stop running.

The lowerst 4 bits are *Exccode*, representing category of exception (see *Exception Supported* for details). Handler may read Exccode and decide what to do according to its value.

Other bits in Cause is reserved for use.

Notice that nested exceptions are not supported. When EXL is set to 1 and another exception appeared, it will be ignored.



## Exception Handler

When exception encountered, hardware will handle it according to the following steps:

1. Set EPC to the address of wrong instrcution.
2. Set Cause(EXL) to 1, Cause(Exccode) to proper value
3. Set PC to start point of exception handler.

Then CPU begins to run the code of exception handler. Normaly, handler should:

1. Save current register value in memory.
2. use `mfcause`read from Cause(Exccode).
3. decide what to do depending on Exccode (e.g. skip instruction when overflow, set register to 0 when empty register etc.) .  `mfepc` and `mtepc` might be used to update EPC.
4. when problem fixed, set Cause(EXL) and Cause(Exccode) to 0, indicating exception has been handled.
5. recover register's value from memory.
6. use `eret` to return to address (stored in EPC) from exception.



## Handler Example

Here is an example of exception handler. You can find this handler example in `codes/handler.txt`.

```assembly
.addr data_base 0x100
.addr addr_inbox 0x282
.addr addr_outbox 0x284
.addr kernel_text_base 0x4000
.addr pow_2_15 0x248
.addr pow_2_14 0x246
.addr pow_2_13 0x244
.addr pow_2_12 0x242
.addr pow_2_11 0x240
.addr zero 0x200
.addr one 0x202
.addr two 0x204
.addr three 0x206
.addr four 0x208
.addr five 0x210
.addr six 0x212
.addr seven 0x214
.addr empty_value 0x280

.data
	reg_field:		.space 2 
	exception_counter:	.space 2
	exccode:		.space 2
	wrong_addr:	.space 2

.macro branch_exccode(%number, %label)
	copyfrom exccode
	sub %number
	jumpz %label
.end_macro

.text
	copyto reg_field 		# save register
	bumpup exception_counter	# update counter
	copyto exception_counter

	mfcause			#read exccode
	sub pow_2_15
	#sub pow_2_14		#remove exl. crash is already zero
	copyto exccode

	branch_exccode(one, overflow)	#branch based on exccode
	branch_exccode(two, empty_mem)
	branch_exccode(three, empty_reg)
	branch_exccode(four, unrec_instr)
	branch_exccode(five, permission_denied)
	branch_exccode(six, inbox_trap)
	branch_exccode(seven, outbox_trap)
	jump end				#unknown exception

crash:
	mfcause		#crash
	add pow_2_14
	mtcause
	jump end

overflow:
permission_denied:
	mfepc		#skip wrong instruction by adding 2 to EPC
	add two
	mtepc
	jump end

empty_mem:
	copyfrom zero	#load 0 to reg
	copyto reg_field
	jump end
	
empty_reg:
	copyfrom zero	#load 0 to reg
	copyto reg_field
	jump end

unrec_instr:
	jump end		#epc has been set to addr(jump/eret)+2 by hardware, so just jump

inbox_trap:
	copyfrom [addr_inbox]
	sub empty_value
	jumpz inbox_empty	#judge if inbox_addr is empty
	copyfrom [addr_inbox]
	jump end
	
inbox_empty:
	jump crash

outbox_trap:
	copyfrom reg_field
	sub empty_value
	jumpz crash	#if reg is empty, carsh
	copyfrom reg_field
	copyto [addr_outbox]
	jump end

end:
	copyfrom zero	#reset cause
	mtcause
	copyfrom reg_field	#recover register
	eret		#return
```

