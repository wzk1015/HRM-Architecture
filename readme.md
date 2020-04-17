# HRM-Architecture

// INDEX HERE

# Introduction

This project is an implementation of Human Resource Machine architecture by a hobbyist. This will include: a CPU based on HRM instruction set, an operating system running on the HRM-architecture CPU(maybe), a compiler for HRM codes to MIPS and an assembler for HRM codes.

Several demos for each part will also be provided to help you to understand how it works.

Please be aware that this is a personal project for study and entertainment.

# Processor

## Overview

The processor is a 16-bit RISC single cyclical CPU, which supports 2 IO instructions(INBOX, OUTBOX), 5 sequential instructions(COPYFROM, COPYTO, ADD, SUB, BUMPUP, BUMPDN), 3 jump instructions(JUMP, JUMPZ, JUMPN) and several kernel instructions(ERET, MFC0, NOP, etc.).

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

The memory is not managed through virtual memory or page tables here, the program will access to physical memory directly. Though the architecture is based on a 16-bit CPU, due to the design of instruction set, the size of the addressing space is only 2^12=4096 bits. Moreover, in the game user's available memory is changeless, so there will be no user stack, all user variables will be stored in a fixed area in memory.

| Address |                      Description                       |
| :-----: | :----------------------------------------------------: |
|  0xfff  |              Memory mapping limit address              |
|  0xfff  |               Kernel space high address                |
|  0x800  | Kernel text base address(Including exception handlers) |
|  0x7fd  |       Reserved address for output device(OUTBOX)       |
|  0x7fb  |        Reserved address for input device(INBOX)        |
|  0x7fa  |                Kernel data high address                |
|  0x400  |                Kernel data base address                |
|  0x3ff  |                   Text limit address                   |
|  0x80   |                   Text base address                    |
|  0x7f   |                   Data limit address                   |
|   0x0   |                   Data base address                    |

