/* Copyright (c) Igor Lesik 2017
 *
 *
 */

package curoles.dedesim.hwlib

import curoles.dedesim.Component
import curoles.dedesim.Module
import curoles.dedesim.Wires
import curoles.dedesim.Simulator.sim

/** Address Decoder.
 *
 *  Decodes N bits of address and sets corresponding Select Line
 *  signal HI. Number of Select Lines is 2^N.
 *
 *  https://en.wikipedia.org/wiki/Address_decoder
 */
class AddrDecoder(
    parent: Component,
    name: String,
    address: Wires,
    select: Wires
)
    extends Module(parent, name)
{
    // Number of select wires must not be less than 2^N where N is width of address.
    require(select.width >= (1 << address.width))

    def addrDecoder(addr: Wires, output: Wires): Unit = {
        def decodeAction() = {
            val addrVal: Int = addr.getSignalAsInt
            sim.afterDelay(0) {
                output.setSignalAsInt(1 << addrVal)
            }
        }

        addr.wires.foreach{ wire => wire.addAction(() => decodeAction) }
    }

    addrDecoder(address, select)
}


import org.scalatest.FlatSpec

class AddrDecoderSpec extends FlatSpec {

    val addr: Wires = new Wires(null, "addr", 2, 3)
    val sel: Wires = new Wires(null, "sel", 4)

    val decoder = new AddrDecoder(null, "decoder", addr, sel)

    it should "decode 00 as 1" in {
        addr.setSignalAsInt(0x0)
        sim.run(1)
        assert(sel.getSignalAsInt == 0x1)
    }

    it should "decode 01 as 2" in {
        addr.setSignalAsInt(0x1)
        sim.run(1)
        assert(sel.getSignalAsInt == 0x2)
    }

    it should "decode 10 as 3" in {
        addr.setSignalAsInt(0x2)
        sim.run(1)
        assert(sel.getSignalAsInt == 0x4)
    }

    it should "decode 11 as 4" in {
        addr.setSignalAsInt(0x3)
        sim.run(1)
        assert(sel.getSignalAsInt == 0x8)
    }

}

