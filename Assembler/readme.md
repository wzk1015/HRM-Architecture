# HRM-Assembler and MIPS2HRM

An assembler and a mips-to-hrm translator



## MIPS2HRM

This a mips-to-hrm transolator. It supports the following instructions:

```assembly
add addu sub subu addi addiu
lw lhu lh lb lbu
sw sh sb
beq bltz
j jal jalr jr
mfc0 mtc0 eret
nop
li move
```

Note: 

* `lw lhu lh lb lbu` are considered same, all handling 16-bit value. Same for `sw sh sb`.
* `add addu sub subu` don't support immediate number.
* `addu subu addiu` are same as *signed* ones. In other words, algebraic overflow is not ignored even when it is unsigned instruction.
* *load* and *save* instructions only support format like `[load/save] [imm]([register])`.
* `jr` is a kernel_mode instruction.
* `mfc0 mtc0` only support *CAUSE* and *EPC* register (`mfc0/mtc0 [reg] $13/$14`)





## HRM-Assembler

This is an assembler of HRM-Assembly language, supporting 17 instructions. It has the following functions. You may find examples at  `Codes/`.

### Assembly

Translate code in `code.txt` into binary codes`machine_code_bin.txt` and hexadecimal codes `machine_code_hex.txt`.

### Translate to MIPS

Translate code in `code.txt` into MIPS code `mips_code.txt`.

Note that MIPS code are not guaranteed to compile successfully. To make sure it works, you should change `.addr` to proper values according to Memory Configuration in your MIPS Assembler.

### Set Labels

Set labels for J instructions. For example:

```assembly
jumpn milk
jump chocolate

chocolate:
add 5
outbox

milk:
bumpup [6]
bumpup 6
```

### Define Macros

Define macros in a similar way to MIPS. For example:

```asm
.macro test(%i,%j)
inbox
add %i
add %j
outbox
.end_macro

test(1,2)
test(3,4)
```

Note that one macro should not be a substring of another (e.g. x_addr and x_addr2) , as assembler simply do string replace. 

### Alloc Data

Allocate memory for data. For example:

```assembly
.data
  data1: .space 8
  data2: .space 16
```

### Manage Memory

Manage memory mapping. For example:

```assembly
.addr kernel_data 0x400
.addr data_base 0x0
```

### Make Annotations

Annotate code. For example:

```asm
inbox #get a number
add 1 #add by memory[1]
```
