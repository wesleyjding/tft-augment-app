package main;

import graphql.parser.MultiSourceReader;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

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
        ImageIcon img = null;
        try {
            InputStream in = TFTApp.class.getResourceAsStream("/main/icon image.png");
            BufferedImage image = ImageIO.read(in);
            img = new ImageIcon(image);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        jFrame.setIconImage(img.getImage());
        jFrame.setTitle("TFT Project");
        jFrame.pack();
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);
    }
}