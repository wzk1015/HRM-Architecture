n_func = {
    "add"     : "0001",
    "bumpup"  : "0010",
    "bumpdown": "0011",
    "copyfrom": "0100",
    "copyto"  : "0101",
    "sub"     : "0110",
}

s_func = {
    "eret"   : "1111000000000000",
    "inbox"  : "1111000000000001",
    "outbox" : "1111000000000010",
    "mfcause": "1111000000000100",
    "mfepc"  : "1111000000000101",
    "mtcause": "1111000000000110",
    "mtepc"  : "1111000000000111",
    "jumpr"  : "1111000000001000",
    "nop"    : "0000000000000000"
}

j_func = {
    "jump" : "1000",
    "jumpn": "1001",
    "jumpz": "1010"
}

labels = {}
macros = {}
addrs = {}
mips = []
text_pc = 0x300


def int_to_bin(integer, bits):
    return bin(integer)[2:].rjust(bits, "0")  #split "0b" and extend to 11 bits


def is_label(line):
    return line.endswith(":")


def remove_annotation_and_empty_lines():
    global lines

    for i in range(len(lines)):
        if "#" in lines[i]:  #remove annotation
            idx = lines[i].index("#")
            lines[i] = lines[i][:idx].strip()
        else:
            lines[i] = lines[i].strip()
    new_lines = lines[:]
    for i in range(len(lines)):
        if lines[i].strip() == "":  #remove empty line (including annotation line)
            new_lines.remove(lines[i])
    lines = new_lines[:]
    #print(lines)


def data_alloc():
    global lines
    global addrs
    global text_pc

    #.addr
    for i in range(len(lines)):
        line = lines[i].strip("\n").strip().lower()
        if line.startswith(".addr"):
            name = line.split()[1]
            addr = line.split()[2]
            addrs[name] = hex(int(int(addr, 16)/2))
            print("{}: {}".format(name, hex(int(addr, 16))))

    if "kernel_data_base" in addrs.keys():
        start_addr = int(addrs["kernel_data_base"], 16)
    elif "data_base" in addrs.keys():
        start_addr = int(addrs["data_base"], 16)
    else:
        start_addr = 0x0
    current_start_addr = start_addr
    #.data
    i, j = -1, -1
    for i in range(len(lines)):
        line = lines[i].strip("\n").strip().lower()
        if line == ".data":
            find_end = False
            for j in range(i+1, len(lines)):
                if lines[j].strip().startswith("."):
                    find_end = True
                    break
            if not find_end:
                print(".data end not found")
                raise RuntimeError
            break
    if (i, j) == (-1, -1):
        return
    for k in range(i+1, j):
        line = lines[k].strip("\n").strip().lower()
        name = line.split(":")[0]
        type = line.split()[1]
        addr = line.split()[2]
        if type == ".space":
            addrs[name] = hex(current_start_addr)
            current_start_addr += int(addr)
            if current_start_addr-start_addr > 0x100:
                print("alloc memory out of range")
                raise MemoryError
        else:
            print("wrong .data type: {}".format(type))
            raise TypeError
        print("{}: {}".format(name, addrs[name]))

    for i in range(len(lines)):
        for name in addrs.keys():
            lines[i] = lines[i].replace(name, addrs[name])

    if "handler_base" in addrs.keys():
        text_pc = int(addrs["handler_base"], 16)
    elif "kernel_text_base" in addrs.keys():
        text_pc = int(addrs["kernel_text_base"], 16)
    elif "text_base" in addrs.keys():
        text_pc = int(addrs["text_base"], 16)
    else:
        text_pc = 0x300


def macro_parse(lines, i, j):
    global macros
    start = lines[i].strip("\n").strip().lower()
    macro_name = start.split()[1].split("(")[0]
    parameters = [m.strip() for m in start.split("(")[1].split(")")[0].split(",")]
    text = [lines[k] for k in range(i+1, j)]
    macros[macro_name] = {"text": text, "parameters": parameters}


