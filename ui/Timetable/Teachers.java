package ui.Timetable;

import objects.Teacher;
import objects.TimeRange;
import ui.ColorCodes;
import ui.Timetable.Elements.ListCallback;
import ui.Timetable.Elements.ListElement;
import ui.Timetable.Elements.TimeRangeSelector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Teachers extends JPanel {
    private Editor frame;
    private TimeRangeSelector timeRangeSelector;
    protected List<String> teachersNameList;
    protected List<Teacher> teachersList;
    protected static Teachers instance;
    private static final String[] daysOfTheWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
    public Teachers(Editor frame, InputStream fileInputStream) {
        super();
        instance = this;
        teachersNameList = new ArrayList<String>();
        teachersList = new ArrayList<Teacher>();
        this.frame = frame;
        try {
            this.parseFile(fileInputStream);
        }catch (Exception e){
            e.printStackTrace();
        }
        this.setLayout(null);
        this.setBounds(0, 0, 1000, 600);
        this.addComponents();
    }
    private void parseFile(InputStream fileInputStream) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
        while (true) {
            String line = reader.readLine();
            if (line == null) break;
            String[] data = line.split(",");
            teachersNameList.add(data[0]);
            List<TimeRange> timings = new ArrayList<>();
            for(int i = 1;i < data.length;i++) {
                timings.add(TimeRange.fromString(data[i], data[++i]));
            }
            teachersList.add(new Teacher(data[0], timings));
        }
    }
    private int selectedIndex = -1;
    private int selectedComboBoxDay = 0;
    private boolean selectingTeacher = false; // To prevent the "please select a teacher" message when switching between teachers.
    private void addComponents() {
        // Teachers timing control
        JLabel title = new JLabel("Select a Teacher");
        title.setFont(new Font("Courier New", Font.BOLD, 24));
        title.setBounds(0, 65, 780, 20);
        this.add(title);

        // Choose day of the week
        JComboBox<String> comboBoxElement = new JComboBox<>(daysOfTheWeek);
        comboBoxElement.setBounds(210, 100, 780, 60);
        comboBoxElement.setBorder(new EmptyBorder(0, 0, 0, 0));
        comboBoxElement.setBackground(ColorCodes.BUTTON_LIGHT);

        JButton updateButton = new JButton("Update");
        updateButton.setBackground(ColorCodes.BUTTON_LIGHT);
        updateButton.setBounds(210, 300, 780, 40);
        this.add(updateButton);

        JButton copyToAll = new JButton("Copy to all days.");
        copyToAll.setBackground(ColorCodes.BUTTON_LIGHT);
        copyToAll.setBounds(210, 340, 780, 40);
        this.add(copyToAll);

        // Time range selector UI
        this.timeRangeSelector = new TimeRangeSelector();
        frame.add(timeRangeSelector);

        // Show teacher's timings
        JTable timingsTable = new JTable(5, 3);
        DefaultTableModel tableModel = (DefaultTableModel) timingsTable.getModel();
        timingsTable.setEnabled(false);
        timingsTable.setFont(new Font("Courier New", Font.PLAIN, 18));
        timingsTable.setBounds(210, 400,780, 150);
        timingsTable.setRowHeight(25);
        timingsTable.setBackground(Color.WHITE);
        this.add(timingsTable);

        timingsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)  {
                final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(row == 0 ? Color.LIGHT_GRAY : row == selectedComboBoxDay+1 && selectedIndex != -1 ? Color.YELLOW : Color.WHITE);
                return c;
            }
        });

        // Teachers list element
        ListElement listElement = new ListElement(frame, teachersNameList);
        listElement.initialise(new ListCallback() {
            @Override
            public void onValueSelected(String value, int index) {
                if(value == null) {
                    selectedIndex = -1;
                    title.setText("Select a Teacher");
                    tableModel.setDataVector(new String[6][3], new String[3]);
                }else{
                    selectingTeacher = true;
                    comboBoxElement.setSelectedIndex(0);
                    selectingTeacher = false;
                    selectedIndex = index;
                    title.setText(value);
                    Teacher teacher = teachersList.get(index);
                    timeRangeSelector.setTime(teacher.getAllowedRangeAtDay(0));
                    // Code to set data in the table
                    updateTable(tableModel, teacher);
                }
            }
            @Override
            public void onAddValue(String value) {
                List<TimeRange> teacherTimings = new ArrayList<>();
                for (int i = 0;i < 5;i++) teacherTimings.add(new TimeRange(8, 15, 15, 30));
                teachersList.add(new Teacher(value, teacherTimings));
            }
            @Override
            public void onUpdateList(DefaultListModel<String> listModel) {
                frame.showFileNeedsToBeSaved();
                Collections.sort(teachersNameList, Comparator.naturalOrder());
                Collections.sort(teachersList, Comparator.comparing(Teacher::getTeachersName));
                if(listModel != null) {
                    listModel.clear();
                    for(String val : teachersNameList) {
                        listModel.addElement(val);
                    }
                }
            }
            @Override
            public void onDeleteValue(int index) {
                teachersList.remove(index);
            }
            @Override
            public void onCopyValue(String value, int index) {
                teachersList.add(new Teacher(teachersList.get(index)));
            }

            @Override
            public void onRename(int index, String value) {
                teachersList.get(index).rename(value);
            }
        });

        comboBoxElement.addActionListener(listener -> {
            TimeRange range = timeRangeSelector.getTimeRange();
            if(!addTimeToDay(range, selectedComboBoxDay, comboBoxElement.getSelectedIndex())) {
                comboBoxElement.setSelectedIndex(selectedComboBoxDay);
            }else{
                selectedComboBoxDay = comboBoxElement.getSelectedIndex();
                updateTable(tableModel, teachersList.get(selectedIndex));
            }
        });
        updateButton.addActionListener(listener -> {
            if(addTimeToDay(timeRangeSelector.getTimeRange(), selectedComboBoxDay, -1)) {
                updateTable(tableModel, teachersList.get(selectedIndex));
            }
        });

        copyToAll.addActionListener(listener -> {
            if (timeRangeSelector.getTimeRange().IsRangeBad()) {
                JOptionPane.showMessageDialog(frame, "Please select a valid time range!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if(selectedIndex == -1) {
                JOptionPane.showMessageDialog(frame, "Please select a teacher!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if(JOptionPane.showConfirmDialog(frame, "Are you sure you want to copy to all days?") != 0) return;
            TimeRange range = timeRangeSelector.getTimeRange();
            Teacher teacher = teachersList.get(selectedIndex);
            List<TimeRange> ranges = teacher.getAllowedTimings();
            for(int i = 0;i < ranges.size();i++) {
                ranges.set(i, new TimeRange(range));
            }
            frame.showFileNeedsToBeSaved();
            updateTable(tableModel, teacher);
        });

        this.add(comboBoxElement);
        this.add(listElement);
    }
    @Override
    public void setVisible(boolean flag) {
        timeRangeSelector.setVisible(flag);
        super.setVisible(flag);
    }
    private void updateTable(DefaultTableModel tableModel, Teacher teacher) {
        String[][] timings = new String[6][3];
        timings[0] = new String[]{"Day of the Week", "Start Time", "End Time"};
        for(int i = 0;i < 5;i++) {
            String[] times = teacher.getAllowedRangeAtDay(i).getAsStrings();
            timings[i+1] = new String[]{daysOfTheWeek[i], times[0]+":"+times[1], times[2]+":"+times[3]};
        }
        tableModel.setDataVector(timings, new String[3]);
    }
    private boolean addTimeToDay(TimeRange range, int oldIndex, int newIndex) {
        if(range.IsRangeBad()) {
            JOptionPane.showMessageDialog(frame, "Please enter a valid timing", "Error", JOptionPane.ERROR_MESSAGE);
        }
        if(selectedIndex == -1 && !selectingTeacher) {
            JOptionPane.showMessageDialog(frame, "Please select a teacher!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        if(selectedIndex == -1 || range.IsRangeBad()) {
            return false;
        }
        teachersList.get(selectedIndex).getAllowedTimings().set(oldIndex, timeRangeSelector.getTimeRange());
        if(newIndex != -1) {
            timeRangeSelector.setTime(teachersList.get(selectedIndex).getAllowedRangeAtDay(newIndex));
        }
        frame.showFileNeedsToBeSaved();
        return true;
    }
    public void saveData(FileOutputStream stream) {
        PrintWriter writer = new PrintWriter(stream);
        teachersList.forEach(teacher -> {
            writer.print(teacher.getTeachersName()+",");
            for(TimeRange range : teacher.getAllowedTimings()) {
                if(range.equals(teacher.getAllowedTimings().get(teacher.getAllowedTimings().size() - 1))) {
                    writer.println(range.getRangeAsString().replace(" - ", ","));
                }else {
                    writer.print(range.getRangeAsString().replace(" - ", ",").concat(","));
                }
            }
        });
        writer.close();
    }
}
