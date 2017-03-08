/* Copyright (c) Igor Lesik 2017
 *
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


/** ALU Arithmetic Logic Unit 
 *
 *  https://en.wikipedia.org/wiki/Adder%E2%80%93subtractor
 */
class ALU (
    parent: Component,
    name: String,
    result: Wires,
    carryOut: Wire,  // C Carry-out
    overflow: Wire,  // V Overflow
    in1: WiresIn,
    in2: WiresIn,
    opIn1AddIn2: WireIn,
    opIn1SubIn2: WireIn,
    opIn2SubIn1: WireIn
)
    extends Module(parent, name)
{
    val width: Int = result.width

    require(in1.width >= width && in2.width >= width)

    val carryIn = wire("carryIn")
    val invertIn1 = wire("invertIn1")
    val invertIn2 = wire("invertIn2")

    Basic.orGate(output = invertIn1, opIn2SubIn1)
    Basic.orGate(output = invertIn2, opIn1SubIn2)
    Basic.orGate(output = carryIn, opIn1SubIn2, opIn2SubIn1)

    val adder = new AdderSubtractor(
        parent = this,
        name = "adder",
        sum = result,
        carryOut = carryOut,
        overflow = overflow,
        carryIn = carryIn,
        in1 = in1,
        invertIn1 = invertIn1,
        in2 = in2,
        invertIn2 = invertIn2
    )
}

import org.scalatest.FlatSpec

class ALUSpec extends FlatSpec {

    val result = new Wires(null, "result", 32)
    val in1 = new Wires(null, "in1", 32)
    val in2 = new Wires(null, "in2", 32)
    val carryOut = new Wire(null, "carryOut")
    val overflow = new Wire(null, "overflow")
    val opIn1AddIn2 = new Wire(null, "opIn1AddIn2")
    val opIn1SubIn2 = new Wire(null, "opIn1SubIn2")
    val opIn2SubIn1 = new Wire(null, "opIn2SubIn2")

    val alu = new ALU (
        parent = null,
        name = "alu",
        result = result,
        carryOut = carryOut,
        overflow = overflow,
        in1 = in1,
        in2 = in2,
        opIn1AddIn2 = opIn1AddIn2,
        opIn1SubIn2 = opIn1SubIn2,
        opIn2SubIn1 = opIn2SubIn1
    )

    it should "sum 22 and 33 as 55" in {
        in1.setSignalAsInt(33)
        in2.setSignalAsInt(22)
        opIn1AddIn2.setSignal(true)
        sim.run(1)
        assert(result.int == 55)
    }

    it should "33-22=11" in {
        in1.setSignalAsInt(33)
        in2.setSignalAsInt(22)
        opIn1AddIn2.setSignal(false)
        opIn1SubIn2.setSignal(true)
        sim.run(1)
        assert(result.int == 11)
    }

}
