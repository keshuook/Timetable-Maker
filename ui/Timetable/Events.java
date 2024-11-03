package ui.Timetable;

import objects.Event;
import objects.GradeTimetable;
import objects.Teacher;
import objects.Venue;
import ui.ColorCodes;
import ui.Timetable.Elements.ListCallback;
import ui.Timetable.Elements.ListElement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Events extends JPanel {
    private Editor frame;
    protected static Events instance;
    private ListElement listElement;
    private List<String> eventsNameList = new ArrayList<>();
    protected List<Event> eventList = new ArrayList<>();
    private JComboBox<Object> teachersComboBox;
    private JComboBox<Object> venuesComboBox;
    private JComboBox<Object> selectClass;
    private Event selectedEvent;
    private JTable eventsInputTable;
    private boolean tableBeingModifiedByCode = false;
    public Events(Editor frame, InputStream fileInputStream) {
        instance = this;
        try {
            parseFile(fileInputStream);
        }catch (IOException e) {
            e.printStackTrace();
        }
        this.frame = frame;
        this.setLayout(null);
        this.setBounds(0, 0, 1000, 600);
        this.addComponents();
    }
    private String[] split(String str, char delimiter) {
        List<String> out = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for(char c : str.toCharArray()) {
            if(c == delimiter) {
                out.add(sb.toString());
                sb = new StringBuilder();
            }else{
                sb.append(c);
            }
        }
        out.add(sb.toString());
        return out.toArray(new String[out.size()]);
    }

    private void parseFile(InputStream fileInputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
        while (reader.ready()) {
            String line = reader.readLine();
            String[] data = split(line, ',');
            eventsNameList.add(data[0]);
            Event event = new Event(data[0]);
            event.setDisplayName(data[1]);
            event.setClassName(data[2]);
            String[] teachersNames = split(data[3], ';');
            List<Teacher> teachersList = new ArrayList<>();
            for (String teacherName : teachersNames) {
                Teachers.instance.teachersList.forEach(teacher -> {
                     if (teacher.getTeachersName().equals(teacherName)) {
                        teachersList.add(teacher);
                     }
                });
            }
            event.updateTeachers(teachersList);

            String[] venuesName = split(data[4],';');
            List<Venue> venuesList = new ArrayList<>();
            for (String venueName : venuesName) {
                Venues.instance.venueList.forEach(venue -> {
                    if (venue.getName().equals(venueName)) {
                        venuesList.add(venue);
                    }
                });
            }
            event.updateVenues(venuesList);
            eventList.add(event);
        }
    }
    private void resetTableValues(Event event) {
        tableBeingModifiedByCode = true;
        int teacherIndex = 0;
        for(Teacher teacher : event.teachers) {
            eventsInputTable.setValueAt(teacher.getTeachersName(), teacherIndex++, 0);
        }
        int venueIndex = 0;
        for(Venue venue: event.venues) {
            eventsInputTable.setValueAt(venue.getName(), venueIndex++, 1);
        }
        tableBeingModifiedByCode = false;
    }
    private void addComponents() {
        JLabel title = new JLabel("Select an Event");
        title.setFont(new Font("Courier New", Font.BOLD, 24));
        title.setBounds(0, 65, 780, 20);
        this.add(title);

        Font plain = new Font("Courier New", Font.PLAIN, 16);
        Font bold = new Font("Courier New", Font.BOLD, 16);

        JLabel displayNameLabel = new JLabel("Display Name: ");
        displayNameLabel.setBounds(210, 100, 140, 30);
        displayNameLabel.setFont(bold);
        this.add(displayNameLabel);

        JTextField displayName = new JTextField();
        displayName.setBounds(350, 100, 640, 30);
        displayName.setFont(plain);
        this.add(displayName);

        displayName.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                SwingUtilities.invokeLater(() -> change());
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                SwingUtilities.invokeLater(() -> change());
            }
            @Override
            public void changedUpdate(DocumentEvent e) {}
            public void change() {
                if(selectedEvent == null) {
                    if(!displayName.getText().equals("")) JOptionPane.showMessageDialog(frame, "Please select an event to modify!", "Error", JOptionPane.ERROR_MESSAGE);
                    displayName.setText("");
                    return;
                }
                frame.showFileNeedsToBeSaved();
                selectedEvent.setDisplayName(displayName.getText());
            }
        });

        JLabel selectClassLabel = new JLabel("Select Class: ");
        selectClassLabel.setBounds(210, 140, 140, 30);
        selectClassLabel.setFont(bold);
        this.add(selectClassLabel);

        selectClass = new JComboBox<>(Timings.instance.timetableNamesList.toArray()); // Classes
        selectClass.setBounds(350, 140, 640, 60);
        selectClass.setBorder(new EmptyBorder(0, 0, 0, 0));
        selectClass.setBackground(ColorCodes.BUTTON_LIGHT);
        selectClass.setFont(plain);
        this.add(selectClass);

        // When class changes
        selectClass.addActionListener(listener -> {
            if(tableBeingModifiedByCode) return;
            if(selectedEvent == null) {
                JOptionPane.showMessageDialog(frame, "Please select an event to modify!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            frame.showFileNeedsToBeSaved();
            selectedEvent.setClassName((String) selectClass.getSelectedItem());
        });

        // Venues and Teachers input
        Object[][] tableContent = new Object[7][2];
        eventsInputTable = new JTable(tableContent, new String[]{"Teachers","Venues"});
        eventsInputTable.setBounds(210, 270, 780, 280);
        eventsInputTable.setRowHeight(40);
        this.add(eventsInputTable.getTableHeader());
        eventsInputTable.getTableHeader().setBackground(Color.LIGHT_GRAY);
        eventsInputTable.getTableHeader().setBounds(210, 230, 780, 40);
        this.add(eventsInputTable);

        // Combo box input
        teachersComboBox = new JComboBox<>(Teachers.instance.teachersNameList.toArray());
        teachersComboBox.insertItemAt("", 0); // To make sure user can clear
        eventsInputTable.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(teachersComboBox));

        venuesComboBox = new JComboBox<>(Venues.instance.venuesNameList.toArray());
        venuesComboBox.insertItemAt("", 0);
        eventsInputTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(venuesComboBox));

        // When table updates
        eventsInputTable.getTableHeader().setResizingAllowed(false);
        eventsInputTable.getTableHeader().setReorderingAllowed(false);
        eventsInputTable.getModel().addTableModelListener(listener -> {
            if(tableBeingModifiedByCode) return;
            if(selectedEvent == null) {
                JOptionPane.showMessageDialog(frame, "Please select an event to modify!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            for(GradeTimetable timetable : Timings.instance.timetablesList) {
                for(Event[] events : timetable.getEventsList()) {
                    for (Event event : events) {
                        if(event ==  selectedEvent) {
                            JOptionPane.showMessageDialog(frame, "You aren't allowed to edit an event if it's in a timetable.", "Event Editing Warning", JOptionPane.WARNING_MESSAGE);
                            this.resetTableValues(selectedEvent);
                            return;
                        }
                    }
                }
            }

            List<Teacher> teachers = new ArrayList<>();
            for(int i = 0;i < 7;i++) {
                String str = (String) eventsInputTable.getModel().getValueAt(i, 0);
                if(str == null || str.equals("")) continue;
                int index = Teachers.instance.teachersNameList.indexOf(str);
                if(index == -1) {
                    JOptionPane.showMessageDialog(frame, "Teacher "+str+" doesn't exist in the teacher list.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                teachers.add(Teachers.instance.teachersList.get(index));
            }
            selectedEvent.updateTeachers(teachers);

            List<Venue> venues = new ArrayList<>();
            for(int i = 0;i < 7;i++) {
                String str = (String) eventsInputTable.getModel().getValueAt(i, 1);
                if(str == null || str.equals("")) continue;
                int index = Venues.instance.venuesNameList.indexOf(str);
                if(index == -1) {
                    JOptionPane.showMessageDialog(frame, "Venue "+str+" doesn't exist in the venue list.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                venues.add(Venues.instance.venueList.get(index));
            }
            selectedEvent.updateVenues(venues);
        });

        this.listElement = new ListElement(frame, eventsNameList);
        listElement.initialise(new ListCallback() {
            @Override
            public void onValueSelected(String value, int index) {
                if(index == -1) {
                    tableBeingModifiedByCode = true;
                    selectedEvent = null;
                    selectClass.setSelectedIndex(-1);
                    displayName.setText("");
                    title.setText("Select an Event");
                    for(int i = 0;i < 2;i++) {
                        for(int j = 0;j < 7;j++) {
                            eventsInputTable.getModel().setValueAt(null, j, i);
                        }
                    }
                    tableBeingModifiedByCode = false;
                }else{
                    tableBeingModifiedByCode = true;
                    selectedEvent = eventList.get(index);
                    selectClass.setSelectedItem(selectedEvent.getClassName());
                    displayName.setText(selectedEvent.getDisplayName());
                    for(int i = 0;i < 2;i++) {
                        for(int j = 0;j < 7;j++) {
                            eventsInputTable.getModel().setValueAt(null, j, i);
                        }
                    }
                    int i = 0;
                    for(Teacher teacher : selectedEvent.teachers) {
                        eventsInputTable.getModel().setValueAt(teacher.getTeachersName(), i++, 0);
                    }
                    i = 0;
                    for (Venue venue : selectedEvent.venues) {
                        eventsInputTable.getModel().setValueAt(venue.getName(), i++, 1);
                    }
                    tableBeingModifiedByCode = false;
                    title.setText(value);
                }
                eventsInputTable.selectAll();
                eventsInputTable.clearSelection();
            }
            @Override
            public void onAddValue(String value) {
                Event event = new Event(value);
                event.setClassName((String) selectClass.getSelectedItem());
                eventList.add(event);
            }
            @Override
            public void onUpdateList(DefaultListModel<String> listModel) {
                frame.showFileNeedsToBeSaved();
                Collections.sort(eventsNameList, Comparator.naturalOrder());
                Collections.sort(eventList, Comparator.comparing(Event::getEventName));
                if(listModel != null) {
                    listModel.clear();
                    for(String val : eventsNameList) {
                        listModel.addElement(val);
                    }
                }
            }
            @Override
            public void onDeleteValue(int index) {
                eventList.remove(index);
            }
            @Override
            public void onCopyValue(String value, int index) {
                eventList.add(new Event(value, eventList.get(index)));
            }
            @Override
            public void onRename(int index, String value) {
                eventList.get(index).eventName = value;
            }
        });

        this.add(listElement);
    }
    public void saveData(FileOutputStream fileOutputStream) {
        PrintWriter writer = new PrintWriter(fileOutputStream);
        eventList.forEach(event -> {
            writer.print(event.eventName+","+event.getDisplayName()+","+event.getClassName()+",");
            int i = 0;
            for(Teacher teacher : event.teachers) {
                writer.print(teacher.getTeachersName()+(++i == event.teachers.size() ? "" : ";"));
            }
            writer.print(",");
            i = 0;
            for(Venue venue : event.venues) {
                writer.print(venue.getName()+(++i == event.venues.size() ? "" : ";"));
            }
            if(!event.equals(eventList.get(eventList.size() - 1))) writer.print("\n");
        });
        writer.close();
    }
    public void reset() {
        tableBeingModifiedByCode = true;

        // Only selecting all and then clearing selection seems to clear the selection.
        eventsInputTable.selectAll();
        eventsInputTable.getSelectionModel().clearSelection();

        teachersComboBox.removeAllItems();
        teachersComboBox.addItem("");
        Teachers.instance.teachersNameList.forEach(teacherName -> {
            teachersComboBox.addItem(teacherName);
        });

        venuesComboBox.removeAllItems();
        venuesComboBox.addItem("");
        Venues.instance.venuesNameList.forEach(venueName -> {
            venuesComboBox.addItem(venueName);
        });

        Object item = selectClass.getSelectedItem();
        selectClass.removeAllItems();
        Timings.instance.timetableNamesList.forEach(name -> {
            selectClass.addItem(name);
        });
        selectClass.setSelectedItem(item);
        tableBeingModifiedByCode = false;
    }
    private int selectedClassIndex;
}
