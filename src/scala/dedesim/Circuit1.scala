package curoles.dedesim

import curoles.dedesim.Simulator.sim

class Circuit1 {

    sim.log("new circuit")

    val reset = new Wire

    val clk = new Wire

    Driver.clock(2, clk)
}
