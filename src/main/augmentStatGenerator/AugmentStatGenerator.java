package main.augmentStatGenerator;

import org.openqa.selenium.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;

// modified from https://www.selenium.dev/documentation/webdriver/getting_started/first_script/
public class AugmentStatGenerator {
    private final HashMap<String, String> stats;
    private final int totalAugments;
    private WebDriver driver;
    public AugmentStatGenerator() {
        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless",  "--window-size=1920,1080", "--silent", "--ignore-certificate-errors", "--remote-allow-origins=*");
        driver = new ChromeDriver(options); // Maybe HtmlUnitDriver for headless?
        driver.get("https://tactics.tools/augments");

        WebElement table = new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(driver1 -> driver1.findElement(By.cssSelector(".z-10")));
        // modified from https://stackoverflow.com/questions/50870939/selenium-get-the-div-count-inside-another-div
        ArrayList<WebElement> elements = (ArrayList<WebElement>) new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(driver1 -> table.findElements(By.xpath("//*[@class='sticky left-0 z-10 tbl-body-inner-md pl-[4px] css-3cf96d']/div")));
        totalAugments = elements.size();
        stats = new HashMap<>(totalAugments + 1);

        WebElement button = new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(driver1 -> driver1.findElement(By.cssSelector(".css-11tbvfe > div:nth-child(1)")));
        button.click();
    }

    public String getAugmentStats(String name) {
        if (stats.get(name) != null) {
            return stats.get(name);
        }
        return "ERROR";

    }
    public void closeDriver() {
        if(driver != null) {
            System.out.println("Closed Driver");
            driver.quit();
            driver = null;
        }
    }
    public void initializeFile(int row, AugmentStatGeneratorObserver observer) {
        WebElement augment = new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(driver1 -> driver1.findElement(By.cssSelector(".z-10 > div:nth-child(" + row + ") > div:nth-child(1)")));
        WebElement augmentStats = new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(driver1 -> driver1.findElement(By.cssSelector("#tbl-body > div:nth-child(1) > div:nth-child(" + row + ")")));
        // Parsing string and removing [champ names]
        String temp = augment.getText();
        if(temp.charAt(0) == '[') {
            temp = temp.substring(temp.indexOf(']') + 2);
        }
        stats.put(temp, augmentStats.getText());
        observer.setCacheProgress(row);
    }

    public int getTotalAugments() {
        return totalAugments;
    }
}