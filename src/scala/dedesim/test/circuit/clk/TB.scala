/* Test Circuit "Clk"
 * Copyright Igor Lesik 2016
 */
package curoles.dedesim
package test.circuit
package clk

import curoles.dedesim.Simulator.sim
import curoles.dedesim.Basic._

class DUT(
    parent: Component,
    name: String,
    clk: Wire
)
    extends Module(parent, name) {

    val clkn = wire("clkn")

    inverter(clk, clkn)

    val bus1 = new Wires(this, "bus1", 3)

    follow(clk, bus1.wires(0))
    follow(clkn, bus1.wires(1))

    Driver.drive('HI, bus1.wires(2))

    val asyncClk = wire("asyncClk")

    Driver.clock(lowPeriod = 1, hiPeriod = 3, asyncClk)

    val asyncClk_ = wire("asyncClk_")

    dff(clk, asyncClk, asyncClk_)
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

    val dut = new DUT(this, "DUT", clk)
}


