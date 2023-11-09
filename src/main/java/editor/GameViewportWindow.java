package editor;

import engine.Camera;
import engine.Renderer;
import engine.display.DisplayManager;
import engine.ecs.Entity;
import engine.ecs.component.Transform;
import engine.input.Keyboard;
import engine.input.Mouse;
import engine.shader.Framebuffer;
import engine.util.MatrixBuilder;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.extension.imguizmo.ImGuizmo;
import imgui.extension.imguizmo.flag.Mode;
import imgui.extension.imguizmo.flag.Operation;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.nio.FloatBuffer;

import static org.lwjgl.glfw.GLFW.*;

public class GameViewportWindow {
    public static boolean focused = false;
    public static ImVec2 previousWindowSize = new ImVec2(-1, -1);



    private static final float[][] OBJECT_MATRICES = {
            {
                    1.f, 0.f, 0.f, 0.f,
                    0.f, 1.f, 0.f, 0.f,
                    0.f, 0.f, 1.f, 0.f,
                    0.f, 0.f, 0.f, 1.f
            },
            {
                    1.f, 0.f, 0.f, 0.f,
                    0.f, 1.f, 0.f, 0.f,
                    0.f, 0.f, 1.f, 0.f,
                    2.f, 0.f, 0.f, 1.f
            },
            {
                    1.f, 0.f, 0.f, 0.f,
                    0.f, 1.f, 0.f, 0.f,
                    0.f, 0.f, 1.f, 0.f,
                    2.f, 0.f, 2.f, 1.f
            },
            {
                    1.f, 0.f, 0.f, 0.f,
                    0.f, 1.f, 0.f, 0.f,
                    0.f, 0.f, 1.f, 0.f,
                    0.f, 0.f, 2.f, 1.f
            }
    };


    static int currentGizmoOperation = Operation.TRANSLATE;

    public static void render(Framebuffer buff, Camera camera, Entity selected) {
        //Remove the border between the edge of the window and the image
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 0);
        ImGui.begin("Game Viewport", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse);
        ImVec2 windowSize = ImGui.getWindowSize();
        //ImVec2 windowSize = getLargestSizeForViewport(buff);
        if(!previousWindowSize.equals(windowSize)){
            System.out.println("Resized viewport");
            Renderer.updateProjection((int) windowSize.x, (int) windowSize.y);
            //DisplayManager.windowSizeCallback(DisplayManager.window, (int) windowSize.x, (int) windowSize.y);
            previousWindowSize = windowSize;
        }


        float[] view = MatrixBuilder.createViewMatrix(camera).get(new float[16]);
        float[] proj = MatrixBuilder.createProjectionMatrix((int) windowSize.x, (int) windowSize.y).get(new float[16]);

        focused = ImGui.isWindowHovered() || Mouse.isMouseHidden();
       // ImVec2 windowPos = getCenteredPositionForViewport(windowSize);
       // ImGui.setCursorPos(windowPos.x, windowPos.y);
     //   ImGui.image(buff.getColorTexture(), windowSize.x, windowSize.y, 0, 1, 1, 0);


        ImGui.image(buff.getColorTexture(), windowSize.x, windowSize.y, 0, 1, 1, 0);


        int currentMode = Mode.LOCAL;

        if(Keyboard.isKeyPressedThisFrame(GLFW_KEY_T)){
            currentGizmoOperation = Operation.TRANSLATE;
        }else if(Keyboard.isKeyPressedThisFrame(GLFW_KEY_G)){
            currentGizmoOperation = Operation.ROTATE;
        }else if(Keyboard.isKeyPressedThisFrame(GLFW_KEY_H)){
            currentGizmoOperation = Operation.SCALE;
        }


        ImGuizmo.setOrthographic(false);
        ImGuizmo.setEnabled(true);
        ImGuizmo.setDrawList();
        ImGuizmo.setRect(ImGui.getWindowPosX(), ImGui.getWindowPosY(), windowSize.x, windowSize.y);
        /*ImGuizmo.drawGrid(view, proj, new float[] {
                1.f, 0.f, 0.f, 0.f,
                0.f, 1.f, 0.f, 0.f,
                0.f, 0.f, 1.f, 0.f,
                0.f, 0.f, 0.f, 1.f
        }, 100);*/

        if(selected != null) {
            Transform t = selected.getTransform();
            float[] model = MatrixBuilder.createTransformationMatrix(t.getPosition(), t.getRotation(), t.getScale()).get(new float[16]);
            ImGuizmo.manipulate(view, proj, model, currentGizmoOperation, currentMode);

            if(ImGuizmo.isUsing()){
                float[] pos = new float[3];
                float[] rot = new float[3];
                float[] sca = new float[3];
                ImGuizmo.decomposeMatrixToComponents(model, pos, rot, sca);
                System.out.println(Math.toDegrees(rot[0]) + "   " + Math.toDegrees(rot[1]) + "   " + Math.toDegrees(rot[2]));
                Quaternionf quat = new Quaternionf();

                quat.rotateZ(Math.toRadians(rot[2]));
                quat.rotateY(Math.toRadians(rot[1]));
                quat.rotateX(Math.toRadians(rot[0]));

                selected.getTransform().setPosition(new Vector3f(pos[0], pos[1], pos[2]));
                selected.getTransform().setRotation(quat);
                selected.getTransform().setScale(new Vector3f(sca[0], sca[1], sca[2]));
            }
        }
        ImGui.end();
        ImGui.popStyleVar();
    }


    private static ImVec2 getLargestSizeForViewport(Framebuffer buff) {
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);
        windowSize.x -= ImGui.getScrollX();
        windowSize.y -= ImGui.getScrollY();

        float targetAspect = ((float) buff.getHeight() / (float) buff.getWidth());

        float aspectWidth = windowSize.x;
        float aspectHeight = aspectWidth * targetAspect;
        if (aspectHeight > windowSize.y) {
            aspectHeight = windowSize.y;
            aspectWidth = aspectHeight / targetAspect;
        }
        return new ImVec2(aspectWidth, aspectHeight);
    }

    private static ImVec2 getCenteredPositionForViewport(ImVec2 aspectSize) {
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);
        windowSize.x -= ImGui.getScrollX();
        windowSize.y -= ImGui.getScrollY();

        float viewPortX = (windowSize.x / 2.0f) - (aspectSize.x / 2.0f);
        float viewPortY = (windowSize.y / 2.0f) - (aspectSize.y / 2.0f);

        return new ImVec2(viewPortX + ImGui.getCursorPosX(), viewPortY + ImGui.getCursorPosY());
    }


}
