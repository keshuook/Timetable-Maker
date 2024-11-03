package ui.Home;

import ui.TopBar;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class Home extends JFrame {
    public static ZipFile ZIP_FILE;
    private static Runnable callback;
    public Home(Runnable callback) {
        super();
        // Callback
        this.callback = callback;

        this.setResizable(false);
        this.setTitle("Timetable Maker");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBounds(200, 50, 1000, 600);
        this.setUndecorated(true);
        this.setLayout(null);

        // Footer
        JLabel label = new JLabel("Made by Kailash Prasad as a Class 11 School Project");
        label.setBounds(20, 565, 1000, 35);
        label.setFont(new Font("Courier New", Font.ITALIC, 14));
        this.add(label);

        this.addButtons();
        this.addTopBar();
        this.setVisible(true);
    }
    private void callback() {
        callback.run();
    }
    private void addTopBar() {

        JComponent topBar = new TopBar(this);
        this.add(topBar);
    }
    private void addButtons() {
        JButton createNew = new Button("Create a new Timetable", 0);
        JButton open = new Button("Open a Timetable", 1);
        this.add(createNew);
        this.add(open);

        // Choose a file to open/save
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("School Timetable", "stt");
        fileChooser.setFileFilter(filter);

        open.addActionListener(listener -> {
            if(fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    ZIP_FILE = new ZipFile(fileChooser.getSelectedFile());
                    this.dispose();
                    callback();
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, e.getMessage(), "Input Output Exception", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        });

        createNew.addActionListener(listener -> {
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    // Note: An stt file is a zip archive with certain files
                    String path = fileChooser.getSelectedFile().getAbsolutePath();
                    if(!fileChooser.getSelectedFile().getAbsolutePath().endsWith(".stt")) path += ".stt";
                    File file = new File(path);
                    file.createNewFile();
                    ZipOutputStream out = new ZipOutputStream(new FileOutputStream(file));
                    out.putNextEntry(new ZipEntry("teachers.csv"));
                    out.closeEntry();
                    out.putNextEntry(new ZipEntry("venues.csv"));
                    out.closeEntry();
                    out.putNextEntry(new ZipEntry("timings.csv"));
                    out.closeEntry();
                    out.putNextEntry(new ZipEntry("events.sv"));
                    out.closeEntry();
                    out.putNextEntry(new ZipEntry("timetable.csv"));
                    out.closeEntry();
                    out.close();
                    ZIP_FILE = new ZipFile(file);
                    this.dispose();
                    callback();
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, e.getMessage(), "Input Output Exception", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        });
    }
}
