package editor;

import imgui.ImGui;
import imgui.ImGuiStyle;

import static imgui.flag.ImGuiCol.*;

/**
 * @author gabed
 * @Date 7/23/2022
 */
public class ImGuiThemer {

    public static void DarkTheme() {
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

    public static void VGUITheme() {
        ImGuiStyle style = ImGui.getStyle();
        style.setColor(Text, 1.00f, 1.00f, 1.00f, 1.00f);
        style.setColor(TextDisabled, 0.50f, 0.50f, 0.50f, 1.00f);
        style.setColor(WindowBg, 0.29f, 0.34f, 0.26f, 1.00f);
        style.setColor(ChildBg, 0.29f, 0.34f, 0.26f, 1.00f);
        style.setColor(PopupBg, 0.24f, 0.27f, 0.20f, 1.00f);
        style.setColor(Border, 0.54f, 0.57f, 0.51f, 0.50f);
        style.setColor(BorderShadow, 0.14f, 0.16f, 0.11f, 0.52f);
        style.setColor(FrameBg, 0.24f, 0.27f, 0.20f, 1.00f);
        style.setColor(FrameBgHovered, 0.27f, 0.30f, 0.23f, 1.00f);
        style.setColor(FrameBgActive, 0.30f, 0.34f, 0.26f, 1.00f);
        style.setColor(TitleBg, 0.24f, 0.27f, 0.20f, 1.00f);
        style.setColor(TitleBgActive, 0.29f, 0.34f, 0.26f, 1.00f);
        style.setColor(TitleBgCollapsed, 0.00f, 0.00f, 0.00f, 0.51f);
        style.setColor(MenuBarBg, 0.24f, 0.27f, 0.20f, 1.00f);
        style.setColor(ScrollbarBg, 0.35f, 0.42f, 0.31f, 1.00f);
        style.setColor(ScrollbarGrab, 0.28f, 0.32f, 0.24f, 1.00f);
        style.setColor(ScrollbarGrabHovered, 0.25f, 0.30f, 0.22f, 1.00f);
        style.setColor(ScrollbarGrabActive, 0.23f, 0.27f, 0.21f, 1.00f);
        style.setColor(CheckMark, 0.59f, 0.54f, 0.18f, 1.00f);
        style.setColor(SliderGrab, 0.35f, 0.42f, 0.31f, 1.00f);
        style.setColor(SliderGrabActive, 0.54f, 0.57f, 0.51f, 0.50f);
        style.setColor(Button, 0.29f, 0.34f, 0.26f, 0.40f);
        style.setColor(ButtonHovered, 0.35f, 0.42f, 0.31f, 1.00f);
        style.setColor(ButtonActive, 0.54f, 0.57f, 0.51f, 0.50f);
        style.setColor(Header, 0.35f, 0.42f, 0.31f, 1.00f);
        style.setColor(HeaderHovered, 0.35f, 0.42f, 0.31f, 0.6f);
        style.setColor(HeaderActive, 0.54f, 0.57f, 0.51f, 0.50f);
        style.setColor(Separator, 0.14f, 0.16f, 0.11f, 1.00f);
        style.setColor(SeparatorHovered, 0.54f, 0.57f, 0.51f, 1.00f);
        style.setColor(SeparatorActive, 0.59f, 0.54f, 0.18f, 1.00f);
        style.setColor(ResizeGrip, 0.19f, 0.23f, 0.18f, 0.00f); // grip invis
        style.setColor(ResizeGripHovered, 0.54f, 0.57f, 0.51f, 1.00f);
        style.setColor(ResizeGripActive, 0.59f, 0.54f, 0.18f, 1.00f);
        style.setColor(Tab, 0.35f, 0.42f, 0.31f, 1.00f);
        style.setColor(TabHovered, 0.54f, 0.57f, 0.51f, 0.78f);
        style.setColor(TabActive, 0.59f, 0.54f, 0.18f, 1.00f);
        style.setColor(TabUnfocused, 0.24f, 0.27f, 0.20f, 1.00f);
        style.setColor(TabUnfocusedActive, 0.35f, 0.42f, 0.31f, 1.00f);
        style.setColor(DockingPreview, 0.59f, 0.54f, 0.18f, 1.00f);
        style.setColor(DockingEmptyBg, 0.20f, 0.20f, 0.20f, 1.00f);
        style.setColor(PlotLines, 0.61f, 0.61f, 0.61f, 1.00f);
        style.setColor(PlotLinesHovered, 0.59f, 0.54f, 0.18f, 1.00f);
        style.setColor(PlotHistogram, 1.00f, 0.78f, 0.28f, 1.00f);
        style.setColor(PlotHistogramHovered, 1.00f, 0.60f, 0.00f, 1.00f);
        style.setColor(TextSelectedBg, 0.59f, 0.54f, 0.18f, 1.00f);
        style.setColor(DragDropTarget, 0.73f, 0.67f, 0.24f, 1.00f);
        style.setColor(NavHighlight, 0.59f, 0.54f, 0.18f, 1.00f);
        style.setColor(NavWindowingHighlight, 1.00f, 1.00f, 1.00f, 0.70f);
        style.setColor(NavWindowingDimBg, 0.80f, 0.80f, 0.80f, 0.20f);
        style.setColor(ModalWindowDimBg, 0.80f, 0.80f, 0.80f, 0.35f);
    }

    public static void GoldTheme() {
        ImGuiStyle style = ImGui.getStyle();
        style.setColor(Text, 0.92f, 0.92f, 0.92f, 1.00f);
        style.setColor(TextDisabled, 0.44f, 0.44f, 0.44f, 1.00f);
        style.setColor(WindowBg, 0.06f, 0.06f, 0.06f, 1.00f);
        style.setColor(ChildBg, 0.00f, 0.00f, 0.00f, 0.00f);
        style.setColor(PopupBg, 0.08f, 0.08f, 0.08f, 0.94f);
        style.setColor(Border, 0.51f, 0.36f, 0.15f, 1.00f);
        style.setColor(BorderShadow, 0.00f, 0.00f, 0.00f, 0.00f);
        style.setColor(FrameBg, 0.11f, 0.11f, 0.11f, 1.00f);
        style.setColor(FrameBgHovered, 0.51f, 0.36f, 0.15f, 1.00f);
        style.setColor(FrameBgActive, 0.78f, 0.55f, 0.21f, 1.00f);
        style.setColor(TitleBg, 0.91f, 0.64f, 0.13f, 1.00f);
        style.setColor(TitleBgActive, 0.91f, 0.64f, 0.13f, 1.00f);
        style.setColor(TitleBgCollapsed, 0.00f, 0.00f, 0.00f, 0.51f);
        style.setColor(MenuBarBg, 0.11f, 0.11f, 0.11f, 1.00f);
        style.setColor(ScrollbarBg, 0.06f, 0.06f, 0.06f, 0.53f);
        style.setColor(ScrollbarGrab, 0.21f, 0.21f, 0.21f, 1.00f);
        style.setColor(ScrollbarGrabHovered, 0.47f, 0.47f, 0.47f, 1.00f);
        style.setColor(ScrollbarGrabActive, 0.81f, 0.83f, 0.81f, 1.00f);
        style.setColor(CheckMark, 0.78f, 0.55f, 0.21f, 1.00f);
        style.setColor(SliderGrab, 0.91f, 0.64f, 0.13f, 1.00f);
        style.setColor(SliderGrabActive, 0.91f, 0.64f, 0.13f, 1.00f);
        style.setColor(Button, 0.51f, 0.36f, 0.15f, 1.00f);
        style.setColor(ButtonHovered, 0.91f, 0.64f, 0.13f, 1.00f);
        style.setColor(ButtonActive, 0.78f, 0.55f, 0.21f, 1.00f);
        style.setColor(Header, 195f / 255f, 135f / 255f, 22f / 255f, 1.00f);
        style.setColor(HeaderHovered, 0.91f, 0.64f, 0.13f, 1.00f);
        style.setColor(HeaderActive, 0.93f, 0.65f, 0.14f, 1.00f);
        style.setColor(Separator, 0.21f, 0.21f, 0.21f, 1.00f);
        style.setColor(SeparatorHovered, 0.91f, 0.64f, 0.13f, 1.00f);
        style.setColor(SeparatorActive, 0.78f, 0.55f, 0.21f, 1.00f);
        style.setColor(ResizeGrip, 0.21f, 0.21f, 0.21f, 1.00f);
        style.setColor(ResizeGripHovered, 0.91f, 0.64f, 0.13f, 1.00f);
        style.setColor(ResizeGripActive, 0.78f, 0.55f, 0.21f, 1.00f);
        style.setColor(Tab, 0.51f, 0.36f, 0.15f, 1.00f);
        style.setColor(TabHovered, 0.91f, 0.64f, 0.13f, 1.00f);
        style.setColor(TabActive, 0.78f, 0.55f, 0.21f, 1.00f);
        style.setColor(TabUnfocused, 0.51f, 0.36f, 0.15f, 1.00f);
        style.setColor(TabUnfocusedActive, 0.51f, 0.36f, 0.15f, 1.00f);
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

        style.setFramePadding(4, 2);
        style.setItemSpacing(10, 2);
        style.setIndentSpacing(12);
        style.setScrollbarSize(10);

        style.setWindowRounding(4);
        style.setFrameRounding(4);
        style.setPopupRounding(4);
        style.setScrollbarRounding(6);
        style.setGrabRounding(4);
        style.setTabRounding(4);

       // style.setWindowTitleAlign(1f, 0.5f);
        style.setWindowMenuButtonPosition(0);
        style.setDisplaySafeAreaPadding(4, 4);

    }

    public static void RedTheme() {
        ImGuiStyle style = ImGui.getStyle();
        style.setColor(Text, 1.00f, 1.00f, 1.00f, 1.00f);
        style.setColor(TextDisabled, 0.73f, 0.75f, 0.74f, 1.00f);
        style.setColor(WindowBg, 0.09f, 0.09f, 0.09f, 0.94f);
        style.setColor(ChildBg, 0.00f, 0.00f, 0.00f, 0.00f);
        style.setColor(PopupBg, 0.08f, 0.08f, 0.08f, 0.94f);
        style.setColor(Border, 0.20f, 0.20f, 0.20f, 0.50f);
        style.setColor(BorderShadow, 0.00f, 0.00f, 0.00f, 0.00f);
        style.setColor(FrameBg, 0.71f, 0.39f, 0.39f, 0.54f);
        style.setColor(FrameBgHovered, 0.84f, 0.66f, 0.66f, 0.40f);
        style.setColor(FrameBgActive, 0.84f, 0.66f, 0.66f, 0.67f);
        style.setColor(TitleBg, 0.47f, 0.22f, 0.22f, 0.67f);
        style.setColor(TitleBgActive, 0.47f, 0.22f, 0.22f, 1.00f);
        style.setColor(TitleBgCollapsed, 0.47f, 0.22f, 0.22f, 0.67f);
        style.setColor(MenuBarBg, 0.34f, 0.16f, 0.16f, 1.00f);
        style.setColor(ScrollbarBg, 0.02f, 0.02f, 0.02f, 0.53f);
        style.setColor(ScrollbarGrab, 0.31f, 0.31f, 0.31f, 1.00f);
        style.setColor(ScrollbarGrabHovered, 0.41f, 0.41f, 0.41f, 1.00f);
        style.setColor(ScrollbarGrabActive, 0.51f, 0.51f, 0.51f, 1.00f);
        style.setColor(CheckMark, 1.00f, 1.00f, 1.00f, 1.00f);
        style.setColor(SliderGrab, 0.71f, 0.39f, 0.39f, 1.00f);
        style.setColor(SliderGrabActive, 0.84f, 0.66f, 0.66f, 1.00f);
        style.setColor(Button, 0.47f, 0.22f, 0.22f, 0.65f);
        style.setColor(ButtonHovered, 0.71f, 0.39f, 0.39f, 0.65f);
        style.setColor(ButtonActive, 0.20f, 0.20f, 0.20f, 0.50f);
        style.setColor(Header, 0.71f, 0.39f, 0.39f, 0.54f);
        style.setColor(HeaderHovered, 0.84f, 0.66f, 0.66f, 0.65f);
        style.setColor(HeaderActive, 0.84f, 0.66f, 0.66f, 0.00f);
        style.setColor(Separator, 0.43f, 0.43f, 0.50f, 0.50f);
        style.setColor(SeparatorHovered, 0.71f, 0.39f, 0.39f, 0.54f);
        style.setColor(SeparatorActive, 0.71f, 0.39f, 0.39f, 0.54f);
        style.setColor(ResizeGrip, 0.71f, 0.39f, 0.39f, 0.54f);
        style.setColor(ResizeGripHovered, 0.84f, 0.66f, 0.66f, 0.66f);
        style.setColor(ResizeGripActive, 0.84f, 0.66f, 0.66f, 0.66f);
        style.setColor(Tab, 0.71f, 0.39f, 0.39f, 0.54f);
        style.setColor(TabHovered, 0.84f, 0.66f, 0.66f, 0.66f);
        style.setColor(TabActive, 0.84f, 0.66f, 0.66f, 0.66f);
        style.setColor(TabUnfocused, 0.07f, 0.10f, 0.15f, 0.97f);
        style.setColor(TabUnfocusedActive, 161f / 255f, 75f / 255f, 75f / 255f, 1.00f);
        style.setColor(PlotLines, 0.61f, 0.61f, 0.61f, 1.00f);
        style.setColor(PlotLinesHovered, 1.00f, 0.43f, 0.35f, 1.00f);
        style.setColor(PlotHistogram, 0.90f, 0.70f, 0.00f, 1.00f);
        style.setColor(PlotHistogramHovered, 1.00f, 0.60f, 0.00f, 1.00f);
        style.setColor(TextSelectedBg, 0.26f, 0.59f, 0.98f, 0.35f);
        style.setColor(DragDropTarget, 1.00f, 1.00f, 0.00f, 0.90f);
        style.setColor(NavHighlight, 0.41f, 0.41f, 0.41f, 1.00f);
        style.setColor(NavWindowingHighlight, 1.00f, 1.00f, 1.00f, 0.70f);
        style.setColor(NavWindowingDimBg, 0.80f, 0.80f, 0.80f, 0.20f);
        style.setColor(ModalWindowDimBg, 0.80f, 0.80f, 0.80f, 0.35f);
    }
}
