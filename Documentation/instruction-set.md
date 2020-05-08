# Instruction Set

## Overview

```assembly
N-instrcutions:
	001 add 	(number/indexed number)
	010 bumpup 	(number/indexed number)
	011 bumpdown (number/indexed number)
	100 copyfrom (number/indexed number)
	101 copyto 	(number/indexed number)
	110 sub 	(number/indexed number)
	
J-instrcutions:
	0001 jump
	1110 jumpn
	1111 jumpz
	
S-instrcutions:
	inbox 	0001
	outbox	0010
	mfcause	0100
	mfepc	0101
	mtcause	0110
	mtepc	0111
	nop		0000
	eret	0011
	jumpr	1000
```



The detailed information of instructions are as follow:

## N-instructions

* **ADD Number**

  | FUNC  | INDEXED |      OPCODE       |
  | :---: | :-----: | :---------------: |
  | 0 0 1 |    0    | ADDRESS OF NUMBER |

  **Format:** ADD *address*

  **Purpose:** To add a number to the handheld number. The number is indexed by *address*. If overflow occurs, or if there isn't any number at that address then trap.

  **Description:** Register ← Register + Memory[*address*]

  **Operation:** 

  ```
  true_address <- address << 1
  Register <- Register + Memory[true_address]
  ```
  
**Exceptions:** Algebraic Overflow, Empty Memory, Empty Register, Permisson Denied
  

  
* **ADD Indexed Number**

  | FUNC  | INDEXED |            OPCODE             |
  | :---: | :-----: | :---------------------------: |
  | 0 0 1 |    1    | ADDRESS OF THE NUMBER'S INDEX |

  **Format:** ADD [*address*]

  **Purpose:** To add a number to the handheld number. The number is indexed by the value stored at *address*. If overflow occurs, or if no number is found then trap.

  **Description:** Register ← Register + Memory[Memory[*address*]]

  **Operation:** 

  ```
  true_address <- address << 1
  true_address <- Memory[true_address]
  true_address <- true_address << 1
  Register <- Register + Memory[true_address]
  ```
  
**Exceptions:** Algebraic Overflow, Empty Memory, Empty Register, Permisson Denied
  

  
* **BUMPUP Number**

  | FUNC  | INDEXED |        OPCODE         |
  | :---: | :-----: | :-------------------: |
  | 0 1 0 |    0    | ADDRESS OF THE NUMBER |

  **Format:** BUMPUP *address*

  **Purpose:** To add a number to one and replace the handheld number with the result. The number is indexed by *address*. If overflow occurs, or if no number is found then trap.

  **Description:** Register ← Memory[*address*] + 0x0001

  **Operation:** 

  ```
  true_address <- address << 1
  Register <- Memory[true_address] + 0x0001
  ```
  
**Exceptions:** Algebraic Overflow, Empty Memory, Permisson Denied
  

  
* **BUMPUP Indexed Number**

  | FUNC  | INDEXED |            OPCODE             |
  | :---: | :-----: | :---------------------------: |
  | 0 1 0 |    1    | ADDRESS OF THE NUMBER'S INDEX |

  **Format:** BUMPUP [*address*]

  **Purpose:** To add a number to one and replace the handheld number with the result. The number is indexed by the value stored at *address*. If overflow occurs, or if no number is found then trap.

  **Description:** Register ← Memory[Memory[*address*]] + 0x0001

  **Operation:** 

  ```
  true_address <- address << 1
  true_address <- Memory[true_address]
  true_address <- true_address << 1
  Register <- Memory[true_address] + 0x0001
  ```

  **Exceptions:** Algebraic Overflow, Empty Memory, Permisson Denied

  

* **BUMPDOWN Number**

  | FUNC  | INDEXED |        OPCODE         |
  | :---: | :-----: | :-------------------: |
  | 0 1 1 |    0    | ADDRESS OF THE NUMBER |

  **Format:** BUMPDN *address*

  **Purpose:** To subtract one from a number and replace the handheld number with the result. The number is indexed by *address*. If overflow occurs, or if no number is found then trap.

  **Description:** Register ← Memory[*address*] - 0x0001

  **Operation:** 

  ```
  true_address <- address << 1
  Register <- Memory[true_address] - 0x0001
  ```

  **Exceptions:** Algebraic Overflow, Empty Memory, Permisson Denied

  

* **BUMPDOWN Indexed Number**

  | FUNC  | INDEXED |        OPCODE         |
  | :---: | :-----: | :-------------------: |
  | 0 1 1 |    1    | ADDRESS OF THE NUMBER |

  **Format:** BUMPDN *address*

  **Purpose:** To subtract one from a number and replace the handheld number with the result. The number is indexed by *address*. If overflow occurs, or if no number is found then trap.

  **Description:** Register ← Memory[Memory[*address*]] - 0x0001

  **Operation:** 

  ```
  true_address <- address << 1
  true_address <- Memory[true_address]
  true_address <- true_address << 1
  Register <- Memory[true_address] - 0x0001
  ```

  **Exceptions:** Algebraic Overflow, Empty Memory, Permisson Denied

  

