package io.hackerschool.hswatch_connection_module.connection_objects;

import androidx.annotation.NonNull;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HSWConnection {

    private static Map<String, IHSWNotificationFilter> notificationMapFilters = null;
    private static Map<String, IHSWProtocolSender> protocolMapSenders = null;
    private static String indicatorTime = "TIM";

    public HSWConnection() {
        if (notificationMapFilters == null) {
            notificationMapFilters = new HashMap<>();
        }
        if (protocolMapSenders == null) {
            protocolMapSenders = new HashMap<>();
            protocolMapSenders.put(indicatorTime, () -> {
                List<String> returningList = new ArrayList<>();
                String[] dateAndTime = Instant.now().toString().split("T");
                String[] date = dateAndTime[0].split("-");
                String[] time = dateAndTime[1].split(":");
                returningList.add(time[0]);
                returningList.add(date[1]);
                returningList.add(date[2].split(".")[0]);
                returningList.add(date[2]);
                returningList.add(date[1]);
                returningList.add(date[0]);
                returningList.add(
                        getDayOfTheWeekNumber(LocalDate.now().getDayOfWeek().toString())
                );
                return returningList;
            });
        }
    }

    @NonNull
    public String getDayOfTheWeekNumber(@NonNull String dayOfWeek) {
        switch (dayOfWeek) {
            case "MONDAY":
                return "2";
            case "TUESDAY":
                return "3";
            case "WEDNESDAY":
                return "4";
            case "THURSDAY":
                return "5";
            case "FRIDAY":
                return "6";
            case "SATURDAY":
                return "7";
            case "SUNDAY":
            default:
                return "1";
        }
    }

    public Map<String, IHSWNotificationFilter> getNotificationFilters() {
        return notificationMapFilters;
    }

    public Map<String, IHSWProtocolSender> getProtocolMapSenders() {
        return protocolMapSenders;
    }

    public void addCallbackNotification(String indicator, IHSWNotificationFilter callback) {
        notificationMapFilters.put(indicator, callback);
    }

    public void addCallbackProtocol(String indicator, IHSWProtocolSender callback) {
        protocolMapSenders.put(indicator, callback);
    }

    public void setIndicatorTime(String indicatorTime, IHSWProtocolSender callback) {
        HSWConnection.indicatorTime = indicatorTime;
        protocolMapSenders.put(indicatorTime, callback);
    }

    public String getIndicatorTime() {
        return indicatorTime;
    }
}
