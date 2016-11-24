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
        root.foreach(0, (level,name,x) => defineComponent(level, x))
        vcd.endDefinitions()

        writeInitLevels()
    }

    def defineComponent(level: Int, c: Component) = {
        //FIXME TODO if level up then $upscope $end 
        if (c.cType == 'module) vcd.setModuleScope(c.name)
        else if (c.cType == 'wire) vcd.defineWire(c.id, 1)
        else if (c.cType == 'wires) vcd.defineWire(c.id, c.asInstanceOf[Wires].width)
    }

    def writeInitLevels() = {
        vcd.write("$dumpvars\n")
        root.foreach(0, (lvl,name,x) => x match {
            //if (x.cType == 'wire) vcd.change(x.id, x.asInstanceOf[Wire].getSignalAsInt, 1)
            //else if (x.cType == 'wires) vcd.change(x.id, x.asInstanceOf[Wires].getSignal)
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

    override def wireEvent(time: Long, id: String, sigVal: Boolean) {
        //println(s"VCD @$time $id changed $sigVal")
        vcd.setTimestamp(time) //@todo if new time FIXME
        vcd.change(id, if (sigVal) 1 else 0, 1)
    }

}
