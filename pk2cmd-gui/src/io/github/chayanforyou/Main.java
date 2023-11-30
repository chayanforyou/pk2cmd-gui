package io.github.chayanforyou;

import javax.swing.*;
import java.io.File;

public class Main {
    public static String version = "1.0";

    public static void main(String[] args) {
        System.out.println("Launching PicKit2 Programmer");
        System.out.println("Checking required files... ");

        File filePK2CMD = new File("/bin/pk2cmd");
        File filePK2DeviceFile = new File("/bin/PK2DeviceFile.dat");

        if (filePK2CMD.exists() && filePK2DeviceFile.exists()) {
            System.out.println("Files seem to be on their place! The installation was correct");
            new MainWindow();
        } else {
            System.err.println("Error: file(s) missing");
            performOperation();
        }
    }

    private static void performOperation() {
        try {
            final String dir = System.getProperty("user.dir");
            String command = "pkexec cp " + dir + "/{pk2cmd,PK2DeviceFile.dat} /bin; pkexec chmod u+s /bin/pk2cmd";

            ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", command);
            processBuilder.environment().put("DISPLAY", System.getenv("DISPLAY"));
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("All operation successful. Now start the program");
                new MainWindow();
            } else {
                System.err.println("Error: copying file");
                JOptionPane.showMessageDialog(null, """
                                No PK2CMD command was found or correctly installed!
                                PLEASE, copy the 'pk2cmd' and 'PK2DeviceFile.dat' files in the
                                /bin folder

                                More information can be found in the README file
                                This program will now close""",
                        "Error: file(s) missing", JOptionPane.ERROR_MESSAGE
                );
                System.exit(0);
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
