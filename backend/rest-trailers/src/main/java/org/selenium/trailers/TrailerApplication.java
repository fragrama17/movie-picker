package org.selenium.trailers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TrailerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrailerApplication.class, args);
        System.setProperty("webdriver.chrome.driver", "C:\\Selenium\\chromedriver.exe");
    }

}