* **COPYFROM Address**

  | FUNC  | INDEXED |               OPCODE               |
  | :---: | :-----: | :--------------------------------: |
  | 1 0 0 |    0    | ADDRESS OF THE NUMBER TO COPY FROM |

  **Format:** COPYFROM *address*

  **Purpose:** To replace the handheld number with the one stored at *address*. If no number is found then trap.

  **Description:** Register ← Memory[*address*]

  **Operation:** 

  ```
  true_address <- address << 1
  Register <- Memory[true_address]
  ```

  **Exceptions:** Empty Memory, Permisson Denied

  

* **COPYFROM Indexed Address**

  | FUNC  | INDEXED |            OPCODE             |
  | :---: | :-----: | :---------------------------: |
  | 1 0 0 |    1    | ADDRESS OF THE NUMBER'S INDEX |

  **Format:** COPYFROM [*address*]

  **Purpose:** To replace the handheld number with the stored one. The number is indexed by the value stored at *address*. If no number is found then trap.

  **Description:** Register ← Memory[Memory[*address*]]

  **Operation:** 

  ```
  true_address <- address << 1
  true_address <- Memory[true_address]
  true_address <- true_address << 1
  Register <- Memory[true_address]
  ```

  **Exceptions:** Empty Memory, Permisson Denied

  

* **COPYTO Address**

  | FUNC  | INDEXED |       OPCODE       |
  | :---: | :-----: | :----------------: |
  | 1 0 1 |    0    | ADDRESS TO COPY TO |

  **Format:** COPYTO *address*

  **Purpose:** To replace the number stored at *address* with the handheld one. If there's no number in hand then trap.

  **Description:** Memory[*address*] ← Register

  **Operation:** 

  ```
  true_address <- address << 1
  Memory[true_address] <- Register
  ```

  **Exceptions:** Empty Register, Permisson Denied

  

* **COPYTO Indexed Address**

  | FUNC  | INDEXED |            OPCODE             |
  | :---: | :-----: | :---------------------------: |
  | 1 0 1 |    1    | ADDRESS OF THE NUMBER'S INDEX |

  **Format:** COPYTO [*address*]

  **Purpose:** To replace the stored number with the handheld one. The number is indexed by the value stored at *address*. If there's no number in hand then trap.

  **Description:** Memory[Memory[*address*]] ← Register

  **Operation:** 

  ```
true_address <- address << 1
  true_address <- Memory[true_address]
  true_address <- true_address << 1
  Memory[true_address] <- Register
  ```
  
  **Exceptions:** Empty Register, Permisson Denied
  
  
  
* **SUBTRACT Number**

  | FUNC  | INDEXED |        OPCODE         |
  | :---: | :-----: | :-------------------: |
  | 1 1 0 |    0    | ADDRESS OF THE NUMBER |

  **Format:** SUB *address*

  **Purpose: ** To subtract a number to the handheld number. The number is indexed by *address*. If overflow occurs, or if there isn't any number at that address then trap.

  **Description:** Register ← Memory[*address*] - Register

  **Operation:** 

  ```
  true_address <- address << 1
  Register <- Memory[true_address] - Register
  ```

  **Exceptions:** Algebraic Overflow, Empty Memory, Empty Register, Permisson Denied

  

* **SUBTRACT Indexed Number**

  | FUNC  | INDEXED |            OPCODE             |
  | :---: | :-----: | :---------------------------: |
  | 1 1 0 |    1    | ADDRESS OF THE NUMBER'S INDEX |

  **Format:** SUB [*address*]

  **Purpose: ** To subtract a number to the handheld number. The number is indexed by the value stored at *address*. If overflow occurs, or if no number is found then trap.

  **Description:** Register ← Memory[Memory[*address*]] - Register

  **Operation:** 

  ```
true_address <- address << 1
  true_address <- Memory[true_address]
  true_address <- true_address << 1
  Register <- Memory[true_address] - Register
  ```
  
  **Exceptions:** Algebraic Overflow, Empty Memory, Empty Register, Permisson Denied
  
  

## J-instrcutions

* **JUMP**

  |  FUNC   |            ADDR            |
  | :-----: | :------------------------: |
  | 0 0 0 1 | ADDRESS OF THE INSTRUCTION |

  **Format:** JUMP *label*

  **Purpose: ** To jump to instruction at *address* unconditionally.

  **Description:** Program Counter ← Address << 1

  **Operation:** 

  ```
  true_address <- address << 1
  PC <- true_address
  ```

  **Exceptions:** Permisson Denied

  

