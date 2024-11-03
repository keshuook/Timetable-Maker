package ui.Home;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Color;

public class Button extends JButton {
    public Button(String text, int n) {
        super("<html><div style='font-size: 40px;text-align: center;'>"+(n == 0 ? "\u2795" : "\uD83D\uDCC1")+"</div><div style='margin-top: 100px;font-size: 16px;text-align: center;'>"+text+"</div></html>");
        this.setBounds(100+(n*500), 100, 300, 400);
        this.setFocusPainted(false);
        this.setBorder(new EmptyBorder(0, 0, 0, 0));
        this.setBackground(Color.WHITE);
    }
}
