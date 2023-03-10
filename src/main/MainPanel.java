package main;

import main.augmentStatGenerator.AugmentStatGenerator;
import main.augmentStatGenerator.AugmentStatGeneratorObserver;
import main.stageReader.StageReader;
import main.tesseractOCR.TesseractOCR;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

class MainPanel extends JPanel implements ActionListener, KeyListener {
    static class AugmentWorker extends SwingWorker<Void, Integer> {
        //TODO: improve GUI
        private final int total;
        private final AugmentStatGenerator generator;
        private final AugmentStatGeneratorObserver observer;
        AugmentWorker(int total, AugmentStatGenerator generator, AugmentStatGeneratorObserver observer) {
            this.total = total;
            this.generator = generator;
            this.observer = observer;
        }
        @Override
        public Void doInBackground() {
            while (!isCancelled()) {
                generator.initializeFile(observer.getCacheProgress() + 1, observer);
                int number = observer.getCacheProgress();
                publish(number);
                setProgress(100 * number / total);
            }
            return null;
        }
        @Override
        public void done() {
            generator.closeDriver();
        }
    }

    private SwingWorker<Void, Integer> worker;
    private final JProgressBar progressBar = new JProgressBar(0, 100);
    private final Font font = new Font("Serif", Font.PLAIN, 20);
    private final int x;
    private final int y;
    Timer timer=new Timer(800, this);
    private final HashMap<String, JLabel> textLabels = new HashMap<>();
    private final ArrayList<String> curAugments = new ArrayList<>(Arrays.asList("", "", ""));
    private final ArrayList<String> curAugmentStats = new ArrayList<>(Arrays.asList("", "", ""));
    private final ArrayList<String> firstStages = new ArrayList<>(Arrays.asList("1-1", "1-2", "1-3", "1-4"));
    private final ArrayList<String> stages = new ArrayList<>(Arrays.asList("2-1a", "2-1b", "2-2", "2-3", "2-4", "2-5", "2-6", "2-7",
            "3-1", "3-2a", "3-2b", "3-3", "3-4", "3-5", "3-6", "3-7",
            "4-1", "4-2a", "4-2b"));

    private AugmentStatGenerator asg = null;
    private final AugmentStatGeneratorObserver asgo = new AugmentStatGeneratorObserver();
    private boolean begunBuilding = false;
    private boolean finishedBuilding = false;
    private final StageReader stageReader;

    public MainPanel() {
        timer.start();
        Dimension frameSize = Toolkit.getDefaultToolkit().getScreenSize();
        stageReader = new StageReader(this);
        x = frameSize.width;
        y = frameSize.height;
        setLayout(null);
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        if (ev.getSource() == timer) {
            repaint();
        }
    }
    private void drawBackground(Graphics g) {
        g.setColor(new Color(255, 255, 255));
        g.drawRect(0, 0, x, y);
    }

    /**
     * @return Name of first augment on screen
     */
    private String getFirstAugmentName() {
        return TesseractOCR.readImage(stageReader.getScreenFirstAugmentImage()).replace("\n", "");
    }

    /**
     * @return Name of second augment on screen
     */
    private String getSecondAugmentName() {
        return TesseractOCR.readImage(stageReader.getScreenSecondAugmentImage()).replace("\n", "");
    }

    /**
     * @return Name of third augment on screen
     */
    private String getThirdAugmentName() {
        return TesseractOCR.readImage(stageReader.getScreenThirdAugmentImage()).replace("\n", "");
    }

    private void writeAugments() {
        Dimension frameSize = this.getSize();
        //TODO: change size and formatting
        JLabel augLabel1 = textLabels.get("Aug1");
        String text = curAugments.get(0) + "\n" + curAugmentStats.get(0);
        if(augLabel1 == null) {
            augLabel1 = new JLabel("<html>" + text.replaceAll("\n", "<br/>") + "</html>");
            augLabel1.setFont(font);
            Dimension size = augLabel1.getPreferredSize();
            add(augLabel1);
            augLabel1.setBounds(frameSize.width/6 - size.width/2, frameSize.height/2, size.width, size.height);
            augLabel1.setHorizontalAlignment(SwingConstants.CENTER);
            textLabels.put("Aug1", augLabel1);
        }
        else {
            augLabel1.setText("<html>" + text.replaceAll("\n", "<br/>") + "</html>");
            Dimension size = augLabel1.getPreferredSize();
            augLabel1.setBounds(frameSize.width/6 - size.width/2, frameSize.height/2, size.width, size.height);
        }

        JLabel augLabel2 = textLabels.get("Aug2");
        text = curAugments.get(1) + "\n" + curAugmentStats.get(1);
        if(augLabel2 == null) {
            augLabel2 = new JLabel("<html>" + text.replaceAll("\n", "<br/>") + "</html>");
            augLabel2.setFont(font);
            Dimension size = augLabel2.getPreferredSize();
            add(augLabel2);
            augLabel2.setBounds(frameSize.width/2 - size.width/2, frameSize.height/2, size.width, size.height);
            augLabel2.setHorizontalAlignment(SwingConstants.CENTER);
            textLabels.put("Aug2", augLabel2);
        }
        else {
            augLabel2.setText("<html>" + text.replaceAll("\n", "<br/>") + "</html>");
            Dimension size = augLabel2.getPreferredSize();
            augLabel2.setBounds(frameSize.width/2 - size.width/2, frameSize.height/2, size.width, size.height);
        }

        JLabel augLabel3 = textLabels.get("Aug3");
        text = curAugments.get(2) + "\n" + curAugmentStats.get(2);
        if(augLabel3 == null) {
            augLabel3 = new JLabel("<html>" + text.replaceAll("\n", "<br/>") + "</html>");
            augLabel3.setFont(font);
            Dimension size = augLabel3.getPreferredSize();
            add(augLabel3);
            augLabel3.setBounds(5 * frameSize.width/6 - size.width/2, frameSize.height/2, size.width, size.height);
            augLabel3.setHorizontalAlignment(SwingConstants.CENTER);
            textLabels.put("Aug3", augLabel3);
        }
        else {
            augLabel3.setText("<html>" + text.replaceAll("\n", "<br/>") + "</html>");
            Dimension size = augLabel3.getPreferredSize();
            augLabel3.setBounds(5 * frameSize.width/6 - size.width/2, frameSize.height/2, size.width, size.height);
        }
        if(!augLabel1.isVisible()) {
            augLabel1.setVisible(true);
            augLabel2.setVisible(true);
            augLabel3.setVisible(true);
        }
    }

