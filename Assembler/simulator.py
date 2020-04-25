import time

n_func = {
    "0001": "add",
    "0010": "bumpup",
    "0011": "bumpdown",
    "0100": "copyfrom",
    "0101": "copyto",
    "0110": "sub",
}

s_func = {
    "1111000000000000": "eret",
    "1111000000000001": "inbox",
    "1111000000000010": "outbox",
    "1111000000000100": "mfcause",
    "1111000000000101": "mfepc",
    "1111000000000110": "mtcause",
    "1111000000000111": "mtepc",
    "1111000000001000": "jumpr",
    "0000000000000000": "nop"
}

j_func = {
    "1000": "jump",
    "1001": "jumpn",
    "1010": "jumpz"
}


def init():
    global mem
    global codes
    with open("memory.txt") as f1:
        lines = f1.readlines()
    with open("../Codes/machine_code_bin.txt") as f2:
        lines2 = f2.readlines()

    for line in lines:
        addr = int(int(line.split()[0], 16)/2)
        value = int(line.split()[1], 16)
        mem[addr] = value

    codes = [line.strip("\n") for line in lines2]


def record():
    print(101*"-", file=log)
    print(101*"-")
    print("PC: {}".format(hex(start_pc + 2*i)), file=log)
    print("PC: {}".format(hex(start_pc + 2*i)))
    try:
        print("instruction: {}".format(codes[i]), file=log)
        print("instruction: {}".format(codes[i]))
    except Exception:
        pass
    print("reg: {}\nmem:".format(hex(reg)), file=log)
    print("reg: {}\nmem:".format(hex(reg)))
    for j in range(8):
        for k in range(16):
            print("|{}|".format(hex(mem[j*16 + k]).center(4)), end=" ", file=log)
            print("|{}|".format(hex(mem[j*16 + k]).center(4)), end=" ")
        print("", file=log)
        print("")

    time.sleep(1)


if __name__ == '__main__':
    log = open("simulator_log.txt", "w")
    empty = 0x8000
    reg = empty
    epc = 0x0
    cause = 0x0
    exception_handler = 0x4180
    static_data_base = 0x200/2
    static_data_limit = 0x300/2
    user_data_base = 0x0/2
    mem = [0x8000 for i in range(32768)]  #each stand for 2 bytes
    codes = []

    init()

    if codes[0].startswith(".addr "):
        start_pc = int(codes[0].split()[1], 16)
    else:
        start_pc = 0x300

    i = 0
    while True:
        record()
        if i > len(codes) or i < 0:
            #permisson denied
            exccode = 5
        else:
            exccode = 0
            code = codes[i]

            if code[:4] in n_func.keys():
                instr = n_func[code[:4]]
                indexed = code[4]
                addr = int(code[5:], 2)
                if indexed == "0":
                    mm = mem[addr]
                else:
                    mm = mem[mem[addr]]

                if instr != "copyto" and (mem[addr] == empty or (indexed == "1" and mem[mem[addr]] == empty)):
                    #empty memory
                    exccode = 2

                if instr in ["add", "sub", "copyto"] and reg == empty:
                    #empty register
                   exccode = 3
                if addr >= static_data_limit or addr < user_data_base or \
                        (instr == "copyto" and static_data_base < addr < static_data_limit):
                    #permisson denied
                    exccode = 5
                if exccode == 0:
                    if instr == "add":
                        reg = mm - reg
                    elif instr == "sub":
                        reg = mm + reg
                    elif instr == "bumpup":
                        reg = mm + 1
                    elif instr == "bumpdown":
                        reg = mm - 1
                    elif instr == "copyfrom":
                        reg = mm
                    elif instr == "copyto":
                        if indexed == "0":
                            mem[addr] = reg
                        else:
                            mem[mem[addr]] = reg

                if reg > 2<<15 - 1 or reg < - 2<<15:
                    #algebraic overflow
                    exccode = 1

            elif code[:4] in j_func.keys():
                instr = j_func[code[:4]]
                addr = int(code[4:], 2)
                if instr == "jump" or (instr == "jumpz" and reg == 0) or (instr == "jumpn" and reg < 0):
                    i = int((addr - start_pc)/2)
                    continue

            elif code in s_func.keys():
                instr = s_func[code]
                if instr in ["eret", "mfcause", "mfepc", "mtcause", "mtepc", "jumpr"]:
                    #permission denied
                    exccode = 5

                if instr == "inbox":
                    #inbox trap
                    exccode = 6
                elif instr == "outbox":
                    #inbox trap
                    exccode = 7
                elif instr == "eret":
                    #clear cause is done by software
                    i = int((epc - start_pc)/2)
                    continue
                elif instr == "mfcause":
                    reg = cause
                elif instr == "mtcause":
                    cause = reg
                elif instr == "mfepc":
                    reg = epc
                elif instr == "mtepc":
                    epc = reg
                elif instr == "jumpr":
                    i = int((reg - start_pc)/2)
                    continue
                elif instr == "nop":
                    pass
            else:
                #unrecgonized instruction
                exccode = 4

        if cause>>15 == 0 and exccode != 0:
            #exception encountered
            print("EXCEPTION {}".format(exccode))
            cause = exccode + 1<<15
            epc = start_pc + 2*i
            i = int((exception_handler - start_pc)/2)
            continue

        i += 1

        if cause>>14%2 == 1:
            #crash
            print("CRASH!")
            exit(-1)

        if i == len(codes):
            print("END")
            exit(0)