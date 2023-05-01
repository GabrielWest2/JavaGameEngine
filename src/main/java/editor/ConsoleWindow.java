package editor;

import imgui.ImGui;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;


public class ConsoleWindow {
    private static ByteArrayOutputStream newConsole;
    private static boolean autoscroll = true;

    public static void init() {
        newConsole = new ByteArrayOutputStream();
        //System.setOut(new PrintStream(newConsole));
    }
    public static void render() {
        ImGui.begin("Console");
        if (ImGui.smallButton("Clear")) {
            newConsole.reset();
        }
        ImGui.sameLine();
        if (ImGui.smallButton("Copy"))
            ImGui.setClipboardText(newConsole.toString());
        ImGui.sameLine();
        if (ImGui.checkbox("Auto Scroll", autoscroll)){
            autoscroll = !autoscroll;
        }

        ImGui.separator();
        ImGui.beginChildFrame(1, ImGui.getContentRegionMaxX(), ImGui.getContentRegionAvail().y);
        ImGui.textWrapped(newConsole.toString());
        if(autoscroll)
            ImGui.setScrollHereY(1.0f);
        ImGui.endChildFrame();

        ImGui.end();
    }
}
