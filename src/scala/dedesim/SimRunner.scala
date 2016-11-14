package curoles.dedesim

//scala -cp "./build/scala/class:./build/scala/extralib/*" curoles.dedesim.SimRunner

object SimRunner {
    def main(args: Array[String]): Unit = {
        val sim = new Simulation
        val circuit = new Circuit1(sim)
        sim.run
        sys.exit(0)
    }
}
