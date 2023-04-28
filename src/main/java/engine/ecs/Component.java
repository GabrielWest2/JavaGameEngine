package engine.ecs;

import editor.CustomHudName;
import editor.Range;
import engine.ecs.Entity;
import engine.util.Color;
import imgui.ImGui;
import imgui.type.ImString;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class Component {
    /*
     * Component Class
     */

    public Entity entity;

    public void onAdded() {
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isTransient(field.getModifiers()))
                continue;

            hasFields = true;
        }
    }

    public void onVariableChanged() {
    }

    public boolean hasFields = false;

    public boolean hasFields() {
        return hasFields;
    }

    public void GUI() {
        try {
            Field[] fields = this.getClass().getDeclaredFields();

            for (Field field : fields) {
                boolean isPrivate = Modifier.isPrivate(field.getModifiers());
                boolean isTransient = Modifier.isTransient(field.getModifiers());
                if (isTransient)
                    continue;

                if (isPrivate) {
                    field.setAccessible(true);
                }

                Class type = field.getType();
                Object value = field.get(this);
                String name = field.getName();

                Range range = field.getAnnotation(Range.class);
                CustomHudName hudName = field.getAnnotation(CustomHudName.class);
                if(hudName != null)
                    name = hudName.displayName();

                if (type == int.class) {
                    int val = (int) value;
                    int[] imInt = {val};
                    if (range == null) {
                        if (ImGui.dragInt(name, imInt)) {
                            field.set(this, imInt[0]);
                            onVariableChanged();
                        }
                    } else {
                        if (ImGui.sliderInt(name, imInt, (int) range.min(), (int) range.max())) {
                            field.set(this, imInt[0]);
                            onVariableChanged();
                        }
                    }
                } else if (type == float.class) {
                    float val = (float) value;
                    float[] imFloat = {val};
                    if (range == null) {
                        if (ImGui.dragFloat(name, imFloat)) {
                            field.set(this, imFloat[0]);
                            onVariableChanged();
                        }
                    } else {
                        if (ImGui.sliderFloat(name, imFloat, range.min(), range.max())) {
                            field.set(this, imFloat[0]);
                            onVariableChanged();
                        }
                    }
                } else if (type == boolean.class) {
                    boolean val = (boolean) value;
                    if (ImGui.checkbox(name, val)) {
                        field.set(this, !val);
                        onVariableChanged();
                    }
                } else if (type == Vector2f.class) {
                    Vector2f val = (Vector2f) value;
                    float[] imVec = {val.x, val.y};
                    if (ImGui.dragFloat2(name, imVec)) {
                        val.set(imVec[0], imVec[1]);
                        onVariableChanged();
                    }
                } else if (type == Vector3f.class) {
                    Vector3f val = (Vector3f) value;
                    float[] imVec = {val.x, val.y, val.z};
                    if (ImGui.dragFloat3(name, imVec)) {
                        val.set(imVec[0], imVec[1], imVec[2]);
                        onVariableChanged();
                    }
                } else if (type == Color.class) {
                    Color val = (Color) value;
                    float[] imVec = {val.r, val.g, val.b};
                    if (ImGui.colorEdit3(name, imVec)) {
                        val.set(imVec[0], imVec[1], imVec[2]);
                        onVariableChanged();
                    }
                } else if (type == Vector4f.class) {
                    Vector4f val = (Vector4f) value;
                    float[] imVec = {val.x, val.y, val.z, val.w};
                    if (ImGui.dragFloat4(name, imVec)) {
                        val.set(imVec[0], imVec[1], imVec[2], imVec[3]);
                        onVariableChanged();
                    }
                } else if (type == String.class) {
                    String val = (String) value;
                    ImString string = new ImString(100);
                    string.set(val);
                    if (ImGui.inputText(name, string)) {
                        field.set(this, string.get());
                        onVariableChanged();
                    }
                }

            }


        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public boolean canBeRemoved(){
        return true;
    }
}
