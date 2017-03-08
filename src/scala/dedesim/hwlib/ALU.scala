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
    cmdIn1AddIn2: WireIn,
    cmdIn1SubIn2: WireIn,
    cmdIn2SubIn1: WireIn
)
    extends Module(parent, name)
{
    val width: Int = result.width

    require(in1.width >= width && in2.width >= width)

    val carryIn = wire("carryIn")
    val invertIn1 = wire("invertIn1")
    val invertIn2 = wire("invertIn2")

    Basic.orGate(output = invertIn1, cmdIn2SubIn1)
    Basic.orGate(output = invertIn2, cmdIn1SubIn2)
    Basic.orGate(output = carryIn, cmdIn1SubIn2, cmdIn2SubIn1)

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
