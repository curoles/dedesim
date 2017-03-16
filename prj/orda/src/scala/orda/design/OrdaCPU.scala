/* Orda CPU top.
 * Copyright Igor Lesik 2017
 */
package curoles.orda
package design

import curoles.dedesim.Simulator.sim
import curoles.dedesim.Component
import curoles.dedesim.Module
import curoles.dedesim.WireIn
import curoles.dedesim.WiresIn

class OrdaCPU(
    parent: Component,
    name: String,
    clk: WireIn,
    reset: WireIn,
    pins: OrdaCpuPins
)
    extends Module(parent, name)
{
    sim.log("Constructing Orda CPU")

}

case class OrdaCpuPins(
    resetAddr: WiresIn
)

