# Instruction Set

## Overview

```assembly
N-instrcutions:
	0001 add (number/indexed number)
	0010 bumpup (number/indexed number)
	0011 bumpdown (number/indexed number)
	0100 copyfrom (number/indexed number)
	0101 copyto (number/indexed number)
	0110 sub (number/indexed number)
	
J-instrcutions:
	1000 jump
	1001 jumpn
	1010 jumpz
	
S-instrcutions:
	inbox 
	outbox
	mfcause
	mfepc
	mtcause
	mtepc
	nop
	eret
```



The detailed information of instructions are as follow:

## N-instructions

* **ADD Number**

  |  FUNC   | INDEXED |      OPCODE       |
  | :-----: | :-----: | :---------------: |
  | 0 0 0 1 |    0    | ADDRESS OF NUMBER |

  **Format:** ADD *address*

  **Purpose:** To add a number to the handheld number. The number is indexed by *address*. If overflow occurs, or if there isn't any number at that address then trap.

  **Description:** Register ← Register + Memory[*address*]

  **Operation:** 

  ```
  true_address <- address << 1
  addend <- Memory[true_address]
  Register <- Register + addend
  ```

  **Exceptions:** Algebraic Overflow, Empty Memory, Empty Register, Permisson Denied

  

* **ADD Indexed Number**

  |  FUNC   | INDEXED |            OPCODE             |
  | :-----: | :-----: | :---------------------------: |
  | 0 0 0 1 |    1    | ADDRESS OF THE NUMBER'S INDEX |

  **Format:** ADD [*address*]

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

  **Exceptions:** Algebraic Overflow, Empty Memory, Empty Register, Permisson Denied

  

* **BUMPUP Number**

  |  FUNC   | INDEXED |        OPCODE         |
  | :-----: | :-----: | :-------------------: |
  | 0 0 1 0 |    0    | ADDRESS OF THE NUMBER |

  **Format:** BUMPUP *address*

  **Purpose:** To add a number to one and replace the handheld number with the result. The number is indexed by *address*. If overflow occurs, or if no number is found then trap.

  **Description:** Register ← Memory[*address*] + 0x0001

  **Operation:** 

  ```
  true_address <- address << 1
  target <- Memory[true_address]
  Register <- target + 0x0001
  ```

  **Exceptions:** Algebraic Overflow, Empty Memory, Permisson Denied

  

* **BUMPUP Indexed Number**

  |  FUNC   | INDEXED |            OPCODE             |
  | :-----: | :-----: | :---------------------------: |
  | 0 0 1 0 |    1    | ADDRESS OF THE NUMBER'S INDEX |

  **Format:** BUMPUP [*address*]

  **Purpose:** To add a number to one and replace the handheld number with the result. The number is indexed by the value stored at *address*. If overflow occurs, or if no number is found then trap.

  **Description:** Register ← Memory[Memory[*address*]] + 0x0001

  **Operation:** //todo

  **Exceptions:** Algebraic Overflow, Empty Memory, Permisson Denied

  

* **BUMPDOWN Number**

  |  FUNC   | INDEXED |        OPCODE         |
  | :-----: | :-----: | :-------------------: |
  | 0 0 1 1 |    0    | ADDRESS OF THE NUMBER |

  **Format:** BUMPDN *address*

  **Purpose:** To subtract one from a number and replace the handheld number with the result. The number is indexed by *address*. If overflow occurs, or if no number is found then trap.

  **Description:** Register ← Memory[*address*] - 0x0001

  **Operation:** //todo

  **Exceptions:** Algebraic Overflow, Empty Memory, Permisson Denied

  

* **BUMPDOWN Indexed Number**

  |  FUNC   | INDEXED |        OPCODE         |
  | :-----: | :-----: | :-------------------: |
  | 0 0 1 1 |    1    | ADDRESS OF THE NUMBER |

  **Format:** BUMPDN *address*

  **Purpose:** To subtract one from a number and replace the handheld number with the result. The number is indexed by *address*. If overflow occurs, or if no number is found then trap.

  **Description:** Register ← Memory[Memory[*address*]] - 0x0001

  **Operation:** //todo

  **Exceptions:** Algebraic Overflow, Empty Memory, Permisson Denied

  

