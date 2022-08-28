package org.selenium.trailers;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrailerService {

    public String getTrailerByTitle(String title) {
//        ChromeOptions chromeOptions = new ChromeOptions(); //headless doesn't work correctly
//        chromeOptions.addArguments("--headless");
        ChromeDriver chromeDriver = new ChromeDriver();
        try {

            chromeDriver.get("https://www.youtube.com/results?sp=mAEB&search_query=" + title); //with "trailer" doesn't work, mah !!

            List<WebElement> elements = chromeDriver.findElements(By.className("yt-simple-endpoint"));
//          F****N' COOKIES !!!
            for (int i = elements.size() - 5; i < elements.size(); i++) {
                WebElement currentElement = elements.get(i);
                if (currentElement.isDisplayed() && currentElement.isEnabled() &&
                        currentElement.getText().contains("RIFIUTA TUTTO")) //I'm from italy :)
                    currentElement.click();
            }

            chromeDriver.findElement(By.id("contents")).findElements(By.id("dismissible")).get(0).click();

            String trailerUrl = chromeDriver.getCurrentUrl();
            chromeDriver.quit();
            return trailerUrl;
        } catch (Exception e) {
            chromeDriver.quit();
            throw e;
        }
    }

}
