/* Test Circuit "Baby"
 * Copyright Igor Lesik 2016
 */
package curoles.dedesim
package test.circuit
package baby

import curoles.dedesim.Simulator.sim
import curoles.dedesim.Driver._
import curoles.dedesim.Basic._

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

}

class TB(parent: Component, name: String) extends Module(parent, name) {

    sim.log("Test Bench \"Manchester Baby\"")

    val WIDTH = 16
    //val DEPTH = 12
    //val STOP_OPCODE = 4'b0111

    val clk = wire("clk")
    val reset = wire("reset")

    clock(period = 2, clk) // Clock generator.

    drive('HI, reset, 0) // Initially reset is HI,
    drive('LO, reset, 3) // then it goes LOW.

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

    /*
    sim.monitor(ir) {
        if (ir[WIDTH-1:WIDTH-4] == STOP_OPCODE) {
            sim.log("STOP instruction executed, end of simulation")
            sim.finish
        }
    }
    */

    /*
    if (monitorEnable) { //rise, fall, level
        sim.monitor('rise -> clk) { tuple(change: Symbol, comp: Component)
            println()
        }
    }
    */
}

