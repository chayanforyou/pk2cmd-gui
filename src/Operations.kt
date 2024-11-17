import java.awt.Cursor
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.swing.SwingWorker

class Operations : SwingWorker<Int?, Int?>() {

    companion object {
        private const val VALUE_ERROR = 111
    }

    override fun doInBackground(): Int? {
        MainWindow.progressBar.isIndeterminate = true
        MainWindow.container.cursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)

        val processBuilder = ProcessBuilder(*MainWindow.command)
        processBuilder.redirectErrorStream(true)

        try {
            val process = processBuilder.start()
            val reader = BufferedReader(InputStreamReader(process.inputStream))

            reader.use { br ->
                var line: String?
                while (br.readLine().also { line = it } != null) {
                    println("Debug:: $line")
                    val progressValue = extractProgress(line!!)

                    if (progressValue in 0..100) {
                        MainWindow.progressBar.isIndeterminate = false
                        MainWindow.progressBar.value = progressValue
                    } else if (progressValue != VALUE_ERROR) {
                        appendToLog(line)
                    }

                    if (progressValue == 2) {
                        if (MainWindow.buttonPressed === MainWindow.buttonTest) {
                            MainWindow.progressBar.value = 0
                        } else {
                            resetProgressBar(line)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            handleError(e)
        } finally {
            MainWindow.progressBar.isIndeterminate = false
            MainWindow.container.cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)
            MainWindow.buttonPressed?.isEnabled = true
        }

        return null
    }

    private fun extractProgress(line: String): Int {
        if (line.contains("No PICkit", ignoreCase = true)) {
            resetProgressBar(line)
            return VALUE_ERROR
        }

        val progressString = line.replace("[^0-9]+".toRegex(), "")
        return try {
            progressString.toInt()
        } catch (_: Exception) {
            MainWindow.logArea.append(line + "\n")
            MainWindow.logArea.caretPosition = MainWindow.logArea.document.length
            VALUE_ERROR
        }
    }

    private fun appendToLog(line: String) {
        MainWindow.logArea.append(line + "\n")
        MainWindow.logArea.caretPosition = MainWindow.logArea.document.length
    }

    private fun resetProgressBar(line: String) {
        MainWindow.progressBar.value = 0
        MainWindow.logArea.append(line + "\n")
        MainWindow.logArea.caretPosition = MainWindow.logArea.document.length
    }

    private fun handleError(e: Exception) {
        MainWindow.logArea.append("Error: ${e.message}\n")
        MainWindow.logArea.caretPosition = MainWindow.logArea.document.length
    }
}
