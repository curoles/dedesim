/* Copyright (c) Igor Lesik 2016
 *
 */

package curoles.dedesim

import curoles.dedesim.Simulator.sim

/** Basic digital circuit components.
 *
 *
 */
object Basic {

    var inverterDelay = 0

    def inverter(input: Wire, output: Wire) = {
        def invertAction() = {
           val inputSig = input.getSignal
           sim.afterDelay(inverterDelay) {
               output setSignal !inputSig
           }
        }
        input addAction invertAction
    }

    def follow(input: Wire, output: Wire) = {
        def followAction() = {
           val inputSig = input.getSignal
           sim.afterDelay(0) {
               output setSignal inputSig
           }
        }
        input addAction followAction
    }

    def dff(clk: Wire, input: Wire, output: Wire) = {
        val ff = new FlipFlop(null, "ff")
        def dffAction() = {
           val rising = clk.getSignal == true
           if (rising) {
               val inputSig = input.getSignal
               sim.afterDelay(0) {
                   ff.setSignal(inputSig)
                   output setSignal ff.getSignal
               }
           }
        }
        clk addAction dffAction
    }

}
