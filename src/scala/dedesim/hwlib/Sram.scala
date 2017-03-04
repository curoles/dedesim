/* Copyright (c) Igor Lesik 2013
 *
 *
 */

package curoles.dedesim.hwlib

//import curoles.dedesim.hwlib.Word
import curoles.dedesim.Component
import curoles.dedesim.Module
import curoles.dedesim.Wire
import curoles.dedesim.Wires
import curoles.dedesim.Simulator.sim
import curoles.dedesim.Basic

/** Static two-port one read one write RAM.
 *
 * http://www-inst.eecs.berkeley.edu/~cs150/fa13/agenda/lec/lec11-sram.pdf
 */
class Sram2R1W(
    parent: Component,
    name: String,
    wordWidth: Int,
    size: Int,
    clk: Wire,
    readAddr: Wires,
    readData: Wires,
    readAddr2: Wires,
    readData2: Wires,
    writeEnable: Wire,
    writeAddr: Wires,
    writeData: Wires
)
    extends Module(parent, name)
    //with MemWithBackdoor
{
    require(wordWidth > 0 && size > 0)
    require(readData.width >= wordWidth)
    require(readData2.width >= wordWidth)
    require(writeData.width >= wordWidth)

    val data: Array[Word] = Array.fill(size){new Word(wordWidth)} //new Array[Word](size)

    def implementRead(addr: Wires, rdData: Wires): Unit = {
        def readAction() = {
            val addrVal: Int = addr.getSignalAsInt.toInt
            sim.afterDelay(0) {
                for (pos <- 0 until wordWidth) {
                    //sim.log(s"SRAM ${name} read at address ${addrVal} bit ${pos}")
                    rdData.setSignal(pos, data(addrVal).getBit(pos))
                }
            }
        }

        //addr.wires.foreach{ wire => wire.addAction(() => readAction) }
        clk.addAction(() => readAction)
    }

    def implementWrite(addr: Wires, wrData: Wires): Unit = {
        def writeAction() = {
            val posedgeClk = clk.getSignal == true
            if (posedgeClk && writeEnable.getSignal) {
                val addrVal: Int = addr.getSignalAsInt.toInt
                sim.afterDelay(0) {
                    for (pos <- 0 until wordWidth) {
                        data(addrVal).setBit(pos, wrData.getSignal(pos))
                    }
                }
            }
        }

        clk.addAction(() => writeAction)
    }


    implementRead(readAddr, readData)
    implementRead(readAddr2, readData2)
    implementWrite(writeAddr, writeData)

    Basic.monitor('rise -> clk) {
        sim.log(f"SRAM2R1W RA=${readAddr.getSignalAsInt}%03x RD=${readData.getSignalAsInt}%04x RA2=${readAddr2.getSignalAsInt}%03x RD2=${readData2.getSignalAsInt}%04x WA=${writeAddr.getSignalAsInt}%03x WD=${writeData.getSignalAsInt}%04x WE=${writeEnable.getSignal}")
    }
}

import org.scalatest.FlatSpec

class SramSpec extends FlatSpec {

class Test(
    parent: Component,
    name: String
)
    extends Module(parent, name)
{
    val clk = wire("clk")
    val readAddr = wires("readAddr", 12)
    val writeAddr = wires("writeAddr", 12)
    val readData = wires("readData", 8)
    val writeData = wires("writeData", 8)
    val writeEnable = wire("writeEnable")

    val mem = new Sram1R1W(
        this,
        "mem",
        8,
        1<<12,
        clk,
        readAddr,
        readData,
        writeEnable,
        writeAddr,
        writeData
    )
}

    it should "not crash after creation" in {
        val test = new Test(null,"test")
        sim.run(1)
    }

}
