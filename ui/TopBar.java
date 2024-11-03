package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class TopBar extends JComponent {
    private boolean dragging;
    private final int RECTX = 0;
    private final int RECTY = 0;
    private final int RECTWIDTH = 1000;
    private final int RECTHEIGHT = 40;
    private static Image iconImage;
    private int offsetX,offsetY;
    private JFrame window;
    private int screenX = (int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth());
    private int screenY = (int)(Toolkit.getDefaultToolkit().getScreenSize().getHeight());
    public static void setIconImage(Image iconImage) {
        TopBar.iconImage = iconImage;
    }
    public TopBar(JFrame frame) {
        super();
        this.setBounds(0, 0, RECTWIDTH, RECTHEIGHT);
        // Initialise the variables
        this.window = frame;
        this.dragging = false;
        this.setupListeners();
    }
    private void setupListeners() {
        // Code to implement window dragging
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(e.getX() >= RECTWIDTH - RECTHEIGHT && e.getX() <= RECTX+RECTWIDTH && e.getY() >= RECTY && e.getY() <= RECTY+RECTHEIGHT) {
                    // If the close button is pressed
                    window.dispose();
                } else if(e.getX() >= (RECTWIDTH - (2*RECTHEIGHT)) && e.getX() <= RECTWIDTH - RECTHEIGHT && e.getY() <= RECTY+RECTHEIGHT) {
                    // If window is minimized
                    window.setState(Frame.ICONIFIED);
                } else if (e.getX() >= RECTX && e.getX() <= RECTX + RECTWIDTH && e.getY() >= RECTY && e.getY() <= RECTY + RECTHEIGHT) {
                    // If click is in the rectangle start drag
                    offsetX = e.getX() - RECTX;
                    offsetY = e.getY() - RECTY;
                    dragging = true;
                }
            }

            // When the user stops clicking, stop dragging the window
            @Override
            public void mouseReleased(MouseEvent e) {
                dragging = false;
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragging) {
                    int windowX = window.getX();
                    int windowY = window.getY();
                    int newX = windowX + e.getX() - offsetX;
                    int newY = windowY + e.getY() - offsetY;
                    // Make sure the window is within the screen
                    newX = Math.max(0, newX);
                    newY = Math.max(0, newY);
                    newX = Math.min(screenX-windowX-window.getWidth()/2+60, newX);
                    newY = Math.min(screenY-windowY-window.getHeight()/2, newY);

                    if(!(newX >= (screenX - windowX - window.getWidth()/2+60) || newY >= screenY - windowY - window.getHeight()/2)) {
                        window.setLocation(newX, newY);
                    }
                    repaint();
                }
            }
        });
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw top bar
        g.setColor(ColorCodes.TOP_BAR);
        g.fillRect(RECTX, RECTY, RECTWIDTH, RECTHEIGHT);

        g.drawImage(this.iconImage, RECTX+14, RECTY+6, 24, 24, null);

        // Draw close button, title and minimize button
        g.setColor(Color.RED);
        g.fillRect(RECTWIDTH+RECTX - RECTHEIGHT, RECTY, RECTHEIGHT, RECTHEIGHT);
        g.setColor(Color.BLACK);
        Font font = new Font("Courier New", Font.BOLD, 24);
        g.setFont(font);
        g.drawString(window.getTitle(), RECTX+50, RECTY+26);
        g.drawString("X", RECTWIDTH+RECTX - RECTHEIGHT+12, RECTY+26);
        g.fillRect(RECTWIDTH+RECTX - (2*RECTHEIGHT), RECTY, RECTHEIGHT, RECTHEIGHT);
        g.setColor(Color.WHITE);
        g.drawString("_", RECTWIDTH+RECTX - (2*RECTHEIGHT)+12, RECTY+13);
    }

}
