package curoles.dedesim

import curoles.dedesim.De.Action

class Simulation {

    private var curtime: Long = 0
    def currentTime: Long = curtime

    private val printStream: java.io.PrintStream = Console.out

    def log(msg: String): Unit = {
        printStream.println(f"${currentTime}%4d | " + msg)
    }

    case class WorkItem(time: Long, action: Action)

    type Agenda = List[WorkItem]

    /** List of items to do */
    private var agenda: Agenda = List()

    private def insert(ag: Agenda, item: WorkItem): Agenda = {
        if (ag.isEmpty || item.time < ag.head.time) item :: ag
        else ag.head :: insert(ag.tail, item)
    }

    def afterDelay(delay: Long)(block: => Unit) = {
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

    var finished = false

    def finish() = this.finished = true

    def run(period: Long = 10) = {
        val maxTime = currentTime + period

        log("Simulation started!")
        if (agenda.isEmpty )log("WARNING: Agenda is empty, nothing to simulate!")

        while (!finished && !agenda.isEmpty && currentTime < maxTime) next()
    }


}