    private void hideAugments() {
        JLabel augLabel1 = textLabels.get("Aug1");
        if(augLabel1 != null) {
            augLabel1.setVisible(false);
        }
        JLabel augLabel2 = textLabels.get("Aug2");
        if(augLabel2 != null) {
            augLabel2.setVisible(false);
        }

        JLabel augLabel3 = textLabels.get("Aug3");
        if(augLabel3 != null) {
            augLabel3.setVisible(false);
        }


    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBackground(g);

        if(!begunBuilding) {
            begunBuilding = true;
            Dimension frameSize = this.getSize();

            asg = new AugmentStatGenerator();
            worker = new AugmentWorker(asg.getTotalAugments(), asg, asgo);

            Dimension PBSize = progressBar.getPreferredSize();
            progressBar.setStringPainted(true);
            add(progressBar);
            progressBar.setBounds(frameSize.width/2 - PBSize.width/2, frameSize.height/2 - PBSize.height/2, PBSize.width, PBSize.height);

            JLabel label = new JLabel("Building Cache");
            Dimension size = label.getPreferredSize();
            add(label);
            label.setBounds(frameSize.width/2 - size.width/2, frameSize.height/2 - size.height/2 - PBSize.height, size.width, size.height);
            textLabels.put("Building Cache", label);

            worker.addPropertyChangeListener(
                    evt -> {
                        if ("progress".equals(evt.getPropertyName())) {
                            progressBar.setValue((Integer) evt.getNewValue());
                        }
                    });
            worker.execute();
        }
        if (asgo.getCacheProgress() < asg.getTotalAugments()) {
            //System.out.println(asgo.getCacheProgress());
            return;
        }
        if(!finishedBuilding) {
            remove(progressBar);
            remove(textLabels.get("Building Cache"));
            textLabels.remove("Building Cache");
            finishedBuilding = true;
        }


        String s = stageReader.getStageNumber();
        JLabel stageLabel = textLabels.get("Stage Number");
        if(stageLabel == null) {
            Dimension frameSize = this.getSize();
            stageLabel = new JLabel("Stage: " + s);
            Dimension size = stageLabel.getPreferredSize();
            add(stageLabel);
            stageLabel.setBounds(frameSize.width/20, frameSize.height/20, size.width, size.height);
            textLabels.put("Stage Number", stageLabel);
        }
        else {
            stageLabel.setText("Stage: " + s);
        }


        if (s.equals("2-1a") || s.equals("3-2a") || s.equals("4-2a")) {

            String firstAugment = getFirstAugmentName();
            if (!curAugments.get(0).equals(firstAugment) && !asg.getAugmentStats(firstAugment).equals("ERROR")) {
                curAugments.set(0, firstAugment);
                curAugmentStats.set(0, asg.getAugmentStats(firstAugment));
            }
            String secondAugment = getSecondAugmentName();
            if (!curAugments.get(1).equals(secondAugment) && !asg.getAugmentStats(secondAugment).equals("ERROR")) {
                curAugments.set(1, secondAugment);
                curAugmentStats.set(1, asg.getAugmentStats(secondAugment));
            }
            String thirdAugment = getThirdAugmentName();
            if (!curAugments.get(2).equals(thirdAugment) && !asg.getAugmentStats(thirdAugment).equals("ERROR")) {
                curAugments.set(2, thirdAugment);
                curAugmentStats.set(2, asg.getAugmentStats(thirdAugment));
            }
            writeAugments();

        } else if (stages.contains(s) || firstStages.contains(s)) {
            hideAugments();
        } 
    }

    public void onClose() {
        System.out.println("Successfully closed");
        worker.cancel(true);
        worker = null;
    }
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(1280, 720);
    }
    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}