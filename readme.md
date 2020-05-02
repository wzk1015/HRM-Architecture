# HRM-Architecture

// INDEX HERE

# Introduction

This project is an implementation of Human Resource Machine architecture by a hobbyist. This will include: a CPU based on HRM instruction set, an operating system running on the HRM-architecture CPU(maybe), a compiler for HRM codes to MIPS and an assembler for HRM codes.

Several demos for each part will also be provided to help you to understand how it works.

Please be aware that this is a personal project for study and entertainment.

# Processor

## Overview

The processor is a 16-bit RISC single cyclical CPU, which supports 2 IO instructions(INBOX, OUTBOX), 6 sequential instructions(COPYFROM, COPYTO, ADD, SUB, BUMPUP, BUMPDN), 3 jump instructions(JUMP, JUMPZ, JUMPN) and several kernel instructions(ERET, MFCAUSE, MTCAUSE, etc.).

We preserved the original instructions in the game, removed COMMENT(it's not an instruction strictly speaking) and added several essential instructions.

## Instruction Set

The instruction set consists of 3 types of instructions: instructions related to system calls(S instructions), normal instructions(N instructions) and jump instructions(J instructions). The encoding fields are:

* S instructions

  |           INSTRUCTION           |
  | :-----------------------------: |
  | 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 |

* N instructions

  |  FUNC   | INDEXED |        OPCODE         |
  | :-----: | :-----: | :-------------------: |
  | 0 0 0 0 |    0    | 0 0 0 0 0 0 0 0 0 0 0 |

* J instructions

  |  FUNC   |          ADDR           |
  | :-----: | :---------------------: |
  | 0 0 0 0 | 0 0 0 0 0 0 0 0 0 0 0 0 |

The detailed information of instructions are listed [here](Documentation/instruction-set.md).

## Physical Address Mapping

The memory is not managed through virtual memory or page tables here, the program will access to physical memory directly. 

The size of addressing space is 2^16^ = `65536` bytes. However, due to the design of instruction set, J-instructions can only reach 2^12<<1^=`8192`bytes, and 2^11<<1^ = `4096` bytes for N-instructions. These are enough for user (as all user memory are lower than `0xfff`), and kernel must use kernel mode instruction `jumpr` to jump to the whole addressing space. Kernel may use **indexed** N-instructions to operate the whole space by putting intended address in user's data area. (e.g. `copyfrom intended_addr  copyto 0x0  add [0]`)

Moreover, in the game user's available memory is changeless, so there will be no user stack, all user variables will be stored in a fixed area in memory.

| Address |                 Description                 |
| :-----: | :-----------------------------------------: |
| 0xffff  |            Memory mapping limit             |
| 0xffff  |             Kernel space limit              |
| 0xfffd  | Reserved address for output device (OUTBOX) |
| 0xfffb  |  Reserved address for input device (INBOX)  |
| 0xfffa  |              Kernel text limit              |
| 0x4180  |              Exception handler              |
| 0x4000  |              Kernel text base               |
| 0x3fff  |              Kernel data limit              |
| 0x1000  |  Kernel data base (lower bound for kernel)  |
|  0xfff  |      Text limit (upper bound for user)      |
|  0x300  |                  Text base                  |
|  0x2ff  |              Static data limit              |
|  0x200  |            Static data*\** base             |
|  0x1ff  |                 Data limit                  |
|   0x0   |                  Data base                  |



*\** About *static data*: Due to the one-register architecture and instruction set which does not support immediate number, assembly codes need constant numbers for operations. HRM memory mapping provides **static data area** with similar functions like constant registers. This area contains common constants.  Static data area is inside user's space so it can be reached by both user and kernel. Constants included are listed as follows:

* basic integers - 0x0~0x10
* power of two  - 2^k^ (k=0, 1, ... 16) 
* value of empty momery and register - 0x8000
* address of inbox - 0xfffb
* address of outbox - 0xfffd
* ...



## Try this!

You can run `Utils/run.py`  to assemble your code and simulate. Try this command:

``` bash
python Utils/run.py --in_code Codes/code.txt --in_mem Simulator/memory.txt --out_log Simulator/simulator_log.txt
```

