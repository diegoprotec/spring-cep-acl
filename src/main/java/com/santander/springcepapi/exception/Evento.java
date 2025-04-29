package com.santander.springcepapi.exception;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Evento {

    public static String getFormattedTimestamp() {
        var timestamp = Instant.now();
        ZoneId localZone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(timestamp, localZone);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return localDateTime.format(formatter);
    }

}
