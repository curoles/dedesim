package curoles.dedesim

//import scala.collection._


/** Wire represents an electrical wire.
 *
 *  Wire is Trigger object, when a signal on wire
 *  changes, it can trigger changes for other wires
 *  and Trigger objects. 
 */
class WireIn(parent: Component, name: String, initVal: Int = 0)
    extends Component(parent, name, 'wire)
    with Trigger
{

    /** Wire can have only 2 states: HI or LOW */
    type Level = Boolean

    /** Current value */
    protected var sigVal: Level = if (initVal == 0) false else true

    /** Get current value */
    def getSignal : Level = sigVal

    def getSignalAsInt: Int = if (getSignal == true) 1 else 0

    def int = getSignalAsInt

    ///** Change value if new value != current value */
    //def setSignal(newSigVal: Level) : Unit = {
    //    if (newSigVal != sigVal) {
    //        sigVal = newSigVal
    //        changeEvent(this)
    //        act // trigger associated actions
    //    }
    //}

    /** Points to current event handler */
    var changeEvent: (Wire) => Unit = dontSendChangeEvent

    /** Do not send change event, do nothing */
    def dontSendChangeEvent(wire: Wire): Unit = { }
}

/** Extends class WireIn with method setSignal.
 *
 */
class Wire(parent: Component, name: String, initVal: Int = 0)
    extends WireIn(parent, name, initVal)
{
    /** Change value if new value != current value */
    def setSignal(newSigVal: Level) : Unit = {
        if (newSigVal != sigVal) {
            sigVal = newSigVal
            changeEvent(this)
            act // trigger associated actions
        }
    }
}

class WiresIn(parent: Component, name: String, val width: Int, initVal: Long = 0)
    extends Component(parent, name, 'wires)
    with Trigger
{
    require(width > 0)
 
    val wires = new Array[Wire](width)

    for { i <- wires.indices } {
        wires(i) = new Wire(this, s"$i", (initVal & (1L << i)).toInt)
    }

    /** Construct from another existing wires */
    def this(name: String, origin: WiresIn, from: Int, to: Int) {
        this(origin.parent, name, to - from + 1, 0)
        for (index <- wires.indices) {
            wires(index) = origin.wires(from + index)
        }
    }

    type WiresFromTo = Tuple3[WiresIn, Int, Int]

    def this(parent: Component, name: String, origins: WiresIn#WiresFromTo*) {
        this(parent, name, 10, 0)//TODO FIXME
    }

    def getSignal(bitNum: Int) = wires(bitNum).getSignal

    def getSignalAsInt: Long = {
        wires.zipWithIndex.foldLeft(0){
           case(acc,(wire,i)) => acc + (wire.getSignalAsInt << i)
        }
    }

    def int = getSignalAsInt

    //def setSignal(bitNum: Int, newVal: Wire#Level) = {
    //    wires(bitNum).setSignal(newVal)
    //}

    //def setSignalAsInt(n: Long): Unit = {
    //    if (n != getSignalAsInt) {
    //        for { bitIndex <- wires.indices } {
    //            val bitVal = (n & (1 << bitIndex)) != 0
    //            setSignal(bitIndex, bitVal)
    //        }
    //    }
    //}

    override def addAction(a: De.Action) = {
        super.addAction(a)
        wires.foreach(wire => wire.addAction(a))
    }

    def slice(from: Int, to: Int): Array[Wire] = wires.slice(from = from, until = to + 1)

    def newSlice(name: String, from: Int, to: Int): WiresIn = {
        new WiresIn(name, this, from, to)
    }
}

class Wires(parent: Component, name: String, width: Int, initVal: Long = 0)
    extends WiresIn(parent, name, width, initVal)
{

    /** Construct from another existing wires */
    def this(name: String, origin: Wires, from: Int, to: Int) {
        this(origin.parent, name, to - from + 1, 0)
        for (index <- wires.indices) {
            wires(index) = origin.wires(from + index)
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

    override def newSlice(name: String, from: Int, to: Int): Wires = {
        new Wires(name, this, from, to)
    }

}
