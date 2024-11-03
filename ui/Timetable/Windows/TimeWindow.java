package ui.Timetable.Windows;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;

public class TimeWindow {
    public static String[] BEGIN_TIME;
    public static String[] END_TIME;
    public static void showWindow(String time, Runnable runnable) {
        JFrame frame = new JFrame();
        frame.setType(Window.Type.UTILITY);
        frame.setTitle("Edit time [Timings Maker]");
        frame.setAlwaysOnTop(true);
        frame.setResizable(false);
        frame.setLayout(null);
        double width = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        double height = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        frame.setBounds((int)(width/2-250), (int)(height/2-150), 500, 250);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setVisible(true);
        addTimeElements(frame, getTimeAsStrings(time), runnable);
    }
    private static void addTimeElements(JFrame frame,String[] times, Runnable runnable) {
        try {
            Font font = new Font("Courier New", Font.PLAIN, 14);

            MaskFormatter formatBegin = new MaskFormatter("Begin ##:##");
            formatBegin.setPlaceholder("#");
            JFormattedTextField timeBegin = new JFormattedTextField(formatBegin);
            frame.add(timeBegin);
            timeBegin.setValue("Begin "+times[0]);
            timeBegin.setBounds(25, 50, 150, 40);
            timeBegin.setFont(font);

            MaskFormatter formatEnd = new MaskFormatter("End ##:##");
            formatEnd.setPlaceholder("#");
            JFormattedTextField timeEnd = new JFormattedTextField(formatEnd);
            frame.add(timeEnd);
            timeEnd.setValue("End "+times[1]);
            timeEnd.setBounds(300, 50, 150, 40);
            timeEnd.setFont(font);

            JLabel label = new JLabel("to");
            label.setFont(font);
            label.setBounds(230, 50, 100, 40);
            frame.add(label);

            JButton save = new JButton("Save");
            save.setFont(font);
            save.setBounds(0, 170, 500, 40);
            frame.add(save);

            save.addActionListener(listener -> {
                try {
                    String begin = timeBegin.getValue().toString().replace("Begin ", "");
                    String end = timeEnd.getValue().toString().replace("End ", "");
                    // Call TimeWindow.BEGIN_TIME to get an array (of the first input) in the format {"HOUR", "MINUTE"}
                    BEGIN_TIME = new String[]{begin.split(":")[0], begin.split(":")[1]};
                    // Call TimeWindow.END_TIME to get an array (of the second input) in the format {"HOUR", "MINUTE"}
                    END_TIME = new String[]{end.split(":")[0], end.split(":")[1]};
                    frame.dispose();
                }catch (Exception e) {
                    JOptionPane.showMessageDialog(frame,"Please type in the time range.", "Error", JOptionPane.WARNING_MESSAGE);
                }
                runnable.run();
            });
        }catch (ParseException e) {}
    }
    private static String[] getTimeAsStrings(String val) {
        val = val.replace(" - ", "#");
        return val.split("#");
    }
}
