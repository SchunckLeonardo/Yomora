package com.yomora.reading.domain;

public record ReadingGoal(int dailyMinutes, int weeklyDays, Integer dailyPages) {
    public ReadingGoal {
        if (dailyMinutes < 1 || weeklyDays < 1 || weeklyDays > 7 || dailyPages != null && dailyPages < 1) {
            throw new IllegalArgumentException("Meta de leitura inválida");
        }
    }
}
