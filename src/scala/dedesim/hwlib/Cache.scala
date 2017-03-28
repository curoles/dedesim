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
  *
  * <hr>
  * <pre class="textdiagram" id="hwlib.Cache.entry">
  * +-----+------------+-----------+
  * | tag | data block | flag bits |
  * +-----+------------+-----------+
  * </pre>
  *
  * <hr>
  * <pre class="textdiagram" id="hwlib.Cache.address">
  * +-----+------------+--------------+
  * | tag |   index    | block offset |
  * +-----+------------+--------------+
  * </pre>
  */
class Cache (
    parent: Component,
    name: String,
    val tagWidth: Int,
    val dataWidth: Int,
    val flagsWidth: Int,
    val nrSets: Int,
    val nrWays: Int,
    readAddr: WiresIn,
    readData: Wires,
    isCacheHit: Wire
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

import org.scalatest.FlatSpec
import org.scalatest.OneInstancePerTest

class CacheSpec extends FlatSpec with OneInstancePerTest {

    behavior of "CPU Cache"

    val readAddr = new Wires(null, "readAddr", 16)
    val readData = new Wires(null, "readData", 16)
    val isCacheHit = new Wire(null, "isCacheHit")

    val cache = new Cache(
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
    )
}
