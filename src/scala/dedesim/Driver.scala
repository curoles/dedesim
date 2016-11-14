package curoles.dedesim

import curoles.dedesim._
//import edc.Wire.Level

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

    def clock(period: Int, output: Wire) {
        def clockAction() = {
            val currentLevel = output.getSignal
            output.sim.afterDelay(period) {
                output.sim.log(s"clock ${currentLevel} -> ${!currentLevel}")
                output setSignal !currentLevel
            }
        }
        output addAction clockAction
    }
}
