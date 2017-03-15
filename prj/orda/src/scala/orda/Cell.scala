/* Copyright (c) Igor Lesik 2017
 *
 *
 */

package curoles.orda

/** Computation Cell.
 *
 *  Cell's interface looks like an interface to a memory,
 *  that is read/write a memory location.  
 *
 */
trait Cell[T] (
    parent: Component,
    name: String,
    start: WireIn,
    writeEn: WireIn,
    writeAddr: WiresIn,
    writeData: WiresIn,
    readEn: WireIn,
    readAddr: WiresIn,
    readData: Wires
)
    extends Module(parent, name)
{


}
