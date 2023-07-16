package communication;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import stepper.dd.api.DataDefinition;
import stepper.dd.impl.DataDefinitionRegistry;
import stepper.dd.impl.file.FileData;
import stepper.dd.impl.relation.Relation;

import java.lang.reflect.Type;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class GsonCreator {
    public static final String DATA_DEFINITION_TYPE = "__TYPE__";
    public static Gson createGson(){
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(FileData.class, new FileDataAdapter());
        gsonBuilder.registerTypeAdapter(Relation.class, new RelationDataAdapter());
        gsonBuilder.registerTypeAdapter(DataDefinition.class, new DataDefinitionAdapter());
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
      public JsonElement serialize(FileData fData, Type type, JsonSerializationContext jsonSerializationContext) {
          JsonObject jsonObject = new JsonObject();
          jsonObject.addProperty("filePath", fData.getPath());
          return jsonObject;
      }
  }

  public static class RelationDataAdapter implements JsonSerializer<Relation>, JsonDeserializer<Relation> {
      @Override
      public Relation deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
          JsonObject jsonObject = jsonElement.getAsJsonObject();
          List<String> colNames = jsonDeserializationContext.deserialize(jsonObject.get("columnsNames"), new TypeToken<List<String>>(){}.getType());
          List<List<String>> rows = jsonDeserializationContext.deserialize(jsonObject.get("rows"), new TypeToken<List<List<String>>>(){}.getType());
          Map<String,List<String>> columns = jsonDeserializationContext.deserialize(jsonObject.get("columns"), new TypeToken<Map<String,List<String>>>(){}.getType());

          return new Relation(colNames, rows, columns);
      }
      @Override
      public JsonElement serialize(Relation obj, Type type, JsonSerializationContext jsonSerializationContext) {
          JsonObject jsonObject = new JsonObject();
          jsonObject.add("columnsNames", jsonSerializationContext.serialize(obj.getColumnsNames()));
          jsonObject.add("rows", jsonSerializationContext.serialize(obj.getRows()));
          jsonObject.add("columns", jsonSerializationContext.serialize(obj.getColumns()));
          return jsonObject;
      }
  }

  public static class DataDefinitionAdapter implements JsonSerializer<DataDefinition>, JsonDeserializer<DataDefinition> {

      @Override
      public DataDefinition deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
           return DataDefinitionRegistry.valueOf(jsonElement.getAsString());
      }

      @Override
      public JsonElement serialize(DataDefinition dataDefinition, Type type, JsonSerializationContext jsonSerializationContext) {
          JsonObject jsonObject = new JsonObject();
          jsonObject.addProperty(DATA_DEFINITION_TYPE, dataDefinition.getType().toString());
          jsonObject.add("data", jsonSerializationContext.serialize(dataDefinition));
          return jsonObject;
      }
  }
}
