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
    //negative, zero, parity
    in1: WiresIn,
    in2: WiresIn,
    opZero: WireIn,
    opOne: WireIn,
    opPassIn1: WireIn,
    opPassIn2: WireIn,
    opIncIn1: WireIn,
    opIncIn2: WireIn,
    opDecIn1: WireIn,
    opDecIn2: WireIn,
    opInvIn1: WireIn,
    opInvIn2: WireIn,
    opNegIn1: WireIn,
    opNegIn2: WireIn,
    opIn1AddIn2: WireIn,
    opIn1SubIn2: WireIn,
    opIn2SubIn1: WireIn
    //add with carry
    //subtract with borrow
    //xor
    //and
    //or
    //nor
    //nand
)
    extends Module(parent, name)
{
    val width: Int = result.width

    require(in1.width >= width && in2.width >= width)

    val carryIn = wire("carryIn")
    val invertIn1 = wire("invertIn1")
    val invertIn2 = wire("invertIn2")
    val setIn1Zero = wire("setIn1Zero")
    val setIn2Zero = wire("setIn2Zero")

    Basic.orGate(output = invertIn1, opIn2SubIn1, opInvIn1, opNegIn1, opDecIn2)
    Basic.orGate(output = invertIn2, opIn1SubIn2, opInvIn2, opNegIn2, opDecIn1)
    Basic.orGate(output = carryIn, opOne, opIn1SubIn2, opIn2SubIn1, opIncIn1, opIncIn2, opNegIn1, opNegIn2)
    Basic.orGate(output = setIn1Zero, opZero, opOne, opPassIn2, opIncIn2, opDecIn2, opInvIn2, opNegIn2)
    Basic.orGate(output = setIn2Zero, opZero, opOne, opPassIn1, opIncIn1, opDecIn1, opInvIn1, opNegIn1)

    val in1_or_zero = wires("in1_or_zero", width)
    val in2_or_zero = wires("in2_or_zero", width)
    val zero = wires("zero", width, 0)
    Basic.mux2to1(output = in1_or_zero, select = setIn1Zero, in1 = in1, in2 = zero)
    Basic.mux2to1(output = in2_or_zero, select = setIn2Zero, in1 = in2, in2 = zero)

    val xorResult = wires("xorResult", width)
    val andResult = wires("andResult", width)
    val orResult = wires("orResult", width)
    Basic.andGate(output = orResult, xorResult, andResult)

    val adder = new AdderSubtractor(
        parent = this,
        name = "adder",
        sum = result,
        xor = xorResult,
        and = andResult,
        carryOut = carryOut,
        overflow = overflow,
        carryIn = carryIn,
        in1 = in1_or_zero,
        invertIn1 = invertIn1,
        in2 = in2_or_zero,
        invertIn2 = invertIn2
    )

    //mux4to1(select0 = , select1 = , output = result,
    //    in1 = sumResult, in2 = xorResult, in3 = andResult, in4 = orResult)
}

import org.scalatest.FlatSpec
import org.scalatest.OneInstancePerTest

class ALUSpec extends FlatSpec with OneInstancePerTest {

    behavior of "ALU - Arithmetic Logic Unit"

    val result = new Wires(null, "result", 32)
    val in1 = new Wires(null, "in1", 32)
    val in2 = new Wires(null, "in2", 32)
    val carryOut = new Wire(null, "carryOut")
    val overflow = new Wire(null, "overflow")

