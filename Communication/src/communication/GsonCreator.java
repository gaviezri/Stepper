package communication;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import stepper.dd.api.DataDefinition;
import stepper.dd.impl.file.FileData;
import stepper.dd.impl.relation.RelationData;

import java.lang.reflect.Type;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class GsonCreator {

    public static Gson createGson(){
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(FileData.class, new FileDataAdapter());
        gsonBuilder.registerTypeAdapter(RelationData.class, new RelationDataAdapter());
        return gsonBuilder.create();
    }
  public static class FileDataAdapter implements JsonSerializer<FileData>, JsonDeserializer<FileData> {
      @Override
      public FileData deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
          JsonObject jsonObject = jsonElement.getAsJsonObject();
          String path = jsonObject.get("filePath").getAsString();
          return new FileData(Paths.get(path));
      }
      @Override
      public JsonElement serialize(FileData dataDefinition, Type type, JsonSerializationContext jsonSerializationContext) {
          JsonObject jsonObject = new JsonObject();
          jsonObject.addProperty("filePath", dataDefinition.getPath());
          return jsonObject;
      }
  }

  public static class RelationDataAdapter implements JsonSerializer<RelationData>, JsonDeserializer<RelationData> {
      @Override
      public RelationData deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
          JsonObject jsonObject = jsonElement.getAsJsonObject();
          List<String> colNames = jsonDeserializationContext.deserialize(jsonObject.get("columnsNames"), new TypeToken<List<String>>(){}.getType());
          List<List<String>> rows = jsonDeserializationContext.deserialize(jsonObject.get("rows"), new TypeToken<List<List<String>>>(){}.getType());
          Map<String,List<String>> columns = jsonDeserializationContext.deserialize(jsonObject.get("columns"), new TypeToken<Map<String,List<String>>>(){}.getType());

          return new RelationData(colNames, rows, columns);
      }
      @Override
      public JsonElement serialize(RelationData obj, Type type, JsonSerializationContext jsonSerializationContext) {
          return jsonSerializationContext.serialize(obj);
      }
  }
}
