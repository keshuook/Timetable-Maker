package ui.Timetable;

import objects.Event;
import objects.GradeTimetable;
import objects.TimeRange;

import javax.swing.*;
import java.awt.*;
import java.io.*;

public class FilesPanel extends JPanel {
    public FilesPanel(JFrame frame) {
        String[] daysOfTheWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};

        this.setBounds(0, 0, 1000, 600);

        GridBagLayout layout = new GridBagLayout();
        this.setLayout(layout);

        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = 0;
        labelConstraints.gridwidth = 1;
        labelConstraints.gridheight = 1;
        GridBagConstraints fileConstraints = new GridBagConstraints();
        fileConstraints.gridx = 0;
        fileConstraints.gridy = 2;
        fileConstraints.gridwidth = 6;
        fileConstraints.gridheight = 4;

        JLabel selectDirTT = new JLabel("Please select a directory to save the timetables in.");
        selectDirTT.setFont(new Font("Courier New", Font.PLAIN, 16));
        this.add(selectDirTT, labelConstraints);

        JFileChooser timetableFileChooser = new JFileChooser();
        timetableFileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        timetableFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        timetableFileChooser.addActionListener(listener -> {
            File dir = timetableFileChooser.getSelectedFile();
            for(GradeTimetable timetable : Timings.instance.timetablesList) {
                File csv = new File(dir.getAbsolutePath()+"/"+timetable.getName()+".csv");
                try {
                    csv.createNewFile();
                    PrintWriter writer = new PrintWriter(new FileOutputStream(csv));
                    writer.print(",");
                    for (TimeRange range : timetable.getTimings()) {
                        writer.print(range.getRangeAsString()+",");
                    }
                    writer.println();
                    for(int i = 0;i < 5;i++) {
                        writer.write(daysOfTheWeek[i]+",");
                        for(Event event : timetable.getEventsList()[i]) {
                            writer.print(event == null ? "," : event.getDisplayName()+",");
                        }
                        writer.println();
                    }
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            JOptionPane.showMessageDialog(frame, "The file(s) have been save successfully!", "File Save Success", 1);
        });

        this.add(timetableFileChooser, fileConstraints);
    }
}
