registers = {
    "$zero": "reg0", "$at": "reg1", "$v0": "reg2", "$v1": "reg3", "$a0": "reg4",
    "$a1"  : "reg5", "$a2": "reg6", "$a3": "reg7", "$t0": "reg8", "$t1": "reg9",
    "$t2"  : "reg10", "$t3": "reg11", "$t4": "reg12", "$t5": "reg13", "$t6": "reg14",
    "$t7"  : "reg15", "$s0": "reg16", "$s1": "reg17", "$s2": "reg18", "$s3": "reg19",
    "$s4"  : "reg20", "$s5": "reg21", "$s6": "reg22", "$s7": "reg23", "$t8": "reg24",
    "$t9"  : "reg25", "$k0": "reg26", "$k1": "reg27", "$gp": "reg28", "$sp": "reg29",
    "fp"   : "reg30", "$ra": "reg31"
}

for i in range(32):
    registers["$" + str(i)] = "reg" + str(i)

mips_opcode = {"000000": "special", "001000": "addi", "001001": "addiu", "100001": "lh",
               "100011": "lw", "100101": "lhu", "101001": "sh", "101011": "sw",
               "000100": "beq",
               "000001": "bltz or bgez", "000010": "j", "000011": "jal", "010000": "mfc0 or mtc0 or eret"
               }
bltz_or_bgez = {"00000": "bltz"}
mfc0_or_mtc0_or_eret = {"00000": "mfc0", "00100": "mtc0", "10000": "eret"}
special = {"100000": "add", "100001": "addu", "100010": "sub", "100011": "subu",
           "001000": "jr", "001001": "jalr", "000000": "nop"}

hrm = []
pc = 0


def add(str):
    global hrm, pc
    hrm.append(str)
    pc += 2

def raise_error(line):
    print(line)
    raise ValueError

def initial_data():
    global hrm
    hrm = [".addr zero 0x800", ".addr one 0x802", ".addr two 0x804", ".addr three 0x806",
           ".addr four 0x808", ".addr five 0x80a", ".addr six 0x80c", ".addr seven 0x80e",
           ".addr eight 0x810", ".addr nine 0x812", ".addr ten 0x814"]

    '''
        ".addr pow2_4 0x222",
        ".addr pow2_5 0x224", ".addr pow2_6 0x226", ".addr pow2_7 0x228", ".addr pow2_8 0x230",
        ".addr pow2_9 0x232", ".addr pow2_10 0x234", ".addr pow2_11 0x236", ".addr pow2_10 0x238",
        ".addr pow2_11 0x240", ".addr pow2_12 0x242", ".addr pow2_13 0x244", ".addr pow2_14 0x246",
        ".addr pow2_15 0x248", ".addr pow2_16 0x250",
        '''

    addr = 0x816
    for i in range(4, 17):
        hrm.append(".addr pow2_{} {}".format(i, hex(addr)))
        addr += 2
    hrm.append(".addr temp {}".format(hex(addr)))
    addr += 2
    for i in range(0, 32):
        hrm.append(".addr reg{} {}".format(i, hex(addr)))
        addr += 2


