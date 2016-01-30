package com.chooblarin.githublarin.util;

import android.content.Context;

import com.chooblarin.githublarin.R;

import org.threeten.bp.Duration;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

public class DateTimeUtils {

    private DateTimeUtils() {
    }

    public static LocalDateTime localDateTimeFrom(long milliSeconds) {
        Instant instant = Instant.ofEpochMilli(milliSeconds);
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }

    public static String timeStamp(LocalDateTime dateTime) {
        ZonedDateTime zonedDateTime = ZonedDateTime.of(dateTime, ZoneOffset.UTC);
        return zonedDateTime.format(DateTimeFormatter.RFC_1123_DATE_TIME);
    }

    public static String pastTimeString(Context context, LocalDateTime from, LocalDateTime to) {
        Duration duration = Duration.between(to, from);
        long days = duration.toDays();
        if (0 < days) {
            return context.getString(R.string.past_time_days_format, days);
        }

        long hours = duration.toHours();
        if (0 < hours) {
            return context.getString(R.string.past_time_hours_format, hours);
        }

        long minutes = duration.toMinutes();
        if (0 < minutes) {
            return context.getString(R.string.past_time_minutes_format, minutes);
        }
        return context.getString(R.string.past_time_just_now_format);
    }
}
