package fr.uparis.backapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Lanceur de l'application.
 */
@SpringBootApplication
public class MapMyWay {
    /**
     * Lance l'application.
     * @param args nécessaire pour lancer l'application.
     */
    public static void main(String[] args) {
        SpringApplication.run(MapMyWay.class, args);
    }
}