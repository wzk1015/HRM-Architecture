## HRM-CPU Design

This is a brief introduction (also a note for designing) of cpu modules and their functions.

## Modules

```mariadb
PC	-- Program Counter
NPC	-- Next Program Counter
IM	-- Instruction Memory
RF	-- Register File
DM	-- Data Memory
ALU	-- Arithmetic Logic Unit
CTRL-- Controller
CP0	-- Co-Processor 0
MUX -- Write Back Multiplexer
```



## PC

### Interfaces

| Interface | I/O    | Description        |
| --------- | ------ | ------------------ |
| clk       | input  | clock              |
| npc[15:0] | input  | next pc address    |
| crash     | input  | crash              |
| pc[15:0]  | output | current pc address |



### Functions

| Function  | Description                      |
| --------- | -------------------------------- |
| update PC | when posedge, update PC with NPC |
| crash     | if crash, froze PC               |



## NPC

### Interfaces

| Interface       | I/O    | Description        |
| --------------- | ------ | ------------------ |
| pc[15:0]        | input  | current pc         |
| j_addr[11:0]    | input  | jump address       |
| cp0_pc[15:0]    | input  | cp0 output address |
| reg_value[15:0] | input  | value of register  |
| npc_sel[2:0]    | input  | npc control signal |
| EXL             | input  | exception level    |
| npc[15:0]       | output | current pc address |

**note: **zero and negative are omitted as it can be judged inside NPC (using reg_value)

### Functions

| Function          | Description                        |
| ----------------- | ---------------------------------- |
| calculate next pc | calculate next pc based on npc_sel |

```python
if cp0_pc != 0:  
    npc = cp0_pc #jump to handler or eret
    
elif jump or (jumpz and reg == 0) 
	or (jumpn and reg<0) 
	or (jumpr and !EXL):	
        npc = (zero_extend) j_addr
        
else:
    npc = pc + 2
```



## IM

### Interfaces

| Interface         | I/O    | Description                       |
| ----------------- | ------ | --------------------------------- |
| pc[15:0]          | input  | current pc                        |
| EXL               | input  | exception level                   |
| crash             | input  | crash                             |
| ir[15:0]          | output | instruction code                  |
| permission_denied | output | illegal pc, kernel addr exception |



### Functions

| Function           | Description                                   |
| ------------------ | --------------------------------------------- |
| init instructions  | read instructions from file when initializing |
| parse instructions | read instructions                             |
| raise exception    | raise permission denied exception if EXL=0    |
| crash              | if crash, output 0x0000 (nop)                 |



## RF

### Interfaces

| Interface        | I/O    | Description               |
| ---------------- | ------ | ------------------------- |
| clk              | input  | clock                     |
| write_data[15:0] | input  | data to write in register |
| EXL              | input  | exception level           |
| reg_write        | input  | write enable signal       |
| reg_value[15:0]  | output | data read from register   |
| empty_register   | output | empty register exception  |



### Functions

| Function        | Description                             |
| --------------- | --------------------------------------- |
| write           | write data to register if enabled       |
| read            | read data from register                 |
| raise exception | raise empty register exception if EXL=0 |



## DM

### Interfaces

| Interface             | I/O    | Description                                                  |
| --------------------- | ------ | ------------------------------------------------------------ |
| clk                   | input  | clock                                                        |
| addr_index[15:0]      | input  | address for indexed N-instructions' first access             |
| addr_final[15:0]      | input  | address for indexed N-instructions' second access and other instructions' only access |
| reg_value[15:0]       | input  | value of register                                            |
| mem_write             | input  | write enable signal                                          |
| EXL                   | input  | exception level                                              |
| mem_value_index[15:0] | output | read value for indexed N-instructions' first access          |
| mem_value_final[15:0] | output | read value for indexed N-instructions' second access and other instructions' only access |
| empty_memory          | output | empty memory exception                                       |
| permission_denied     | output | illegal addr, kernel addr exception                          |



### Functions

| Function         | Description                                      |
| ---------------- | ------------------------------------------------ |
| write            | write data to memory if enabled                  |
| read             | read data from memory                            |
| raise exceptions | raise empty memory or permission denied if EXL=0 |



## ALU

### Interfaces

| Interface          | I/O    | Description                  |
| ------------------ | ------ | ---------------------------- |
| mem_value[15:0]    | input  | value of memory              |
| reg_value[15:0]    | input  | value of register            |
| alu_op[2:0]        | input  | alu operation signal         |
| EXL                | input  | exception level              |
| alu_out[15:0]      | output | result of alu calculation    |
| algebraic_overflow | output | algebraic overflow exception |

**note: **zero and negative are omitted as it can be judged inside NPC (using reg_value)

### Functions

