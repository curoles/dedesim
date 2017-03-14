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

/*
 *  http://rtlery.com/components/barrel-shifter
 *  The direction of the rotate and shift operation is implemented by reversing
 *  the input and output vector. Using this method allows for the shift or rotate
 *  logic to be kept simple, performing only right shift. For a left shift,
 *  the input vector is reversed at the input, goes through the shift logic
 *  which performs a right shift according to the select input and at the output stage,
 *  it is reversed again, resulting in a left shift of the vector.
 *
 * http://preserve.lehigh.edu/cgi/viewcontent.cgi?article=1714&context=etd
 */
class BarrelShifter (
    parent: Component,
    name: String,
    output: Wires,
    select: WiresIn,
    directionLeftRight: WireIn,
    isShiftOrRotate: WireIn,
    in: WiresIn
)
    extends Module(parent, name)
{
    require(output.width >= in.width)
 
    val width = in.width
    val shiftSize = select.width

    val inputOriginalOrReversed = wires("inputOriginalOrReversed", width)
    val outputShifted = wires("outputShifted", width)

    /*  The direction of the rotate operation is implemented by reversing
     *  the input and output vector.
     */
    for (i <- 0 until width) {
        Basic.mux2to1(
            output = inputOriginalOrReversed.wires(i),
            select = directionLeftRight,
            in1 = in.wires(i),
            in2 = in.wires(width - 1 - i)
        )
        Basic.mux2to1(
            output = output.wires(i),
            select = directionLeftRight,
            in1 = outputShifted.wires(i),
            in2 = outputShifted.wires(width - 1 - i)
        )
    }

    val res = new Array[Wires](shiftSize + 1)
    for (i <- 0 until res.length) { res(i) = wires(s"res${i}", width) }
    Basic.follow(output = res(0), input = inputOriginalOrReversed)
    res(shiftSize) = outputShifted

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



import org.scalatest.FlatSpec
import org.scalatest.OneInstancePerTest

class ShifterSpec extends FlatSpec {

    it should "rotate right 4 times b1010000 to b101" in {
        val in = new Wires(null, "in", 8, 0x50)
        val out = new Wires(null, "out", 8)
        val sel = new Wires(null, "sel", 3, 4)
        val dir = new Wire(null, "dir", 0)
        val shiftRotate = new Wire(null, "shiftRotate", 0)
        val shifter = new BarrelShifter(null, "shifter", out, sel, dir, shiftRotate, in)
        sim.run(1)
        assert(out.int == 0x5)
        in.setSignalAsInt(0xf5)
        sim.run(1)
        assert(out.int == 0x5f)
    }

    it should "rotate left 5 times 0x05 to 0xa0" in {
        val in = new Wires(null, "in", 8, 0x05)
        val out = new Wires(null, "out", 8)
        val sel = new Wires(null, "sel", 3, 5)
        val dir = new Wire(null, "dir", 1)
        val shiftRotate = new Wire(null, "shiftRotate", 0)
        val shifter = new BarrelShifter(null, "shifter", out, sel, dir, shiftRotate, in)
        sim.run(1)
        assert(out.int == 0xa0)
        in.setSignalAsInt(0xf5)
        sel.setSignalAsInt(4)
        sim.run(1)
        assert(out.int == 0x5f)
    }

}
