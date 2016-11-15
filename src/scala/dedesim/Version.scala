package curoles.dedesim

final class Version {
    val Major = 0
    val Minor = 1

    override def toString: String = {
        Major.toString + "." + Minor.toString + "." + releaseStr
    }

    def releaseStr: String = {
        //ClassLoader cl = this.getClass().getClassLoader();
        //java.io.InputStream in = cl.getResourceAsStream("curoles/dedesim/release.txt");
        "TODO"
    }
}
