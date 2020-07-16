package org.pradale.dailynotes;

import javafx.application.Application;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PreDestroy;

@Slf4j
@SpringBootApplication
public class MainDailyNotesApplication {

    public static void main(String[] args) {
        Application.launch(JavafxApplication.class, args);
    }

    @PreDestroy
    public void onExit() {
//        try {
//            Thread.sleep(5 * 1000);
//        } catch (InterruptedException e) {
//            log.error("", e);;
//        }
    }
}