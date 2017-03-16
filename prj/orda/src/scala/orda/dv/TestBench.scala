/* Test Bench for "Orda CPU"
 * Copyright Igor Lesik 2017
 */
package curoles.orda
package dv

import curoles.dedesim.Simulator.sim
import curoles.dedesim.Component
import curoles.dedesim.Module
import curoles.dedesim.Basic._
import curoles.dedesim.Driver._

import curoles.orda.design.OrdaCPU
import curoles.orda.design.OrdaCpuPins

class TestBench(
    parent: Component,
    name: String
)
    extends Module(parent, name)
{
    sim.log("Constructing Test Bench for \"Orda CPU\"")

    val clk = wire("clk", 1)
    val reset = wire("reset", 1)

    // Clock generator
    clock(period = 5, clk) // 5 low and 5 high ticks, 10 ticks between posedges.

    // Reset sequence
    drive('HI, reset, 0) // Initially reset is HI,
    drive('LO, reset, 15) // then it goes LOW.

    val cpuPins = connectPins()

    val cpu = new OrdaCPU (
        parent = this,
        name = "cpu",
        clk = clk,
        reset = reset,
        pins = cpuPins
    )

    monitor('fall -> reset) {
       sim.log( "*******************************************")
       sim.log(s"**********   RESET true->${reset.getSignal}   **********")
       sim.log( "*******************************************")
    }


    def connectPins(): OrdaCpuPins = {
        //
        OrdaCpuPins(
            resetAddr = wires("resetAddr", 8, 0x0)
        )
    }
}


