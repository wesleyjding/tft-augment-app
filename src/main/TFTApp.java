package main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.InputStream;

// modified from https://docs.oracle.com/javase/7/docs/api/javax/swing/JFrame.html
public class TFTApp {

    public TFTApp() {
        initComponents();
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(TFTApp::new);
    }

    private void initComponents() {
        JFrame jFrame = new JFrame();
        MainPanel mainPanel = new MainPanel();

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
        ImageIcon img = null;
        try {
            InputStream in = TFTApp.class.getResourceAsStream("/main/icon image.png");
            assert in != null;
            BufferedImage image = ImageIO.read(in);
            img = new ImageIcon(image);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        jFrame.setIconImage(img.getImage());
        jFrame.setResizable(false);
        jFrame.setTitle("TFT Project");
        jFrame.pack();
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);
    }
}