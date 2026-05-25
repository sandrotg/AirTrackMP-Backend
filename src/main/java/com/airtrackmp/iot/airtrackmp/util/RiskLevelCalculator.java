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

    public static boolean requiresAlert(float pm25, float pm10) {
        String level = fromPmValues(pm25, pm10);
        return "HIGH".equals(level) || "CRITICAL".equals(level);
    }

    public static String buildAlertMessage(float pm25, float pm10, String level) {
        return String.format(
                "Air quality alert [%s]: PM2.5=%.1f ug/m3, PM10=%.1f ug/m3 exceed safe thresholds",
                level,
                pm25,
                pm10
        );
    }
}
