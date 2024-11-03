package ui.Timetable.Elements;

import objects.TimeRange;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;

public class TimeRangeSelector extends JPanel {
    private JFormattedTextField timeBegin;
    private JFormattedTextField timeEnd;
    public TimeRangeSelector() {
        super();
        this.setBounds(210, 160, 780, 100);
        this.setLayout(null);
        try {
            Font font = new Font("Courier New", Font.PLAIN, 14);

            MaskFormatter formatBegin = new MaskFormatter("Begin ##:##");
            formatBegin.setPlaceholder("#");
            this.timeBegin = new JFormattedTextField(formatBegin);
            this.add(timeBegin);
            timeBegin.setValue("Begin 00:00");
            timeBegin.setBounds(100, 50, 150, 40);
            timeBegin.setFont(font);

            MaskFormatter formatEnd = new MaskFormatter("End ##:##");
            formatEnd.setPlaceholder("#");
            this.timeEnd = new JFormattedTextField(formatEnd);
            this.add(timeEnd);
            timeEnd.setValue("End 23:59");
            timeEnd.setBounds(530, 50, 150, 40);
            timeEnd.setFont(font);

            JLabel label = new JLabel("to");
            label.setFont(font);
            label.setBounds(372, 50, 36, 40);
            this.add(label);
        }catch (ParseException e) {}
    }
    public TimeRange getTimeRange() {
        return TimeRange.fromString(timeBegin.getText().replace("Begin ", ""), timeEnd.getText().replace("End ", ""));
    }
    public void setTime(TimeRange timeRange) {
        String[] times = timeRange.getAsStrings();
        timeBegin.setValue("Begin "+times[0]+":"+times[1]);
        timeEnd.setValue("End "+times[2]+":"+times[3]);
    }
}
