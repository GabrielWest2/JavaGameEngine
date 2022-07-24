package editor;

import imgui.ImGui;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;


public class ConsoleWindow {
    private static ByteArrayOutputStream newConsole;

    public static void init() {
        newConsole = new ByteArrayOutputStream();
        System.setOut(new PrintStream(newConsole));
    }

    public static void render() {
        ImGui.begin("Console");
        if (ImGui.smallButton("Clear")) {
            newConsole.reset();
        }
        ImGui.sameLine();
        if (ImGui.smallButton("Copy"))
            ImGui.setClipboardText(newConsole.toString());

        ImGui.separator();
        ImGui.beginChildFrame(1, ImGui.getContentRegionMaxX(), ImGui.getContentRegionAvail().y);
        ImGui.textWrapped(newConsole.toString());
        ImGui.endChildFrame();

        ImGui.end();
    }
}
