package com.chooblarin.githublarin.serializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.lang.reflect.Type;

public class LocalDateTimeDeserializer implements JsonDeserializer<LocalDateTime> {
    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        String timeStamp = json.getAsString();
        DateTimeFormatter formatter
                = DateTimeFormatter.ofPattern("yyyy'-'MM'-'dd'T'HH':'mm':'ss'Z'");
        return LocalDateTime.parse(timeStamp, formatter);
    }
}
