/* Test Circuit "Clk"
 * Copyright Igor Lesik 2016
 */
package curoles.dedesim
package test.circuit
package clk

import curoles.dedesim.Simulator.sim
import curoles.dedesim.Basic._

class DUT(parent: Component, name: String) extends Module(parent, name) {
}

class TB(parent: Component, name: String) extends Module(parent, name) {

    sim.log("Test Bench \"Clk\"")

    // 1st way to define wire
    val reset = new Wire(this, "reset")

    // 2nd way to define wire
    val resetn = wire("resetn")

    inverter(reset, resetn)

    val clk = new Wire(this, "clk")

    Driver.clock(period = 2, clk)

    val dut = new DUT(this, "DUT")
}


