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

/**
  *
  * https://en.wikipedia.org/wiki/CPU_cache
  *
  * <hr>
  * <pre class="textdiagram" id="hwlib.CacheEntry">
  * +-----+------------+-----------+
  * | tag | data block | flag bits |
  * +-----+------------+-----------+
  * </pre>
  *
  */
class CacheEntry (
    val tagWidth:   Int,
    val dataWidth:  Int,
    val flagsWidth: Int
)
{
    val tag: Word = new Word(tagWidth)
    val data: Word = new Word(dataWidth)
    val flags: Word = new Word(flagsWidth)
}

class CacheSet (
    val tagWidth:   Int,
    val dataWidth:  Int,
    val flagsWidth: Int,
    val nrWays: Int
)
{
    val ways: Array[CacheEntry] = Array.fill(nrWays) {
        new CacheEntry(tagWidth, dataWidth, flagsWidth)
    }
}

/**
  *
  * https://en.wikipedia.org/wiki/CPU_cache
  * http://www.ijcaonline.org/research/volume129/number1/chauan-2015-ijca-906787.pdf
  *
  * <hr>
  * <pre class="textdiagram" id="hwlib.CacheArray.entry">
  * +-----+------------+-----------+
  * | tag | data block | flag bits |
  * +-----+------------+-----------+
  * </pre>
  *
  * <hr>
  * <pre class="textdiagram" id="hwlib.CacheArray.address">
  * +-----+------------+--------------+
  * | tag |   index    | block offset |
  * +-----+------------+--------------+
  * </pre>
  */
class ReadableCacheArray (
    parent: Component,
    name: String,
    val tagWidth: Int,
    val dataWidth: Int,
    val flagsWidth: Int,
    val nrSets: Int,
    val nrWays: Int,
    // Interfrace with CPU/Driver
    readData: Wires,
    // Interface with Cache Controller
    index: WiresIn,
    offset: WiresIn,
    // Interface with Memory
    memReadData: WiresIn
)
    extends Module(parent, name)
{
    val sets: Array[CacheSet] = Array.fill(nrSets) {
        new CacheSet(
            tagWidth = tagWidth,
            dataWidth = dataWidth,
            flagsWidth = flagsWidth,
            nrWays = nrWays
        )
    }

    //def getAddrBlockOffset = readAddr.getSignalAsInt(from = 0, to = blockOffsetWidth - 1)
    //def getAddrIndex =
    //def getAddrTag =
}

class CacheArray (
    parent: Component,
    name: String,
    override val tagWidth: Int,
    override val dataWidth: Int,
    override val flagsWidth: Int,
    override val nrSets: Int,
    override val nrWays: Int,
    // Interfrace with CPU/Driver
    readData: Wires,
    writeData: WiresIn,
    // Interface with Cache Controller
    index: WiresIn,
    offset: WiresIn,
    //refill
    //update
    //isCacheHit: Wire
    // Interface with Memory
    memReadData: WiresIn,
    memWriteData: Wires
)
    extends ReadableCacheArray(
        parent = parent,
        name = name,
        tagWidth = tagWidth,
        dataWidth = dataWidth,
        flagsWidth = flagsWidth,
        nrSets = nrSets,
        nrWays = nrWays,
        // Interfrace with CPU/Driver
        readData = readData,
        // Interface with Cache Controller
        index = index,
        offset = offset,
        // Interface with Memory
        memReadData = memReadData
    )
{

}

class CacheController (
    parent: Component,
    name: String
) //? extends ReadableCacheController ???
{


}

class Cache (
    parent: Component,
    name: String
)
    extends Module(parent, name)
{
    //val array = new CacheArray
    //val controller = new CacheController
}

import org.scalatest.FlatSpec
import org.scalatest.OneInstancePerTest

class CacheSpec extends FlatSpec with OneInstancePerTest {

    behavior of "CPU Cache"

    val readAddr = new Wires(null, "readAddr", 16)
    val readData = new Wires(null, "readData", 16)
    val isCacheHit = new Wire(null, "isCacheHit")

    /*val cache = new CacheArray(
        parent = null,
        name = "cache",
        tagWidth = 3,
        dataWidth = 16,
        flagsWidth = 2,
        nrSets = 16,
        nrWays = 4,
        readAddr = readAddr,
        readData = readData,
        isCacheHit = isCacheHit
    )*/
}
