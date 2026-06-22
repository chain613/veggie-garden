package com.veggie.service;

import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class WeatherService {
    private String currentWeather = "晴天";

    public String getCurrentWeather() {
        return currentWeather;
    }

    public void generateWeather() {
        int month = LocalDate.now().getMonthValue();
        double r = ThreadLocalRandom.current().nextDouble();

        if (month >= 6 && month <= 8) {
            if (r < 0.45) currentWeather = "晴天";
            else if (r < 0.65) currentWeather = "阴天";
            else if (r < 0.85) currentWeather = "下雨";
            else currentWeather = "大风";
        } else if (month >= 3 && month <= 5) {
            if (r < 0.30) currentWeather = "晴天";
            else if (r < 0.50) currentWeather = "阴天";
            else if (r < 0.80) currentWeather = "下雨";
            else currentWeather = "大风";
        } else if (month >= 9 && month <= 11) {
            if (r < 0.55) currentWeather = "晴天";
            else if (r < 0.75) currentWeather = "阴天";
            else if (r < 0.90) currentWeather = "下雨";
            else currentWeather = "大风";
        } else {
            if (r < 0.40) currentWeather = "晴天";
            else if (r < 0.60) currentWeather = "阴天";
            else if (r < 0.75) currentWeather = "下雨";
            else currentWeather = "大风";
        }
    }
}
