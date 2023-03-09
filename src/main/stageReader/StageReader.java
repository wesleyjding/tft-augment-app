package main.stageReader;

import main.TFTApp;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class StageReader {
    ImageObserver observer;
    private final ArrayList<String> firstStages = new ArrayList<>(Arrays.asList("1-1", "1-2", "1-3", "1-4"));
    private final ArrayList<String> stages = new ArrayList<>(Arrays.asList("2-1a", "2-1b", "2-2", "2-3", "2-4", "2-5", "2-6", "2-7",
            "3-1", "3-2a", "3-2b", "3-3", "3-4", "3-5", "3-6", "3-7",
            "4-1", "4-2a", "4-2b"));

    private final int x;
    private final int y;

    public StageReader(ImageObserver observer) {
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        x = size.width;
        y = size.height;
        this.observer = observer;
    }

    // https://stackoverflow.com/questions/11006394/is-there-a-simple-way-to-compare-bufferedimage-instances
    /**
     * Compares two images pixel by pixel.
     *
     * @param imgA the first image.
     * @param imgB the second image.
     * @return whether the images are both the same or not.
     */
    private static boolean compareImages(BufferedImage imgA, BufferedImage imgB) {
        // The images must be the same size.
        if (imgA.getWidth() != imgB.getWidth() || imgA.getHeight() != imgB.getHeight()) {
            return false;
        }

        int width  = imgA.getWidth();
        int height = imgA.getHeight();

        // Loop over every pixel.
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Compare the pixels for equality.
                if (imgA.getRGB(x, y) != imgB.getRGB(x, y)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * @return cropped image of screen containing the stage number (only for stage 1)
     */
    public BufferedImage getScreenFirstStageImage() {
        Robot screen;
        try {
            screen = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
        return screen.createScreenCapture(new Rectangle(43 * x / 100, 0, x / 30, 3 * y / 80));
    }

    /**
     * @return cropped image of screen containing the stage number (only for stage 2+)
     */
    public BufferedImage getScreenStageImage() {
        Robot screen;
        try {
            screen = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
        BufferedImage image = screen.createScreenCapture(new Rectangle(0, 0, x, y));
        image = image.getSubimage(4 * x / 10, 0, x / 30, 3 * y / 80);
        return image;
    }

    /**
     * @return cropped image of screen containing the first augment selection
     */
    public BufferedImage getScreenFirstAugmentImage() {
        Robot screen;
        try {
            screen = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
        BufferedImage image = screen.createScreenCapture(new Rectangle(0, 0, x, y));
        image = image.getSubimage(216 * x / 1000, 245 * y / 500, x/7, y/20);
        return image;

    }

    /**
     * @return cropped image of screen containing the second augment selection
     */
    public BufferedImage getScreenSecondAugmentImage() {
        Robot screen;
        try {
            screen = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
        BufferedImage image = screen.createScreenCapture(new Rectangle(0, 0, x, y));
        image = image.getSubimage(429 * x / 1000, 245 * y / 500, x/7, y/20);
        return image;
    }

    /**
     * @return cropped image of screen containing the third augment selection
     */
    public BufferedImage getScreenThirdAugmentImage() {
        Robot screen;
        try {
            screen = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
        BufferedImage image = screen.createScreenCapture(new Rectangle(0, 0, x, y));
        image = image.getSubimage(642 * x / 1000, 245 * y / 500, x/7, y/20);
        return image;
    }

    /**
     * @param stageNum TFT stage number for comparison
     * @return Correct comparison image corresponding to the stage number
     */
    private BufferedImage getCompareImage(String stageNum) {
        BufferedImage image = null;
        try {
            InputStream in = TFTApp.class.getResourceAsStream("/main/stageReader/Reference Stage Numbers/" + stageNum + ".png");
            assert in != null;
            image = ImageIO.read(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }

    /**
     * @return Current TFT stage number
     * Note: TesseractOCR cannot be used for this because this needs to recognize when augments are onscreen versus when
     * they are collapsed
     */
    public String getStageNumber() {
        for (String stageNum:firstStages) {
            if(compareImages(getScreenFirstStageImage(), getCompareImage(stageNum))) {
                return stageNum;
            }
        }
        for (String stageNum:stages) {
            if(compareImages(getScreenStageImage(), getCompareImage(stageNum))) {
                return stageNum;
            }
        }

        return "Other"; //TODO: Ideally could recognize 4-3+ versus not focused
    }
}
