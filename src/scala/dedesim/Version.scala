package curoles.dedesim

final class Version {
    val Major = 0
    val Minor = 1

    override def toString: String = {
        Major.toString + "." + Minor.toString + ".r" + releaseStr
    }

    def releaseStr: String = {
        val cl = getClass.getClassLoader
        val path = "curoles/dedesim/release.txt"
        val in: java.io.InputStream = cl.getResourceAsStream(path)
        val release: String =
            new java.io.BufferedReader(new java.io.InputStreamReader(in)
            ).lines().collect(java.util.stream.Collectors.joining("\n"))
        release
    }
}