    val opZero = new Wire(null, "opZero")
    val opOne = new Wire(null, "opOne")
    val opPassIn1 = new Wire(null, "opPassIn1")
    val opPassIn2 = new Wire(null, "opPassIn2")
    val opIncIn1 = new Wire(null, "opIncIn1")
    val opIncIn2 = new Wire(null, "opIncIn2")
    val opDecIn1 = new Wire(null, "opDecIn1")
    val opDecIn2 = new Wire(null, "opDecIn2")
    val opInvIn1 = new Wire(null, "opInvIn1")
    val opInvIn2 = new Wire(null, "opInvIn2")
    val opNegIn1 = new Wire(null, "opNegIn1")
    val opNegIn2 = new Wire(null, "opNegIn2")
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
        opZero = opZero,
        opOne = opOne,
        opPassIn1 = opPassIn1,
        opPassIn2 = opPassIn2,
        opIncIn1 = opIncIn1,
        opIncIn2 = opIncIn2,
        opDecIn1 = opDecIn1,
        opDecIn2 = opDecIn2,
        opInvIn1 = opInvIn1,
        opInvIn2 = opInvIn2,
        opNegIn1 = opNegIn1,
        opNegIn2 = opNegIn2,
        opIn1AddIn2 = opIn1AddIn2,
        opIn1SubIn2 = opIn1SubIn2,
        opIn2SubIn1 = opIn2SubIn1
    )

    it should "use working Half Adder" in {
        val i1 = new Wire(null, "i1")
        val i2 = new Wire(null, "i2")
        val s = new Wire(null, "s")
        val c = new Wire(null, "c")
        Adder.halfAdder(sum = s, carry = c, in1 = i1, in2 = i2)
        sim.run(1)
        assert(s.getSignal == false && c.getSignal == false)
        i1.setSignal(true)
        sim.run(1)
        assert(s.getSignal == true && c.getSignal == false)
        i2.setSignal(true)
        sim.run(1)
        assert(s.getSignal == false && c.getSignal == true)
    }

    it should "use working RippleCarryAdder" in {
        in1.setSignalAsInt(331)
        in2.setSignalAsInt(221)
        val rcaSum = new Wires(null, "rcaSum", 32)
        val rcaXor = new Wires(null, "rcaXor", 32)
        val rcaAnd = new Wires(null, "rcaAnd", 32)
        val rcaCarryOut = new Wire(null, "rcaCarryOut")
        val rcaOverflow = new Wire(null, "rcaOverflow")
        val rcaCarryIn = new Wire(null, "rcaCarryIn")
        val rca = new RippleCarryAdder(
            parent = null,
            name = "rca",
            sum = rcaSum,
            xor = rcaXor,
            and = rcaAnd,
            carryOut = rcaCarryOut,
            overflow = rcaOverflow,
            carryIn = rcaCarryIn,
            in1 = in1, in2 = in2
        )
        sim.run(1)
        assert(rcaSum.int == 552)
    }

    it should "opOne makes result=1" in {
        in1.setSignalAsInt(33)
        in2.setSignalAsInt(22)
        opOne.setSignal(true)
        sim.run(1)
        assert(result.int == 1)
    }

    it should "pass in1 when opPassIn1" in {
        opPassIn1.setSignal(true)
        in1.setSignalAsInt(77)
        in2.setSignalAsInt(22)
        sim.run(1)
        assert(result.int == in1.int)
    }

    it should "pass in2 when opPassIn2" in {
        opPassIn2.setSignal(true)
        in1.setSignalAsInt(22)
        in2.setSignalAsInt(777)
        sim.run(1)
        assert(result.int == in2.int)
    }

    it should "increment in1 when opIncIn1" in {
        opIncIn1.setSignal(true)
        in1.setSignalAsInt(77)
        in2.setSignalAsInt(22)
        sim.run(1)
        assert(alu.in2_or_zero.int == 0 && alu.in1_or_zero.int == 77)
        assert(alu.invertIn1.getSignal == false && alu.invertIn2.getSignal == false)
        assert(alu.adder.in1_or_not.int == in1.int && alu.adder.in2_or_not.int == 0)
        assert(result.int == (in1.int + 1))
    }

    it should "increment in2 when opIncIn2" in {
        opIncIn2.setSignal(true)
        in1.setSignalAsInt(77)
        in2.setSignalAsInt(22)
        sim.run(1)
        assert(result.int == (in2.int + 1))
    }

    it should "decrement 33 to 32" in {
        in1.setSignalAsInt(33)
        in2.setSignalAsInt(22)
        opDecIn1.setSignal(true)
        sim.run(1)
        assert(result.int == (in1.int - 1))
        opDecIn1.setSignal(false)
        opDecIn2.setSignal(true)
        sim.run(1)
        assert(result.int == (in2.int - 1))
    }

    it should "sum 221 and 331 as 552" in {
        in1.setSignalAsInt(331)
        in2.setSignalAsInt(221)
        opIn1AddIn2.setSignal(true)
        sim.run(1)
        assert(result.int == 552)
    }

    it should "33-22=11" in {
        in1.setSignalAsInt(33)
        in2.setSignalAsInt(22)
        opIn1SubIn2.setSignal(true)
        sim.run(1)
        assert(result.int == 11)
        opIn1SubIn2.setSignal(false)
        opIn2SubIn1.setSignal(true)
        sim.run(1)
        assert(result.int == -11)
    }

    it should "invert 0xf to 0xff..0" in {
        in1.setSignalAsInt(0)
        in2.setSignalAsInt(0xf)
        opInvIn1.setSignal(true)
        sim.run(1)
        assert(result.int == 0xffffffff)
        opInvIn1.setSignal(false)
        opInvIn2.setSignal(true)
        sim.run(1)
        assert(result.int == 0xfffffff0)
    }

    it should "negate 33 to -33" in {
        in1.setSignalAsInt(33)
        in2.setSignalAsInt(22)
        opNegIn1.setSignal(true)
        sim.run(1)
        assert(result.int == -33)
        opNegIn1.setSignal(false)
        opNegIn2.setSignal(true)
        sim.run(1)
        assert(result.int == -22)
    }

}
