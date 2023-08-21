package GUI.Support;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class JobHistoryTypeAdapter extends TypeAdapter<JobHistory> {
    @Override
    public void write(JsonWriter out, JobHistory value) {
        // Write the JobHistory object to JSON here
    }

    @Override
    public JobHistory read(JsonReader in) throws IOException {
        JobHistory jobHistory = new JobHistory();
        in.beginObject();
        while (in.hasNext()) {
            String fieldName = in.nextName();
            if ("jobHistory".equals(fieldName)) {
                in.beginArray();
                while (in.hasNext()) {
                    Job job = readJob(in); // Helper method to read Job objects
                    jobHistory.addJob(job);
                }
                in.endArray();
            } else {
                in.skipValue();
            }
        }
        in.endObject();
        return jobHistory;
    }

    private Job readJob(JsonReader in) throws IOException {
        in.beginObject();
        String link = null;
        String dir = null;
        String filename = null;
        while (in.hasNext()) {
            String fieldName = in.nextName();
            switch (fieldName) {
                case "link":
                    link = in.nextString();
                    break;
                case "dir":
                    dir = in.nextString();
                    break;
                case "filename":
                    filename = in.nextString();
                    break;
                // Handle other fields as needed
                default:
                    in.skipValue();
                    break;
            }
        }
        in.endObject();
        return new Job(link, dir, filename);
    }
}
