package GUI.Support;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.LinkedList;

public class FoldersTypeAdapter implements JsonSerializer<Folders>, JsonDeserializer<Folders> {

    @Override
    public JsonElement serialize(Folders folders, Type type, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("folders", context.serialize(folders.getFolders()));
        return jsonObject;
    }

    @Override
    public Folders deserialize(JsonElement json, Type type, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonArray jsonArray = jsonObject.getAsJsonArray("folders");

        Folders folders = new Folders();
        LinkedList<String> folderList = context.deserialize(jsonArray, new TypeToken<LinkedList<String>>(){}.getType());

        // Use reflection to set the 'folders' field
        try {
            java.lang.reflect.Field field = Folders.class.getDeclaredField("folders");
            field.setAccessible(true);
            field.set(folders, folderList);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return folders;
    }
}
