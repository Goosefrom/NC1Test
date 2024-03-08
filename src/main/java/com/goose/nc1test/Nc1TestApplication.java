package com.goose.nc1test;

import com.goose.nc1test.config.FXInitializer;
import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Nc1TestApplication {

    public static void main(String[] args) {
        Application.launch(FXInitializer.class, args);
    }

}
