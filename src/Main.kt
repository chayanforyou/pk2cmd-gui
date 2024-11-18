import java.awt.Color
import java.awt.Dimension
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.io.File
import javax.swing.JEditorPane
import javax.swing.JOptionPane
import javax.swing.JScrollPane
import javax.swing.UIManager
import javax.swing.event.HyperlinkEvent
import kotlin.system.exitProcess

object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        println("Launching PicKit2 Programmer")
        println("Checking required files... ")

        val isWindowsOs = System.getProperty("os.name").contains("win", ignoreCase = true)
        if (checkRequiredFiles(isWindowsOs)) {
            println("Files seem to be in place! The installation was correct.")
            MainWindow()
        } else {
            println("Error: file(s) missing")
            showErrorDialog(isWindowsOs)
            exitProcess(1)
        }
    }

    private fun checkRequiredFiles(isWindows: Boolean): Boolean {
        val pk2cmdPath = if (isWindows) "C:\\PICkit_2" else "/usr/local/bin"
        val pk2cmdFileName = if (isWindows) "pk2cmd.exe" else "pk2cmd"

        val filePK2CMD = File("$pk2cmdPath/$pk2cmdFileName")
        val filePK2DeviceFile = File("$pk2cmdPath/PK2DeviceFile.dat")
        return filePK2CMD.exists() && filePK2DeviceFile.exists()
    }

    private fun showErrorDialog(isWindows: Boolean) {
        val panelBG = UIManager.get("Panel.background") as Color

        val textWinOs = """
            <html>
            <body style='font-family:sans-serif;'>
                <p>No pk2cmd command was found or correctly installed!</p>
                <p>Please, copy the <code>pk2cmd.exe</code> and <code>PK2DeviceFile.dat</code> files
                to <code>C:\PICkit_2 manually or by executing the script.</p>
                
                <p>Double click to run the script:<br>
                <span style='color:blue;'>setup_pk2cmd.bat</span></p>
                
                <p>More information can be found in the README file.<br>
                This program will now close.</p>
            </body>
            </html>
        """.trimIndent()

        val textUnix = """
            <html>
            <body style='font-family:sans-serif;'>
                <p>No pk2cmd command was found or correctly installed!</p>
                <p>Please, copy the <code>pk2cmd</code> and <code>PK2DeviceFile.dat</code> files
                to <code>/usr/local/bin</code> manually or by executing the script.</p>
                
                <p>To make it executable, use:<br>
                <a href='copy:chmod +x setup_pk2cmd.sh' style='color:blue; text-decoration:none;'>chmod +x setup_pk2cmd.sh</a></p>
                
                <p>Then you can run it using:<br>
                <a href='copy:./setup_pk2cmd.sh' style='color:blue; text-decoration:none;'>./setup_pk2cmd.sh</a></p>
                
                <p>More information can be found in the README file.<br>
                This program will now close.</p>
            </body>
            </html>
        """.trimIndent()

        val editorPane = JEditorPane().apply {
            contentType = "text/html"
            isEditable = false
            highlighter = null
            background = panelBG
            text = if (isWindows) textWinOs else textUnix

            addHyperlinkListener { event ->
                if (event.eventType == HyperlinkEvent.EventType.ACTIVATED) {
                    if (event.description.startsWith("copy:")) {
                        val command = event.description.removePrefix("copy:")
                        copyToClipboard(command)
                        JOptionPane.showMessageDialog(
                            null,
                            "Command copied to clipboard:\n$command",
                            "Copied",
                            JOptionPane.INFORMATION_MESSAGE
                        )
                    }
                }
            }
        }

        val scrollPane = JScrollPane(editorPane).apply {
            border = null
            preferredSize = Dimension(400, 240)
        }

        JOptionPane.showMessageDialog(
            null,
            scrollPane,
            "Error: File(s) Missing",
            JOptionPane.ERROR_MESSAGE
        )
    }

    private fun copyToClipboard(text: String) {
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        val selection = StringSelection(text)
        clipboard.setContents(selection, selection)
    }
}
