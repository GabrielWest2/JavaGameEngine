package engine.display;

import engine.GameEngine;
import engine.shader.Framebuffer;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImGuiStyle;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.type.ImBoolean;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static imgui.flag.ImGuiCol.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class DisplayManager {
    private static final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private static final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
    private static final String glslVersion = null;
    public static long window;
    private static int width = 856, height = 482;
    //private static int width = 1920, height = 1080;
    private static GameEngine engine;

    public static void initOpenGL(GameEngine engine) {

        DisplayManager.engine = engine;
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
        glfwWindowHint(GLFW_SAMPLES, 4);

        // Create the window
        window = glfwCreateWindow(width, height, "Game", NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.


        // Get the thread stack and push a new frame
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);
        glfwWindowHint(GLFW_SAMPLES, 4);
        GL.createCapabilities();
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        initImGui();
    }


    private static void initImGui() {
        ImGui.createContext();

        final ImGuiIO io = ImGui.getIO();

        io.setIniFilename("imgui.ini");
        io.setConfigFlags(ImGuiConfigFlags.DockingEnable);

        imGuiGlfw.init(window, true);
        imGuiGl3.init(glslVersion);


        ImGuiStyle style = ImGui.getStyle();
        style.setColor(Text, 0.95f, 0.96f, 0.98f, 1.00f);
        style.setColor(TextDisabled, 0.36f, 0.42f, 0.47f, 1.00f);
        style.setColor(WindowBg, 0.11f, 0.15f, 0.17f, 1.00f);
        style.setColor(ChildBg, 0.15f, 0.18f, 0.22f, 1.00f);
        style.setColor(PopupBg, 0.08f, 0.08f, 0.08f, 0.94f);
        style.setColor(Border, 0.08f, 0.10f, 0.12f, 1.00f);
        style.setColor(BorderShadow, 0.00f, 0.00f, 0.00f, 0.00f);
        style.setColor(FrameBg, 0.20f, 0.25f, 0.29f, 1.00f);
        style.setColor(FrameBgHovered, 0.12f, 0.20f, 0.28f, 1.00f);
        style.setColor(FrameBgActive, 0.09f, 0.12f, 0.14f, 1.00f);
        style.setColor(TitleBg, 0.09f, 0.12f, 0.14f, 0.65f);
        style.setColor(TitleBgActive, 0.08f, 0.10f, 0.12f, 1.00f);
        style.setColor(TitleBgCollapsed, 0.00f, 0.00f, 0.00f, 0.51f);
        style.setColor(MenuBarBg, 0.15f, 0.18f, 0.22f, 1.00f);
        style.setColor(ScrollbarBg, 0.02f, 0.02f, 0.02f, 0.39f);
        style.setColor(ScrollbarGrab, 0.20f, 0.25f, 0.29f, 1.00f);
        style.setColor(ScrollbarGrabHovered, 0.18f, 0.22f, 0.25f, 1.00f);
        style.setColor(ScrollbarGrabActive, 0.09f, 0.21f, 0.31f, 1.00f);
        style.setColor(CheckMark, 0.28f, 0.56f, 1.00f, 1.00f);
        style.setColor(SliderGrab, 0.28f, 0.56f, 1.00f, 1.00f);
        style.setColor(SliderGrabActive, 0.37f, 0.61f, 1.00f, 1.00f);
        style.setColor(Button, 0.20f, 0.25f, 0.29f, 1.00f);
        style.setColor(ButtonHovered, 0.28f, 0.56f, 1.00f, 1.00f);
        style.setColor(ButtonActive, 0.06f, 0.53f, 0.98f, 1.00f);
        style.setColor(Header, 0.20f, 0.25f, 0.29f, 0.55f);
        style.setColor(HeaderHovered, 0.26f, 0.59f, 0.98f, 0.80f);
        style.setColor(HeaderActive, 0.26f, 0.59f, 0.98f, 1.00f);
        style.setColor(Separator, 0.20f, 0.25f, 0.29f, 1.00f);
        style.setColor(SeparatorHovered, 0.10f, 0.40f, 0.75f, 0.78f);
        style.setColor(SeparatorActive, 0.10f, 0.40f, 0.75f, 1.00f);
        style.setColor(ResizeGrip, 0.26f, 0.59f, 0.98f, 0.25f);
        style.setColor(ResizeGripHovered, 0.26f, 0.59f, 0.98f, 0.67f);
        style.setColor(ResizeGripActive, 0.26f, 0.59f, 0.98f, 0.95f);
        style.setColor(Tab, 0.11f, 0.15f, 0.17f, 1.00f);
        style.setColor(TabHovered, 0.26f, 0.59f, 0.98f, 0.80f);
        style.setColor(TabActive, 0.20f, 0.25f, 0.29f, 1.00f);
        style.setColor(TabUnfocused, 0.11f, 0.15f, 0.17f, 1.00f);
        style.setColor(TabUnfocusedActive, 0.11f, 0.15f, 0.17f, 1.00f);
        style.setColor(PlotLines, 0.61f, 0.61f, 0.61f, 1.00f);
        style.setColor(PlotLinesHovered, 1.00f, 0.43f, 0.35f, 1.00f);
        style.setColor(PlotHistogram, 0.90f, 0.70f, 0.00f, 1.00f);
        style.setColor(PlotHistogramHovered, 1.00f, 0.60f, 0.00f, 1.00f);
        style.setColor(TextSelectedBg, 0.26f, 0.59f, 0.98f, 0.35f);
        style.setColor(DragDropTarget, 1.00f, 1.00f, 0.00f, 0.90f);
        style.setColor(NavHighlight, 0.26f, 0.59f, 0.98f, 1.00f);
        style.setColor(NavWindowingHighlight, 1.00f, 1.00f, 1.00f, 0.70f);
        style.setColor(NavWindowingDimBg, 0.80f, 0.80f, 0.80f, 0.20f);
        style.setColor(ModalWindowDimBg, 0.80f, 0.80f, 0.80f, 0.35f);
    }

    public static void createDockspace() {
        int windowFlags = ImGuiWindowFlags.MenuBar | ImGuiWindowFlags.NoDocking;
        ImGui.setNextWindowPos(0.0f, 0.0f, ImGuiCond.Always);
        ImGui.setNextWindowSize(getWidth(), getHeight());
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.0f);
        windowFlags |= ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoCollapse |
                ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove |
                ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNavFocus;

        ImGui.begin("Dockspace", new ImBoolean(true), windowFlags);
        ImGui.popStyleVar(2);

        ImGui.dockSpace(ImGui.getID("Dockspace"));

        ImGui.end();
    }

    public static void newImguiFrame() {
        imGuiGlfw.newFrame();
        ImGui.newFrame();


    }

    public static void endImguiFrame() {
        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());

        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final long backupWindowPtr = GLFW.glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            GLFW.glfwMakeContextCurrent(backupWindowPtr);
        }
    }

    public static void setCallbacks() {
        glfwSetWindowSizeCallback(window, (window, w, h) -> {
            width = w;
            height = h;
            GameEngine.renderer.UpdateProjection();
            glViewport(0, 0, width, height);
            Framebuffer.setFrameBufferSize(width, height);
            System.out.println("Resized window! " + w + "  " + h);
        });
    }

    public static int getWidth() {
        return width;
    }

    public static int getHeight() {
        return height;

    }
}
