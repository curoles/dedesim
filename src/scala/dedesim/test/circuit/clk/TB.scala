/* Circuit "Clk"
 * Copyright Igor Lesik 2016
 */
package curoles.dedesim
package test.circuit
package clk

import curoles.dedesim.Simulator.sim

class DUT {
}

class TB {
    sim.log("Test Bench \"Clk\"")

    val reset = new Wire

    val clk = new Wire

    Driver.clock(period = 2, clk)

    val dut = new DUT
}


