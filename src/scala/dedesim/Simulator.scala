package curoles.dedesim

object Simulator {

    val version = new Version

    val sim = new Simulation

    val root = new RootModule(null, "root")

    val msg = new Messenger

    def wireChangeEvent(wire: Wire): Unit = {
        msg.wireEvent(sim.currentTime, wire.id, wire.getSignal)
        if (wire.parent.cType == 'wires) {
            val ws = wire.parent.asInstanceOf[Wires]
            msg.wireEvent(sim.currentTime, ws.id, ws.getSignalAsInt.toLong, ws.width)
        }
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
