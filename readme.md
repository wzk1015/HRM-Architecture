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

The detailed information of instructions are listed as follow:

* **ADD Number**

  |  FUNC   | INDEXED |      OPCODE       |
  | :-----: | :-----: | :---------------: |
  | 0 0 0 1 |    0    | ADDRESS OF NUMBER |

  **Format: ** ADD *address*
  
  **Purpose:** To add a number to the handheld number. The number is indexed by *address*. If overflow occurs, or if there isn't any number at that address then trap.
  
  **Description:** Register ← Register + Memory[*address*]
  
  **Operation:** 
  
  ```
  true_address <- address << 1
  addend <- Memory[true_address]
  Register <- Register + addend
  ```
  
  **Exceptions:** Algebraic Overflow, Empty Memory
  
* **ADD Indexed Number**

  |  FUNC   | INDEXED |            OPCODE             |
  | :-----: | :-----: | :---------------------------: |
  | 0 0 0 1 |    1    | ADDRESS OF THE NUMBER'S INDEX |

  **Format: ** ADD [*address*]

  **Purpose:** To add a number to the handheld number. The number is indexed by the value stored at *address*. If overflow occurs, or if no number is found then trap.

  **Description:** Register ← Register + Memory[Memory[*address*]]

  **Operation:** 

  ```
  true_address <- address << 1
  true_address <- Memory[true_address]
  true_address <- true_address << 1
  addend = Memory[true_address]
  Register <- Register + addend
  ```

  **Exceptions:** Algebraic Overflow, Empty Memory

* **BUMPUP Number**

  |  FUNC   | INDEXED |        OPCODE         |
  | :-----: | :-----: | :-------------------: |
  | 0 0 1 0 |    0    | ADDRESS OF THE NUMBER |

  **Format:** BUMPUP *address*

  **Purpose: ** To add a number to one and replace the handheld number with the result. The number is indexed by *address*. If overflow occurs, or if no number is found then trap.

  **Description:** Register ← Memory[*address*] + 0x0001

  **Operation:** 

  ```
  true_address <- address << 1
  target <- Memory[true_address]
  Register <- target + 0x0001
  ```

  **Exceptions:** Algebraic Overflow, Empty Memory

* **BUMPUP Indexed Number**

  |  FUNC   | INDEXED |            OPCODE             |
  | :-----: | :-----: | :---------------------------: |
  | 0 0 1 0 |    1    | ADDRESS OF THE NUMBER'S INDEX |

  **Format:** BUMPUP [*address*]

  **Purpose: ** To add a number to one and replace the handheld number with the result. The number is indexed by the value stored at *address*. If overflow occurs, or if no number is found then trap.

  **Description:** Register ← Memory[Memory[*address*]] + 0x0001

  **Operation:** //todo

  **Exceptions:** Algebraic Overflow, Empty Memory

* **BUMPDOWN Number**

  |  FUNC   | INDEXED |        OPCODE         |
  | :-----: | :-----: | :-------------------: |
  | 0 0 1 1 |    0    | ADDRESS OF THE NUMBER |

  **Format:** BUMPDN *address*

  **Purpose: ** To subtract one from a number and replace the handheld number with the result. The number is indexed by *address*. If overflow occurs, or if no number is found then trap.

  **Description:** Register ← Memory[*address*] - 0x0001

  **Operation:** //todo

  **Exceptions:** Algebraic Overflow, Empty Memory

* **BUMPDOWN Indexed Number**

  |  FUNC   | INDEXED |        OPCODE         |
  | :-----: | :-----: | :-------------------: |
  | 0 0 1 1 |    1    | ADDRESS OF THE NUMBER |

  **Format:** BUMPDN *address*

  **Purpose: ** To subtract one from a number and replace the handheld number with the result. The number is indexed by *address*. If overflow occurs, or if no number is found then trap.

  **Description:** Register ← Memory[Memory[*address*]] - 0x0001

  **Operation:** //todo

  **Exceptions:** Algebraic Overflow, Empty Memory

* **COPYFROM Address**

  |  FUNC   | INDEXED |               OPCODE               |
  | :-----: | :-----: | :--------------------------------: |
  | 0 1 0 0 |    0    | ADDRESS OF THE NUMBER TO COPY FROM |

  **Format:** COPYFROM *address*

  **Purpose: ** To replace the handheld number with the one stored at *address*. If no number is found then trap.

  **Description:** Register ← Memory[*address*]

  **Operation:** //todo

  **Exceptions:** Empty Memory

* **COPYFROM Indexed Address**

  |  FUNC   | INDEXED |            OPCODE             |
  | :-----: | :-----: | :---------------------------: |
  | 0 1 0 0 |    1    | ADDRESS OF THE NUMBER'S INDEX |

  **Format:** COPYFROM [*address*]

  **Purpose: ** To replace the handheld number with the stored one. The number is indexed by the value stored at *address*. If no number is found then trap.

  **Description:** Register ← Memory[Memory[*address*]]

  **Operation:** //todo

  **Exceptions:** Empty Memory

