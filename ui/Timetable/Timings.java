package ui.Timetable;

import objects.Event;
import objects.EventAdditionReturn;
import objects.GradeTimetable;
import objects.TimeRange;
import ui.ColorCodes;
import ui.Timetable.Elements.ListCallback;
import ui.Timetable.Elements.ListElement;
import ui.Timetable.Windows.TimeWindow;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.*;
import java.awt.*;

public class Timings extends JPanel {
    protected static Timings instance;
    private ListElement timetablesListElement;
    protected List<String> timetableNamesList = new ArrayList<String>();
    private DefaultListModel<String> timetableFormatList = new DefaultListModel<>();
    protected List<GradeTimetable> timetablesList = new ArrayList<>();
    private Editor frame;
    public Timings(Editor frame, InputStream fileInputStream) {
        super();
        instance = this;
        try {
            this.parseFile(fileInputStream);
        }catch (Exception e){
            e.printStackTrace();
        }
        this.frame = frame;
        this.setLayout(null);
        this.setOpaque(true);
        this.setBounds(0, 60, 1000, 540);
        this.addTimetableSelector();
    }
    private void parseFile(InputStream stream) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        while (true) {
            String line = reader.readLine();
            if (line == null) break;
            String[] data = line.split(",");
            timetableNamesList.add(data[0]);
            List<TimeRange> timings = new ArrayList<>();
            for(int i = 1;i < data.length;i++) {
                timings.add(TimeRange.fromString(data[i], data[++i]));
            }
            timetablesList.add(new GradeTimetable(data[0], timings));
        }
    }
    private int selectedIndex;
    private void addTimetableSelector() {
        // Timings UI
        JLabel title = new JLabel("Select a timetable");
        title.setFont(new Font("Courier New", Font.BOLD, 24));
        title.setBounds(0, 5, 780, 20);
        this.add(title);

        // Timings time settings
        JButton addPeriod = new JButton("Add");
        addPeriod.setBackground(ColorCodes.BUTTON_LIGHT);
        addPeriod.setBounds(210, 480, 780, 20);
        this.add(addPeriod);

        JButton edit = new JButton("Edit");
        edit.setBackground(ColorCodes.BUTTON_LIGHT);
        edit.setBounds(210, 500, 780, 20);
        this.add(edit);

        JButton deleteTime = new JButton("Delete");
        deleteTime.setBounds(210, 520, 780, 20);
        deleteTime.setBackground(Color.RED);
        this.add(deleteTime);

        // List
        DefaultListModel<String> timingsList = new DefaultListModel<String>();
        JList<String> timingsElement = new JList(timingsList);
        timingsElement.setFixedCellHeight(100);
        timingsElement.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
        timingsElement.setFont(new Font("Courier New", Font.PLAIN, 20));

        timetablesListElement = new ListElement(frame, timetableNamesList);
        selectedIndex = -1;
        timetablesListElement.initialise(new ListCallback() {
            @Override
            // When a new timetable is selected
            public void onValueSelected(String value, int index) {
                selectedIndex = index;
                if(value != null) {
                    title.setText(value);
                    timingsList.clear();
                    List<TimeRange> timings = timetablesList.get(index).getTimings();
                    for(TimeRange range : timings) {
                        timingsList.addElement(range.getRangeAsString());
                    }
                }else{
                    title.setText("Select a timetable");
                    timingsList.clear();
                }
            }
            @Override
            public void onAddValue(String value) {
                timetablesList.add(new GradeTimetable(value, new ArrayList<>()));
            }

            @Override
            public void onUpdateList(DefaultListModel<String> modelList) {
                frame.showFileNeedsToBeSaved();
                // Bubble sort
                boolean sorted = false;
                int c = 0;
                while(!sorted) {
                    c++;
                    sorted = true;
                    for(int i = 0;i < timetableNamesList.size()-c;i++) {
                        if(timetableNamesList.get(i).compareTo(timetableNamesList.get(i+1)) > 0) {
                            sorted = false;
                            Collections.swap(timetablesList, i, i + 1);
                            Collections.swap(timetableNamesList, i, i+1);
                        }
                    }
                }
                if(modelList != null) {
                    modelList.clear();
                    for(String val : timetableNamesList) {
                        modelList.addElement(val);
                    }
                }
            }

            @Override
            public void onDeleteValue(int index) {
                timetablesList.remove(index);
            }

            @Override
            public void onCopyValue(String value, int index) {
                timetablesList.add(new GradeTimetable(value, new ArrayList<>(timetablesList.get(index).getTimings())));
            }

            @Override
            public void onRename(int index, String value) {}
        });

        addPeriod.addActionListener(listener -> {
            if(selectedIndex == -1) {
                JOptionPane.showMessageDialog(frame, "Please select a timetable!", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            TimeWindow.showWindow("00:00 - 00:00", () -> {
                TimeRange newRange = new TimeRange(Integer.parseInt(TimeWindow.BEGIN_TIME[0]), Integer.parseInt(TimeWindow.BEGIN_TIME[1]), Integer.parseInt(TimeWindow.END_TIME[0]), Integer.parseInt(TimeWindow.END_TIME[1]));
                if(newRange.IsRangeBad()) {
                    JOptionPane.showMessageDialog(frame, "Please input a valid time range!", "Error", JOptionPane.WARNING_MESSAGE);
                }else{
                    List<TimeRange> ranges = timetablesList.get(selectedIndex).getTimings();
                    for(TimeRange range : ranges) {
                        if(newRange.isClashingWith(range)) {
                            JOptionPane.showMessageDialog(frame, "This timings clashes with another timing!", "Error", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                    }
                    timingsList.addElement(newRange.getRangeAsString());
                    timetablesList.get(selectedIndex).getTimings().add(newRange);
                    timetablesList.get(selectedIndex).update();
                    sortTimingsList(timingsList, timetablesList.get(selectedIndex).getTimings());
                    timetablesList.get(selectedIndex).shiftEvents(true, timetablesList.get(selectedIndex).getTimings().indexOf(newRange));
                    frame.showFileNeedsToBeSaved();
                }
            });
        });
        edit.addActionListener(listener -> {
            if(timingsElement.getSelectedIndex() == -1) {
                JOptionPane.showMessageDialog(frame, "Please select an item to be changed.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            TimeWindow.showWindow(timingsElement.getSelectedValue(), () -> {
                TimeRange newRange = new TimeRange(Integer.parseInt(TimeWindow.BEGIN_TIME[0]), Integer.parseInt(TimeWindow.BEGIN_TIME[1]), Integer.parseInt(TimeWindow.END_TIME[0]), Integer.parseInt(TimeWindow.END_TIME[1]));
                for(int i = 0;i < timetablesList.get(selectedIndex).getEventsList().length;i++) {
                    Event event = timetablesList.get(selectedIndex).getEventsList()[i][timingsElement.getSelectedIndex()];
                    if(event != null) {
                        event.removeEvent(timetablesList.get(selectedIndex).getTimings().get(timingsElement.getSelectedIndex()), i);
                        EventAdditionReturn reasonOfEventAddition = event.canBeAddedTo(newRange, i);
                        if(reasonOfEventAddition != EventAdditionReturn.OK) {
                            JOptionPane.showMessageDialog(frame, event.getEventName()+" cannot be in the new timetable timings because: "+reasonOfEventAddition, "Time Change Failed", JOptionPane.ERROR_MESSAGE);
                            event.addEvent(timetablesList.get(selectedIndex).getTimings().get(timingsElement.getSelectedIndex()), i);
                            return;
                        }else{
                            event.addEvent(newRange, i);
                        }
                    }
                }
                if(newRange.IsRangeBad()) {
                    JOptionPane.showMessageDialog(frame, "Please input a valid time range!", "Error", JOptionPane.WARNING_MESSAGE);
                }else{
                    List<TimeRange> ranges = timetablesList.get(selectedIndex).getTimings();
                    if(timingsElement.getSelectedIndex() == -1) return; //  Some cancellation happened
                    for(TimeRange range : ranges) {
                        if(range.equals(ranges.get(timingsElement.getSelectedIndex()))) continue; // If it clashes with the item that's being changed it doesn't matter
                        if(newRange.isClashingWith(range)) {
                            JOptionPane.showMessageDialog(frame, "This timing clashes with another timing!", "Error", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                    }
                    timingsList.add(timingsElement.getSelectedIndex()+1, newRange.getRangeAsString());
                    ranges.remove(timingsElement.getSelectedIndex());
                    ranges.add(timingsElement.getSelectedIndex(), newRange);
                    timingsList.remove(timingsElement.getSelectedIndex());
                    sortTimingsList(timingsList, timetablesList.get(selectedIndex).getTimings());
                    frame.showFileNeedsToBeSaved();
                }
            });
        });
        deleteTime.addActionListener(listener -> {
            if(timingsElement.getSelectedIndex() == -1) {
                JOptionPane.showMessageDialog(frame, "Please select an item to be changed.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if(JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete this?") == 0) {
                for(Event[] events : timetablesList.get(selectedIndex).getEventsList()) {
                    if(events[timingsElement.getSelectedIndex()] != null) {
                        JOptionPane.showMessageDialog(frame, "There is an event in the specified time slot! Please remove it to delete the time slot.", "Event Exists in Time Slot", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
                timetablesList.get(selectedIndex).shiftEvents(false, timingsElement.getSelectedIndex()); // Shrink events array
                System.out.println(timetablesList.get(selectedIndex).getEventsList()[0].length);
                timetablesList.get(selectedIndex).getTimings().remove(timingsElement.getSelectedIndex());
                timetablesList.get(selectedIndex).update();
                timingsList.remove(timingsElement.getSelectedIndex());
                frame.showFileNeedsToBeSaved();
            }
        });

        JScrollPane timingsPane = new JScrollPane(timingsElement);
        timingsPane.setBounds(210, 40, 780, 440);
        this.add(timingsPane);
    }
    public void saveData(FileOutputStream stream) {
        PrintWriter writer = new PrintWriter(stream);
        for(int i = 0;i < timetableNamesList.size();i++) {
            writer.print(timetableNamesList.get(i)+",");
            List<TimeRange> ranges = timetablesList.get(i).getTimings();
            for(int j = 0;j < ranges.size();j++) {
                if(j == ranges.size() - 1) {
                    writer.println(ranges.get(j).getRangeAsString().replace(" - ", ","));
                }else{
                    writer.print(ranges.get(j).getRangeAsString().replace(" - ", ",").concat(","));
                }
            }
            if(ranges.size() == 0 && i != timetableNamesList.size() - 1) {
                writer.println();
            }
        }
        writer.close();
    }
    private void sortTimingsList(DefaultListModel<String> timingsList, List<TimeRange> rangesList) {
        List<String> timingsStringList = new ArrayList<>();
        for(int i = 0;i < timingsList.size();i++) {
            timingsStringList.add(timingsList.get(i));
        }
        // Bubble sort
        boolean sorted = false;
        int c = 0;
        while(!sorted) {
            c++;
            sorted = true;
            for(int i = 0;i < timingsList.size()-c;i++) {
                if(timingsStringList.get(i).compareTo(timingsStringList.get(i+1)) > 0) {
                    sorted = false;
                    Collections.swap(rangesList, i, i + 1);
                    Collections.swap(timingsStringList, i, i+1);
                }
            }
        }
        timingsList.clear();
        for(String val : timingsStringList) {
            timingsList.addElement(val);
        }
    }
    @Override
    public void setVisible(boolean flag) {
        timetablesListElement.setVisible(flag);
        super.setVisible(flag);
    }
}