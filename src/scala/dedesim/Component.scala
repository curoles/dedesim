/* Copyright (c) Igor Lesik 2016
 *
 */

package curoles.dedesim

import scala.collection._

/** Component is any physical or logical element of circuit/design.
 *
 *  @author Igor Lesik
 */
abstract class Component(val parent: Component, val name: String, val cType: Symbol) {

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

    def foreach[U](level: Int, f: (Int,String,Component) => U):Unit = {
        if (!isEmpty) for ((componentName,component) <- subComponents.get) {
            f(level, componentName, component)
            component.foreach(level + 1, f)
        }

    }

    val id: String = if (parent eq null) name else parent.id + "." + name

    def hierarchyString(level: Int = 0): String = {
        var s = (" " * level) + name
        if (!isEmpty) for ((componentName,component) <- subComponents.get) {
            s = s + "\n" + component.hierarchyString(level + 1)
        }
        s
    }
}
