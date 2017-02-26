/* Test Circuit "Baby"
 * Copyright Igor Lesik 2016
 */
package curoles.dedesim
package test.circuit
package baby

import curoles.dedesim.Simulator.sim
import curoles.dedesim.Driver._
import curoles.dedesim.Basic._
import curoles.dedesim.util.StringUtils._
import curoles.dedesim.hwlib.Sram1R1W

object Baby {
    val OPCODE_WIDTH = 4
    val DATA_WIDTH = 4
    val WORD_WIDTH = OPCODE_WIDTH + DATA_WIDTH
    val ADDR_WIDTH = 12
    val ADDR_SIZE  = 1 << 12
}

/** The Manchester Small-Scale Experimental Machine (SSEM), nicknamed Baby.
 *
 *  https://en.wikipedia.org/wiki/Manchester_Small-Scale_Experimental_Machine
 *  http://www.computerhistory.org/timeline/computers/
 *  https://github.com/nkkav/mu0
 */
class Baby(
    parent: Component,
    name: String,
    clk: Wire,
    reset: Wire,
    ir: Wires
)
    extends Module(parent, name)
{
    val readAddr = wires("readAddr", Baby.ADDR_WIDTH)
    val writeAddr = wires("writeAddr", Baby.ADDR_WIDTH)
    val readData = wires("readData", Baby.WORD_WIDTH)
    val writeData = wires("writeData", Baby.WORD_WIDTH)
    val writeEnable = wire("writeEnable")

    val mem = new Sram1R1W(
        this,
        "mem",
        Baby.WORD_WIDTH,
        Baby.ADDR_SIZE,
        clk,
        readAddr,
        readData,
        writeEnable,
        writeAddr,
        writeData
    )

    val zero_addr = wires("zero_addr", Baby.ADDR_WIDTH, 0)
    val zero_word = wires("zero_word", Baby.WORD_WIDTH, 0)

    val pc = wires("pc", Baby.ADDR_WIDTH)
    val new_pc = wires("new_pc", Baby.ADDR_WIDTH)
    dff(clk, input = new_pc, output = pc)

    val next_pc = wires("next_pc", Baby.ADDR_WIDTH)
    val addr_1 = wires("addr_1", Baby.ADDR_WIDTH, 1)
    mux2to1(select = reset, in2 = zero_addr, in1 = next_pc, output = new_pc)
    adder(output = next_pc, in1 = pc, in2 = addr_1)

    monitor('level -> pc, 'level -> next_pc, 'level -> new_pc) {
        sim.log(s"pc=${pc.getSignalAsInt} next_pc=${next_pc.getSignalAsInt} new_pc=${new_pc.getSignalAsInt}")
    }

    val LDA = b"0000"

 
    //dff(clk, input = zero, output = ir)
    //dff(clk, input = zero, output = acc)

/*
module mu0 (clk, reset, pc, ir, acc);
  parameter MAXWIDTH = 16, MAXDEPTH = 12;
  parameter [3:0] LDA = 4'b0000, STO = 4'b0001, ADD = 4'b0010, SUB = 4'b0011;
  parameter [3:0] JMP = 4'b0100, JGE = 4'b0101, JNE = 4'b0110, STP = 4'b0111;
  input  clk, reset;
  output [MAXWIDTH-1:0] pc, ir, acc;
  reg    [MAXWIDTH-1:0] pc, ir, acc;
  reg    [MAXWIDTH-1:MAXWIDTH-4] opcode;
  reg    [MAXWIDTH-5:0] address;
  reg    [MAXWIDTH-1:0] mem [0:(1<<MAXDEPTH)-1];

  initial begin
    $readmemh("prog.lst", mem, 0, (1<<MAXDEPTH)-1);
  end

  always @(posedge clk)
  begin
    if (reset == 1'b1) begin
      ir  <= 0;
      pc  <= 0;
      acc <= 0;
    end
    else begin
      ir = mem[pc];
      if (ir[MAXWIDTH-1:MAXWIDTH-4] != STP) begin
        pc = pc + 1;
      end
    end
  end

  always @(opcode or address or ir or pc or acc)
  begin
    opcode  = ir[MAXWIDTH-1:MAXWIDTH-4];
    address = ir[MAXWIDTH-5:0];
    case (opcode)
      LDA: acc = mem[address];
      STO: mem[address] = acc;
      ADD: acc = acc + mem[address];
      SUB: acc = acc - mem[address];
      JMP: pc = address;
      JGE: if (acc[MAXWIDTH-1] == 1'b0) begin
             pc = address;
           end
      JNE: if (acc != 0) begin
             pc = address;
           end
      STP: pc = pc;
      default: begin
           end
    endcase
end
*/
}

class TB(parent: Component, name: String) extends Module(parent, name) {

    sim.log("Test Bench \"Manchester Baby\"")

    val WIDTH = Baby.WORD_WIDTH
    //val DEPTH = 12
    //val STOP_OPCODE = 4'b0111

    val clk = wire("clk", 1)
    val reset = wire("reset")

    // Clock generator
    clock(period = 5, clk) // 5 low and 5 high ticks, 10 ticks between posedges.

    drive('HI, reset, 0) // Initially reset is HI,
    drive('LO, reset, 15) // then it goes LOW.

    //val pc = wires("pc")
    val ir = wires("ir", WIDTH)
    //val acc = wires("acc")

    val dut = new Baby(
        parent = this,
        name = "Baby",
        clk = clk,
        reset = reset,
        ir = ir
    )

    monitor('level -> ir) {
        /*if (ir[WIDTH-1:WIDTH-4] == STOP_OPCODE) {
            sim.log("STOP instruction executed, end of simulation")
            sim.finish
        }*/
    }

    //if (monitorEnabled) {
        monitor('level -> clk, 'fall -> reset) {
            sim.log(s"clk=${clk.getSignalAsInt} reset=${reset.getSignalAsInt}")
        }
    //}
}