* **COPYFROM Address**

  |  FUNC   | INDEXED |               OPCODE               |
  | :-----: | :-----: | :--------------------------------: |
  | 0 1 0 0 |    0    | ADDRESS OF THE NUMBER TO COPY FROM |

  **Format:** COPYFROM *address*

  **Purpose:** To replace the handheld number with the one stored at *address*. If no number is found then trap.

  **Description:** Register ← Memory[*address*]

  **Operation:** //todo

  **Exceptions:** Empty Memory, Permisson Denied

  

* **COPYFROM Indexed Address**

  |  FUNC   | INDEXED |            OPCODE             |
  | :-----: | :-----: | :---------------------------: |
  | 0 1 0 0 |    1    | ADDRESS OF THE NUMBER'S INDEX |

  **Format:** COPYFROM [*address*]

  **Purpose:** To replace the handheld number with the stored one. The number is indexed by the value stored at *address*. If no number is found then trap.

  **Description:** Register ← Memory[Memory[*address*]]

  **Operation:** //todo

  **Exceptions:** Empty Memory, Permisson Denied

  

* **COPYTO Address**

  |  FUNC   | INDEXED |       OPCODE       |
  | :-----: | :-----: | :----------------: |
  | 0 1 0 1 |    0    | ADDRESS TO COPY TO |

  **Format:** COPYTO *address*

  **Purpose:** To replace the number stored at *address* with the handheld one. If there's no number in hand then trap.

  **Description:** Memory[*address*] ← Register

  **Operation:** //todo

  **Exceptions:** Empty Register, Permisson Denied

  

* **COPYTO Indexed Address**

  |  FUNC   | INDEXED |            OPCODE             |
  | :-----: | :-----: | :---------------------------: |
  | 0 1 0 1 |    1    | ADDRESS OF THE NUMBER'S INDEX |

  **Format:** COPYTO [*address*]

  **Purpose:** To replace the stored number with the handheld one. The number is indexed by the value stored at *address*. If there's no number in hand then trap.

  **Description:** Memory[Memory[*address*]] ← Register

  **Operation:** //todo

  **Exceptions:** Empty Register, Permisson Denied

  
  
* **SUBTRACT Number**

  |  FUNC   | INDEXED |        OPCODE         |
  | :-----: | :-----: | :-------------------: |
  | 0 1 1 0 |    0    | ADDRESS OF THE NUMBER |

  **Format:** SUB *address*

  **Purpose: ** //todo

  **Description:** Register ← Register - Memory[*address*]

  **Operation:** //todo

  **Exceptions:** Algebraic Overflow, Empty Memory, Empty Register, Permisson Denied

  

* **SUBTRACT Indexed Number**

  |  FUNC   | INDEXED |            OPCODE             |
  | :-----: | :-----: | :---------------------------: |
  | 0 1 1 0 |    1    | ADDRESS OF THE NUMBER'S INDEX |

  **Format:** SUB [*address*]

  **Purpose: ** //todo

  **Description:** Register ← Register - Memory[Memory[*address*]]

  **Operation:** //todo

  **Exceptions:** Algebraic Overflow, Empty Memory, Empty Register, Permisson Denied

  

## J-instrcutions

* **JUMP**

  |  FUNC   |            ADDR            |
  | :-----: | :------------------------: |
  | 1 0 0 0 | ADDRESS OF THE INSTRUCTION |

  **Format:** JUMP *label*

  **Purpose: ** //todo

  **Description:** Program Counter ← Address << 1

  **Operation:** //todo

  **Exceptions:** Permisson Denied

  

* **JUMP if NEGATIVE**

  |  FUNC   |            ADDR            |
  | :-----: | :------------------------: |
  | 1 0 0 1 | ADDRESS OF THE INSTRUCTION |

  **Format:** JUMPN *label*

  **Purpose: ** //todo

  **Description:** if Register < 0 then JUMP

  **Operation:** //todo

  **Exceptions:** Permisson Denied

  

