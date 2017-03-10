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

object Adder {

    /** Half adder.
     *
     *  @param in1 first input bit
     *  @param in2 second input bit
     *  @param sum is sum of 2 input bits
     *  @param carry is 1 if both inputs are 1
     *
     *  <hr>
     *  <pre class="textdiagram" id="hwlib.Adder.halfAdder">
     *
     *
     *            +----+
     *            |XOR |
     *    a -+--->|    |  Sum
     *       |    |    +----->
     *    b ---+->|    |
     *       | |  +----+
     *       | |
     *       | |
     *       | |  +----+
     *       | |  |AND |
     *       +--->|    |  Carry
     *         |  |    +----->
     *         +->|    |
     *            +----+
     *
     *  </pre>
     */
    def halfAdder(sum: Wire, carry: Wire, in1: WireIn, in2: WireIn): Unit = {
        Basic.xor2Gate(output = sum, in1 = in1, in2 = in2)
        Basic.and2Gate(output = carry, in1 = in1, in2 = in2)
    }


    /** Full Adder.
     *
     *  @param in1 first input bit
     *  @param in2 second input bit
     *  @param carryIn carry in
     *  @param sum is sum of 2 input bits and carry-in
     *  @param carryOut carry-out
     *
     *  <hr>
     *  <pre class="textdiagram" id="hwlib.Adder.fullAdder">
     *
     *
     *            +------+         +------+
     *            |Half  |  s1     |Half  |            Sum
     *    a ----->|Adder +-------->|Adder +-------------->
     *            |      |  c1     |      |  c2
     *    b ----->|      +--+  +-->|      +--+   +---+
     *            |      |  |  |   |      |  |   |OR |  Carry
     *            +------+  |  |   +------+  +-->|   +--->
     *                      |  |                 |   |
     *                      +------------------->|   |
     *                         |                 +---+
     *    cin -----------------+
     *
     *  </pre>
     */
    def fullAdder(sum: Wire, carryOut: Wire, carryIn: WireIn, in1: WireIn, in2: WireIn): Unit = {
        val sum1 = new Wire(null, "sum1")
        val carry1 = new Wire(null, "carry1")
        val carry2 = new Wire(null, "carry2")
        halfAdder(sum = sum1, carry = carry1, in1 = in1, in2 = in2)
        halfAdder(sum = sum, carry = carry2, in1 = sum1, in2 = carryIn)
        Basic.or2Gate(output = carryOut, in1 = carry2, in2 = carry1)
    }

    /** Full Adder with AND and XOR outputs.
     *
     *  @param in1 first input bit
     *  @param in2 second input bit
     *  @param carryIn carry in
     *  @param sum is sum of 2 input bits and carry-in
     *  @param carryOut carry-out
     *
     *  <hr>
     *  <pre class="textdiagram" id="hwlib.Adder.fullAdderAndXor">
     *
     *                         -------------------------->
     *                         |                       Xor
     *            +------+     |   +------+
     *            |Half  |  s1 |   |Half  |            Sum
     *    a ----->|Adder +-----+-->|Adder +-------------->
     *            |      |  c1     |      |  c2
     *    b ----->|      +--+  +-->|      +--+   +---+
     *            |      |  |  |   |      |  |   |OR |  Carry
     *            +------+  |  |   +------+  +-->|   +--->
     *                      |  |                 |   |
     *                      +---------------+--->|   |
     *                         |            |    +---+  And
     *    cin -----------------+            +------------>
     *
     *  </pre>
     */
    def fullAdderAndXor(
        sum: Wire,
        xor: Wire,
        and: Wire,
        carryOut: Wire,
        carryIn: WireIn,
        in1: WireIn,
        in2: WireIn
    ): Unit =
    {
        val sum1 = new Wire(null, "sum1")
        val carry1 = new Wire(null, "carry1")
        val carry2 = new Wire(null, "carry2")
        halfAdder(sum = sum1, carry = carry1, in1 = in1, in2 = in2)
        halfAdder(sum = sum, carry = carry2, in1 = sum1, in2 = carryIn)
        Basic.or2Gate(output = carryOut, in1 = carry2, in2 = carry1)
    }

}

class RippleCarryAdder (
    parent: Component,
    name: String,
    sum: Wires,
    xor: Wires,
    and: Wires,
    carryOut: Wire,
    overflow: Wire,
    carryIn: WireIn,
    in1: WiresIn,
    in2: WiresIn
)
    extends Module(parent, name)
{
    val width: Int = sum.width

    require(in1.width >= width && in2.width >= width)

    val carry = wires("carry", width + 1)
    Basic.follow(output = carry.wires(0), input = carryIn)
    carry.wires(width) = carryOut

    for (wireId <- 0 until width) {
        Adder.fullAdderAndXor(
            sum      = sum.wires(wireId),
            xor      = xor.wires(wireId),
            and      = and.wires(wireId),
            carryOut = carry.wires(wireId + 1),
            carryIn  = carry.wires(wireId),
            in1      = in1.wires(wireId),
            in2      = in2.wires(wireId)
        )
    }

    Basic.xor2Gate(output = overflow, in1 = carryOut, in2 = carry.wires(width - 1)) 
}

/** Adder-Subtractor
 *
 *
 * https://en.wikipedia.org/wiki/Adder%E2%80%93subtractor
 */
class AdderSubtractor (
    parent: Component,
    name: String,
    sum: Wires,
    xor: Wires,
    and: Wires,
    carryOut: Wire,  // C Carry-out
    overflow: Wire,  // V Overflow
    carryIn: WireIn,
    in1: WiresIn,
    invertIn1: WireIn,
    in2: WiresIn,
    invertIn2: WireIn
)
    extends Module(parent, name)
{
    val width: Int = sum.width

    val not_in1 = wires("not_in1", width)
    Basic.inverter(output = not_in1, in1)

    val not_in2 = wires("not_in2", width)
    Basic.inverter(output = not_in2, in2)

    val in1_or_not = wires("in1_or_not", width)
    Basic.mux2to1(output = in1_or_not, select = invertIn1, in1 = in1, in2 = not_in1)

    val in2_or_not = wires("in2_or_not", width)
    Basic.mux2to1(output = in2_or_not, select = invertIn2, in1 = in2, in2 = not_in2)

    val adder = new RippleCarryAdder(
        parent = this,
        name = "adder",
        sum = sum,
        xor = xor,
        and = and,
        carryOut = carryOut,
        overflow = overflow,
        carryIn = carryIn,
        in1 = in1_or_not,
        in2 = in2_or_not
    )
}


