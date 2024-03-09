import javax.swing.*;
import java.io.File;

public class Main {
    public static String version = "2.0";

    public static void main(String[] args) {
        System.out.println("Launching PicKit2 Programmer");
        System.out.println("Checking required files... ");

        final String pk2cmdPath = "/usr/local/bin";
        File filePK2CMD = new File(pk2cmdPath + "/pk2cmd");
        File filePK2DeviceFile = new File(pk2cmdPath + "/PK2DeviceFile.dat");

        if (filePK2CMD.exists() && filePK2DeviceFile.exists()) {
            System.out.println("Files seem to be on their place! The installation was correct");
            new MainWindow();
        } else {
            System.err.println("Error: file(s) missing");
            JOptionPane.showMessageDialog(null, """
                            No PK2CMD command was found or correctly installed!
                            Please, copy the 'pk2cmd' and 'PK2DeviceFile.dat' files
                            by running this command ./copy_pk2cmd.sh in terminal.

                            More information can be found in the README file
                            This program will now close""",
                    "Error: file(s) missing", JOptionPane.ERROR_MESSAGE
            );
            System.exit(0);
        }
    }
}
