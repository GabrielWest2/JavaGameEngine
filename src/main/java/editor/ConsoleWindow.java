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
        ImGui.showDemoWindow();
        if (ImGui.smallButton("Clear")) {
            newConsole.reset();
        }
        ImGui.sameLine();
        if (ImGui.smallButton("Copy"))
            ImGui.setClipboardText(newConsole.toString());

        ImGui.separator();

        ImGui.textWrapped(newConsole.toString());

        ImGui.end();
    }
}
