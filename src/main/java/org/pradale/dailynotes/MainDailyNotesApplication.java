package org.pradale.dailynotes;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MainDailyNotesApplication {

    public static void main(String[] args) {
        Application.launch(JavafxApplication.class, args);
    }
}