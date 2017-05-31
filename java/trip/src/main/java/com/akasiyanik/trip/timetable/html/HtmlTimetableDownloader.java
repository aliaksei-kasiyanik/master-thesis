package com.akasiyanik.trip.timetable.html;

import com.akasiyanik.trip.timetable.MinskTransRouteEnum;
import com.akasiyanik.trip.utils.IOUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

/**
 * @author akasiyanik
 *         5/10/17
 */
@Service
public class HtmlTimetableDownloader {

    public HtmlTimetableDownloader() {
        System.setProperty("webdriver.chrome.driver", "/Users/akasiyanik/FPMI/diploma/java/trip/src/main/resources/selenium-driver/chromedriver");
    }

    public HtmlInfoData download(MinskTransRouteEnum routeEnum) {
        WebDriver driver = null;
        try {
            driver = new ChromeDriver();
            driver.get(routeEnum.getParseUrl());

            final String divId = "schedule_list";
            WebDriverWait wait = new WebDriverWait(driver, 15);
            WebElement div = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(divId)));

            HtmlInfoData htmlData = new HtmlInfoData();
            htmlData.setTimetableHtml(div.getAttribute("outerHTML"));

            driver.findElement(By.id("show_time_a")).click();
            driver.findElement(By.name("routenum")).click();

            wait.until(ExpectedConditions.presenceOfElementLocated(By.className("number_link")));

            WebElement stopDiv = driver.findElement(By.id("schedule_result"));
            htmlData.setStopsHtml(stopDiv.getAttribute("outerHTML"));

            return htmlData;
        } catch (Exception e) {
            throw new RuntimeException("Selenium can't load page", e);
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }
//
//    public void saveToFile(MinskTransRouteEnum routeEnum, String html) {
//        IOUtils.writeToFile(getHtmlFilePath(routeEnum), html);
//    }
//
//    private String getHtmlFilePath(MinskTransRouteEnum routeEnum) {
//        return "routes/" + routeEnum.getNumber() + ".html";
//    }
}
