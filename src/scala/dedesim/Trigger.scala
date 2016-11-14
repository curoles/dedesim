package curoles.dedesim

import curoles.dedesim.De.Action
import curoles.dedesim.Simulator.sim

/** Trigger is an object that triggers actions upon its change. 
 *
 *  The simulation is driven by changes in a system. The changes
 *  happen to ''trigger'' objects. Each ''trigger'' object has a list
 *  of actions associated with it. The actions change state of other
 *  ''trigger'' objects and thus the system constantly evolves in time.
 *
 *  An ''action'' is a function, this function is called by the ''trigger''
 *  object but at this point it does not do all work, instead it
 *  tells global instance of '''`Simulator`''' what to do and __WHEN__. 
 */
abstract class Trigger {

    /** List of all Actions associated with this trigger object */
    protected var actions: List[Action] = List()

    def numActions = actions.length

    /** Call/trigger all Actions*/
    def act() : Unit = {
        actions foreach (_ ())
    }

    /** Adds new Action in to the list of Actions */
    def addAction(a: Action) = {
        //sim.log("add action:" + a.toString)
        actions = a :: actions
        a()
    }
}



import org.scalatest.FlatSpec

//scala -cp "./build/scala/class:./build/scala/extralib/*" org.scalatest.run  FunctionalSpec
//http://doc.scalatest.org/3.0.0/#org.scalatest.FlatSpec
//
class TriggerSpec extends FlatSpec {

    class Test extends Trigger {
    }

    val t = new Test
    var count = 0
    def incr() = count += 1

    it should "have as many actions as we added" in {
        t.addAction(incr)
        assert(count == 1)
        t.addAction(incr)
        assert(count == 2 && t.numActions == 2)
        t.act
        assert(count == 4)
    }

    it should "trigger all actions in the list" in {
        count = 0
        t.act
        assert(count == t.numActions)
    }
}

