package curoles.dedesim

import curoles.dedesim.De.Action
//import curoles.dedesim.Simulation

class Wire(simi: Simulation) {

    val sim: Simulation = simi;

    type Level = Boolean

    private var sigVal: Level = false
    private var actions: List[Action] = List()

    def getSignal : Level = sigVal

    def setSignal(newSigVal: Level) : Unit = {
        if (newSigVal != sigVal) {
            sigVal = newSigVal
            actions foreach (_ ())
        }
    }

    def addAction(a: Action) = {
        sim.log("wire add action:" + a.toString)
        actions = a :: actions
        a()
    }
}
