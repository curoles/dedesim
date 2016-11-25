/* Copyrigth (c) Igor Lesik 2016
 *
 */

package curoles.dedesim

class VCDumper(
    fileName: String,
    msg: Messenger,
    root: Module
    /*list of signals*/
) extends Messenger {

    val vcd = new curoles.dedesim.util.VCDWriter(fileName)

    defineWires()
    subscribeForEvents()

    def close() = vcd.close()

    def defineWires() = {
        var prevLevel = 0
        root.foreach(0,
            (level,name,x) => {defineComponent(level, prevLevel, x); prevLevel = level}
        )
        vcd.endDefinitions()

        writeInitLevels()
    }

    def defineComponent(level: Int, prevLevel: Int, c: Component) = {
        if (level < prevLevel) vcd.upScope() 
        c match {
        case m: Module => vcd.setModuleScope(m.name)
        case w: Wire => vcd.defineWire(w.id, 1)
        case ws: Wires => vcd.defineWire(ws.id, ws.width); vcd.setModuleScope(ws.name)
        case _ => Unit
        }
        level
    }

    def writeInitLevels() = {
        vcd.write("$dumpvars\n")
        root.foreach(0, (lvl,name,x) => x match {
            case w: Wire => vcd.change(w.id, w.getSignalAsInt, 1)
            case ws: Wires => vcd.change(ws.id, ws.getSignalAsInt, ws.width)
            case _ => Unit
            }
        )
        vcd.write("$end\n")
    }

    def subscribeForEvents() = {
        msg.subscribe(this)
    }

    var lastTimestamp: Long = 0

    override def wireEvent(time: Long, id: String, sigVal: Boolean) {
        //println(s"VCD @$time $id changed $sigVal")
        if (time != lastTimestamp) {
            lastTimestamp = time
            vcd.setTimestamp(time)
        }
        vcd.change(id, if (sigVal) 1 else 0, 1)
    }

}
