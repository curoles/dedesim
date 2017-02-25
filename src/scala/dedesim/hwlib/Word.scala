/* Copyright (c) Igor Lesik 2017
 *
 *
 */

package curoles.dedesim.hwlib


/** Abstraction for word of data.
 *
 */
class Word(
    size: Int
)
{
    require(size > 0)

    val bits = scala.collection.mutable.BitSet()

    def setBit(pos: Int) = {
        require(pos < size && pos >= 0)
        bits += pos
    }

    def clearBit(pos: Int) = bits -= pos

    def setBit(pos: Int, value: Boolean): Unit = if (value) setBit(pos) else clearBit(pos)

    def getBit(pos: Int) = bits(pos)

    def isZero = bits.isEmpty

    def toBitMask: Array[Long] = bits.toBitMask

    def toInteger: Long = bits.toBitMask(0)

    def fromInteger(bitMask: Long, offset: Int = 0): Unit = {
        for (bitPos <- 0 until Math.min(size, 64)) {
            setBit(bitPos + offset, (bitMask & (1 << bitPos)) != 0)
        }
    }
}

import org.scalatest.FlatSpec

class WordSpec extends FlatSpec {

    it should "be zero after creation" in {
        val word = new Word(32)
        assert(word.toInteger == 0x0)
    }

    it should "read what was assigned" in {
        val word = new Word(32)
        word.fromInteger(0x5)
        //println("word is "+word.bits.mkString+","+word.bits.toString)
        assert(word.toInteger == 0x5)
        word.fromInteger(0xdeadbeef)
        //println("word is "+word.bits.mkString+","+word.bits.toString)
        assert(word.toInteger == 0xdeadbeefL)
    }
}
