package GUI.Support;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentLinkedDeque;

public class JobHistoryTypeAdapter implements JsonSerializer<JobHistory>,
        JsonDeserializer<JobHistory> {
    @Override
    public JsonElement serialize(JobHistory jobHistory, Type type, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("jobList", context.serialize(jobHistory.getJobList()));
        return jsonObject;
    }

    public JobHistory deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonArray jsonArray = jsonObject.getAsJsonArray("jobList");

        JobHistory jobHistory = new JobHistory();
        ConcurrentLinkedDeque<Job> jobList = context.deserialize(jsonArray, new TypeToken<ConcurrentLinkedDeque<JobHistory>>(){}.getType());

        jobHistory.setJobList(jobList);

        return jobHistory;
    }
}
