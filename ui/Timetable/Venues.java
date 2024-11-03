package ui.Timetable;

import objects.Teacher;
import objects.Venue;
import ui.Timetable.Elements.ListCallback;
import ui.Timetable.Elements.ListElement;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Venues extends JPanel {
    protected List<String> venuesNameList = new ArrayList<>();
    protected List<Venue> venueList = new ArrayList<>();
    protected static Venues instance;
    private ListElement listElement;
    private Editor frame;
    public Venues(Editor frame, InputStream fileInputStream) {
        super();
        instance = this;
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
        if(!reader.ready()) return;
        String[] names = reader.readLine().split(",");
        for (String name : names) {
            venueList.add(new Venue(name));
            venuesNameList.add(name);
        }
    }
    private void addComponents() {
        JLabel title = new JLabel("Select a Venue");
        title.setFont(new Font("Courier New", Font.BOLD, 24));
        title.setBounds(0, 65, 780, 20);
        this.add(title);

        this.listElement = ListElement.NewWithWidth(frame, venuesNameList, 1000);
        listElement.initialise(new ListCallback() {
            @Override
            public void onValueSelected(String value, int index) {
                if(index == -1) {
                    title.setText("Select a Venue");
                }else{
                    title.setText(value);
                }
            }
            @Override
            public void onAddValue(String value) {
                venueList.add(new Venue(value));
            }
            @Override
            public void onUpdateList(DefaultListModel<String> listModel) {
                frame.showFileNeedsToBeSaved();
                Collections.sort(venuesNameList, Comparator.naturalOrder());
                Collections.sort(venueList, Comparator.comparing(Venue::getName));
                if(listModel != null) {
                    listModel.clear();
                    for(String val : venuesNameList) {
                        listModel.addElement(val);
                    }
                }
            }
            @Override
            public void onDeleteValue(int index) {
                venueList.remove(index);
            }
            @Override
            public void onCopyValue(String value, int index) {
                venueList.add(new Venue(venuesNameList.get(venuesNameList.size()-1)));
            }
            @Override
            public void onRename(int index, String value) {
                venueList.get(index).setName(value);
            }
        });

        // Make full
        listElement.setBounds(0, 100, 1000, 500);

        this.add(listElement);
    }
    public void saveData(FileOutputStream stream) {
        PrintWriter writer = new PrintWriter(stream);
        venuesNameList.forEach(venue -> {
            if(venue.equals(venuesNameList.get(venuesNameList.size()-1))) {
                writer.println(venue);
            }else{
                writer.print(venue.concat(","));
            }
        });
        writer.close();
    }
}
