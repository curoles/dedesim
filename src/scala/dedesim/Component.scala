/* Copyright (c) Igor Lesik 2016
 *
 */

package curoles.dedesim

import scala.collection._

/** Component is any physical or logical element of circuit/design.
 *
 *  @author Igor Lesik
 */
abstract class Component(val parent: Component, val name: String) {

    registerWithParent()


    def registerWithParent(): Unit = {
        if (parent ne null) {
            parent.registerComponent(this, name)
        }
    }

    type SubComponentsMap = immutable.Map[String, Component]

    var subComponents = None: Option[SubComponentsMap]

    def registerComponent(component: Component, name: String): Unit = {
        subComponents = Some( 
          if (subComponents == None) immutable.Map[String, Component](name -> component)
          else subComponents.get + (name -> component)
        )
    }

    def isEmpty = subComponents.isEmpty

    def hierarchyString(level: Int = 0): String = {
        var s = (" " * level) + name
        if (!isEmpty) for ((componentName,component) <- subComponents.get) {
            s = s + "\n" + component.hierarchyString(level + 1)
        }
        s
    }
}
