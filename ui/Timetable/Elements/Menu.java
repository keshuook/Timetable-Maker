package ui.Timetable.Elements;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Menu extends JPanel {
    public JButton fileButton;
    public JButton timetableFormatButton;
    public JButton eventsButton;
    public JButton venueButton;
    public JButton teachersButton;
    public JButton timetableButton;
    public Menu() {
        super();
        this.setLayout(null);
        this.setBounds(0, 40, 1000, 20);
        this.setBackground(Color.LIGHT_GRAY);
        this.fileButton = new JButton("File");
        styleButton(fileButton, 0);
        timetableFormatButton = new JButton("Timings");
        styleButton(timetableFormatButton, 1);
        eventsButton = new JButton("Events");
        styleButton(eventsButton, 2);
        teachersButton = new JButton("Teachers");
        styleButton(teachersButton, 3);
        venueButton = new JButton("Venues");
        styleButton(venueButton, 4);
        timetableButton = new JButton("Timetable");
        styleButton(timetableButton, 5);
        timetableButton.setBackground(Color.PINK);
        this.add(fileButton);
        this.add(timetableFormatButton);
        this.add(eventsButton);
        this.add(teachersButton);
        this.add(venueButton);
        this.add(timetableButton);
    }
    private void styleButton(JButton button, int x) {
        button.setBounds(x*100, 0, 100, 20);
        button.setBackground(Color.CYAN);
        button.setBorder(new EmptyBorder(0, 10, 5, 10));
        button.setBackground(Color.LIGHT_GRAY);
    }
}
