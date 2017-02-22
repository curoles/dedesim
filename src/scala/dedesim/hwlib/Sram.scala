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

/** Static RAM.
 *
 */
class Sram(
    parent: Component,
    name: String,
    wordWidth: Int,
    size: Int,
    clk: Wire,
    //readEnable: Wire,
    readAddr: Wires,
    readData: Wires,
    writeEnable: Wire,
    writeAddr: Wires,
    writeData: Wires
)
    extends Module(parent, name)
    //with MemWithBackdoor
{
    require(wordWidth > 0 && size > 0)
    require(readData.width >= wordWidth)
    require(writeData.width >= wordWidth)

    val data = new Array[Word](size)

    def implementRead(addr: Wires, rdData: Wires): Unit = {
        def readAction() = {
            val addrVal: Int = addr.getSignalAsInt
            sim.afterDelay(0) {
                for (pos <- 0 until wordWidth) {
                    rdData.setSignal(pos, data(addrVal).getBit(pos))
                }
            }
        }

        addr.wires.foreach{ wire => wire.addAction(() => readAction) }
    }

    def implementWrite(addr: Wires, wrData: Wires): Unit = {
        def writeAction() = {
            val posedgeClk = clk.getSignal == true
            if (posedgeClk && writeEnable.getSignal) {
                val addrVal: Int = addr.getSignalAsInt
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
    implementWrite(writeAddr, writeData)
}