* **JUMP if ZERO**

  |  FUNC   |            ADDR            |
  | :-----: | :------------------------: |
  | 1 0 1 0 | ADDRESS OF THE INSTRUCTION |

  **Format:** JUMPN *label*

  **Purpose: ** //todo

  **Description:** if Register == 0 then JUMP

  **Operation:** //todo

  **Exceptions:** Permisson Denied



## S-instructions

* **INBOX**

  |              INBOX              |
  | :-----------------------------: |
  | 1 1 1 1 0 0 0 0 0 0 0 0 0 0 0 1 |

  **Format:** INBOX

  **Purpose: ** To replace the handheld number with the one picked from inbox. If there's nothing left in inbox then trap.

  **Description:** Register ← Inbox Register

  **Operation:** //todo

  **Exceptions:** Inbox Trap

  

* **OUTBOX**

  |             OUTBOX              |
| :-----------------------------: |
  | 1 1 1 1 0 0 0 0 0 0 0 0 0 0 1 0 |
  
  **Format:** OUTBOX

  **Purpose: ** //todo

  **Description:** Outbox Register ← Register

  **Operation:** //todo

  **Exceptions:** Outbox Trap

  

* **MOVE FROM CAUSE REGISTER** *(kernel mode instruction)*

  |             MFCAUSE             |
  | :-----------------------------: |
  | 1 1 1 1 0 0 0 0 0 0 0 0 0 1 0 0 |

  **Format:** MFCAUSE

  **Purpose: ** To load a number from CAUSE register in coprocessor 0.

  **Description:** Register ← CP0[CAUSE]

  **Operation:** //todo

  **Exceptions:** Permisson Denied



* **MOVE FROM EPC REGISTER** *(kernel mode instruction)*

  |              MFEPC              |
  | :-----------------------------: |
  | 1 1 1 1 0 0 0 0 0 0 0 0 0 1 0 1 |

  **Format:** MFEPC

  **Purpose: ** To load a number from EPC register in coprocessor 0.

  **Description:** Register ← CP0[EPC]

  **Operation:** //todo

  **Exceptions:** Permisson Denied



* **MOVE TO CAUSE REGISTER** *(kernel mode instruction)*

  |             MTCAUSE             |
  | :-----------------------------: |
  | 1 1 1 1 0 0 0 0 0 0 0 0 0 1 1 0 |

  **Format:** MTCAUSE

  **Purpose: ** To load a number to CAUSE register in coprocessor 0.

  **Description:** CP0[CAUSE] ← Register 

  **Operation:** //todo

  **Exceptions:** Permisson Denied



* **MOVE TO EPC REGISTER ** *(kernel mode instruction)*

  |              MFEPC              |
  | :-----------------------------: |
  | 1 1 1 1 0 0 0 0 0 0 0 0 0 1 1 1 |

  **Format:** MTEPC

  **Purpose: ** To load a number to EPC register in coprocessor 0.

  **Description:** CP0[EPC] ← Register 

  **Operation:** //todo

  **Exceptions:** Permisson Denied



* **NO OPERATION**

  |               NOP               |
  | :-----------------------------: |
  | 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 |

  **Format:** NOP

  **Purpose: ** //todo

  **Description:** None

  **Operation:** //todo

  **Exceptions:** None

  

* **EXCEPTION RETURN** *(kernel mode instruction)*

  |              ERET               |
  | :-----------------------------: |
  | 1 1 1 1 0 0 0 0 0 0 0 0 0 0 0 0 |

  **Format:** ERET

  **Purpose: ** //todo

  **Description:** Program Counter ← CP0[EPC]

  **Operation:** //todo

  **Exceptions:** Permisson Denied



* **JUMP REGISTER** *(kernel mode instruction)*

  |              JUMPR              |
  | :-----------------------------: |
  | 1 1 1 1 0 0 0 0 0 0 0 0 1 0 0 0 |

  **Format:** JUMPR

  **Purpose: ** //todo

  **Description:**  JUMP to address in Register

  **Operation:** //todo

  **Exceptions:** Permisson Denied