package curoles.dedesim

import curoles.dedesim.De.Action

class Simulation {

    private var curtime = 0
    def currentTime: Int = curtime

    private val printStream: java.io.PrintStream = Console.out

    def log(msg: String): Unit = {
        printStream.println(f"${currentTime}%4d | " + msg)
    }

    case class WorkItem(time: Int, action: Action)

    type Agenda = List[WorkItem]

    private var agenda: Agenda = List()

    private def insert(ag: Agenda, item: WorkItem): Agenda = {
        if (ag.isEmpty || item.time < ag.head.time) item :: ag
        else ag.head :: insert(ag.tail, item)
    }

    def afterDelay(delay: Int)(block: => Unit) = {
        val item = WorkItem(currentTime + delay, () => block)
        agenda = insert(agenda, item)
    }


    private def next() = {
        (agenda: @unchecked) match {
        case item :: rest =>
            agenda = rest
            curtime = item.time
            item.action()
        }
    }

    def run() = {
        afterDelay(0) { log("simulation started") }

        while (!agenda.isEmpty && currentTime < 10) next()
    }
}


