package engine.serialization;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import engine.ecs.component.Transform;

import java.io.IOException;

public class TransformTypeAdapter extends TypeAdapter<Transform> {
    @Override
    public void write(JsonWriter writer, Transform transform) throws IOException {
        if (transform == null) {
            writer.nullValue();
            return;
        }

        writer.value(transform.getPosition().x + " " +
        transform.getPosition().y + " " +
        transform.getPosition().z + " " +
        transform.getRotation().x + " " +
        transform.getRotation().y + " " +
        transform.getRotation().z + " " +
        transform.getRotation().w + " " +
        transform.getScale().x + " " +
        transform.getScale().y + " " +
        transform.getScale().z);
    }

    @Override
    public Transform read(JsonReader jsonReader) throws IOException {
        return null;
    }
}
