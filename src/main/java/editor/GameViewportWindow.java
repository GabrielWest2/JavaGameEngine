package editor;

import editor.util.FAIcons;
import engine.ecs.Entity;
import engine.ecs.component.Transform;
import engine.input.Keyboard;
import engine.input.Mouse;
import engine.rendering.Camera;
import engine.rendering.Renderer;
import engine.scene.SceneManager;
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
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.List;

import static com.jogamp.opengl.GL.GL_FLOAT;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glReadBuffer;
import static org.lwjgl.opengl.GL11.glReadPixels;
import static org.lwjgl.opengles.GLES20.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengles.GLES20.GL_RGBA;

public class GameViewportWindow {

    public static boolean focused = false;

    private static ImVec2 previousWindowSize = new ImVec2(-1, -1);

    private static int currentGizmoOperation = Operation.TRANSLATE;

    private static int currentMode = Mode.LOCAL;

    /**
     * Samples a framebuffer at a coordinate and
     * decodes the color inorder to select the correct
     * entity
     * @param buffer the mousepicking frame buffer
     * @param xCoord the {@code x} coordinate (0 is left)
     * @param yCoord the {@code y} coordinate (0 is bottom)
     */
    public static void sampleFb(Framebuffer buffer, int xCoord, int yCoord){
        buffer.bind();
        float[] pixels = new float[4];
        glReadBuffer(GL_COLOR_ATTACHMENT0);
        glReadPixels(xCoord, yCoord, 1, 1,
                GL_RGBA, GL_FLOAT, pixels);
        float red = pixels[0];
        float green = pixels[1];
        float blue = pixels[2];
        if(!Float.isNaN(red)){
            int newR = java.lang.Math.round(red * 0xff);
            int newG = java.lang.Math.round(green * 0xff) << 8;
            int newB = java.lang.Math.round(blue * 0xff) << 16;
            int index = newR + newB + newG - 1;
            List<Entity> entities = SceneManager.loadedScene.getEntities();

            if(Mouse.isMouseClicked(0)){
                if(
                    index >= 0 &&
                    index < entities.size() &&
                    !entities.get(index).isLocked()
                ) {
                    LightingWindow.selectedPointLight = null;
                    LightingWindow.selectedSpotLight = null;
                    ExplorerWindow.selectedEntity = entities.get(index);
                }else {
                    ExplorerWindow.selectedEntity = null;
                    LightingWindow.selectedPointLight = null;
                    LightingWindow.selectedSpotLight = null;
                }
            }
        }
        buffer.unbind();
    }

    /**
     * Game viewport render function
     * @param buff the framebuffer to render
     * @param camera the {@code Camera} to be used to render gizmos
     * @param selected the currently selected {@code Entity}
     */
    public static void render(
            Framebuffer buff,
            Framebuffer pickingBuffer,
            Camera camera,
            Entity selected
    ) {
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 0);
        int flags =
                ImGuiWindowFlags.NoScrollbar |
                ImGuiWindowFlags.NoScrollWithMouse |
                ImGuiWindowFlags.NoTitleBar;

        ImGui.begin(FAIcons.ICON_EYE + " Game Viewport", flags);
        ImVec2 windowSize = ImGui.getWindowSize();
        if(!previousWindowSize.equals(windowSize)){
            Renderer.updateProjection((int) windowSize.x, (int) windowSize.y);
            previousWindowSize = windowSize;
        }

        float[] view = MatrixBuilder.createViewMatrix(camera).get(new float[16]);
        float[] proj = MatrixBuilder.createProjectionMatrix((int) windowSize.x, (int) windowSize.y).get(new float[16]);
        focused = ImGui.isWindowFocused();
        ImGui.image(buff.getColorTexture(), windowSize.x, windowSize.y, 0, 1, 1, 0);
        boolean shouldIgnoreClick = updateModes();
        Vector2f mousePos = Mouse.getPos();
        boolean mouseOverViewport = false;
        //Scale the window cursor position into framebuffer space
        int scaledCoordX = (int) ((int) (mousePos.x - ImGui.getWindowPosX()) / windowSize.x * pickingBuffer.getWidth());
        int scaledCoordY = pickingBuffer.getHeight() - (int) ((int) (mousePos.y - ImGui.getWindowPosY()) / windowSize.y * pickingBuffer.getHeight());
        if(mousePos.x > ImGui.getWindowPosX() && mousePos.x < ImGui.getWindowPosX() + windowSize.x && mousePos.y > ImGui.getWindowPosY() && mousePos.y < ImGui.getWindowPosY() + windowSize.y){
            mouseOverViewport = true;
        }
        ImGuizmo.setOrthographic(false);
        ImGuizmo.setEnabled(true);
        ImGuizmo.setDrawList();
        ImGuizmo.setRect(ImGui.getWindowPosX(), ImGui.getWindowPosY(), windowSize.x, windowSize.y);

