package curoles.dedesim

import curoles.dedesim.Simulator.sim
import curoles.dedesim.Simulator.msg

/** Wire represents an electrical wire.
 *
 *  Wire is Trigger object, when a signal on wire
 *  changes it can trigger changes for other wires
 *  and Trigger objects. 
 */
class Wire(parent: Component, name: String)
    extends Component(parent, name, 'wire)
    with Trigger {

    /** Wire can have only 2 states: HI or LOW */
    type Level = Boolean

    /** Current value */
    private var sigVal: Level = false

    /** Get current value */
    def getSignal : Level = sigVal

    def getSignalAsInt: Int = if (getSignal == true) 1 else 0

    /** Change value if new value != current value */
    def setSignal(newSigVal: Level) : Unit = {
        if (newSigVal != sigVal) {
            sigVal = newSigVal
            changeEvent(sigVal)
            act // trigger associated actions
        }
    }

    /*override def addAction(a: Action) = {
        sim.log("wire add action:" + a.toString)
        actions = a :: actions
        a()
    }*/

    /** Points to current event handler */
    var changeEvent: (Level) => Unit = /*dontS*/sendChangeEvent

    /** Do not send change event, do nothing */
    def dontSendChangeEvent(sigVal: Level): Unit = { }

    def sendChangeEvent(sigVal: Level): Unit = {
        msg.wireEvent(sim.currentTime, id, sigVal)
    }

}

class Wires(parent: Component, name: String, val width: Int)
    extends Component(parent, name, 'wires)
    with Trigger
{
    require(width > 0)
 
    type WireArray = Array[Wire]

    val wires = new WireArray(width)
    for { i <- wires.indices } wires(i) = new Wire(this, s"$name[$i]")

    def getSignal(bitNum: Int) = wires(bitNum).getSignal

    def getSignalAsInt: Int = {
        wires.zipWithIndex.foldLeft(0){ case(acc,(wire,i)) => acc + (wire.getSignalAsInt << i) } 
    }

    def setSignal(bitNum: Int, newVal: Wire#Level) = wires(bitNum).setSignal(newVal)

    /*def setSignal(newSigVal: Level) : Unit = {
        if (newSigVal != sigVal) {
            sigVal = newSigVal
            changeEvent(sigVal)
            act // trigger associated actions
        }
    }*/


}



