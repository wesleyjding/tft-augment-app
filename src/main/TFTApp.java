package main;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class TFTApp {

    public TFTApp() {
        initComponents();
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TFTApp app = new TFTApp();
            }
        });


    }

    private void initComponents() {
        JFrame jFrame = new JFrame();
        MainPanel mainPanel = new MainPanel();

        mainPanel.addKeyListener(mainPanel);
        mainPanel.setFocusable(true);
        jFrame.add(mainPanel);
        jFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                mainPanel.onClose();
                jFrame.setVisible(false);
                jFrame.dispose();
            }
        });
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ImageIcon img = new ImageIcon("src/main/icon image.png");
        jFrame.setIconImage(img.getImage());
        jFrame.setTitle("TFT Project");
        jFrame.pack();
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);
    }
}