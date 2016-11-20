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
    }

    def writeInitLevels() = {
        vcd.write("$dumpvars\n")
        root.foreach(0, (lvl,name,x) => if (x.cType == 'wire) vcd.change(x.id, false))//FIXME
        vcd.write("$end\n")
    }

    def subscribeForEvents() = {
        msg.subscribe(this)
    }

    override def wireEvent(time: Long, id: String, sigVal: Boolean) {
        //println(s"VCD @$time $id changed $sigVal")
        vcd.setTimestamp(time) //@todo if new time FIXME
        vcd.change(id, sigVal)
    }

}
