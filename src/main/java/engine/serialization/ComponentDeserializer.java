package engine.serialization;

import com.google.gson.*;
import engine.ecs.Component;

import java.lang.reflect.Type;

public class ComponentDeserializer implements JsonSerializer<Component>, JsonDeserializer<Component> {
    @Override
    public Component deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        String subclass = object.get("subc").getAsString();
        JsonObject fieldData = object.get("data").getAsJsonObject();

        try {
            return jsonDeserializationContext.deserialize(fieldData, Class.forName(subclass));
        } catch (ClassNotFoundException e) {
           e.printStackTrace();
        }
        return null;
    }

    @Override
    public JsonElement serialize(Component component, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();
        result.add("subc", new JsonPrimitive(component.getClass().getCanonicalName()));
        result.add("data", jsonSerializationContext.serialize(component, component.getClass()));
        return result;
    }
}
