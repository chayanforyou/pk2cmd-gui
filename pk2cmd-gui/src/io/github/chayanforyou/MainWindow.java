package io.github.chayanforyou;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicProgressBarUI;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.URI;
import java.util.prefs.Preferences;

public class MainWindow extends JFrame {
    private final Preferences prefs = Preferences.userNodeForPackage(getClass());
    protected static Container container;
    private final JButton buttonWrite;
    private final JButton buttonErase;
    private final JButton buttonRun;
    private final JButton buttonStop;
    private final JButton buttonChoose;
    private final JButton buttonCheckPicVersion;
    private final JButton buttonCheckPicInstalled;
    private final JButton buttonClearLog;
    protected static JButton buttonTest;
    protected static JButton buttonPressed;
    private final JTextField hexName;
    protected static JProgressBar progressBar;
    protected static JTextArea logArea;
    private final JFileChooser fileChooser;
    protected static String hexLocation;
    protected static String[] command;

    public MainWindow() {
        super("PicKit2 for Linux v" + Main.version + " - By Chayan Mistry");

        String lastPath = prefs.get("LastPath", System.getProperty("user.home"));
        container = this.getContentPane();
        setBounds(50, 50, 700, 480);

        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new FlowLayout());
        panelPrincipal.setBorder(BorderFactory.createTitledBorder("PICkit 2 Programmer"));

        JPanel panelButtons = new JPanel();
        panelButtons.setLayout(new GridLayout(1, 5));
        panelButtons.setBorder(BorderFactory.createTitledBorder("PIC Operations"));

        JPanel panelLogs = new JPanel();
        panelLogs.setBorder(BorderFactory.createTitledBorder("Logs"));

        hexName = new JTextField("Please choose a file");
        hexName.setEditable(false);

        logArea = new JTextArea("Welcome to Pickit2 Programmer!\nPlease wait while checking your connected PIC...\n");
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setPreferredSize(new Dimension(600, 270));

        DefaultCaret caret = (DefaultCaret) logArea.getCaret();
        caret.setUpdatePolicy(2);

        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        progressBar.setBackground(new Color(230, 230, 230));
        progressBar.setForeground(new Color(120, 230, 90));
        progressBar.setUI(new BasicProgressBarUI() {
            @Override
            protected Color getSelectionBackground() {
                return Color.DARK_GRAY;
            }

            @Override
            protected Color getSelectionForeground() {
                return Color.DARK_GRAY;
            }
        });