def macro_define():
    global lines
    #define macro
    for i in range(len(lines)):
        line = lines[i].strip("\n").strip().lower()
        if line.startswith(".macro"):
            find_end = False
            for j in range(i+1, len(lines)):
                if lines[j].strip("\n").strip().lower() == ".end_macro":
                    macro_parse(lines, i, j)
                    find_end = True
                    break
            if not find_end:
                print("macro end not found")
                raise RuntimeError
    #replace macro
    new_lines: list = lines[:]
    k = 0
    for i in range(len(lines)):
        line = lines[i].strip("\n").strip().lower()
        for macro_name in macros.keys():
            if line.startswith(macro_name):
                new_lines.remove(lines[i])
                k -= 1
                real_parameters = [m.strip() for m in line.split("(")[1].split(")")[0].split(",")]
                para_dict = {}
                for j in range(len(macros[macro_name]["parameters"])):
                    para_dict[macros[macro_name]["parameters"][j]] = real_parameters[j]
                text_insert = []
                for text in macros[macro_name]["text"]:
                    text_replace = text
                    for l in para_dict.keys():
                        text_replace = text_replace.replace(l, para_dict[l])
                    text_insert.append(text_replace)
                for text_line in text_insert:
                    new_lines.insert(k+1, text_line)
                    k += 1
        k += 1

    lines = new_lines[:]


def make_labels():
    global lines
    pc = text_pc

    find_text = False
    for i in range(len(lines)):
        line = lines[i].strip("\n").strip().lower()
        if line == ".text":
            find_text = True
            continue
        if not find_text:
            continue
        if i < len(lines)-1:
            next_line = lines[i+1].strip("\n").strip()
            if is_label(line) and not is_label(next_line):
                labels[line.strip(":")] = pc
                prev = i-1
                while prev >= 0:
                    previous_line = lines[prev].strip("\n").strip()
                    if not is_label(previous_line):
                        break
                    labels[previous_line.strip(":")] = pc
                    prev -= 1
            elif not is_label(line):
                pc += 0x2
        else:
            if is_label(line):
                labels[line.strip(":")] = pc
                prev = i-1
                while prev >= 0:
                    previous_line = lines[prev].strip("\n").strip()
                    if not is_label(previous_line):
                        break
                    labels[previous_line.strip(":")] = pc
                    prev -= 1
            elif not is_label(line):
                pc += 0x2
    if not find_text:
        print(".text not found")
        raise RuntimeError


def parse_instructions():
    global lines
    global assemble_code
    global machine_code_bin
    pc = text_pc

    find_text = True
    for i in range(len(lines)):
        line = lines[i].strip()
        if line.startswith("."):  #if no pseudo found, then all are .text
            find_text = False
            break
    for i in range(len(lines)):
        line = lines[i].strip("\n").strip().lower()
        if line == ".text":
            find_text = True
            continue
        if not find_text:
            continue
        if not is_label(line):
            if line in s_func.keys():
                ir = line
                assemble_code.append({"ir": ir, "type": "s", "pc": hex(pc)})
                machine_code_bin.append(s_func[ir])
            else:
                #print(line)
                ir = line.split()[0]
                op = line.split()[1]

                if ir in n_func.keys():
                    if op.startswith("[") and op.endswith("]"):
                        op = op.strip("[").strip("]")
                        indexed = "1"
                    else:
                        indexed = "0"
                    if op.startswith("0x"):
                        imm = int(op, 16)
                    else:
                        imm = int(op)
                    if imm >= 0x800:
                        print("number to large: {}".format(op))
                        raise ValueError
                    bin_imm = int_to_bin(imm, 11)
                    assemble_code.append({"ir": ir, "type": "n", "pc": hex(pc), "indexed": indexed, "imm": str(imm)})
                    machine_code_bin.append(n_func[ir]+indexed+bin_imm)

                elif ir in j_func.keys():
                    if op not in labels.keys():
                        print("label not found: '{}'".format(op))
                        raise RuntimeError
                    addr = labels[op]
                    bin_addr = int_to_bin(addr, 12)
                    assemble_code.append({"ir": ir, "type": "j", "pc": hex(pc), "addr": hex(addr)})
                    machine_code_bin.append(j_func[ir]+bin_addr)
            pc += 0x2


