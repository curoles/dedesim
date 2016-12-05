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
    clk: Wire
)
    extends Module(parent, name)
{

}

class TB(parent: Component, name: String) extends Module(parent, name) {

    sim.log("Test Bench \"Baby\"")

    val clk = wire("clk")
    val reset = wire("reset")

    clock(period = 2, clk)

    val dut = new Baby(this, "Baby", clk)
}

