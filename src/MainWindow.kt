import java.awt.*
import java.awt.event.*
import java.io.File
import java.net.URI
import java.util.jar.Manifest
import java.util.prefs.Preferences
import javax.swing.*
import javax.swing.filechooser.FileNameExtensionFilter
import javax.swing.plaf.basic.BasicProgressBarUI
import javax.swing.text.DefaultCaret
import kotlin.system.exitProcess

class MainWindow : JFrame("PicKit2 v$appVersion - $osName") {

    companion object {
        lateinit var container: Container
        var buttonPressed: JButton? = null

        val command = if (osName == "Windows") {
            arrayOf<String>("cmd.exe", "/c", "")
        } else {
            arrayOf<String>("/bin/bash", "-c", "")
        }

        val logArea = JTextArea("Welcome to Pickit2 Programmer!\nPlease wait while checking your connected PIC...\n").apply {
            isEditable = false
        }

        val buttonTest = JButton("Test HEX").apply {
            toolTipText = "Check .hex integrity on PIC"
            isEnabled = false
        }

        val progressBar = JProgressBar().apply {
            isStringPainted = true
            background = Color(230, 230, 230)
            foreground = Color(120, 230, 90)
            border = BorderFactory.createLineBorder(Color.GRAY)
            setUI(object : BasicProgressBarUI() {
                override fun getSelectionBackground(): Color {
                    return Color.DARK_GRAY
                }

                override fun getSelectionForeground(): Color {
                    return Color.DARK_GRAY
                }
            })
        }

        private val appVersion: String?
            get() {
                try {
                    val manifestStream = MainWindow::class.java.getResourceAsStream("/META-INF/MANIFEST.MF")
                    if (manifestStream != null) {
                        val manifest = Manifest(manifestStream)
                        return manifest.mainAttributes.getValue("Implementation-Version")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return null
            }

        private val osName: String
            get() {
                val osName = System.getProperty("os.name").lowercase()
                return when {
                    "win" in osName -> "Windows"
                    "mac" in osName -> "MacOS"
                    "nix" in osName || "nux" in osName || "aix" in osName -> "Linux"
                    else -> "Unknown OS"
                }
            }
    }

    private val pref: Preferences = Preferences.userNodeForPackage(javaClass)
    private var hexLocation: String? = null

    private val hexName = JTextField("Please choose a file...").apply { isEditable = false }
    private val buttonChoose = createButton("Choose .hex", "Choose a .hex to write on your PIC")
    private val buttonCheckPicVersion = createButton("Show PicKit2 firmware", "Show PicKit2 firmware version and .dat file version")
    private val buttonCheckPicConnected = createButton("Show connected PIC", "Attempt automatic device detection")
    private val buttonWrite = createButton("Write HEX", "Flash the .hex on your PIC", false)
    private val buttonRun = createButton("Run HEX", "Run the currently loaded .hex on PIC", false)
    private val buttonStop = createButton("Stop HEX", "Stop the running .hex on PIC", false)
    private val buttonErase = createButton("Erase", "Erase any .hex loaded on PIC")
    private val buttonClearLog = createButton("Clear Log", "Empty the log window")

    private val fileChooser = JFileChooser().apply {
        currentDirectory = File(pref["LastPath", System.getProperty("user.home")])
        fileFilter = FileNameExtensionFilter("HEX file", "hex")
    }

    private val scrollPane = JScrollPane(logArea).apply {
        preferredSize = Dimension(620, 270)
        val caret = logArea.caret as DefaultCaret
        caret.updatePolicy = DefaultCaret.ALWAYS_UPDATE
    }

    private val labelContact = JLabel("<html>&nbsp;&nbsp;Made with <span style='color:red;'>&#10084;</span> by Chayan</html>").apply {
            cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
            toolTipText = "Visit my profile"
            addMouseListener(object : MouseAdapter() {
                override fun mouseReleased(event: MouseEvent) {
                    val desktop = Desktop.getDesktop()
                    try {
                        val uri = URI("https://github.com/chayanforyou")
                        desktop.browse(uri)
                    } catch (e: Exception) {
                        System.err.println(e.message)
                    }
                }
            })
        }

    private val windowListener = object : WindowAdapter() {
        override fun windowClosing(event: WindowEvent) {
            if (!buttonRun.isEnabled) {
                val answer = JOptionPane.showConfirmDialog(
                    null,
                    "PIC operation still running. Are you sure you want to exit?",
                    "Warning", JOptionPane.YES_NO_OPTION
                )
                if (answer == JFileChooser.APPROVE_OPTION) {
                    exitProcess(0)
                }
            } else {
                exitProcess(0)
            }
        }
    }

    init {
        setupUI()
        setupEventHandlers()
        setLocationRelativeTo(parent)
        buttonCheckPicConnected.doClick()
        isVisible = true
        isResizable = false
        defaultCloseOperation = DO_NOTHING_ON_CLOSE
        addWindowListener(windowListener)
    }

    private fun setupUI() {
        container = contentPane
        bounds = Rectangle(50, 50, 700, 480)
        add(buildMainPanel(), BorderLayout.NORTH)
        add(buildLogPanel(), BorderLayout.CENTER)
        add(buildButtonPanel(), BorderLayout.SOUTH)
    }

    private fun buildMainPanel(): JPanel {
        return JPanel().apply {
            layout = FlowLayout()
            border = BorderFactory.createTitledBorder("PICkit 2 Programmer")
            add(hexName)
            add(buttonChoose)
            add(buttonCheckPicVersion)
            add(buttonCheckPicConnected)
        }
    }

    private fun buildLogPanel(): JPanel {
        return JPanel().apply {
            border = BorderFactory.createTitledBorder("Logs")
            add(scrollPane)
            add(progressBar)
            add(labelContact)
        }
    }

    private fun buildButtonPanel(): JPanel {
        return JPanel().apply {
            layout = GridLayout(1, 5)
            border = BorderFactory.createTitledBorder("PIC Operations")
            add(buttonWrite)
            add(buttonRun)
            add(buttonStop)
            add(buttonErase)
            add(buttonTest)
            add(buttonClearLog)
        }
    }

    private fun setupEventHandlers() {
        buttonChoose.addActionListener { handleChooseFile() }
        buttonCheckPicVersion.addActionListener { handleCheckPicVersion() }
        buttonCheckPicConnected.addActionListener { handleCheckPicConnected() }
        buttonWrite.addActionListener { handleWriteHex() }
        buttonRun.addActionListener { handleRunHex() }
        buttonStop.addActionListener { handleStopHex() }
        buttonErase.addActionListener { handleSErase() }
        buttonTest.addActionListener { handleSTestHex() }
        buttonClearLog.addActionListener { handleClearLog() }
    }

    private fun createButton(text: String, tooltip: String, isEnabled: Boolean = true): JButton {
        return JButton(text).apply {
            toolTipText = tooltip
            this.isEnabled = isEnabled
        }
    }

    private fun handleChooseFile() {
        val answer = fileChooser.showOpenDialog(this)
        if (answer == JFileChooser.APPROVE_OPTION) {
            val file = fileChooser.selectedFile
            pref.put("LastPath", file.absolutePath)
            hexLocation = file.path
            hexName.text = hexLocation
            buttonTest.isEnabled = true
            buttonWrite.isEnabled = true
            buttonRun.isEnabled = true
            logArea.append("File ${file.name} successfully loaded\n")
        }
    }

    private fun handleCheckPicVersion() {
        command[2] = "pk2cmd -?v"
        progressBar.value = 0
        logArea.append("Checking PicKit2 Version...\n")
        Operations().execute()
    }

    private fun handleCheckPicConnected() {
        command[2] = "pk2cmd -P -JN"
        progressBar.value = 0
        buttonCheckPicConnected.isEnabled = false
        buttonPressed = buttonCheckPicConnected
        logArea.append("Searching for your PIC on board...\n")
        Operations().execute()
    }

    private fun handleWriteHex() {
        command[2] = "pk2cmd -P -M -JN -F$hexLocation"
        progressBar.value = 0
        buttonWrite.isEnabled = false
        buttonPressed = buttonWrite
        logArea.append("Writing HEX on PIC...\n")
        Operations().execute()
    }

    private fun handleRunHex() {
        command[2] = "pk2cmd -P -JN -R -T"
        progressBar.value = 0
        buttonRun.isEnabled = false
        buttonPressed = buttonStop
        logArea.append("Running HEX...\n")
        Operations().execute()
    }

    private fun handleStopHex() {
        command[2] = "pk2cmd -P -JN -R"
        progressBar.value = 0
        buttonStop.isEnabled = false
        buttonPressed = buttonRun
        Operations().execute()
    }

    private fun handleSErase() {
        command[2] = "pk2cmd -P -JN -E"
        val answer = JOptionPane.showConfirmDialog(
            null,
            """
                Are you sure you want to erase your PIC ?
                Existing .hex on your PIC will be lost !
            """.trimIndent(),
            "Warning",
            JOptionPane.YES_NO_OPTION
        )
        progressBar.value = 0
        if (answer == JFileChooser.APPROVE_OPTION) {
            buttonErase.isEnabled = false
            buttonPressed = buttonErase
            logArea.append("Erasing your PIC... (Don't panic!)\n")
            Operations().execute()
        } else {
            logArea.append("Erasing aborted\n")
        }
    }

    private fun handleSTestHex() {
        command[2] = "pk2cmd -P -Y -JN -F$hexLocation"
        progressBar.value = 0
        buttonTest.isEnabled = false
        buttonPressed = buttonTest
        logArea.append("Testing HEX integrity...\nThis might take a while\n")
        Operations().execute()
    }

    private fun handleClearLog() {
        progressBar.value = 0
        logArea.text = "Logs cleared\n"
    }
}