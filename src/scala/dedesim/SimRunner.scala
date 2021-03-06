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
        println("DeDeSim " + Simulator.version + " by Igor Lesik 2016")

        val exitCode = if (args.isEmpty) showHelp()
                       else              run(args)

        sys.exit(exitCode)
    }

    def showHelp(): Int = {
        val helpStr = """
            |DeDeSim does not have any command line options.
            |Instead any argument is treated as a string of Scala script,
            |or name of a file with Scala script.
            |
            |Option-like Scala commands:
            |  Print version: dedesim "println(simulator.version)"
            |
            |Example of how to run circuit simulation:
            |dedesim "val circuit = new curoles.dedesim.test.circuit.clk.TB; sim.run(10)"
            |""".stripMargin
        println(helpStr)
        SysOK
    }

    def run(args: Array[String]): Int = {
        //println("Arguments: " + args.mkString(" -|- "))
        runJobs(args)

        SysOK
    }

    def runJobs(jobs: Array[String]): Unit = {
        for (job <- jobs) runJob(job)
    }

    def runJob(job: String): Unit = {
        sim.log("Job: " + job)
        val jobScript = getJobScript(job)
        runScalaScript(jobScript)
    }

    def runScalaScript(code: String): Unit = {
        import scala.reflect.runtime._
        import scala.tools.reflect.ToolBox

        val cm = universe.runtimeMirror(getClass.getClassLoader)
        val tb = cm.mkToolBox()
 
        tb.eval(tb.parse(Preamble + code))
    }

    /** Job is either file with script or script */
    def getJobScript(job: String): String = {
        import java.nio.file.{Paths, Files}

        if (Files.exists(Paths.get(job)))
            io.Source.fromFile(job).mkString
        else
            job
    }

    val Preamble = """
      |import scala.reflect.runtime.{universe => ru}
      |val m = ru.runtimeMirror(getClass.getClassLoader)
      |val simSymbol = m.staticModule("curoles.dedesim.Simulator")
      |val simMirror = m.reflectModule(simSymbol)
      |val simulatorInstance = simMirror.instance.asInstanceOf[curoles.dedesim.Simulator.type]
      |val simulator = simulatorInstance
      |val sim = simulator.sim
      |class VCD(f:String) extends curoles.dedesim.VCDumper(f,simulator.msg,simulator.root)
      |//sim.log("preamble parsed and evaluated")
      |""".stripMargin
}
