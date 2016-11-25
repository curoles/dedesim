/* Copyrigth (c) Igor Lesik 2016
 *
 */

package curoles.dedesim

import scala.collection._

class Messenger {

    val subscribers = mutable.ListBuffer[Messenger]()

    def subscribe(other: Messenger) = subscribers += other

    def wireEvent(time: Long, id: String, sigVal: Boolean) {
        //println(s"@$time signal $id changed to $sigVal")
        subscribers foreach (x => x.wireEvent(time, id, sigVal))
    }

    def wireEvent(time: Long, id: String, sigVal: Long, width: Int) {
        //println(s"@$time signal $id changed to $sigVal")
        subscribers foreach (x => x.wireEvent(time, id, sigVal, width))
    }

}

