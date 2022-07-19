package engine.model;

import de.javagl.obj.Obj;
import de.javagl.obj.ObjData;
import de.javagl.obj.ObjReader;
import de.javagl.obj.ObjUtils;
import engine.GameEngine;
import engine.texture.Texture;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


public class OBJLoader {

    public static Model loadOBJ(String path) {
        try {
            InputStream inputStream = new FileInputStream("res/" + path);
            Obj obj = ObjUtils.convertToRenderable(
                    ObjReader.read(inputStream));
            Model model = GameEngine.getModelCreator().loadToVAO(ObjData.getVertices(obj), ObjData.getFaceVertexIndices(obj));
            return model;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Model loadTexturedOBJ(String path, Texture texture) {
        try {
            InputStream inputStream = new FileInputStream("res/" + path);
            Obj obj = ObjUtils.convertToRenderable(
                    ObjReader.read(inputStream));
            Model model = GameEngine.getModelCreator().loadToTexturedVAO(ObjData.getVertices(obj), ObjData.getFaceVertexIndices(obj), ObjData.getTexCoords(obj, 2), ObjData.getNormals(obj), texture);
            return model;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
