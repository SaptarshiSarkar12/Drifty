package GUI.Support;

import com.google.gson.*;
import java.lang.reflect.Type;

public class JobTypeAdapter implements JsonSerializer<Job>, JsonDeserializer<Job> {

    @Override
    public JsonElement serialize(Job job, Type type, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("link", job.getLink());
        jsonObject.addProperty("dir", job.getDir());
        jsonObject.addProperty("filename", job.getFilename());
        jsonObject.addProperty("error", job.getError());
        return jsonObject;
    }

    @Override
    public Job deserialize(JsonElement json, Type type, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String link = jsonObject.get("link").isJsonNull() ? null : jsonObject.get("link").getAsString();
        String dir = jsonObject.get("dir").isJsonNull() ? null : jsonObject.get("dir").getAsString();
        String filename = jsonObject.get("filename").isJsonNull() ? null : jsonObject.get("filename").getAsString();
        String error = jsonObject.get("error").isJsonNull() ? null : jsonObject.get("error").getAsString();

        Job job = new Job(link, dir, filename);
        job.setError(error);

        return job;
    }
}
