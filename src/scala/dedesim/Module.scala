/* Copyright (c) Igor Lesik 2016
 *
 */

package curoles.dedesim

//import scala.collection._

/** Module is Component that contains other sub-components.
 *
 *  @author Igor Lesik
 *
 *  Component may or may not have subcomponent.
 *  Module normally has sub-components. Module adds helpers to class Component
 *  that allow for syntactic sugar.
 */
class Module(parent: Component, name: String)
    extends Component(parent, name, 'module) {

    /** Gets wire with the name or creates it.
     * 
     *  Note that new Wire(this,...) adds itself to this.subComponents
     */
    def wire(name: String, initVal: Int = 0): Wire = {
        if (!subComponents.contains(name)) new Wire(this, name, initVal)
        else subComponents.get.apply(name).asInstanceOf[Wire]
    }

    def wires(name: String, width: Int, initVal: Int = 0): Wires = {
        if (!subComponents.contains(name)) new Wires(this, name, width, initVal)
        else subComponents.get.apply(name).asInstanceOf[Wires]
    }


}
