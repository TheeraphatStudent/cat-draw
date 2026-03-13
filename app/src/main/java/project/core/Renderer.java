package project.core;

import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVGGL3.*;
import static org.lwjgl.opengl.GL11.*;

public class Renderer {
    private long nvg;
    private int windowWidth;
    private int windowHeight;

    public static final NVGColor COLOR_BACKGROUND = NVGColor.create();
    public static final NVGColor COLOR_PANEL = NVGColor.create();
    public static final NVGColor COLOR_ACCENT = NVGColor.create();
    public static final NVGColor COLOR_ACCENT_HOVER = NVGColor.create();
    public static final NVGColor COLOR_TEXT_PRIMARY = NVGColor.create();
    public static final NVGColor COLOR_TEXT_SECONDARY = NVGColor.create();
    public static final NVGColor COLOR_DANGER = NVGColor.create();
    public static final NVGColor COLOR_WHITE = NVGColor.create();
    public static final NVGColor COLOR_CANVAS_BG = NVGColor.create();

    static {
        nvgRGBA((byte) 0x1E, (byte) 0x1E, (byte) 0x2E, (byte) 0xFF, COLOR_BACKGROUND);
        nvgRGBA((byte) 0x2A, (byte) 0x2A, (byte) 0x3D, (byte) 0xFF, COLOR_PANEL);
        nvgRGBA((byte) 0x6C, (byte) 0x63, (byte) 0xFF, (byte) 0xFF, COLOR_ACCENT);
        nvgRGBA((byte) 0x85, (byte) 0x7D, (byte) 0xFF, (byte) 0xFF, COLOR_ACCENT_HOVER);
        nvgRGBA((byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, COLOR_TEXT_PRIMARY);
        nvgRGBA((byte) 0xAA, (byte) 0xAA, (byte) 0xCC, (byte) 0xFF, COLOR_TEXT_SECONDARY);
        nvgRGBA((byte) 0xFF, (byte) 0x4C, (byte) 0x4C, (byte) 0xFF, COLOR_DANGER);
        nvgRGBA((byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, COLOR_WHITE);
        nvgRGBA((byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, COLOR_CANVAS_BG);
    }

    public void init(int width, int height) {
        this.windowWidth = width;
        this.windowHeight = height;

        nvg = nvgCreate(NVG_ANTIALIAS | NVG_STENCIL_STROKES);
        if (nvg == MemoryUtil.NULL) {
            throw new RuntimeException("Could not init NanoVG");
        }

        FontRegistry.init(nvg);
        IconRegistry.init(nvg);
    }

    public void updateSize(int width, int height) {
        this.windowWidth = width;
        this.windowHeight = height;
    }

    public void beginFrame(int width, int height, float pixelRatio) {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_CULL_FACE);
        glDisable(GL_DEPTH_TEST);

        nvgBeginFrame(nvg, width, height, pixelRatio);
    }

    public void endFrame() {
        nvgEndFrame(nvg);
    }

    public void cleanup() {
        nvgDelete(nvg);
    }

    public long getNvg() {
        return nvg;
    }

    public int getWindowWidth() {
        return windowWidth;
    }

    public int getWindowHeight() {
        return windowHeight;
    }

    public static NVGColor rgba(int r, int g, int b, int a, NVGColor color) {
        nvgRGBA((byte) r, (byte) g, (byte) b, (byte) a, color);
        return color;
    }

    public static NVGColor hexColor(String hex, float alpha, NVGColor color) {
        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }
        int r = Integer.parseInt(hex.substring(0, 2), 16);
        int g = Integer.parseInt(hex.substring(2, 4), 16);
        int b = Integer.parseInt(hex.substring(4, 6), 16);
        int a = (int) (alpha * 255);
        return rgba(r, g, b, a, color);
    }
}
