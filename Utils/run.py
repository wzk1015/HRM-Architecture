import argparse
import sys; sys.path.append("../")

from HRM.Assembler.HRM_assembler import assemble
from HRM.Simulator.simulator import simulate


def run():
    parser = argparse.ArgumentParser()
    parser.add_argument(
        "--in_code", type=str,
        default='Codes/code.txt',
        help="path of HRM assembly code"
    )
    parser.add_argument(
        "--in_mem", type=str,
        default='Simulator/memory.txt',
        help="path of memory initial value"
    )
    parser.add_argument(
        "--out_log", type=str,
        default=None,
        help="path of simulator output"
    )
    print("parsing args")
    args = parser.parse_args()

    print("assembling")
    assemble(code_path=args.in_code,
             machine_code_bin_path="Utils/temp/mcb.txt",
             machine_code_hex_path="Utils/temp/mch.txt",
             mips_code_path="Utils/temp/mips_code.txt",
             output=False
             )
    print("simulating")
    simulate(memory_path=args.in_mem,
             log_path=args.out_log,
             machine_code_path="Utils/temp/mcb.txt"
             )


if __name__ == '__main__':
    run()
