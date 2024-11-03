import ui.Home.Home;
import ui.Timetable.Editor;
import ui.TopBar;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.ZipFile;

public class Main {
    public static void main(String[] args) throws IOException {
        ImageIcon imageIcon = new ImageIcon(Main.class.getResource("/icon.png"));
        TopBar.setIconImage(imageIcon.getImage());
        if(args.length == 0) {
            new Home(() -> {
                Editor editor = new Editor(Home.ZIP_FILE);
                editor.setIconImage(imageIcon.getImage());
            }).setIconImage(imageIcon.getImage());
        }else{
            try {
                ZipFile zipFile = new ZipFile(args[0]);
                Editor editor = new Editor(zipFile);
                editor.setIconImage(imageIcon.getImage());
            }catch (FileNotFoundException exception) {
                JOptionPane.showMessageDialog(null, "We couldn't open the file "+args[0]+". As the system couldn't find it.", "File Not Found Error!", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}