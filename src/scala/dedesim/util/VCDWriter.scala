/* Copyright (c) Igor Lesik 2016
 *
 *
 */

package curoles.dedesim.util

import java.io._

/** Value Change Dump (VCD) writer.
 *
 *
 */
class VCDWriter(
    val fileName: String = "wave.vcd",
    version: String = "",
    timescale: String = "1ps",
    comment: String = ""
)
{
    val file = new File(fileName)
    val writer = new BufferedWriter(new FileWriter(file))
    writer.write(header(version, timescale, comment))

    /** Close the file */
    def close() = writer.close()

    def header(version: String, timescale: String, comment: String): String = {
        s"""
        |$$date
        |   Date text. For example: November 11, 2009.
        |$$end
        |$$version DeDeSim $version $$end
        |$$comment $comment $$end
        |$$timescale $timescale $$end
        |$$scope module top $$end
        |""".stripMargin
    }

    def endDefinitions(): Unit = {
        writer.write("$enddefinitions $end\n")
    }

    def defineWire(id: String, size: Int): Unit = {
writer.write("$scope module m1 $end\n")
        val s = s"$$var wire $size $id $id $$end\n"
        writer.write(s)
    }

    def setTimestamp(timestamp: Long): Unit = {
        writer.write(s"#$timestamp\n")
    }

    def change(id: String, value: Boolean): Unit = {
        val vstr = "b" + (if (value) 1 else 0).toString
        writer.write(vstr + " " + id + "\n")
    }

    def change(timestamp: Long, id: String, value: Boolean): Unit = {
        setTimestamp(timestamp)
        change(id, value)
    }
}