* **JUMP if NEGATIVE**

  |  FUNC   |            ADDR            |
  | :-----: | :------------------------: |
  | 1 1 1 0 | ADDRESS OF THE INSTRUCTION |

  **Format:** JUMPN *label*

  **Purpose: ** To jump to instruction at *address* if value of Register is negative.

  **Description:** if Register < 0 then JUMP

  **Operation:** 

  ```
  if (Register < 0)
  	true_address <- address << 1
  	PC <- true_address
  else
  	PC <- PC + 2
  ```

  **Exceptions:** Permisson Denied

  

* **JUMP if ZERO**

  |  FUNC   |            ADDR            |
  | :-----: | :------------------------: |
  | 1 1 1 1 | ADDRESS OF THE INSTRUCTION |

  **Format:** JUMPZ *label*

  **Purpose: ** To jump to instruction at *address* if value of Register is zero.

  **Description:** if Register == 0 then JUMP

  **Operation:** 

  ```
  if (Register == 0)
  	true_address <- address << 1
  	PC <- true_address
  else
  	PC <- PC + 2
  ```
  
  **Exceptions:** Permisson Denied



## S-instructions

* **INBOX**

  |              INBOX              |
  | :-----------------------------: |
  | 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1 |

  **Format:** INBOX

  **Purpose: ** To replace the handheld number with the one picked from inbox. If there's nothing left in inbox then trap.

  **Description:** Register ← Memory[Inbox]

  **Operation:** 

  ```
  Register <- Memory[Inbox]
  ```

  **Exceptions:** Inbox Trap

  

* **OUTBOX**

  |             OUTBOX              |
  | :-----------------------------: |
  | 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1 0 |

  **Format:** OUTBOX

  **Purpose: ** To put the handheld number into the outbox. If there's nothing left in register then trap.

  **Description:** Memory[Outbox] ← Register

  **Operation:** 

  ```
  Memory[Outbox] <- Register
  ```

  **Exceptions:** Outbox Trap

  

* **MOVE FROM CAUSE REGISTER** *(kernel mode instruction)*

  |             MFCAUSE             |
  | :-----------------------------: |
  | 0 0 0 0 0 0 0 0 0 0 0 0 0 1 0 0 |

  **Format:** MFCAUSE

  **Purpose: ** To load a number from CAUSE register in coprocessor 0.

  **Description:** Register ← CP0[CAUSE]

  **Operation:** 

  ```
  Register <- CP0[CAUSE]
  ```
  
  **Exceptions:** Permisson Denied



* **MOVE FROM EPC REGISTER** *(kernel mode instruction)*

  |              MFEPC              |
  | :-----------------------------: |
  | 0 0 0 0 0 0 0 0 0 0 0 0 0 1 0 1 |

  **Format:** MFEPC

  **Purpose: ** To load a number from EPC register in coprocessor 0.

  **Description:** Register ← CP0[EPC]

  **Operation:** 

  ```
  Register <- CP0[EPC]
  ```
  
  **Exceptions:** Permisson Denied



* **MOVE TO CAUSE REGISTER** *(kernel mode instruction)*

  |             MTCAUSE             |
  | :-----------------------------: |
  | 0 0 0 0 0 0 0 0 0 0 0 0 0 1 1 0 |

  **Format:** MTCAUSE

  **Purpose: ** To load a number to CAUSE register in coprocessor 0.

  **Description:** CP0[CAUSE] ← Register 

  **Operation:** 

  ```
  CP0[CAUSE] <- Register
  ```
  
  **Exceptions:** Permisson Denied



* **MOVE TO EPC REGISTER ** *(kernel mode instruction)*

  |              MFEPC              |
  | :-----------------------------: |
  | 0 0 0 0 0 0 0 0 0 0 0 0 0 1 1 1 |

  **Format:** MTEPC

  **Purpose: ** To load a number to EPC register in coprocessor 0.

  **Description:** CP0[EPC] ← Register 

  **Operation:** 

  ```
  CP0[EPC] <- Register
  ```
  
  **Exceptions:** Permisson Denied



* **NO OPERATION**

  |               NOP               |
  | :-----------------------------: |
  | 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 |

  **Format:** NOP

  **Purpose: ** To do nothing.

  **Description:** None

  **Operation:** None

  **Exceptions:** None

  

* **EXCEPTION RETURN** *(kernel mode instruction)*

  |              ERET               |
  | :-----------------------------: |
  | 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1 1 |

  **Format:** ERET

  **Purpose: ** To return from exception by jump to address in EPC register in coprocessor 0.

  **Description:** Program Counter ← CP0[EPC]

  **Operation:** 

  ```
  PC <- CP0[EPC]
  ```
  
  **Exceptions:** Permisson Denied



* **JUMP REGISTER** *(kernel mode instruction)*

  |              JUMPR              |
  | :-----------------------------: |
  | 0 0 0 0 0 0 0 0 0 0 0 0 1 0 0 0 |

  **Format:** JUMPR

  **Purpose: ** To jump to instruction at address in Register.

  **Description:**  JUMP to address in Register

  **Operation:** 

  ```
  PC <- Register
  ```
  
  **Exceptions:** Permisson Denied
  
  