package main.tesseractOCR;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import net.sourceforge.tess4j.*;
import net.sourceforge.tess4j.util.LoadLibs;

import javax.imageio.ImageIO;

// Modified from https://tess4j.sourceforge.net/tutorial/
public class TesseractOCR {

    public static String readImage(BufferedImage image) {
        // System.setProperty("jna.library.path", "32".equals(System.getProperty("sun.arch.data.model")) ? "lib/win32-x86" : "lib/win32-x86-64");

        File imageFile = new File("temp.png");
        try {
            ImageIO.write(image, "png", imageFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        imageFile.deleteOnExit();
        //ITesseract instance = new Tesseract();  // JNA Interface Mapping
        ITesseract instance = new Tesseract1(); // JNA Direct Mapping
        File tessDataFolder = LoadLibs.extractTessResources("tessdata"); // Maven build bundles English data
        instance.setDatapath(tessDataFolder.getPath());


        try {
            return instance.doOCR(imageFile);
        } catch (TesseractException e) {
            System.out.println(e.getMessage());
            return "ERROR";
        }
    }
}