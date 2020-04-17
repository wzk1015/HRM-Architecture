# HRM-Assembler

This is a assembler of HRM-Assembly language, supporting 17 instructions.



## Funtions

This assembler supports various functions. You may find examples at  `Assembler/examples/`.

### Assembly

Translate code in `code.txt` into binary codes`machine_code_bin.txt` and hexadecimal codes `machine_code_hex.txt`.

### Translate to MIPS

Translate code in `code.txt` into MIPS code `mips_code.txt`.

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

### Alloc Data

Alloc memory for data. For example:

```assembly
.data
  data1: .space 8
  data2: .space 16
```

### Manage Memory

Manage menory mapping. For example:

```assembly
.addr kernel_data 0x400
.addr data_base 0x0
```

