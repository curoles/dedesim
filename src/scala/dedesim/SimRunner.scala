package curoles.dedesim

//scala -cp "./build/scala/class:./build/scala/extralib/*" curoles.dedesim.SimRunner

import curoles.dedesim.Simulator.sim

/** Launches simulation.
 *
 *  @author Igor Lesik 2016
 */
object SimRunner {

    val SysOK = 0;

    /** Program entry point
     *
     */
    def main(args: Array[String]): Unit = {
        val exitCode = if (args.isEmpty) showHelp()
                       else              run(args)
        sys.exit(exitCode)
    }

    def showHelp(): Int = {
        val helpStr = """
            |DeDeSim does not have any command line options.
            |Instead any argument is treated as string of Scala script to execute,
            |or name of a file with Scala script.
            |""".stripMargin
        println(helpStr)
        SysOK
    }

    def run(args: Array[String]): Int = {
        println("Arguments: " + args.mkString(" "))
        println("Version: " + Simulator.version)
        runJobs(args)
        val circuit = new Circuit1
        sim.run()

        SysOK
    }

    def runJobs(jobs: Array[String]): Unit = {
        for (job <- jobs) runJob(job)
    }

    def runJob(job: String): Unit = {
        sim.log("Job: " + job)
        runScalaScript("""println("Hi")""")
    }

    def runScalaScript(code: String): Unit = {
        import scala.reflect.runtime._
        import scala.tools.reflect.ToolBox

        val cm = universe.runtimeMirror(getClass.getClassLoader)
        val tb = cm.mkToolBox()
 
        tb.eval(tb.parse(code)) 
    }
}
