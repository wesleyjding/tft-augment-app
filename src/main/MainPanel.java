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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

class MainPanel extends JPanel implements ActionListener, KeyListener {
    class AugmentWorker extends SwingWorker<Void, Integer> {

        private int total;
        private AugmentStatGenerator generator;
        private AugmentStatGeneratorObserver observer;
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
        /*
        @Override
        protected void process(ArrayList<Integer> chunks) {
            for (int number : chunks) {
                textArea.append(number + "\n");
            }
        }*/
        @Override
        public void done() {
            generator.closeDriver();
        }
    }

    private SwingWorker<Void, Integer> worker;
    private final JProgressBar progressBar = new JProgressBar(0, 100);
    private final int x;
    private final int y;
    Timer timer=new Timer(800, this); //TODO: timer back to 1000
    private final ArrayList<String> curAugments = new ArrayList<String>(Arrays.asList("", "", ""));
    private final ArrayList<String> curAugmentStats = new ArrayList<String>(Arrays.asList("", "", ""));
    private final ArrayList<String> firstStages = new ArrayList<String>(Arrays.asList("1-1", "1-2", "1-3", "1-4"));
    private final ArrayList<String> stages = new ArrayList<String>(Arrays.asList("2-1a", "2-1b", "2-2", "2-3", "2-4", "2-5", "2-6", "2-7",
            "3-1", "3-2a", "3-2b", "3-3", "3-4", "3-5", "3-6", "3-7",
            "4-1", "4-2a", "4-2b"));

    private AugmentStatGenerator asg = null;
    private final AugmentStatGeneratorObserver asgo = new AugmentStatGeneratorObserver();
    private boolean begunBuilding = false;
    private final StageReader stageReader;

    public MainPanel() {
        timer.start();
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        stageReader = new StageReader(this);
        x = size.width;
        y = size.height;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        if (ev.getSource() == timer) {
            repaint();
        }
    }
    private void drawString(Graphics g, String text, int x, int y) {
        for (String line : text.split("\n"))
            g.drawString(line, x, y += g.getFontMetrics().getHeight());
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

    private void drawAugments(Graphics g) {
        drawString(g, curAugments.get(0), 100, 180);
        drawString(g, curAugments.get(1), 500, 180);
        drawString(g, curAugments.get(2), 900, 180);
        drawString(g, curAugmentStats.get(0), 100, 280);
        drawString(g, curAugmentStats.get(1), 500, 280);
        drawString(g, curAugmentStats.get(2), 900, 280);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBackground(g);

        g.setFont(new Font(g.getFont().getFontName(), Font.PLAIN, g.getFont().getSize() * 3));
        g.setColor(new Color(0, 0, 0));

        // TODO: figure out how to close window while invokeLater() is running
        if(!begunBuilding) {
            g.drawString("Building Cache: ", 200, 200);
            asg = new AugmentStatGenerator();
            worker = new AugmentWorker(290, asg, asgo);
            add(progressBar);
            worker.addPropertyChangeListener(
                    new PropertyChangeListener() {
                        public void propertyChange(PropertyChangeEvent evt) {
                            if ("progress".equals(evt.getPropertyName())) {
                                progressBar.setValue((Integer) evt.getNewValue());
                            }
                        }
                    });
            worker.execute();
            begunBuilding = true;
        }
        if (asgo.getCacheProgress() < 290) {
            g.drawString("Building Cache: ", 200, 200);
            System.out.println(asgo.getCacheProgress());
            return;
        }
        remove(progressBar);
        boolean processFound = false;

        // https://stackoverflow.com/questions/35129457/how-to-check-if-a-process-is-running-on-windows TODO: fix this
        String findProcess = "League of Legends.exe";
        String filenameFilter = "/nh /fi \"Imagename eq " + findProcess + "\"";
        String tasksCmd = System.getenv("windir") + "/system32/tasklist.exe " + filenameFilter;
        try {
            Process p = Runtime.getRuntime().exec(tasksCmd);
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));

            ArrayList<String> procs = new ArrayList<String>();
            String line = null;
            while ((line = input.readLine()) != null)
                procs.add(line);

            input.close();

            processFound = procs.stream().anyMatch(row -> row.contains(findProcess));
            if (processFound) {
                System.out.println("Process found");
            } else {
                //System.out.println("Process not found");
            }
            // Head-up! If no processes were found - we still get:
            // "INFO: No tasks are running which match the specified criteria."
        } catch (IOException e) {
            System.out.println(e);
        }


        if (processFound || true) { // TODO: take out true
            //BufferedImage screenImage = stageReader.getScreenStageImage();
            //BufferedImage firstScreenImage = stageReader.getScreenFirstStageImage();
            //g.drawImage(screenImage, 10, 10, this);
            //g.drawImage(firstScreenImage, 80, 10, this);

            String s = stageReader.getStageNumber();
            g.drawString("Stage: " + s, 50, 50);

            if (s.equals("2-1a") || s.equals("3-2a") || s.equals("4-2a")) {
                g.drawImage(stageReader.getScreenFirstAugmentImage(), 10, 80, this);
                g.drawImage(stageReader.getScreenSecondAugmentImage(), 410, 80, this);
                g.drawImage(stageReader.getScreenThirdAugmentImage(), 810, 80, this);

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
                drawAugments(g);

            } else if (s.equals("2-1b") || s.equals("3-2b") || s.equals("4-2b")) {
                drawAugments(g);
            } else if (stages.contains(s) || firstStages.contains(s)) {
                g.drawString("Not Augment Round", 100, 80);
            } else {
                drawAugments(g);
            }
        } else {
            g.drawString("League of Legends not open", 200, 50);
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