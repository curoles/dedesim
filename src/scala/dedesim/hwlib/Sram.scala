/* Copyright (c) Igor Lesik 2013
 *
 *
 */

package curoles.dedesim.hwlib

/** Static RAM.
 *
 */
class Sram(
    parent: Component,
    name: String,
    wordWidth: Int,
    size: Int,
    clk: Wire,
    readEnable: Wire,
    readAddr: Wires,
    reading: Wires,
    writeEnable: Wire,
    writeAddr: Wires,
    writing: Wires
)
    externds Module(parent, name)
{


    def read(atIndex: Int): Int = {

    }
}
