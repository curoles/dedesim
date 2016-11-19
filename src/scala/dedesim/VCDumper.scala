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
        root.foreach((name,x) => vcd.defineWire(x.id, 1))//FIXME size and diff types
        //vcd.defineWire("root.TB.clk", 1)
        vcd.endDefinitions()
    }

    def subscribeForEvents() = {
        msg.subscribe(this)
    }

    override def wireEvent(time: Long, id: String, sigVal: Boolean) {
        //println(s"VCD @$time $id changed $sigVal")
        vcd.setTimestamp(time) //@todo if new time
        vcd.change(id, sigVal)
    }

}
