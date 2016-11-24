package curoles.dedesim

import curoles.dedesim._
import curoles.dedesim.Simulator.sim

object Driver {
    def drive(sigVal: Wire#Level, output: Wire): Unit= {
        sim.afterDelay(0) {
            output setSignal sigVal
        }
    }

    def drive(sigVal: Int, output: Wire): Unit = {
        require(sigVal == 1 || sigVal == 0)
        drive(if (sigVal == 1) true else false, output)
    }

    // 'HI 'LO
    def drive(sigVal: Symbol, output: Wire): Unit = {
        require(sigVal == 'HI || sigVal == 'LO)
        drive(if (sigVal == 'HI) true else false, output)
    }

    /** Generates periodic symmetrical clock signal.
     *
     *  @param period time from rising to falling edge
     *
     *  <pre class="textdiagram">
     *    +----------+          +----------+          +----------+
     *    |          |          |          |          |          |
     *  --+          +----------+          +----------+          +--
     *     <-period-> <-period->
     *  </pre>
     */
    def clock(period: Int, output: Wire) {
        def clockAction() = {
            val currentLevel = output.getSignal
            sim.afterDelay(period) {
                sim.log(s"clock ${currentLevel} -> ${!currentLevel}")
                output setSignal !currentLevel
            }
        }
        output addAction clockAction
    }

    /** Generates periodic asymmetrical clock signal.
     *
     *  @param lowPeriod time falling to rising edge
     *  @param hiPeriod time rising to falling edge
     *
     *  <pre class="textdiagram">
     *    +----------+       +----------+       +----------+
     *    |          |       |          |       |          |
     *  --+          +-------+          +-------+          +--
     *     <---hi---> <-low->
     *  </pre>
     */
    def clock(lowPeriod: Int, hiPeriod: Int, output: Wire) {
        def clockAction() = {
            val currentLevel = output.getSignal
            val period = if (currentLevel == true) hiPeriod else lowPeriod
            sim.afterDelay(period) {
                sim.log(s"clock ${currentLevel} -> ${!currentLevel}")
                output setSignal !currentLevel
            }
        }
        output addAction clockAction
    }

}
