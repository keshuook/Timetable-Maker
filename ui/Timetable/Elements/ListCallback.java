package ui.Timetable.Elements;

import javax.swing.*;

public interface ListCallback {
    public void onValueSelected(String value, int index);
    public void onAddValue(String value);
    public void onUpdateList(DefaultListModel<String> listModel);
    public void onDeleteValue(int index);
    public void onCopyValue(String value, int index);
    public void onRename(int index, String value);
}
