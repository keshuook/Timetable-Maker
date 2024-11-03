package ui.Timetable;

import ui.Timetable.Elements.Menu;
import ui.TopBar;
import util.ZipFileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Editor extends JFrame {
    public Editor(ZipFile zipFile) {
        super();
        this.setResizable(false);
        this.setTitle("Timetable Maker ["+zipFile.getName().substring(Math.max(zipFile.getName().lastIndexOf('\\')+1, 0))+"]");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBounds(200, 60, 1000, 600);
        this.setUndecorated(true);
        this.setLayout(null);
        this.addComponents(zipFile);
        this.implementSaveListener();
        this.setVisible(true);
    }
    private String fileName;
    private Timings timingsPanel;
    private Teachers teachersPanel;
    private Venues venuePanel;
    private Events eventsPanel;
    private Timetable timetablePanel;
    private FilesPanel filePanel;
    private void hideAll() {
        filePanel.setVisible(false);
        teachersPanel.setVisible(false);
        timingsPanel.setVisible(false);
        venuePanel.setVisible(false);
        eventsPanel.setVisible(false);
        timetablePanel.setVisible(false);
    }
    private void implementSaveListener() {
        this.getRootPane().registerKeyboardAction(action -> {
            try {
                // Initialise variables to save files
                File f = new File(fileName);
                ZipFileUtils file = new ZipFileUtils(f);

                // Save files
                File teachersCSV = new File("teachers.csv");
                teachersPanel.saveData(new FileOutputStream(teachersCSV));
                File timingsCSV = new File("timings.csv");
                timingsPanel.saveData(new FileOutputStream(timingsCSV));
                File venueCSV = new File("venues.csv");
                venuePanel.saveData(new FileOutputStream(venueCSV));
                File eventsSV = new File("events.sv");
                eventsPanel.saveData(new FileOutputStream(eventsSV));
                File timetableCSV = new File("timetable.csv");
                timetablePanel.saveData(new FileOutputStream(timetableCSV));

                if(this.getTitle().endsWith(" *")) {
                    this.setTitle(this.getTitle().substring(0, this.getTitle().length() - 2));
                }
                this.repaint();

                // Zip up the files
                file.zipFiles(new File[]{timingsCSV, teachersCSV, venueCSV, eventsSV, timetableCSV});

                teachersCSV.delete();
                timingsCSV.delete();
                venueCSV.delete();
                eventsSV.delete();
                timetableCSV.delete();
            } catch (IOException e) {}
        }, KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);
    }
    public void showFileNeedsToBeSaved() {
        if(!this.getTitle().endsWith(" *")) {
            this.setTitle(this.getTitle().concat(" *"));
            this.repaint();
        }
    }
    private void addComponents(ZipFile zipFile) {
        // Top bar
        TopBar topBar = new TopBar(this);
        this.add(topBar);
        ui.Timetable.Elements.Menu menu = new Menu();
        this.add(menu);

        menu.timetableFormatButton.addActionListener(listener -> {
            hideAll();
            timingsPanel.setVisible(true);
        });
        menu.teachersButton.addActionListener(listener -> {
            hideAll();
            teachersPanel.setVisible(true);
        });
        menu.venueButton.addActionListener(listener -> {
            hideAll();
            venuePanel.setVisible(true);
        });
        menu.fileButton.addActionListener(listener -> {
            hideAll();
            filePanel.setVisible(true);
        });
        menu.eventsButton.addActionListener(listener -> {
            hideAll();
            eventsPanel.reset();
            eventsPanel.setVisible(true);
        });
        menu.timetableButton.addActionListener(listener -> {
            hideAll();
            timetablePanel.reset();
            timetablePanel.setVisible(true);
        });

        // Parse ZipFile
        // Timings
        ZipEntry zipEntryTimingsCSV = zipFile.getEntry("timings.csv");
        ZipEntry zipEntryTeachersCSV = zipFile.getEntry("teachers.csv");
        ZipEntry zipEntryVenuesCSV = zipFile.getEntry("venues.csv");
        ZipEntry zipEntryEventsSV = zipFile.getEntry("events.sv"); // CSV with multiple delimiters
        ZipEntry zipEntryTimetableCSV = zipFile.getEntry("timetable.csv");
        if(zipEntryTimingsCSV == null || zipEntryTeachersCSV == null || zipEntryVenuesCSV == null || zipEntryEventsSV == null || zipEntryTimetableCSV == null) {
            JOptionPane.showMessageDialog(this, "The file is corrupt.", "Error with File", JOptionPane.ERROR_MESSAGE);
        } else {
            try {
                timingsPanel = new Timings(this, zipFile.getInputStream(zipEntryTimingsCSV));
                this.add(timingsPanel);

                teachersPanel = new Teachers(this, zipFile.getInputStream(zipEntryTeachersCSV));
                this.add(teachersPanel);

                venuePanel = new Venues(this, zipFile.getInputStream(zipEntryVenuesCSV));
                this.add(venuePanel);

                eventsPanel = new Events(this, zipFile.getInputStream(zipEntryEventsSV));
                this.add(eventsPanel);

                timetablePanel = new Timetable(this, zipFile.getInputStream(zipEntryTimetableCSV));
                this.add(timetablePanel);

                filePanel = new FilesPanel(this);
                this.add(filePanel);

                hideAll();
                filePanel.setVisible(true);

                fileName = zipFile.getName();
                zipFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
