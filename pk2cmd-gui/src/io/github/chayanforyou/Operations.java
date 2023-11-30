package io.github.chayanforyou;

import java.awt.Cursor;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.swing.SwingWorker;

import static io.github.chayanforyou.MainWindow.*;

public class Operations extends SwingWorker<Integer, Integer> {

    private static final int VALUE_ERROR = 111;

    public Integer doInBackground() throws Exception {
        progressBar.setIndeterminate(true);
        container.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
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
                logArea.append(lineRead + "\n");
                logArea.setCaretPosition(logArea.getDocument().getLength());
            }

            if (progressValue >= 0 && progressValue <= 100) {
                progressBar.setIndeterminate(false);
                progressBar.setValue(progressValue);
            } else if (progressValue != VALUE_ERROR) {
                logArea.append(lineRead + "\n");
                logArea.setCaretPosition(logArea.getDocument().getLength());
            }

            if (progressValue == 2) {
                if (MainWindow.buttonPressed == MainWindow.buttonTest) {
                    progressBar.setValue(0);
                } else {
                    progressBar.setValue(0);
                    logArea.append(lineRead + "\n");
                    logArea.setCaretPosition(logArea.getDocument().getLength());
                }
            }
        }

        progressBar.setIndeterminate(false);
        container.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        buttonPressed.setEnabled(true);
        return null;
    }
}
