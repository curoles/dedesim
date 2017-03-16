// println("DeDeSim job script \"Health Check\"")

import curoles.orda.dv.TestBench

val circuit = new TestBench(simulator.root, "TB")

sim.run(120)

//    val vcd = new VCD(\"wave.vcd\"); \
//    simulator.enableWireEvent(); \
//    sim.run(120); \
//    vcd.close();

