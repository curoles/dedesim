/* Circuit "Clk"
 * Copyright Igor Lesik 2016
 */
package curoles.dedesim
package test.circuit
package clk

import curoles.dedesim.Simulator.sim

class DUT(parent: Component, name: String) extends Component(parent, name) {
}

class TB(parent: Component, name: String) extends Component(parent, name) {

    sim.log("Test Bench \"Clk\"")

    val reset = new Wire(this, "reset")

    val clk = new Wire(this, "clk")

    Driver.clock(period = 2, clk)

    val dut = new DUT(this, "DUT")
}


