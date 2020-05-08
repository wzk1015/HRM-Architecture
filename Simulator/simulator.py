import time

n_func = {
    "001": "add",
    "010": "bumpup",
    "011": "bumpdown",
    "100": "copyfrom",
    "101": "copyto",
    "110": "sub",
}

s_func = {
    "0000000000000011": "eret",
    "0000000000000001": "inbox",
    "0000000000000010": "outbox",
    "0000000000000100": "mfcause",
    "0000000000000101": "mfepc",
    "0000000000000110": "mtcause",
    "0000000000000111": "mtepc",
    "0000000000001000": "jumpr",
    "0000000000000000": "nop"
}

j_func = {
    "0001": "jump",
    "1110": "jumpn",
    "1111": "jumpz"
}


mem = None
codes = None
reg = 0
start_pc = 0


def init(memory_path, machine_code_path):
    global mem
    global codes
    with open(memory_path) as f1:
        lines = f1.readlines()
    with open(machine_code_path) as f2:
        lines2 = f2.readlines()

    for line in lines:
        if line == "\n":
            continue
        addr = int(int(line.split()[0], 16)/2)
        if line.split()[1].startswith("0x"):
            value = int(line.split()[1], 16)
        else:
            value = int(line.split()[1])
        mem[addr] = value

    codes = [line.strip("\n") for line in lines2]


def record(do_log, i, log=None):
    if do_log:
        print(55*"-", file=log)
    print(55*"-")

    if do_log:
        print("PC: {}".format(hex(start_pc + 2*i)), file=log)
    print("PC: {}".format(hex(start_pc + 2*i)))
    try:
        if do_log:
            print("instruction: {}".format(codes[i]), file=log)
        print("instruction: {}".format(codes[i]))
    except Exception:
        pass
    if do_log:
        print("reg: {}\nmem:".format(hex(reg)), file=log)
    print("reg: {}\nmem:".format(hex(reg)))
    for j in range(8):
        for k in range(8):
            if do_log:
                print("|{}|".format(hex(mem[j*8 + k])[2:].rjust(4)), end=" ", file=log)
            print("|{}|".format(hex(mem[j*8 + k])[2:].rjust(4)), end=" ")
        if do_log:
            print("", file=log)
        print("")

    time.sleep(1)


def simulate(memory_path, machine_code_path, log_path=None):
    global mem
    global start_pc
    global reg
    global codes
    do_log = (log_path is not None)
    log = open(log_path, "w") if do_log else None

    empty = 0x8000
    reg = empty
    epc = 0x0
    cause = 0x0
    exception_handler = 0x4180
    static_data_base = 0x800/2
    static_data_limit = 0x1000/2
    user_data_base = 0x0/2
    mem = [0x8000 for i in range(32768)]  #each stand for 2 bytes

    init(memory_path, machine_code_path)

    if codes[0].startswith(".addr "):
        start_pc = int(codes[0].split()[1], 16)
    else:
        start_pc = 0x1000

    i = 0
    while True:
        record(do_log, i, log)
        if i > len(codes) or i < 0:
            #permisson denied
            exccode = 5
        else:
            exccode = 0
            code = codes[i]

            if code[:3] in n_func.keys():
                instr = n_func[code[:3]]
                indexed = code[3]
                addr = int(code[4:], 2)
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
            if do_log:
                print("EXCEPTION {}".format(exccode))
            cause = exccode + 1<<15
            epc = start_pc + 2*i
            i = int((exception_handler - start_pc)/2)
            continue

        i += 1

        if cause>>14%2 == 1:
            #crash
            print("CRASH!")
            if do_log:
                print("CRASH!")
            exit(-1)

        if i == len(codes):
            print("END")
            if do_log:
                print("END")
            exit(0)


if __name__ == '__main__':
    simulate("../Codes/code.txt", "../Codes/machine_code_bin.txt", "simulator_log.txt")