package messenger.utils;

import messenger.properties.ServerProperties;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class DateUtils {

    private DateUtils() {

    }

    public static Timestamp getCurrentTime() {
        LocalDateTime localDateTime = LocalDateTime.now(getServerZoneId());
        return Timestamp.valueOf(localDateTime);
    }

    private static ZoneId getServerZoneId() {
        String zoneId = ServerProperties.getProperty("server.time_zone");
        return ZoneId.of(zoneId);
    }
}
