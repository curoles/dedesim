package curoles.dedesim

object Simulator {

    val version = new Version

    val sim = new Simulation

    val root = new RootModule(null, "root")

    val msg = new Messenger

    def wireChangeEvent(wireId: String, sigVal: Boolean): Unit = {
        msg.wireEvent(sim.currentTime, wireId, sigVal)
    }

    def enableWireEvent() = {

        def enable(c: Component) =
        c match {
            case m: Module => Unit
            case w: Wire => w.changeEvent = wireChangeEvent
            case ws: Wires => Unit
            case _ => Unit
        }

        root.foreach(0, (level,name,x) => enable(x))
    }
}
