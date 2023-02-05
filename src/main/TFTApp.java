package main;

import javax.swing.*;
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
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainPanel.addKeyListener(mainPanel);
        mainPanel.setFocusable(true);
        jFrame.add(mainPanel);
        ImageIcon img = new ImageIcon("src/icon image.png");
        jFrame.setIconImage(img.getImage());
        jFrame.setTitle("TFT Project");
        jFrame.pack();
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);
    }
}