| Function        | Description                                 |
| --------------- | ------------------------------------------- |
| operate         | do operation based on alu_op                |
| raise exception | raise algebraic overflow exception if EXL=0 |

```python
if alu_op == ADD:
	alu_out = mem_value + reg_value
elif alu_op == SUB:
	alu_out = mem_value - reg_value
elif alu_op == BUMPUP:
	alu_out = mem_value + 0x1
elif alu_op == BUMPDOWN:
    alu_out = mem_value - 0x1
elif alu_op == COPYFROM:
    alu_out = mem_value
# COPYTO does not need ALU
```



## CTRL

### Interfaces

| Interface                | I/O    | Description                          |
| ------------------------ | ------ | ------------------------------------ |
| ir[15:0]                 | input  | instruction code                     |
| EXL                      | input  | exception level                      |
| crash                    | input  | crash                                |
| addr[11:0]               | output | lower 12/11 bits of instruction code |
| npc_sel[2:0]             | output | npc control signal                   |
| alu_op[2:0]              | output | alu operation signal                 |
| mem_write                | output | memory write enable signal           |
| reg_write                | output | register write enable signal         |
| eret                     | output | eret instruction                     |
| mfcause                  | output | mfcause instruction                  |
| mfepc                    | output | mfepc instruction                    |
| mtcause                  | output | mtcause instruction                  |
| mtepc                    | output | mtepc instruction                    |
| unrecgonized_instrcution | output | unrecgonized instrcution exception   |
| permission_denied        | output | permission denied exception          |
| inbox_trap               | output | inbox trap exception                 |
| outbox_trap              | output | outbox trap exception                |

**note: **highest bit of addr is meaningless for N-instructions

### Functions

| Function                 | Description                                          |
| ------------------------ | ---------------------------------------------------- |
| send out control signals | parse instruction code and decide values of signals  |
| split address            | split last 11/12 bits of instruction code as address |
| raise exceptions         | raise four kinds of exceptions if EXL=0              |



## CP0

### Interfaces

| Interface                | I/O    | Description                                   |
| ------------------------ | ------ | --------------------------------------------- |
| unrecgonized_instrcution | input  | unrecgonized instrcution exception            |
| permission_denied        | input  | permission denied exception                   |
| inbox_trap               | input  | inbox trap exception                          |
| outbox_trap              | input  | outbox trap exception                         |
| algebraic_overflow       | input  | algebraic overflow exception                  |
| empty_memory             | input  | empty memory exception                        |
| empty_register           | input  | empty register exception                      |
| pc[15:0]                 | input  | current pc                                    |
| reg_value[15:0]          | input  | value of register                             |
| clk                      | input  | clock                                         |
| eret                     | input  | eret instruction                              |
| mfcause                  | input  | mfcause instruction                           |
| mfepc                    | input  | mfepc instruction                             |
| mtcause                  | input  | mtcause instruction                           |
| mtepc                    | input  | mtepc instruction                             |
| cp0_pc[15:0]             | output | 0x4180 when exception,  epc when eret, else 0 |
| cp0_reg_value[15:0]      | output | value of epc or cause                         |
| EXL                      | output | exception level (highest bit of cause)        |
| crash                    | output | crash (second highest bit of cause)           |

**note: **please refer to `exceptions and cp0.md` for detailed information

### Functions

| Function                              | Description                                              |
| ------------------------------------- | -------------------------------------------------------- |
| catch exceptions, turn on kernel mode | set EXL=1, cp0_pc=0x4180, cause and epc to proper values |
| return from exception                 | set EXL=0, cp0_pc=epc, clear cause(by software)          |
| write                                 | write data from register if mtcause or mtepc             |
| read                                  | read data if mfcause or mfepc                            |
| crash                                 | if crash=1, send out crash signal                        |



## MUX

### Interfaces

| Interface           | I/O    | Description               |
| ------------------- | ------ | ------------------------- |
| alu_out[15:0]       | input  | result of alu calculation |
| cp0_reg_value[15:0] | input  | value of cause or epc     |
| mfcause             | input  | mfcause instruction       |
| mfepc               | input  | mtepc instruction         |
| write_data[15:0]    | output | data written to register  |

### Functions

| Function                  | Description                                           |
| ------------------------- | ----------------------------------------------------- |
| select data to write back | if mfcause or epc, choose cp0_reg_value, else alu_out |



## Other Notes

* `addr` is extended to 16 bits before getting into DM (or always extend to 16 bits in CTRL, if so, change this document)
* cp0's `permission_denied` is a OR operation of  three sources of `permission_denies` (CTRL DM IM)
* when *Unrecgonized Instrcution* encountered after J-instructions or `eret`, EPC is set to address of previous instruction's next instruction, i.e. `addr(jump/eret) + 2`, by hardware.