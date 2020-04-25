registers = {
    "$zero": "reg0",
    "$at"  : "reg1",
    "$v0"  : "reg2",
    "$v1"  : "reg3",
    "$a0"  : "reg4",
    "$a1"  : "reg5",
    "$a2"  : "reg6",
    "$a3"  : "reg7",
    "$t0"  : "reg8",
    "$t1"  : "reg9",
    "$t2"  : "reg10",
    "$t3"  : "reg11",
    "$t4"  : "reg12",
    "$t5"  : "reg13",
    "$t6"  : "reg14",
    "$t7"  : "reg15",
    "$s0"  : "reg16",
    "$s1"  : "reg17",
    "$s2"  : "reg18",
    "$s3"  : "reg19",
    "$s4"  : "reg20",
    "$s5"  : "reg21",
    "$s6"  : "reg22",
    "$s7"  : "reg23",
    "$t8"  : "reg24",
    "$t9"  : "reg25",
    "$k0"  : "reg26",
    "$k1"  : "reg27",
    "gp"   : "reg28",
    "sp"   : "reg29",
    "fp"   : "reg30",
    "ra"   : "reg31"
}

for i in range(32):
    registers["$%d"%i] = "reg%d"%i


def mips2hrm():
    f = open("mips_code.txt", "r")
    lines = f.readlines()
    for i in range(len(lines)):
        lines[i] = lines[i].strip()
    hrm = []
    for line in lines:
        pass

if __name__ == '__main__':
    print(registers)