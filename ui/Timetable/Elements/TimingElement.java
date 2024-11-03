package ui.Timetable.Elements;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;

public class TimingElement extends JButton {
    public TimingElement(int n) {
        super();
        this.setBackground(Color.WHITE);
        this.setBounds(0, n*80, 780, 80);
        this.setBorder(new EmptyBorder(0, 0, 10, 10));
    }
}
