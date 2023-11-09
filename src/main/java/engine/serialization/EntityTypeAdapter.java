package engine.serialization;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import engine.GameEngine;
import engine.ecs.Component;
import engine.ecs.Entity;

import java.io.IOException;

public class EntityTypeAdapter extends TypeAdapter<Entity> {
    @Override
    public void write(JsonWriter writer, Entity entity) throws IOException {
        if (entity == null) {
            writer.nullValue();
            return;
        }
        writer.value(entity.getName());
        writer.beginArray();
        for(Component component : entity.getComponents()){
            String ser = GameEngine.gson.toJson(component);
            writer.value(ser);
        }
        writer.endArray();
    }

    @Override
    public Entity read(JsonReader jsonReader) throws IOException {
        return null;
    }
}
