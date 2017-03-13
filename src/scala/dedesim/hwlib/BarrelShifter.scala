/* Copyright (c) Igor Lesik 2017
 * File BarrelShifter.scala
 *
 */

package curoles.dedesim.hwlib

import curoles.dedesim.Component
import curoles.dedesim.Module
import curoles.dedesim.WireIn
import curoles.dedesim.Wire
import curoles.dedesim.WiresIn
import curoles.dedesim.Wires
import curoles.dedesim.Simulator.sim
import curoles.dedesim.Basic

class BarrelShifter (
    parent: Component,
    name: String,
    output: Wires,
    select: WiresIn,
    in: WiresIn
)
    extends Module(parent, name)
{
    require(output.width >= in.width)
 
    val width = in.width
    val shiftSize = select.width

    val res = new Array[Wires](shiftSize + 1)
    //res.zipWithIndex.foreach {case (w,i) => w = wires(s"res${i}", width)}
    for (i <- 0 until res.length) { res(i) = wires(s"res${i}", width) }
    Basic.follow(output = res(0), input = in)
    res(shiftSize) = output

    def makeShift(shiftId: Int) = {
        val distance = 1 << shiftId
        for (i <- 0 until width) {
            Basic.mux2to1(
                output = res(shiftId + 1).wires(i),
                select = select.wires(shiftId),
                in1 = res(shiftId).wires(i),
                in2 = res(shiftId).wires((i + distance) % width)
            )
        }
    }

    for (shiftId <- 0 until shiftSize) {
        makeShift(shiftId)
    }
}

