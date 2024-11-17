import java.io.File
import javax.swing.JOptionPane
import kotlin.system.exitProcess
import java.util.logging.Logger

object Main {

    private val logger = Logger.getLogger(Main::class.java.name)

    @JvmStatic
    fun main(args: Array<String>) {
        println("Launching PicKit2 Programmer")
        println("Checking required files... ")

        val pk2cmdPath = System.getenv("PK2CMD_PATH") ?: "/usr/local/bin"

        if (checkRequiredFiles(pk2cmdPath)) {
            println("Files seem to be in place! The installation was correct.")
            MainWindow()
        } else {
            logger.severe("Error: file(s) missing")
            showErrorDialog()
            exitProcess(1)
        }
    }

    private fun checkRequiredFiles(path: String): Boolean {
        val filePK2CMD = File("$path/pk2cmd")
        val filePK2DeviceFile = File("$path/PK2DeviceFile.dat")
        return filePK2CMD.exists() && filePK2DeviceFile.exists()
    }

    private fun showErrorDialog() {
        JOptionPane.showMessageDialog(
            null,
            """
                No PK2CMD command was found or correctly installed!
                Please, copy the 'pk2cmd' and 'PK2DeviceFile.dat' files
                by running this command ./copy_pk2cmd.sh in terminal.

                More information can be found in the README file.
                This program will now close.
            """.trimIndent(),
            "Error: file(s) missing", JOptionPane.ERROR_MESSAGE
        )
    }
}
