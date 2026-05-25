package com.airtrackmp.iot.airtrackmp.util;

public final class RiskLevelCalculator {

    private RiskLevelCalculator() {
    }

    public static String fromPmValues(float pm25, float pm10) {
        if (pm25 >= 55f || pm10 >= 150f) {
            return "CRITICAL";
        }
        if (pm25 >= 35f || pm10 >= 100f) {
            return "HIGH";
        }
        if (pm25 >= 12f || pm10 >= 50f) {
            return "MODERATE";
        }
        return "LOW";
    }
}