        boolean displaylight = LightingWindow.selectedPointLight != null;

        float[] model = null;
        if(displaylight){
            model = MatrixBuilder.createTransformationMatrix(LightingWindow.selectedPointLight.getPosition(), new Quaternionf(), new Vector3f(1)).get(new float[16]);
            ImGuizmo.manipulate(view, proj, model, Operation.TRANSLATE, Mode.WORLD);
        } else if(selected != null) {
            Transform t = selected.getTransform();
            model = MatrixBuilder.createTransformationMatrix(t.getPosition(), t.getRotation(), t.getScale()).get(new float[16]);
            ImGuizmo.manipulate(view, proj, model, currentGizmoOperation, currentMode);
        }

        if(ImGuizmo.isUsing()){
            float[] pos = new float[3];
            float[] rot = new float[3];
            float[] sca = new float[3];
            ImGuizmo.decomposeMatrixToComponents(model, pos, rot, sca);
            Quaternionf quat = new Quaternionf();

            quat.rotateZ(Math.toRadians(rot[2]));
            quat.rotateY(Math.toRadians(rot[1]));
            quat.rotateX(Math.toRadians(rot[0]));

            if(displaylight){
                LightingWindow.selectedPointLight.setPosition(new Vector3f(pos[0], pos[1], pos[2]));
            }else if(selected != null) {
                selected.getTransform().setPositionUpdate(new Vector3f(pos[0], pos[1], pos[2]));
                selected.getTransform().setRotationUpdate(quat);
                selected.getTransform().setScale(new Vector3f(sca[0], sca[1], sca[2]));
            }
        }else if(mouseOverViewport && ImGui.isWindowFocused() && !shouldIgnoreClick){
            sampleFb(pickingBuffer, scaledCoordX, scaledCoordY);
        }


        ImGui.end();
        ImGui.popStyleVar();
    }

    /**
     * Changes the current gizmo {@code Operation}
     * and the current space {@code Mode} based
     * on the keybindings
     * @see Operation
     * @see Mode
     * @return true if one of the mode buttons was pressed
     */
    private static boolean updateModes() {
        //TODO Consolidate random keybindings in a configurable file

        if(Keyboard.isKeyPressedThisFrame(GLFW_KEY_T)){
            currentGizmoOperation = Operation.TRANSLATE;
        }else if(Keyboard.isKeyPressedThisFrame(GLFW_KEY_G)){
            currentGizmoOperation = Operation.ROTATE;
        }else if(Keyboard.isKeyPressedThisFrame(GLFW_KEY_H)){
            currentGizmoOperation = Operation.SCALE;
        }

        boolean f = false;
        ImGui.setCursorPos(10, 10);
        if(ImGui.button(FAIcons.ICON_ARROWS_ALT + " World")){
            currentMode = Mode.WORLD;
            f = true;
        }
        ImGui.sameLine();
        if(ImGui.button(FAIcons.ICON_EXPAND_ARROWS_ALT + " Local")){
            currentMode = Mode.LOCAL;
            f = true;
        }

        ImGui.setCursorPos(10, 35);
        ImGui.textColored(0xff247019, "FPS: " + (int)ImGui.getIO().getFramerate());

        return f;
    }

    /**
     * Calculates the maximum size the framebuffer with the
     * current aspect ratio can be to fit within the window
     * @param buff the final framebuffer
     * @return an {@code ImVec2} representing the width {@code x},
     * and height {@code y}
     */
    private static ImVec2 getLargestSizeForViewport(Framebuffer buff) {
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);
        windowSize.x -= ImGui.getScrollX();
        windowSize.y -= ImGui.getScrollY();

        float targetAspect = ((float) buff.getHeight() /
                (float) buff.getWidth());

        float aspectWidth = windowSize.x;
        float aspectHeight = aspectWidth * targetAspect;
        if (aspectHeight > windowSize.y) {
            aspectHeight = windowSize.y;
            aspectWidth = aspectHeight / targetAspect;
        }
        return new ImVec2(aspectWidth, aspectHeight);
    }

    /**
     * Gets the offset required inorder to center the image in the window
     * @param aspectSize the precalculated size for the image
     * @return an {@code ImVec2} representing the horizontal
     * offset {@code x}, and vertical offset {@code y}
     */
    private static ImVec2 getCenteredPositionForViewport(ImVec2 aspectSize) {
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);
        windowSize.x -= ImGui.getScrollX();
        windowSize.y -= ImGui.getScrollY();

        float viewPortX = (windowSize.x / 2.0f) - (aspectSize.x / 2.0f);
        float viewPortY = (windowSize.y / 2.0f) - (aspectSize.y / 2.0f);

        return new ImVec2(viewPortX + ImGui.getCursorPosX(),
                viewPortY + ImGui.getCursorPosY());
    }
}