        JLabel labelDonate = new JLabel("Reach me out :)");
        labelDonate.setForeground(Color.BLUE);
        labelDonate.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        labelDonate.setToolTipText("Click there reach me :)");
        labelDonate.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent event) {
                Desktop desktop = Desktop.getDesktop();
                try {
                    URI uri = new URI("https://github.com/chayanforyou");
                    desktop.browse(uri);
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        });

        command = new String[3];
        FileNameExtensionFilter filter = new FileNameExtensionFilter("HEX file", "hex", "HEX");
        fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(lastPath));
        fileChooser.addChoosableFileFilter(filter);
        fileChooser.setFileFilter(filter);

        buttonChoose = new JButton("Choose .hex");
        buttonChoose.setToolTipText("Choose a .hex to write on your PIC");

        buttonCheckPicVersion = new JButton("Show PicKit2 firmware");
        buttonCheckPicVersion.setToolTipText("Show the firmware of PicKit 2, PK2CMD version and .dat file version");

        buttonCheckPicInstalled = new JButton("Show connected PIC");
        buttonCheckPicInstalled.setToolTipText("Attempt an automatic device detection to display PIC information");

        buttonWrite = new JButton("Write HEX");
        buttonWrite.setToolTipText("Flash the .hex on your PIC");
        buttonWrite.setEnabled(false);

        buttonRun = new JButton("Run HEX");
        buttonRun.setToolTipText("Run the currently loaded .hex on PIC");

        buttonTest = new JButton("Test HEX");
        buttonTest.setToolTipText("Check .hex integrity on PIC");
        buttonTest.setEnabled(false);

        buttonErase = new JButton("Erase");
        buttonErase.setToolTipText("Erase any .hex loaded on PIC");

        buttonStop = new JButton("Stop HEX");
        buttonStop.setToolTipText("Stop the running .hex on PIC");
        buttonStop.setEnabled(false);

        buttonClearLog = new JButton("Clear Log");
        buttonClearLog.setToolTipText("Empty the log window");

        GestationButtons gb = new GestationButtons();
        buttonChoose.addActionListener(gb);
        buttonCheckPicVersion.addActionListener(gb);
        buttonWrite.addActionListener(gb);
        buttonRun.addActionListener(gb);
        buttonTest.addActionListener(gb);
        buttonErase.addActionListener(gb);
        buttonStop.addActionListener(gb);
        buttonCheckPicInstalled.addActionListener(gb);
        buttonClearLog.addActionListener(gb);

        panelPrincipal.add(hexName);
        panelPrincipal.add(buttonChoose);
        panelPrincipal.add(buttonCheckPicVersion);
        panelPrincipal.add(buttonCheckPicInstalled);
        panelLogs.add(scrollPane);
        panelLogs.add(progressBar);
        panelLogs.add(labelDonate);
        panelButtons.add(buttonWrite);
        panelButtons.add(buttonRun);
        panelButtons.add(buttonStop);
        panelButtons.add(buttonErase);
        panelButtons.add(buttonTest);
        panelButtons.add(buttonClearLog);
        container.add(panelPrincipal, "North");
        container.add(panelLogs, "Center");
        container.add(panelButtons, "South");
        buttonCheckPicInstalled.doClick();

        setLocationRelativeTo(getParent());
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                if (!buttonRun.isEnabled()) {
                    int answer = JOptionPane.showConfirmDialog(null,
                            "PIC operation still running. Are you sure you want to exit?",
                            "Warning", JOptionPane.YES_NO_OPTION);
                    if (answer == 0) {
                        System.exit(0);
                    }
                } else {
                    System.exit(0);
                }
            }
        });
        setResizable(false);
    }

    public class GestationButtons implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            int answer;
            if (event.getSource() == buttonChoose) {
                answer = fileChooser.showOpenDialog(MainWindow.this);
                if (answer == 0) {
                    File file = fileChooser.getSelectedFile();
                    prefs.put("LastPath", file.getAbsolutePath());
                    System.out.println("Opening file : " + file.getName() + ".");
                    hexLocation = file.getPath();
                    System.out.println("File complete location : " + hexLocation);
                    hexName.setText(hexLocation);
                    buttonTest.setEnabled(true);
                    buttonWrite.setEnabled(true);
                    buttonRun.setEnabled(true);
                    logArea.append("File " + file.getName() + " successfully loaded\n");
                } else {
                    System.err.println("Open command cancelled by user.");
                }
            }

            if (event.getSource() == buttonCheckPicVersion) {
                progressBar.setValue(0);
                logArea.append("Checking PicKit2 Version...\n");
                command[0] = "/bin/bash";
                command[1] = "-c";
                command[2] = "pk2cmd -?v";
                new Operations().execute();
            }

            if (event.getSource() == buttonCheckPicInstalled) {
                buttonCheckPicInstalled.setEnabled(false);
                progressBar.setValue(0);
                command[0] = "/bin/bash";
                command[1] = "-c";
                command[2] = "pk2cmd -P -JN";
                progressBar.setValue(0);
                logArea.append("Searching for your PIC on board...\n");
                buttonPressed = buttonCheckPicInstalled;
                new Operations().execute();
            }

            if (event.getSource() == buttonWrite) {
                buttonWrite.setEnabled(false);
                command[0] = "/bin/bash";
                command[1] = "-c";
                command[2] = "pk2cmd -P -M -JN -F" + hexLocation;
                progressBar.setValue(0);
                buttonPressed = buttonWrite;
                buttonWrite.setEnabled(false);
                logArea.append("Writing HEX on PIC...\n");
                new Operations().execute();
            }

            if (event.getSource() == buttonTest) {
                buttonTest.setEnabled(false);
                command[0] = "/bin/bash";
                command[1] = "-c";
                command[2] = "pk2cmd -P -Y -JN -F" + hexLocation;
                progressBar.setValue(0);
                buttonPressed = buttonTest;
                logArea.append("Testing HEX integrity...\nThis might take a while\n");
                new Operations().execute();
            }

            if (event.getSource() == buttonErase) {
                command[0] = "/bin/bash";
                command[1] = "-c";
                command[2] = "pk2cmd -P -JN -E";
                progressBar.setValue(0);
                answer = JOptionPane.showConfirmDialog(null, """
                                Are you sure you want to erase your PIC ?
                                Existing .hex on your PIC will be lost !""",
                        "Warning",
                        JOptionPane.YES_NO_OPTION);
                if (answer == 0) {
                    buttonErase.setEnabled(false);
                    buttonPressed = buttonErase;
                    logArea.append("Erasing your PIC... (Don't panic!)\n");
                    new Operations().execute();
                } else {
                    logArea.append("Erasing aborted\n");
                }
            }

            if (event.getSource() == buttonRun) {
                command[0] = "/bin/bash";
                command[1] = "-c";
                command[2] = "pk2cmd -P -JN -R -T";
                progressBar.setValue(0);
                logArea.append("Running HEX...\n");
                buttonPressed = buttonStop;
                new Operations().execute();
                buttonRun.setEnabled(false);
            }

            if (event.getSource() == buttonStop) {
                command[0] = "/bin/bash";
                command[1] = "-c";
                command[2] = "pk2cmd -P -JN -R";
                progressBar.setValue(0);
                buttonPressed = buttonRun;
                new Operations().execute();
                buttonStop.setEnabled(false);
            }

            if (event.getSource() == buttonClearLog) {
                progressBar.setValue(0);
                logArea.setText("Logs cleared\n");
            }
        }
    }
}
