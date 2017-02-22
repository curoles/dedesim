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

    val bits = scala.collection.mutable.BitSet(size)

    def setBit(pos: Int) = bits += pos

    def clearBit(pos: Int) = bits -= pos

    def setBit(pos: Int, value: Boolean): Unit = if (value) setBit(pos) else clearBit(pos)

    def getBit(pos: Int) = bits(pos)

    def isZero = bits.isEmpty

    def toBitMask: Array[Long] = bits.toBitMask

    def toInteger: Long = bits.toBitMask(0)

    def fromInteger(bitMask: Long, offset: Int = 0): Unit = {
        for (bitPos <- 0 to 63) {
            setBit(bitPos + offset, (bitMask & (1 << bitPos)) != 0)
        }
    }
}