def mips2hrm(mips_assembly_code_path, save_hrm_dir, start_pc=0x300):
    global hrm, pc
    f = open(mips_assembly_code_path)
    f_out = open(save_hrm_dir, "w")
    lines = f.readlines()
    for i in range(len(lines)):
        lines[i] = lines[i].strip().replace(",", ", ")

    initial_data()

    for line in lines:
        if line == "":
            continue
        instr = line.split()[0]

        if line.strip().endswith(":"):
            #label
            add(line.strip())

        elif instr == "li":
            try:
                reg1 = registers[line.split()[1].strip(",")]
                if line.split()[2].startswith("0x"):
                    imm = int(line.split()[2].strip(","), 16)
                else:
                    imm = int(line.split()[2].strip(","))
            except:
                raise_error(line)
            add("copyfrom zero")
            for j in range(16):
                if imm>>j == 1:
                    add("add pow2_{}".format(j))
            add("copyto {}".format(reg1))

        elif instr == "move":
            try:
                reg1 = registers[line.split()[1].strip(",")]
                reg2 = registers[line.split()[2].strip(",")]
            except:
                raise_error(line)
            add("copyfrom {}".format(reg2))
            add("copyto {}".format(reg1))

        elif instr in ["add", "addu", "sub", "subu"]:
            try:
                reg1 = registers[line.split()[1].strip(",")]
                reg2 = registers[line.split()[2].strip(",")]
                reg3 = registers[line.split()[3].strip(",")]
            except:
                raise_error(line)
            add("copyfrom {}".format(reg2))
            if instr in ["add", "addu"]:
                add("add {}".format(reg3))
            else:
                add("sub {}".format(reg3))
            add("copyto {}".format(reg1))

        elif instr in ["addi", "addiu"]:
            try:
                reg1 = registers[line.split()[1].strip(",")]
                reg2 = registers[line.split()[2].strip(",")]
                if line.split()[3].startswith("0x"):
                    imm = int(line.split()[3].strip(","), 16)
                else:
                    imm = int(line.split()[3].strip(","))
            except:
                raise_error(line)
            add("copyfrom {}".format(reg2))
            for j in range(16):
                if imm>>j == 1:
                    add("add pow2_{}".format(j))
            add("copyto {}".format(reg1))

        elif instr in ["lw", "lhu", "lh", "sw", "sh", "lbu", "lb", "sb"]:
            #treat lw, lh, lb equally
            try:
                reg1 = registers[line.split()[1].strip(",")]
                reg2 = registers[line.split("(")[1].split(")")[0].strip(",")]  #only support lw $1, 0($2)
                if line.split()[1].split("(")[0].startswith("0x"):
                    imm = int(line.split()[2].split("(")[0], 16)
                else:
                    imm = int(line.split()[2].split("(")[0])
            except:
                raise_error(line)
            add("copyfrom {}".format(reg2))
            for j in range(16):
                if imm>>j == 1:
                    add("add pow2_{}".format(j))
            add("copyto temp")

            if instr in ["lw", "lhu", "lh", "lbu", "lb"]:
                add("copyfrom [temp]")
                add("copyto {}".format(reg1))
            elif instr in ["sw", "sh", "sb"]:
                add("copyfrom {}".format(reg1))
                add("copyto [temp]")

        elif instr == "beq":
            try:
                reg1 = registers[line.split()[1].strip(",")]
                reg2 = registers[line.split()[2].strip(",")]
                label = line.split()[3]
            except:
                raise_error(line)

            add("copyfrom {}".format(reg1))
            add("sub {}".format(reg2))
            add("jumpz {}".format(label))

        elif instr == "bltz":
            try:
                reg1 = registers[line.split()[1].strip(",")]
                label = line.split()[2]
            except:
                raise_error(line)
            add("copyfrom {}".format(reg1))
            add("jumpn {}".format(label))

        elif instr in ["jal", "jalr"]:
            pc_old = pc
            add("copyfrom zero")
            for j in range(16):
                if pc_old>>j == 1:
                    add("add pow2_{}".format(j))
                    add("add four")  #make up for extra pc by add

            if instr == "jal":
                try:
                    label = line.split()[1]
                except:
                    raise_error(line)
                add("add eight")  #make up for extra pc; refer to next instr after jump
                add("copyto reg31")
                add("jump {}".format(label))
            elif instr == "jalr":
                try:
                    reg1 = registers[line.split()[1].strip(",")]
                    reg2 = registers[line.split()[2].strip(",")]
                except:
                    raise_error(line)
                add("add ten")  #make up for extra pc; refer to next instr after jump
                add("copyto {}".format(reg1))
                add("copyfrom {}".format(reg2))
                add("jr")

        elif instr == "jr":
            try:
                reg1 = registers[line.split()[1].strip(",")]
            except:
                raise_error(line)
            add("copyfrom {}".format(reg1))
            add("jr")

        elif instr == "j":
            try:
                label = line.split()[1]
            except:
                raise_error(line)
            add("jump {}".format(label))

        elif instr in ["mfc0", "mtc0"]:
            try:
                reg = registers[line.split()[1].strip(",")]
                reg_cp0 = line.split()[2]
            except:
                raise_error(line)
            if reg_cp0 == "$13":
                if instr == "mfc0":
                    add("mfcause")
                    add("copyto {}".format(reg))
                elif instr == "mtc0":
                    add("copyfrom {}".format(reg))
                    add("mtcause")
            elif reg_cp0 == "$14":
                if instr == "mfc0":
                    add("mfepc")
                    add("copyto {}".format(reg))
                elif instr == "mtc0":
                    add("copyfrom {}".format(reg))
                    add("mtepc")
            else:
                raise_error(line)

        elif instr in ["eret", "nop"]:
            add(instr)

        else:
            print("Unsupported MIPS instruction: {}".format(line))
            raise NotImplementedError

    for line in hrm:
        print(line)
        print(line, file=f_out)
    f.close()
    f_out.close()


if __name__ == '__main__':
    mips2hrm("../../mips_test.txt", "hrm_out.txt")