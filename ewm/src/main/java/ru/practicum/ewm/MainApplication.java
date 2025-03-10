package ru.practicum.ewm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class MainApplication {
    private static final int N = 10;
    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);

        Runnable helloWorldRunnable = () -> {
            System.out.println("Hello, world!");
        };

        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            threads.add(new Thread(helloWorldRunnable));
        }
    }
}