* **COPYTO Address**

  |  FUNC   | INDEXED |       OPCODE       |
  | :-----: | :-----: | :----------------: |
  | 0 1 0 1 |    0    | ADDRESS TO COPY TO |

  **Format:** COPYTO *address*

  **Purpose: ** To replace the number stored at *address* with the handheld one. If there's no number in hand then trap.

  **Description:** Memory[*address*] ← Register

  **Operation:** //todo

  **Exceptions:** Empty Memory

* **COPYTO Indexed Address**

  |  FUNC   | INDEXED |            OPCODE             |
  | :-----: | :-----: | :---------------------------: |
  | 0 1 0 1 |    1    | ADDRESS OF THE NUMBER'S INDEX |

  **Format:** COPYTO [*address*]

  **Purpose: ** To replace the stored number with the handheld one. The number is indexed by the value stored at *address*. If there's no number in hand then trap.

  **Description:** Memory[Memory[*address*]] ← Register

  **Operation:** //todo

  **Exceptions:** Empty Memory

* **EXCEPTION RETURN**

  |              INBOX              |
  | :-----------------------------: |
  | 1 1 1 1 1 0 0 0 0 0 0 0 0 0 1 0 |

  **Format:** ERET

  **Purpose: ** //todo

  **Description:** Program Counter ← CP0[EPC]

  **Operation:** //todo

  **Exceptions:** None

* **INBOX**

  |              INBOX              |
  | :-----------------------------: |
  | 1 1 1 1 1 0 0 0 0 0 0 0 0 0 0 0 |

  **Format:** INBOX

  **Purpose: ** To replace the handheld number with the one picked from inbox. If there's nothing left in inbox then trap.

  **Description:** Register ← Inbox Register

  **Operation:** //todo

  **Exceptions:** Inbox Empty

* **JUMP**

  |   FUNC    |            ADDR            |
  | :-------: | :------------------------: |
  | 0 1 1 0 0 | ADDRESS OF THE INSTRUCTION |

  **Format:** JUMP *label*

  **Purpose: ** //todo

  **Description:** Program Counter ← Address << 1

  **Operation:** //todo

  **Exceptions:** None

* **JUMP if NEGATIVE**

  |   FUNC    |            ADDR            |
  | :-------: | :------------------------: |
  | 0 1 1 1 0 | ADDRESS OF THE INSTRUCTION |

  **Format:** JUMPN *label*

  **Purpose: ** //todo

  **Description:** if Register < 0 then JUMP

  **Operation:** //todo

  **Exceptions:** None

* **JUMP if ZERO**

  |   FUNC    |            ADDR            |
  | :-------: | :------------------------: |
  | 1 0 0 0 0 | ADDRESS OF THE INSTRUCTION |

  **Format:** JUMPN *label*

  **Purpose: ** //todo

  **Description:** if Register == 0 then JUMP

  **Operation:** //todo

  **Exceptions:** None

* **MOVE FROM COPROCESSOR 0**

  |             MFC0              |           RS           |
  | :---------------------------: | :--------------------: |
  | 1 1 1 1 1 0 0 0 0 0 0 0 0 1 0 | OFFSET OF THE REGISTER |

  **Format:** MFC0 *rs*

  **Purpose: ** To load a number from one of the registers in coprocessor 0.

  **Description:** Register ← CP0[*offset*], *offset* = 0 for *rs* is CAUSE, *offset* = 1 for *rs* is EPC

  **Operation:** //todo

  **Exceptions:** None

* **NO OPERATION**

  |              INBOX              |
  | :-----------------------------: |
  | 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 |

  **Format:** NOP

  **Purpose: ** //todo

  **Description:** None

  **Operation:** //todo

  **Exceptions:** None

* **OUTBOX**

  |              INBOX              |
  | :-----------------------------: |
  | 1 1 1 1 1 0 0 0 0 0 0 0 0 0 0 1 |

  **Format:** OUTBOX

  **Purpose: ** //todo

  **Description:** Outbox Register ← Register

  **Operation:** //todo

  **Exceptions:** Empty Memory

* **SUBTRACT Number**

  |  FUNC   | INDEXED |        OPCODE         |
  | :-----: | :-----: | :-------------------: |
  | 1 0 0 1 |    0    | ADDRESS OF THE NUMBER |

  **Format:** SUB *address*

  **Purpose: ** //todo

  **Description:** Register ← Register + Memory[*address*]

  **Operation:** //todo

  **Exceptions:** Algebraic Overflow, Empty Memory

* **SUBTRACT Indexed Number**

  |  FUNC   | INDEXED |            OPCODE             |
  | :-----: | :-----: | :---------------------------: |
  | 1 0 0 1 |    1    | ADDRESS OF THE NUMBER'S INDEX |

  **Format:** SUB [*address*]

  **Purpose: ** //todo

  **Description:** Register ← Register + Memory[Memory[*address*]]

  **Operation:** //todo

  **Exceptions:** Algebraic Overflow, Empty Memory

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

