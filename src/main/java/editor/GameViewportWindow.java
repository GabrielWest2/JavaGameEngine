package editor;

import engine.Camera;
import engine.Renderer;
import engine.display.DisplayManager;
import engine.ecs.Entity;
import engine.ecs.component.Transform;
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

import java.nio.FloatBuffer;

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
        int currentGizmoOperation = Operation.TRANSLATE;

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
                Matrix4f newModel = new Matrix4f(FloatBuffer.wrap(model));


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
