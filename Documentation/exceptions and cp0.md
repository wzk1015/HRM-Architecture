#Exceptions and CP0

HRM-CPU supports exception handling by utilizing CP0.  This document explains:

+ Various exceptions supported by HRM-Architecture
+ Design of CP0 registers and functions
+ Behaviors of exception handler
+ Examples of exception handler



## Exceptions Supported

| Exccode | Description              | Source                           |
| ------- | ------------------------ | -------------------------------- |
| 0x1     | Algebraic Overflow       | add sub bumpup bumpdown          |
| 0x2     | Empty Memory             | add sub bumpup bumpdown copyfrom |
| 0x3     | Empty Register           | add sub copyto mtcause mtepc     |
| 0x4     | Unrecgonized Instrcution | *after J-instructions or eret*   |
| 0x5     | Permission Denied        | add sub bumpup bumpdown copyfrom |
| 0x6     | Inbox Trap               | inbox                            |
| 0x7     | Outbox Trap              | outbox                           |

About *Permission Denied*: This can appear when:

* using `mfcause` `mtcause` `mfepc` `mtepc` `eret` in user mode (*EXL*=0)
* jumping to kernel address in user mode
* reading or writing address in user mode

Note that *Permission Denied* will not appear in kernel mode (In fact, **all** exceptions will be ignored in kernel mode).



## CP0 Design

### EPC 

EPC register stores the PC address. When exception encountered, EPC-writing is disabled, so that it stores the PC of the wrong instruction.

Exception handler may read EPC by `mfepc`, and then modify it by `mtepc`. For example, handler may add EPC by 2 to skip the wrong instruction.

When `eret`, PC is set to the address in EPC to return from exception.

Note that when *Unrecgonized Instrcution* encountered （after J-instructions or `eret`）, EPC is set to address of previous instruction's next instruction, i.e. `addr(jump/eret) + 2`.

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

```