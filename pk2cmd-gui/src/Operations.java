import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Operations extends SwingWorker<Integer, Integer> {

    private static final int VALUE_ERROR = 111;

    public Integer doInBackground() throws Exception {
        MainWindow.progressBar.setIndeterminate(true);
        MainWindow.container.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        ProcessBuilder pb = new ProcessBuilder(MainWindow.command);
        pb.redirectErrorStream(true);
        Process p = pb.start();
        InputStreamReader isr = new InputStreamReader(p.getInputStream());
        BufferedReader br = new BufferedReader(isr);

        String lineRead;
        while ((lineRead = br.readLine()) != null) {
            System.out.println("Debug: " + lineRead);
            String progressString = lineRead.replaceAll("[^0-9]+", "");

            int progressValue;
            try {
                progressValue = Integer.parseInt(progressString);
            } catch (Exception e) {
                progressValue = VALUE_ERROR;
                MainWindow.logArea.append(lineRead + "\n");
                MainWindow.logArea.setCaretPosition(MainWindow.logArea.getDocument().getLength());
            }

            if (progressValue >= 0 && progressValue <= 100) {
                MainWindow.progressBar.setIndeterminate(false);
                MainWindow.progressBar.setValue(progressValue);
            } else if (progressValue != VALUE_ERROR) {
                MainWindow.logArea.append(lineRead + "\n");
                MainWindow.logArea.setCaretPosition(MainWindow.logArea.getDocument().getLength());
            }

            if (progressValue == 2) {
                if (MainWindow.buttonPressed == MainWindow.buttonTest) {
                    MainWindow.progressBar.setValue(0);
                } else {
                    MainWindow.progressBar.setValue(0);
                    MainWindow.logArea.append(lineRead + "\n");
                    MainWindow.logArea.setCaretPosition(MainWindow.logArea.getDocument().getLength());
                }
            }
        }

        MainWindow.progressBar.setIndeterminate(false);
        MainWindow.container.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        MainWindow.buttonPressed.setEnabled(true);
        return null;
    }
}
