package GUI.Support;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentLinkedDeque;

public class JobsTypeAdapter implements JsonSerializer<Jobs>, JsonDeserializer<Jobs> {

    @Override
    public JsonElement serialize(Jobs jobs, Type type, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("jobList", context.serialize(jobs.jobList()));
        return jsonObject;
    }

    @Override
    public Jobs deserialize(JsonElement json, Type type, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonArray jsonArray = jsonObject.getAsJsonArray("jobList");

        Jobs jobs = new Jobs();
        ConcurrentLinkedDeque<Job> jobList = context.deserialize(jsonArray, new TypeToken<ConcurrentLinkedDeque<Job>>(){}.getType());

        jobs.setJobList(jobList);

        return jobs;
    }
}
