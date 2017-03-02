package curoles.dedesim

//import scala.collection._


/** Wire represents an electrical wire.
 *
 *  Wire is Trigger object, when a signal on wire
 *  changes it can trigger changes for other wires
 *  and Trigger objects. 
 */
class Wire(parent: Component, name: String, initVal: Int = 0)
    extends Component(parent, name, 'wire)
    with Trigger {

    /** Wire can have only 2 states: HI or LOW */
    type Level = Boolean

    /** Current value */
    private var sigVal: Level = if (initVal == 0) false else true

    /** Get current value */
    def getSignal : Level = sigVal

    def getSignalAsInt: Int = if (getSignal == true) 1 else 0

    /** Change value if new value != current value */
    def setSignal(newSigVal: Level) : Unit = {
        if (newSigVal != sigVal) {
            sigVal = newSigVal
            changeEvent(this)
            act // trigger associated actions
        }
    }

    /*override def addAction(a: Action) = {
        sim.log("wire add action:" + a.toString)
        actions = a :: actions
        a()
    }*/

    /** Points to current event handler */
    var changeEvent: (Wire) => Unit = dontSendChangeEvent

    /** Do not send change event, do nothing */
    def dontSendChangeEvent(wire: Wire): Unit = { }

    //def sendChangeEvent(sigVal: Level): Unit = {
    //    msg.wireEvent(sim.currentTime, id, sigVal)
    //}

}

class Wires(parent: Component, name: String, val width: Int, initVal: Long = 0)
    extends Component(parent, name, 'wires)
    with Trigger
{
    require(width > 0)
 
    val wires = new Array[Wire](width)

    for { i <- wires.indices } {
        wires(i) = new Wire(this, s"$i", (initVal & (1L << i)).toInt)
    }

    /** Construct from another existing wires */
    def this(name: String, origin: Wires, from: Int, to: Int) {
        this(origin.parent, name, to - from + 1, 0)
        for (index <- wires.indices) {
            wires(index) = origin.wires(from + index)
        }
    }

    def getSignal(bitNum: Int) = wires(bitNum).getSignal

    def getSignalAsInt: Long = {
        wires.zipWithIndex.foldLeft(0){
           case(acc,(wire,i)) => acc + (wire.getSignalAsInt << i)
        }
    }

    def setSignal(bitNum: Int, newVal: Wire#Level) = {
        wires(bitNum).setSignal(newVal)
    }

    def setSignalAsInt(n: Long): Unit = {
        if (n != getSignalAsInt) {
            for { bitIndex <- wires.indices } {
                val bitVal = (n & (1 << bitIndex)) != 0
                setSignal(bitIndex, bitVal)
            }
        }
    }

    override def addAction(a: De.Action) = {
        super.addAction(a)
        wires.foreach(wire => wire.addAction(a))
    }

    def slice(from: Int, to: Int): Array[Wire] = wires.slice(from = from, until = to + 1)

    def newSlice(name: String, from: Int, to: Int): Wires = {
        new Wires(name, this, from, to)
    }
}

