package augmentStatGenerator;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashMap;

public class AugmentStatGenerator {
    private HashMap<String, String> stats;
    public AugmentStatGenerator() {
        stats = new HashMap<>(300); //TODO: adaptive size
        initializeFiles();
    }

    public String getAugmentStats(String name) {
        if (stats.get(name) != null) {
            return stats.get(name);
        }
        return "ERROR";

    }
    public void initializeFiles() {
        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);

        System.out.println("Clearing Cache");
        stats.clear();
        System.out.println("Building Cache");

        var options = new ChromeOptions();
        options.addArguments("--headless",  "--window-size=1920,1080", "--silent", "--ignore-certificate-errors");

        WebDriver driver = new ChromeDriver(options); // Maybe HtmlUnitDriver for headless?

        driver.get("https://tactics.tools/augments");


        WebElement button = new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(driver1 -> driver1.findElement(By.cssSelector(".css-11tbvfe > div:nth-child(1)")));
        button.click();

        for (int i = 1; i <= 290; i++) { //TODO: adaptive size, take out [champ names]
            int finalI = i;
            WebElement augment = new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(driver1 -> driver1.findElement(By.cssSelector(".z-10 > div:nth-child(" + finalI + ") > div:nth-child(1)")));
            WebElement augmentStats = new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(driver1 -> driver1.findElement(By.cssSelector("#tbl-body > div:nth-child(1) > div:nth-child(" + finalI + ")")));

            // Parsing string and removing [champ names]
            String temp = augment.getText();
            if(temp.charAt(0) == '[') {
                temp = temp.substring(temp.indexOf(']') + 2);
            }


            stats.put(temp, augmentStats.getText());
        }
        System.out.println("Finished Building Cache");

        driver.quit();
    }

}