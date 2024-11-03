package ui.Timetable.Elements;

import ui.ColorCodes;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

public class ListElement extends JPanel {
    private JFrame frame;
    public List<String> stringsList;
    public JList<String> listElement;
    public DefaultListModel<String> modelList;
    private ListCallback callback;
    private int WIDTH = 200;
    public static ListElement NewWithWidth(JFrame frame, List<String> stringsList, int width) {
        ListElement element = new ListElement(frame, stringsList);
        element.WIDTH = width;
        element.setBounds(0, 100, width, 500);
        return element;
    }
    public ListElement(JFrame frame, List<String> stringsList) {
        // Initialise
        super();
        this.setLayout(null);
        this.setBounds(0, 100, WIDTH, 500);
        this.frame = frame;
        this.stringsList = stringsList;
        this.modelList = new DefaultListModel<>();
        this.listElement = new JList<>(modelList);
        frame.add(this);
    }
    public void initialise(ListCallback callback) {
        this.callback = callback;

        // Input
        JTextField input = new JTextField();
        input.setBounds(0, 0, (int) (WIDTH-(WIDTH*0.3)), 20);
        JButton enter = new JButton();
        enter.setBounds((int) (WIDTH-(WIDTH*0.3)), 0, (int)(WIDTH*0.3), 20);
        enter.setText("New");
        enter.setBackground(Color.WHITE);
        input.addActionListener(event -> {
            addTimetable(input);
        });

        // Code for searching
        input.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {}
            @Override
            public void keyReleased(KeyEvent e) {
                // When new letter typed
                String searchText = input.getText();
                modelList.clear();
                for(String str : stringsList) {
                    if(str.toUpperCase().startsWith(searchText.toUpperCase())) {
                        modelList.addElement(str);
                    }
                }
            }
        });
        enter.addActionListener(event -> {
            addTimetable(input);
        });

        // Timings Name Controls
        JButton rename = new JButton("Rename");
        rename.setBackground(ColorCodes.BUTTON_LIGHT);
        rename.setBounds(0, 440, WIDTH, 20);
        this.add(rename);

        JButton copy = new JButton("Duplicate");
        copy.setBackground(ColorCodes.BUTTON_LIGHT);
        copy.setBounds(0, 460, WIDTH, 20);
        this.add(copy);

        JButton delete = new JButton("Delete");
        delete.setBackground(Color.RED);
        delete.setBounds(0, 480, WIDTH, 20);
        this.add(delete);

        // List for timetables
        JList<String> timetablesListElement = new JList<>(modelList);
        callback.onUpdateList(modelList);
        timetablesListElement.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane pane = new JScrollPane(timetablesListElement);
        pane.setBounds(0, 20, WIDTH, 420);
        this.add(pane);
        this.add(input);
        this.add(enter);

        delete.addActionListener(listener -> {
            if(timetablesListElement.getSelectedIndex() != -1 && JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete this?") == 0) {
                stringsList.remove(timetablesListElement.getSelectedIndex());
                callback.onDeleteValue(timetablesListElement.getSelectedIndex());
            }
            callback.onUpdateList(modelList);
        });
        rename.addActionListener(listener -> {
            if(timetablesListElement.getSelectedIndex() != -1) {
                String name = JOptionPane.showInputDialog("Rename to", modelList.get(timetablesListElement.getSelectedIndex()));
                if(name == null || name.equals("")) return;
                for(String val : stringsList) {
                    if(val.equals(name)) {
                        JOptionPane.showMessageDialog(frame, "There is already an object by this name!", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                callback.onRename(timetablesListElement.getSelectedIndex(), name);
                stringsList.add(stringsList.indexOf(timetablesListElement.getSelectedValue()), name);
                stringsList.remove(timetablesListElement.getSelectedValue());
                callback.onUpdateList(modelList);
            }
        });
        copy.addActionListener(listener -> {
            String name = JOptionPane.showInputDialog("Copy as", modelList.get(timetablesListElement.getSelectedIndex()));
            if(name == null || name.equals("")) return;
            for(String val : stringsList) {
                if(val.equals(name)) {
                    JOptionPane.showMessageDialog(frame, "There is already an object by this name!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            stringsList.add(name);
            callback.onCopyValue(name, timetablesListElement.getSelectedIndex());
            callback.onUpdateList(modelList);
        });
        timetablesListElement.addListSelectionListener(listener -> {
            callback.onValueSelected(timetablesListElement.getSelectedValue(), timetablesListElement.getSelectedIndex());
        });
    }
    private void addTimetable(JTextField input) {
        if(input.getText().equals("")) {
            JOptionPane.showMessageDialog(frame, "You haven't entered anything!");
            return;
        }
        for(String val : stringsList) {
            if(val.equals(input.getText())) {
                JOptionPane.showMessageDialog(frame, "There is already an object by this name!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        stringsList.add(input.getText());
        callback.onAddValue(input.getText());
        callback.onUpdateList(modelList);
        input.setText("");
    }
    public void select(String value, int index) {
        callback.onValueSelected(value, index);
    }
}
