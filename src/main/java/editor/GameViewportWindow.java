package editor;

import engine.input.Mouse;
import engine.shader.Framebuffer;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;

public class GameViewportWindow {
    public static boolean focused = false;

    public static void render(Framebuffer buff) {
        ImGui.begin("Game Viewport", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse);
        ImVec2 windowSize = getLargestSizeForViewport(buff);
        focused = ImGui.isWindowHovered() || Mouse.isMouseHidden();
        ImVec2 windowPos = getCenteredPositionForViewport(windowSize);
        ImGui.setCursorPos(windowPos.x, windowPos.y);
        ImGui.image(buff.getColorTexture(), windowSize.x, windowSize.y, 0, 1, 1, 0);
        ImGui.end();
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
