package curoles.dedesim

import curoles.dedesim._
import curoles.dedesim.Simulator.sim

object Driver {
    def driver(sigVal: Wire#Level, output: Wire): Unit= {
        output setSignal sigVal
    }

    def driver(sigVal: Int, output: Wire): Unit = {
        require(sigVal == 1 || sigVal == 0)
        driver(if (sigVal == 1) true else false, output)
    }

    // 'hi 'lo
    def driver(sigVal: Symbol, output: Wire): Unit = {
        require(sigVal == 'hi || sigVal == 'lo)
        driver(if (sigVal == 'hi) true else false, output)
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
