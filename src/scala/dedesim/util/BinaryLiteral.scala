/* Copyright (c) Igor Lesik 2016
 *
 *
 */

package curoles.dedesim.util

object StringUtils {

implicit class BinaryLiteral(val sc: StringContext) extends AnyVal
{
    def b(args: Any*): Int = {
        val strings = sc.parts.iterator
        val expressions = args.iterator
        var buf = new StringBuffer(strings.next)
        while(strings.hasNext) {
           buf append expressions.next
           buf append strings.next
        }
        //parseBinary(buf.toString).getOrElse(0xdeadbeef)
        Integer.parseInt(buf.toString, 2)
    }


    /*def parseBinary(s: String): Option[Int] = {
        var i = s.length - 1
        var sum = 0
        var mult = 1
        while (i >= 0) {
          s.charAt(i) match {
            case '1' => sum += mult
            case '0' =>
            case x => return None
          }
          mult *= 2
          i -= 1
        }
        Some(sum)
    }*/

}

}