def hrm2mips():
    global mips
    global assemble_code

    mips_label = {}
    jump_label = {}
    label_count = 1
    for ac in assemble_code:
        ir = ac["ir"]
        if ir in j_func.keys():
            addr = ac["addr"]
            if addr not in mips_label.keys():
                mips_label[addr] = ["label"+str(label_count)]
            else:
                mips_label[addr].append("label"+str(label_count))
            jump_label[ac["pc"]] = "label"+str(label_count)
            label_count += 1

    for ac in assemble_code:
        ir = ac["ir"]
        if ac["pc"] in mips_label.keys():
            for label in mips_label[ac["pc"]]:
                mips.append(label+":")
        if ir == "add":
            if ac["indexed"] == 0:
                mips.append("li $14, {}".format(ac["imm"]))
                mips.append("lb $12, 0($14)")
                mips.append("add $11, $11, $12")
            else:
                mips.append("li $14, {}".format(ac["imm"]))
                mips.append("lb $13, 0($14)")
                mips.append("lb $12, 0($13)")
                mips.append("add $11, $11, $12")
        elif ir == "sub":
            if ac["indexed"] == 0:
                mips.append("li $14, {}".format(ac["imm"]))
                mips.append("lb $12, 0($14)")
                mips.append("sub $11, $11, $12")
            else:
                mips.append("li $14, {}".format(ac["imm"]))
                mips.append("lb $13, 0($14)")
                mips.append("lb $12, 0($13)")
                mips.append("sub $11, $11, $12")
        elif ir == "bumpup":
            if ac["indexed"] == 0:
                mips.append("li $14, {}".format(ac["imm"]))
                mips.append("lb $12, 0($14)")
                mips.append("add $11, $12, 1")
            else:
                mips.append("li $14, {}".format(ac["imm"]))
                mips.append("lb $13, 0($14)")
                mips.append("lb $12, 0($13)")
                mips.append("add $11, $12, 1")
        elif ir == "bumpdown":
            if ac["indexed"] == 0:
                mips.append("li $14, {}".format(ac["imm"]))
                mips.append("lb $12, 0($14)")
                mips.append("sub $11, $12, 1")
            else:
                mips.append("li $14, {}".format(ac["imm"]))
                mips.append("lb $13, 0($14)")
                mips.append("lb $12, 0($13)")
                mips.append("sub $11, $12, 1")
        elif ir == "copyfrom":
            if ac["indexed"] == 0:
                mips.append("li $14, {}".format(ac["imm"]))
                mips.append("lb $11, 0($14)")
            else:
                mips.append("li $14, {}".format(ac["imm"]))
                mips.append("lb $12, 0($14)")
                mips.append("lb $11, 0($12)")
        elif ir == "copyto":
            if ac["indexed"] == 0:
                mips.append("li $14, {}".format(ac["imm"]))
                mips.append("sb $11, 0($14)")
            else:
                mips.append("li $14, {}".format(ac["imm"]))
                mips.append("lb $12, 0($14)")
                mips.append("sb $11, 0($12)")
        elif ir == "inbox":
            mips.append("li $v0, 5")
            mips.append("syscall")
            mips.append("move $11, $v0")
        elif ir == "outbox":
            mips.append("move $a0, $1")
            mips.append("li $v0, 1")
            mips.append("syscall")
        elif ir == "eret":
            mips.append("eret")
        elif ir == "nop":
            mips.append("nop")
        elif ir == "mfcause":
            mips.append("mfc0 $11,$13")
        elif ir == "mfepc":
            mips.append("mfc0 $11,$14")
        elif ir == "mtcause":
            mips.append("mtc0 $11,$13")
        elif ir == "mtepc":
            mips.append("mtc0 $11,$14")
        elif ir == "nop":
            mips.append("nop")
        elif ir == "jumpr":
            mips.append("jr $11")
        elif ir in j_func.keys():
            addr = jump_label[ac["pc"]]
            if ir == "jump":
                mips.append("j {}".format(addr))
            elif ir == "jumpz":
                mips.append("beq $11, $0, {}".format(addr))
            elif ir == "jumpn":
                mips.append("bltz $11, {}".format(addr))

    mips.append("li $v0,10")
    mips.append("syscall")
    for i in range(len(mips)):
        mips[i] += "\n"
    return mips


if __name__ == '__main__':
    f_in = open("../codes/code.txt")
    f_out1 = open("../codes/machine_code_bin.txt", "w")
    f_out2 = open("../codes/machine_code_hex.txt", "w")
    f_out3 = open("../codes/mips_code.txt", "w")
    lines = f_in.readlines()
    assemble_code = []
    machine_code_bin = []
    machine_code_hex = []

    remove_annotation_and_empty_lines()

    macro_define()

    data_alloc()

    make_labels()

    parse_instructions()

    print(labels)

    for mcb in machine_code_bin:
        machine_code_hex.append(hex(int("0b"+mcb, 2)).rjust(4, "0"))

    for mcb in machine_code_bin:
        print(mcb)
        print(mcb,file=f_out1)

    for mch in machine_code_hex:
        print(mch)
        print(mch, file=f_out2)

    for ac in assemble_code:
        print(ac)
    hrm2mips()
    for mip in mips:
        print(mip, end="")
        print(mip, file=f_out3)