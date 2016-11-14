package curoles.dedesim

import curoles.dedesim.Simulator.sim

/** Wire represents an electrical wire.
 *
 *  Wire is Trigger object, when a signal on wire
 *  changes it can trigger changes for other wires
 *  and Trigger objects. 
 */
class Wire extends Trigger {

    /** Wire can have only 2 states: HI or LOW */
    type Level = Boolean

    /** Current value */
    private var sigVal: Level = false

    /** Get current value */
    def getSignal : Level = sigVal

    /** Change value if new value != current value */
    def setSignal(newSigVal: Level) : Unit = {
        if (newSigVal != sigVal) {
            sigVal = newSigVal
            act // trigger associated actions
        }
    }

    /*override def addAction(a: Action) = {
        sim.log("wire add action:" + a.toString)
        actions = a :: actions
        a()
    }*/
}

class Wires(val width: Int) extends Trigger {
    require(width > 0)
    
    type WireArray = Array[Wire]

    val bit = new WireArray(width)

    def setSignal(bitNum: Int, newVal: Wire#Level) = bit(bitNum).setSignal(newVal)
}



