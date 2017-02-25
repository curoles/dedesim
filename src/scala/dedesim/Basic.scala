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
    var andGateDelay = 0
    var orGateDelay = 0

    def inverter(input: Wire, output: Wire): Unit = {
        def invertAction() = {
           val inputSig = input.getSignal
           sim.afterDelay(inverterDelay) {
               output setSignal !inputSig
           }
        }
        input addAction (() => invertAction)
    }

    def inverter(input: Wires, output: Wires): Unit = {
        require(input.width == output.width)
        (input.wires, output.wires).zipped.foreach((i,o) => inverter(i,o))
    }


    def follow(input: Wire, output: Wire): Unit = {
        def followAction() = {
           val inputSig = input.getSignal
           sim.afterDelay(0) {
               output setSignal inputSig
           }
        }
        input addAction (() => followAction)
    }

    def follow(input: Wires, output: Wires): Unit = {
        require(input.width == output.width)
        (input.wires, output.wires).zipped.foreach((i,o) => follow(i,o))
    }

    def dff(clk: Wire, input: Wire, output: Wire): Unit = {
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
        clk addAction (() => dffAction)
    }

    def dff(clk: Wire, input: Wires, output: Wires): Unit = {
        require(input.width == output.width)
        (input.wires, output.wires).zipped.foreach((i,o) => dff(clk,i,o))
    }

    def andGate(in1: Wire, in2: Wire, output: Wire): Unit = {
        def andAction() = {
            val in1Sig = in1.getSignal
            val in2Sig = in2.getSignal
            sim.afterDelay(andGateDelay) {
                output setSignal (in1Sig & in2Sig)
            }
        }
        in1 addAction (() => andAction)
        in2 addAction (() => andAction)
    }

    def andGate(in1: Wires, in2: Wires, output: Wires): Unit = {
        require(in1.width == in2.width && in2.width == output.width)
        (in1.wires, in2.wires, output.wires).zipped.foreach((i1,i2,o) => andGate(i1,i2,o))
    }

    def orGate(in1: Wire, in2: Wire, output: Wire) = {
        def orAction() = {
            val in1Sig = in1.getSignal
            val in2Sig = in2.getSignal
            sim.afterDelay(orGateDelay) {
                output setSignal (in1Sig & in2Sig)
            }
        }
        in1 addAction (() => orAction)
        in2 addAction (() => orAction)
    }

    def orGate(in1: Wires, in2: Wires, output: Wires): Unit = {
        require(in1.width == in2.width && in2.width == output.width)
        (in1.wires, in2.wires, output.wires).zipped.foreach((i1,i2,o) => orGate(i1,i2,o))
    }


    def monitor(components: Tuple2[Symbol,Trigger]*)(block: => Unit) = {
        def monitorLevel() = {
            sim.afterDelay(delay = 0)(block)
        }
        def addMonitorAction(c: Tuple2[Symbol,Trigger]) = {
            val action: De.Action = c._1 match {
                //case 'rise => monitorRise
                //case 'fall => monitorFall
                case _     => (() => monitorLevel)
            }
            c._2.addAction(action)
        }
        components foreach addMonitorAction
    }

    def mux2to1(select: Wire, in1: Wire, in2: Wire, output: Wire): Unit = {
        def muxAction() = {
            val in1Sig = in1.getSignal
            val in2Sig = in2.getSignal
            val selSig = select.getSignal
            sim.afterDelay(0) {
                output setSignal (if (selSig) in2Sig else in1Sig)
            }
        }
        in1 addAction (() => muxAction)
        in2 addAction (() => muxAction)
        select addAction (() => muxAction)
    }

    def mux2to1(select: Wire, in1: Wires, in2: Wires, output: Wires): Unit = {
        require(in1.width == in2.width && in2.width == output.width)
        (in1.wires, in2.wires, output.wires).zipped.foreach((i1,i2,o) => mux2to1(select,i1,i2,o))
    }

    def adder(in1: Wires, in2: Wires, output: Wires): Unit = {
        require(in1.width == in2.width && in2.width == output.width)
        def addAction() = {
            val in1Sig = in1.getSignalAsInt
            val in2Sig = in2.getSignalAsInt
            sim.afterDelay(0) {
                output.setSignalAsInt(in1Sig + in2Sig)
            }
        }
        in1 addAction (() => addAction)
        in2 addAction (() => addAction)
    }

}
