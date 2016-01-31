package com.chooblarin.githublarin.serializer;

import com.chooblarin.githublarin.model.GistFile;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GistFilesDeserializer implements JsonDeserializer<List<GistFile>> {

    @Override
    public List<GistFile> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        Set<Map.Entry<String, JsonElement>> entries = jsonObject.entrySet();
        List<GistFile> gistFiles = new ArrayList<>(entries.size());
        for (Map.Entry<String, JsonElement> entry : entries) {
            GistFile gistFile = GistFile.createFrom(entry.getValue().getAsJsonObject());
            gistFiles.add(gistFile);
        }
        return gistFiles;
    }
}
