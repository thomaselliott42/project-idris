//package com.main;
//
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.InputProcessor;
//import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
//import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
//import com.jcraft.jogg
//import imgui.ImGui;
//import imgui.ImGuiIO;
//import imgui.gl3.ImGuiImplGl3;
//import imgui.glfw.ImGuiImplGlfw;
//
//public class ImGuiHandler {
//    private static ImGuiImplGlfw imGuiGlfw;
//    private static ImGuiImplGl3 imGuiGl3;
//    private static InputProcessor tmpProcessor;
//
//    public static void initImGui() {
//        imGuiGlfw = new ImGuiImplGlfw();
//        imGuiGl3 = new ImGuiImplGl3();
//
//        long windowHandle = ((Lwjgl3Graphics) Gdx.graphics).getWindow().getWindowHandle();
//        ImGui.createContext();
//        ImGuiIO io = ImGui.getIO();
//        io.setIniFilename(null);
//        io.getFonts().addFontDefault();
//        io.getFonts().build();
//
//        imGuiGlfw.init(windowHandle, true);
//        imGuiGl3.init("#version 150");
//    }
//
//    public static void startImGui() {
//        if (tmpProcessor != null) {
//            Gdx.input.setInputProcessor(tmpProcessor);
//            tmpProcessor = null;
//        }
//
//        imGuiGlfw.newFrame();
//        imGuiGlfw.newFrame();
//        ImGui.newFrame();
//    }
//
//    public static void renderUI() {
//        // Example UI
//        ImGui.begin("Map Maker Debug");
//        ImGui.text("Press L to switch view.");
//        ImGui.text("Selected tile: ");
//        ImGui.end();
//    }
//
//    public static void endImGui() {
//        ImGui.render();
//        imGuiGl3.renderDrawData(ImGui.getDrawData());
//
//        if (ImGui.getIO().getWantCaptureKeyboard() || ImGui.getIO().getWantCaptureMouse()) {
//            tmpProcessor = Gdx.input.getInputProcessor();
//            Gdx.input.setInputProcessor(null);
//        }
//    }
//
//    public static void disposeImGui() {
//        imGuiGl3.dispose();
//        imGuiGlfw.dispose();
//        ImGui.destroyContext();
//    }
//}
