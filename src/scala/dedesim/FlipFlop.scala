package curoles.dedesim



/** FlipFlop is a wire with memory, its output value is one
 *  from prev. change and not last change.
 *
 */
class FlipFlop(parent: Component, name: String)
    extends Component(parent, name, 'flipflop)
{

    /** FlipFlop can have only 2 states: HI or LOW */
    type Level = Boolean

    /** Current value */
    private var sigVal: Level = false //TODO option to randomize

    /** Next value */
    private var sigNextVal: Level = false //TODO option to randomize

    /** Get current value */
    def getSignal : Level = sigVal

    def getSignalAsInt: Int = if (getSignal == true) 1 else 0

    /** Changes internal state, changes output to next value.
     *
     *  Call it once per clock change.
     */
    def setSignal(newSigVal: Level) : Unit = {
        sigVal = sigNextVal
        sigNextVal = newSigVal
    }

    def flip(input: Wire) = {
        setSignal(input.getSignal)
    }
}
