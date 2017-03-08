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
import curoles.dedesim.hwlib.Sram2R1W

object Baby {
    val OPCODE_WIDTH = 4
    val DATA_WIDTH = 12
    val WORD_WIDTH = OPCODE_WIDTH + DATA_WIDTH
    val OPCODE_POS = DATA_WIDTH
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
    clk: WireIn,
    reset: WireIn,
    ir: Wires
)
    extends Module(parent, name)
{
    val readAddr = wires("readAddr", Baby.ADDR_WIDTH)
    val readData = wires("readData", Baby.WORD_WIDTH)
    val readAddr2 = wires("readAddr2", Baby.ADDR_WIDTH)
    val readData2 = wires("readData2", Baby.WORD_WIDTH)
    val writeAddr = wires("writeAddr", Baby.ADDR_WIDTH)
    val writeData = wires("writeData", Baby.WORD_WIDTH)
    val writeEnable = wire("writeEnable")

    val mem = new Sram2R1W(
        this,
        "mem",
        Baby.WORD_WIDTH,
        1024,
        clk,
        readAddr,
        readData,
        readAddr2,
        readData2,
        writeEnable,
        writeAddr,
        writeData
    )

    loadProgram(mem)

    val pc = wires("pc", Baby.ADDR_WIDTH)
    val nextPC = wires("nextPC", Baby.ADDR_WIDTH)

    val frontEnd = new FrontEnd(
        parent = this,
        name = "frontEnd",
        clk = clk,
        reset = reset,
        pc = pc,
        nextPC = nextPC,
        ir = ir,
        memReadAddr = readAddr,
        memReadData = readData
    )


    val acc = wires("acc", Baby.WORD_WIDTH)
    val acc_write = wires("acc_write", Baby.WORD_WIDTH)
    val acc_write_en = wire("acc_write_en")

    val regFile = new RegFile(
        parent = this,
        name = "regFile",
        clk = clk,
        reset = reset,
        acc_read = acc,
        acc_write = acc_write,
        acc_write_en = acc_write_en
    )

    val executer = new Executer(
        parent = this,
        name = "executer",
        clk = clk,
        reset = reset,
        ir = ir,
        pc = pc,
        nextPC = nextPC,
        memReadAddr = readAddr2,
        memReadData = readData2,
        memWriteEnable = writeEnable,
        memWriteAddr = writeAddr,
        memWriteData = writeData
    )


    /** Front End controls PC and IR.
     *
     *  <hr>
     *  <pre class="textdiagram" id="Baby.FrontEnd">
     *  +-------------------------------------------+
     *  |                                           |
     *  |    +---+                                  |
     *  +--->|0  |          +---+       +--+        |
     *       |mux|--------->|DFF|------>|+2|--------+
     *  ---->|1  |  new_pc  +---+  pc   +--+  next_pc
     *  0    +---+
     *         | reset
     *  -------+
     *
     *  +----------------------- mem[new_pc]
     *  |
     *  |    +---+
     *  +--->|0  |          +---+
     *       |mux|--------->|DFF|------>
     *  ---->|1  |  new_ir  +---+  ir
     *  0    +---+
     *         | reset
     *  -------+
     *  </pre>
     */
    class FrontEnd(
        parent: Component,
        name: String,
        clk: WireIn,
        reset: WireIn,
        pc: Wires,
        nextPC: WiresIn,
        ir: Wires,
        memReadAddr: Wires,
        memReadData: WiresIn
    )
    {
        val reset_d1 = wire("reset_d1", 1)
        dff(clk, output = reset_d1, input = reset)

        val zero_addr = wires("zero_addr", Baby.ADDR_WIDTH, 0)
        val zero_word = wires("zero_word", Baby.WORD_WIDTH, 0)
    
        //val pc = wires("pc", Baby.ADDR_WIDTH)
        val new_pc = wires("new_pc", Baby.ADDR_WIDTH)
        dff(clk, output = pc, input = new_pc)
    
        //val next_pc = wires("next_pc", Baby.ADDR_WIDTH)
        //val addr_2 = wires("addr_2", Baby.ADDR_WIDTH, 2)
        mux2to1(select = reset_d1, in2 = zero_addr, in1 = nextPC, output = new_pc)
        //adder(output = next_pc, in1 = pc, in2 = addr_2)
    
    
        follow(output = memReadAddr, input = new_pc)
        val new_ir = wires("new_ir", Baby.WORD_WIDTH)
        mux2to1(select = reset, in2 = zero_word, in1 = memReadData, output = new_ir)
        dff(clk, output = ir, input = new_ir)

        //monitor('rise -> clk) {
        //    sim.log(f"PC: ${pc.int}%03x -> ${new_pc.int}%03x  IR: ${ir.int}%04x -> ${new_ir.int}%04x")
        //}
    }

    /** Register File has only one register called "acc(umulator)"
     *
     */
    class RegFile(
        parent: Component,
        name: String,
        clk: WireIn,
        reset: WireIn,
        acc_read: Wires,
        acc_write: WiresIn,
        acc_write_en: Wire
    )
        extends Module(parent, name)
    {
        register(clk = clk, read = acc_read, write = acc_write, write_en = acc_write_en)
    }

    /** Instruction Execution Unit.
     *
     *  {{{
     *  case (opcode)
     *    LDA: acc = mem[address];
     *    STO: mem[address] = acc;
     *    ADD: acc = acc + mem[address];
     *    SUB: acc = acc - mem[address];
     *    JMP: pc = address;
     *    JGE: if (acc[MAXWIDTH-1] == 1'b0) begin
     *           pc = address;
     *         end
     *    JNE: if (acc != 0) begin
     *           pc = address;
     *         end
     *    STP: pc = pc;
     *    default: begin
     *  }}}
     */
    class Executer(
        parent: Component,
        name: String,
        clk: WireIn,
        reset: WireIn,
        ir: WiresIn,
        pc: WiresIn,
        nextPC: Wires,
        memReadAddr: Wires,
        memReadData: Wires,
        memWriteEnable: Wire,
        memWriteAddr: Wires,
        memWriteData: Wires
    )
        extends Module(parent, name)
    {
        val isLDA = wire("isLDA")
        val isSTO = wire("isSTO")
        val isADD = wire("isADD")
        val isSUB = wire("isSUB")
        val isJMP = wire("isJMP")
        val isJGE = wire("isJGE")
        val isJNE = wire("isJNE")
        val isSTP = wire("isSTP")

        val decoder = new Decoder(
            parent = this,
            name = "decoder",
            clk = clk,
            reset = reset,
            opcode = ir.newSlice("ir", Baby.OPCODE_POS, Baby.OPCODE_POS + Baby.OPCODE_WIDTH - 1),
            isLDA = isLDA,
            isSTO = isSTO,
            isADD = isADD,
            isSUB = isSUB,
            isJMP = isJMP,
            isJGE = isJGE,
            isJNE = isJNE,
            isSTP = isSTP
        )

        val accRead = wires("accRead", Baby.DATA_WIDTH)
        val accWrite = wires("accWrite", Baby.DATA_WIDTH)
        val accWriteEnable = wire("accWriteEnable")
        register(clk = clk, read = accRead, write = accWrite, write_en = accWriteEnable)

        // ALU output
        val aluResult = wires("aluResult", Baby.DATA_WIDTH)

        //////////////////////////////
        // Pipeline stage #1 DECODE
        //////////////////////////////

        val isAccWrite = wire("isAccWrite")
        orGate(output = isAccWrite, isLDA, isADD, isSUB)
        val isAccWrite_d1 = wire("isAccWrite_d1")
        dff(clk = clk, output = isAccWrite_d1, input = isAccWrite)
        val isAccWrite_d2 = wire("isAccWrite_d2")
        dff(clk = clk, output = isAccWrite_d2, input = isAccWrite_d1)
        val isAccWrite_d3 = wire("isAccWrite_d3")
        dff(clk = clk, output = isAccWrite_d3, input = isAccWrite_d2)

        val isAccRead = wire("isAccRead")
        orGate(output = isAccRead, isSTO, isADD, isSUB)
        val isAccRead_d1 = wire("isAccRead_d1")
        dff(clk = clk, output = isAccRead_d1, input = isAccRead)
        val isAccRead_d2 = wire("isAccRead_d2")
        dff(clk = clk, output = isAccRead_d2, input = isAccRead_d1)
        val isAccRead_d3 = wire("isAccRead_d3")
        dff(clk = clk, output = isAccRead_d3, input = isAccRead_d2)

        //val isAccNotEqZero = wire("isAccNotEqZero")
        //orGate(output = isAccNotEqZero, acc.wires: _*)


        val isMemStore = wire("isMemStore")
        orGate(output = isMemStore, isSTO)
        val isMemStore_d1 = wire("isMemStore_d1")
        dff(clk = clk, output = isMemStore_d1, input = isMemStore)
        val isMemStore_d2 = wire("isMemStore_d2")
        dff(clk = clk, output = isMemStore_d2, input = isMemStore_d1)
        val isMemStore_d3 = wire("isMemStore_d3")
        dff(clk = clk, output = isMemStore_d3, input = isMemStore_d2)

        val memAddrIRSlice = ir.newSlice("memAddrIRSlice", 0, Baby.DATA_WIDTH - 1)
        follow(output = memReadAddr, input = memAddrIRSlice)
        val memAddr_d1 = wires("memAddr_d1", Baby.DATA_WIDTH)
        dff(clk = clk, output = memAddr_d1, input = memAddrIRSlice)
        val memAddr_d2 = wires("memAddr_d2", Baby.DATA_WIDTH)
        dff(clk = clk, output = memAddr_d2, input = memAddr_d1)
        val memAddr_d3 = wires("memAddr_d3", Baby.DATA_WIDTH)
        dff(clk = clk, output = memAddr_d3, input = memAddr_d2)

        val ir_d1 = wires("ir_d1", Baby.WORD_WIDTH)
        dff(clk = clk, output = ir_d1, input = ir)
        val ir_d2 = wires("ir_d2", Baby.WORD_WIDTH)
        dff(clk = clk, output = ir_d2, input = ir_d1)
        val ir_d3 = wires("ir_d3", Baby.WORD_WIDTH)
        dff(clk = clk, output = ir_d3, input = ir_d2)

        val pc_d1 = wires("pc_d1", Baby.ADDR_WIDTH)
        dff(clk = clk, output = pc_d1, input = pc)
        val pc_d2 = wires("pc_d2", Baby.ADDR_WIDTH)
        dff(clk = clk, output = pc_d2, input = pc_d1)
        val pc_d3 = wires("pc_d3", Baby.ADDR_WIDTH)
        dff(clk = clk, output = pc_d3, input = pc_d2)

        val acc_d1 = wires("acc_d1", Baby.DATA_WIDTH)
        dff(clk = clk, output = acc_d1, input = accRead)
        val acc_d2 = wires("acc_d2", Baby.DATA_WIDTH)
        dff(clk = clk, output = pc_d2, input = acc_d1)
        val acc_d3 = wires("acc_d3", Baby.DATA_WIDTH)
        dff(clk = clk, output = acc_d3, input = acc_d2)

        val consecutivePC = wires("consecutivePC", Baby.ADDR_WIDTH)
        val addr_2 = wires("addr_2", Baby.ADDR_WIDTH, 2)
        adder(output = consecutivePC, in1 = pc, in2 = addr_2)

        mux2to1(select = isSTP, output = nextPC, in1 = consecutivePC, in2 = pc)

        //////////////////////////////
        // Pipeline stage #2 READ DATA
        //////////////////////////////

        val memDataReadSlice = memReadData.newSlice("memDataReadSlice", 0, Baby.DATA_WIDTH - 1)
        val memData = wires("memData", Baby.DATA_WIDTH)
        dff(clk = clk, output = memData, input = memDataReadSlice)

        val source1 = wires("source1", Baby.DATA_WIDTH)
        val source2 = wires("source2", Baby.DATA_WIDTH)

        // If the instruction before is going to write ALU result to ACCumulator,
        // then we need to bypass the ALU result here because it is not written to ACC yet.
        val acc_or_bypass = wires("acc_or_bypass", Baby.DATA_WIDTH)
        mux2to1(select = isAccWrite_d2, output = acc_or_bypass, in1 = acc_d1, in2 = aluResult)

        val dataZero = wires("dataZero", Baby.DATA_WIDTH, 0)
        val zero_or_acc = wires("zero_or_acc", Baby.DATA_WIDTH)
        mux2to1(select = isAccRead_d1, output = zero_or_acc, in1 = dataZero, in2 = acc_or_bypass)

        // If MEM read then source1 is memData
        dff(clk, output = source1, input = memData)
        dff(clk, output = source2, input = zero_or_acc) 

        val source1_d1 = wires("source1_d1", Baby.DATA_WIDTH)
        dff(clk = clk, output = source1_d1, input = source1)

        val source2_d1 = wires("source2_d1", Baby.DATA_WIDTH)
        dff(clk = clk, output = source2_d1, input = source2)

        //////////////////////////////
        // Pipeline stage #3 EXECUTE
        //////////////////////////////

        adder(output = aluResult, in1 = source1, in2 = source2)

        val aluResult_d1 = wires("aluResult_d1", Baby.DATA_WIDTH)
        dff(clk = clk, output = aluResult_d1, input = aluResult)
 
        ////////////////////////////////////
        // Pipeline stage #4 WRITE BACK DATA
        ////////////////////////////////////

        // If isAccWrite then acc = aluResult
        follow(output = accWrite, input = aluResult_d1)
        follow(output = accWriteEnable, input = isAccWrite_d3) 

        // If isMemStore then mem[addr] = aluResult
        follow(output = memWriteEnable, input = isMemStore_d3)
        follow(output = memWriteAddr, input = memAddr_d3)
        val aluResultWord = wires("aluResultWord", Baby.WORD_WIDTH, 0)
        aluResult_d1.wires.zipWithIndex.foreach{case (w,i) => aluResultWord.wires(i) = w}
        follow(output = memWriteData, input = aluResultWord)


        // If isExeFlowChange then pc = result

        monitor('rise -> clk) {
sim.log( "Pipe     WB  EXE   RD  DECD")
sim.log(f"Pipe PC=${pc_d3.int}%04x ${pc_d2.int}%04x ${pc_d1.int}%04x ${pc.int}%04x")
sim.log(f"Pipe IR=${ir_d3.int}%04x ${ir_d2.int}%04x ${ir_d1.int}%04x ${ir.int}%04x")
sim.log(f"Pipe AC=${acc_d3.int}%04x ${acc_d2.int}%04x ${acc_d1.int}%04x ${accRead.int}%04x")
sim.log(f"Pipe AW=${isAccWrite_d3.int}%4d ${isAccWrite_d2.int}%4d ${isAccWrite_d1.int}%4d ${isAccWrite.int}%4d")
sim.log(f"Pipe AR=${isAccRead_d3.int}%4d ${isAccRead_d2.int}%4d ${isAccRead_d1.int}%4d ${isAccRead.int}%4d")
sim.log(f"Pipe RS=${aluResult_d1.int}%04x ${aluResult.int}%04x")
sim.log(f"Pipe S1=${source1_d1.int}%04x ${source1.int}%04x ____ ____")
sim.log(f"Pipe S2=${source2_d1.int}%04x ${source2.int}%04x ____ ____")
sim.log(f"Pipe AB=____ ____ ${acc_or_bypass.int}%04x ____")

            if (isAccWrite_d3.getSignal == true) {
                sim.log(f"Executer ACC <- ${aluResult_d1.int}%03x")
            }

            if (isMemStore_d3.getSignal == true) {
                sim.log(f"Executer MEM[${memAddr_d3.int}%03x] <- ${aluResult_d1.int}%03x")
            }

            //sim.log(f"mem ADDR=${memAddrIRSlice.int}%03x  DATA=${memDataReadSlice.int}%03x")


        }
    }

    /** Decoder.
     *
     */
    class Decoder(
        parent: Component,
        name: String,
        clk: WireIn,
        reset: WireIn,
        opcode: WiresIn,
        isLDA: Wire, // 4'b0000
        isSTO: Wire, // 4'b0001
        isADD: Wire, // 4'b0010
        isSUB: Wire, // 4'b0011
        isJMP: Wire, // 4'b0100
        isJGE: Wire, // 4'b0101
        isJNE: Wire, // 4'b0110
        isSTP: Wire  // 4'b0111
    )
        extends Module(parent, name)
    {
        require(opcode.width == Baby.OPCODE_WIDTH)
        val opcodeN = wires("opcodeN", Baby.OPCODE_WIDTH)
        inverter(input = opcode, output = opcodeN)

        andGate(output = isLDA, opcodeN.wires(3), opcodeN.wires(2), opcodeN.wires(1), opcodeN.wires(0))
        andGate(output = isSTO, opcodeN.wires(3), opcodeN.wires(2), opcodeN.wires(1),  opcode.wires(0))
        andGate(output = isADD, opcodeN.wires(3), opcodeN.wires(2),  opcode.wires(1), opcodeN.wires(0))
        andGate(output = isSUB, opcodeN.wires(3), opcodeN.wires(2),  opcode.wires(1),  opcode.wires(0))
        andGate(output = isJMP, opcodeN.wires(3),  opcode.wires(2), opcodeN.wires(1), opcodeN.wires(0))
        andGate(output = isJGE, opcodeN.wires(3),  opcode.wires(2), opcodeN.wires(1),  opcode.wires(0))
        andGate(output = isJNE, opcodeN.wires(3),  opcode.wires(2),  opcode.wires(1), opcodeN.wires(0))
        andGate(output = isSTP, opcodeN.wires(3),  opcode.wires(2),  opcode.wires(1),  opcode.wires(0))

        /*monitor('level -> opcode) {
            sim.log(s"OPCODE=${opcode.getSignalAsInt} LDA:${isLDA.getSignal} STO:${isSTO.getSignal}")
        }*/
    }

    /** Helper to load a program code into SRAM via backdoor access.
     *
     */
    def loadProgram(mem: Sram2R1W): Unit = {
        //LDA = 0x0, STO = 0x1, ADD = 0x2, SUB = 0x3
        //JMP = 0x4, JGE = 0x5, JNE = 0x6, STP = 0x7

        //https://github.com/nkkav/mu0/blob/master/sim/archc/test/test1.hex
                                        //.text:
        mem.data(0).fromInteger(0x0008) //0000 0008  0 LDA acc=mem[8]   ;acc=a
        mem.data(1).fromInteger(0x200a) //0002 200a  2 ADD acc+=mem[a]  ;acc=a+1
        mem.data(2).fromInteger(0x100c) //0004 100c  1 STO mem[c]=acc   ;mem[c]=b
        mem.data(3).fromInteger(0x7000) //0006 7000  7 STP
                                        //.data:
        mem.data(4).fromInteger(0x000a) //0008 000a
        mem.data(5).fromInteger(0x0001) //000a 0001
        mem.data(6).fromInteger(0x0000) //000c 0000

        //test2.hex
        //.text:
        //0000 000c  0 LDA acc=mem[c]     ;acc=4
        //0002 300e  3 SUB acc=acc-mem[e] ;acc=4-1=3
        //0004 100c  1 STO mem[c]=acc     ;mem[c]=3
        //0006 000c  0 LDA acc=mem[c]     ;acc=3
        //0008 5000  5 JGE pc=0 if acc[11]!=0 
        //000a 7000  7 STP
        //.data:
        //000c 0004
        //000e 0001

    }
}

class TB(parent: Component, name: String) extends Module(parent, name) {

    sim.log("Test Bench \"Manchester Baby\"")

    val WIDTH = Baby.WORD_WIDTH
    //val DEPTH = 12
    //val STOP_OPCODE = 4'b0111

    val clk = wire("clk", 1)
    val reset = wire("reset", 1)

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

    monitor('fall -> reset) {
       sim.log(s"**********   RESET true->${reset.getSignal}   **********")
       sim.log( "*******************************************")
    }

    //if (monitorEnabled) {
        /*monitor('level -> clk, 'fall -> reset) {
            sim.log(s"clk=${clk.getSignalAsInt} reset=${reset.getSignalAsInt}")
        }*/
    //}
}


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

