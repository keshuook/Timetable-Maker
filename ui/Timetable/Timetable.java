package ui.Timetable;

import objects.Event;
import objects.EventAdditionReturn;
import objects.GradeTimetable;
import objects.TimeRange;
import ui.ColorCodes;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.io.*;
import java.util.List;

public class Timetable extends JPanel {
    private Editor frame;
    private JComboBox<Object> eventsBox;
    private JComboBox<Object> classSelector;
    public Timetable(Editor frame, InputStream fileInputStream) {
        this.frame = frame;
        try {
            this.parseFile(fileInputStream);
        }catch (IOException e) {
            e.printStackTrace();
        }
        this.setLayout(null);
        this.setBounds(0, 0, 1000, 600);
        this.addComponents();
    }
    public void parseFile(InputStream fileInputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
        int i = 0;

        String data;
        while ((data = reader.readLine()) != null) {
            GradeTimetable gradeTimetable = Timings.instance.timetablesList.get(i++);
            for(int j = 0;j < 5;j++) {
                if(data == null) break; // In case the timings haven't been made yet
                String[] csvData = data.split(",");
                for(int k = 0;k < csvData.length;k++) {
                    for(Event event : Events.instance.eventList) {
                        if(csvData[k].equals(event.eventName)) {
                            event.addEvent(gradeTimetable.getTimings().get(k), j);
                            gradeTimetable.setEvent(event, j, k);
                        }
                    }
                }
                if(j != 4) data = reader.readLine();
            }
        }
    }
    public void saveData(FileOutputStream fileOutputStream) {
        List<GradeTimetable> timetables = Timings.instance.timetablesList;
        PrintWriter writer = new PrintWriter(fileOutputStream);

        // Note: not having a last comma for the csv does cause ambiguity but it doesn't matter
        for(GradeTimetable timetable : timetables) {
            for(int i = 0;i < timetable.getEventsList().length;i++) {
                for(int j = 0;j < timetable.getEventsList()[i].length;j++) {
                    Event event = timetable.getEventsList()[i][j];
                    writer.print(event == null ? "" : event.eventName);
                    if(j+1 != timetable.getEventsList()[i].length && timetable.getEventsList()[i].length != 1) writer.print(",");
                }
                if(!(timetable.equals(timetables.get(timetables.size() - 1)) && timetable.getEventsList().length == i+1)) writer.println();
            }
        }
        writer.close();
    }
    private Event addedEvent;
    private boolean tableChangedByCode = false;
    private void addComponents() {
        JLabel title = new JLabel("Select a Class");
        title.setFont(new Font("Courier New", Font.BOLD, 24));
        title.setBounds(0, 65, 780, 20);
        this.add(title);

        eventsBox = new JComboBox<>();

        classSelector = new JComboBox<>(Timings.instance.timetableNamesList.toArray());
        classSelector.setBounds(20, 90, 960, 40);
        classSelector.setBackground(ColorCodes.BUTTON_LIGHT);

        JTable timetable = new JTable();
        timetable.getTableHeader().setBounds(20, 140, 960, 20);
        timetable.getTableHeader().setBackground(Color.LIGHT_GRAY);
        timetable.getTableHeader().setResizingAllowed(false);
        timetable.getTableHeader().setReorderingAllowed(false);
        timetable.setBounds(20, 160, 960, 430);
        timetable.setRowHeight(86);

        this.add(timetable.getTableHeader());
        this.add(timetable);

        TableCellRenderer renderer = (table, value, isSelected, hasFocus, row, column) -> {
            if(value == null) value = "";
            JTextArea component = new JTextArea(value.toString());
            component.setWrapStyleWord(true);
            component.setLineWrap(true);
            return component;
        };
        String[] daysOfTheWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        classSelector.addActionListener(listener -> {
            if(classSelector.getSelectedIndex() == -1) return;
            timetable.clearSelection();
            title.setText(classSelector.getSelectedItem().toString());

            // Combo box chooser
            eventsBox.removeAllItems();
            eventsBox.addItem(null);
            Events.instance.eventList.forEach(event -> {
                if(classSelector.getSelectedItem().equals(event.getClassName())) {
                    eventsBox.addItem(event.eventName);
                }
            });
            List<TimeRange> timings =  Timings.instance.timetablesList.get(classSelector.getSelectedIndex()).getTimings();
            timetable.setModel(new DefaultTableModel(5, timings.size()+1){
                @Override
                public boolean isCellEditable(int row, int column) {
                    return  column != 0;
                }
            });
            // So the table says Monday - Friday
            for(int i = 0;i < 5;i++) {
                timetable.getModel().setValueAt(daysOfTheWeek[i], i, 0);
            }
            for(int i = 0;i < 5;i++) {
                for(int k = 1;k <= timings.size();k++) { // Because we're reading the k-1 event, we need k <= timings.size()
                    Event ttevent = Timings.instance.timetablesList.get(classSelector.getSelectedIndex()).getEventsList()[i][k-1];
                    timetable.getModel().setValueAt(ttevent == null ? "" : ttevent.eventName, i, k);
                }
            }

            int i = 1;
            timetable.getColumnModel().getColumn(0).setHeaderValue("Day");
            for (TimeRange timeRange : timings) {
                timetable.getColumnModel().getColumn(i++).setHeaderValue(timeRange.getRangeAsString());
            }
            DefaultCellEditor cellEditor = new DefaultCellEditor(eventsBox);
            for(int c = 0;c < timings.size();c++) {
                timetable.getColumnModel().getColumn(c+1).setCellRenderer(renderer); // For line wrap
                timetable.getColumnModel().getColumn(c+1).setCellEditor(cellEditor);
            }
            timetable.getModel().addTableModelListener(modelListener -> {
                if(tableChangedByCode) return;
                frame.showFileNeedsToBeSaved();
                int day = modelListener.getFirstRow();
                int period = modelListener.getColumn();

                // First remove previous event
                Event eventToBeRemoved = Timings.instance.timetablesList.get(classSelector.getSelectedIndex()).getEventsList()[day][period-1];
                if(eventToBeRemoved != null) {
                    Timings.instance.timetablesList.get(classSelector.getSelectedIndex()).removeEvent(eventToBeRemoved, day, period - 1);
                }

                // Then get new event name
                String eventName = (String)(timetable.getModel().getValueAt(day, period));
                for(Event instevent : Events.instance.eventList) {
                    if(instevent.eventName.equals(eventName)) {
                        addedEvent = instevent;
                        break;
                    }
                }

                if(addedEvent == null || timetable.getModel().getValueAt(day, period) == null) return; // If it doesn't exist (maybe because the event name is "") return.

                EventAdditionReturn eventAdditionReturn = addedEvent.canBeAddedTo(timings.get(period-1), day);

                if(eventAdditionReturn != EventAdditionReturn.OK) {
                    tableChangedByCode = true;
                    timetable.getModel().setValueAt(null, day, period);
                    tableChangedByCode = false;
                    JOptionPane.showMessageDialog(frame, "The event could not be created because "+eventAdditionReturn, "Event Creation Failed", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // Note period 0 has days of the week
                Timings.instance.timetablesList.get(classSelector.getSelectedIndex()).setEvent(addedEvent, day, period-1);
                addedEvent.addEvent(timings.get(period - 1), day);
                addedEvent = null;
            });
        });

        this.add(classSelector);
    }
    public void reset() {
        tableChangedByCode = true;
        if(classSelector == null) return;
        classSelector.removeAllItems();
        for (String name : Timings.instance.timetableNamesList) {
            classSelector.addItem(name);
        }
        tableChangedByCode = false;
    }
}
