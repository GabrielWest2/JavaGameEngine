package editor;

import engine.GameEngine;
import engine.ecs.Component;
import engine.input.Keyboard;
import engine.input.Mouse;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiSelectableFlags;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImString;
import org.lwjgl.glfw.GLFW;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_DELETE;

/**
 * @author gabed
 * @Date 7/22/2022
 */
public class InspectorWindow {
    private static boolean popup = false;

    //private static Texture searchIcon = null;

    private static boolean wasPressed = false;

    private static boolean innerMenuHovered = false;

    private static String search = "";

    private static List<String> items = null;

    private static HashMap<String, Class> possibleComponents;

    public static void render() {
        boolean pressedThisFrame = !wasPressed && Mouse.isMousePressed(0);
        wasPressed = Mouse.isMousePressed(0);
        if(items == null) {
            List<Class> classes = new ArrayList<>(findAllClassesUsingReflectionsLibrary());
            items = classes.stream().map(Class::getSimpleName).collect(Collectors.toList());
            int i = 0;
            possibleComponents = new HashMap<>();
            for(String n : items){
                possibleComponents.put(n, classes.get(i));
                i++;
            }
        }
        //if(searchIcon == null)
        //    searchIcon = TextureLoader.loadTexture("engine/search.png");

        ImGui.begin(FAIcons.ICON_SEARCH + " Inspector");
        //Remove after iterating to prevent java.util.ConcurrentModificationException
        List<Component> componentsToRemove = new ArrayList<>();
        if (ExplorerWindow.selectedEntity != null) {
            ImString string = new ImString(ExplorerWindow.selectedEntity.getName(), 50);
            if (ImGui.inputText("Name", string)){
                ExplorerWindow.selectedEntity.setName(string.get());
            }
            ImGui.sameLine();
            boolean entityLocked = ExplorerWindow.selectedEntity.isLocked();
            char icon = entityLocked ? FAIcons.ICON_LOCK : FAIcons.ICON_LOCK_OPEN;
            if(ImGui.button(icon + "")){
                ExplorerWindow.selectedEntity.setLocked(!ExplorerWindow.selectedEntity.isLocked());
            }
            ImGui.pushStyleColor(ImGuiCol.Button, 255, 0, 0, 255);
            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 138, 6, 6, 255);
            if(ImGui.button("Delete", -1,  30) || Keyboard.isKeyPressed(GLFW_KEY_DELETE)){
                GameEngine.getInstance().loadedScene.removeEntity(ExplorerWindow.selectedEntity);
                ExplorerWindow.selectedEntity = null;
                ImGui.popStyleColor();
                ImGui.popStyleColor();
                ImGui.end();
                return;
            }
            ImGui.popStyleColor();
            ImGui.popStyleColor();


            for (Component component : ExplorerWindow.selectedEntity.getComponents()) {
                if(component.canBeRemoved()){
                    ImBoolean show = new ImBoolean(true);
                    if (ImGui.collapsingHeader(component.getClass().getSimpleName(),  show, ImGuiTreeNodeFlags.DefaultOpen | ImGuiTreeNodeFlags.AllowItemOverlap)) {
                        if(!show.get()){
                            componentsToRemove.add(component);
                            continue;
                        }

                        component.GUI();
                        ImGui.separator();
                    }
                }else{
                    if (ImGui.collapsingHeader(component.getClass().getSimpleName(), ImGuiTreeNodeFlags.DefaultOpen | ImGuiTreeNodeFlags.AllowItemOverlap)) {
                        component.GUI();
                        ImGui.separator();
                    }
                }
            }

            for(Component c : componentsToRemove){
                ExplorerWindow.selectedEntity.removeComponent(c);
            }
            float width = 0.0f;
            width += ImGui.getStyle().getItemSpacingX();
            width += 150.0f;
            width += ImGui.getStyle().getItemSpacingX();
            AlignForWidth(width);
            if (ImGui.button("Add Component", 150, 0)) {
                popup = true;
                search = "";
            }
            if (Keyboard.isKeyPressed(GLFW.GLFW_KEY_ESCAPE)){
                popup = false;
            }
            if (popup) {
                ImGui.setNextWindowSize(250, 300);
                if (ImGui.begin("Add Component", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoDocking | ImGuiWindowFlags.NoCollapse))
                {
                    if(pressedThisFrame && !ImGui.isWindowHovered() && !innerMenuHovered)
                        popup = false;
                    //ImGui.image(searchIcon.textureID(), 20, 20, 0, 0);
                    ImGui.sameLine();

                    ImGui.pushItemWidth(-1);
                    ImString str = new ImString(search, 50);
                    if (ImGui.inputTextWithHint("Hi", "Search...", str)){
                        search = str.get();
                    }

                    ImGui.popItemWidth();


                    ImGui.beginChild("ChildDrawList", -1, 233);
                    innerMenuHovered = ImGui.isWindowHovered();

                    if (ImGui.beginListBox("##draw_list", -1, 232)) {
                        List<String> tempList = possibleComponents.keySet().stream().filter(s -> containsIgnoreCase(s, search)).toList();
                        if(ImGui.isWindowHovered())
                            innerMenuHovered = true;
                        for (String s : tempList) {
                            Class c = possibleComponents.get(s);
                            if (ImGui.selectable(s, true, ExplorerWindow.selectedEntity.getComponent(c) == null ? 0 : ImGuiSelectableFlags.Disabled)) {
                                try {
                                    if (ExplorerWindow.selectedEntity.getComponent(c) == null) {
                                        Component component = (Component) c.getDeclaredConstructor().newInstance();
                                        component.entity = ExplorerWindow.selectedEntity;
                                        ExplorerWindow.selectedEntity.addComponent(component);
                                    }
                                } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                                         InvocationTargetException e) {
                                    throw new RuntimeException(e);
                                }
                                popup = false;
                            }
                        }
                        ImGui.endListBox();
                    }
                        ImGui.endChild();
                    }

                    ImGui.end();
                }


        } else {
            ImGui.text("There are no objects selected.");
        }
        ImGui.end();
    }

    public static Set<Class<? extends Component>> findAllClassesUsingReflectionsLibrary() {
        Reflections reflections = new Reflections("engine.ecs.component");
        return reflections.getSubTypesOf(Component.class);
    }

    static void AlignForWidth(float width)
    {
        float avail = ImGui.getContentRegionAvail().x;
        float off = (avail - width) * 0.5f;
        if (off > 0.0f)
            ImGui.setCursorPosX(ImGui.getCursorPosX() + off);
    }

    public static boolean containsIgnoreCase(String src, String what) {
        final int length = what.length();
        if (length == 0)
            return true; // Empty string is contained

        final char firstLo = Character.toLowerCase(what.charAt(0));
        final char firstUp = Character.toUpperCase(what.charAt(0));

        for (int i = src.length() - length; i >= 0; i--) {
            // Quick check before calling the more expensive regionMatches() method:
            final char ch = src.charAt(i);
            if (ch != firstLo && ch != firstUp)
                continue;

            if (src.regionMatches(true, i, what, 0, length))
                return true;
        }

        return false;
    }
}
