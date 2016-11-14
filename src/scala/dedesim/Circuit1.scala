package curoles.dedesim

class Circuit1(simi: Simulation) {

    val sim: Simulation = simi;

    sim.log("new circuit")

    val reset = new Wire(sim)

    val clk = new Wire(sim)

    Driver.clock(2, clk)
    clk.setSignal(true)
}
