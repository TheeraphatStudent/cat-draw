package project.ui;

import org.lwjgl.nanovg.NVGColor;
import project.core.Renderer;
import project.tools.ToolManager;

import static org.lwjgl.nanovg.NanoVG.*;

public class Toolbar {
    public static final int WIDTH = 60;

    private final Renderer renderer;
    private final ToolManager toolManager;

    private static final String[] TOOL_LABELS = {"B", "E", "L", "T"};
    private static final ToolManager.Tool[] TOOLS = {
        ToolManager.Tool.BRUSH,
        ToolManager.Tool.ERASER,
        ToolManager.Tool.LINE,
        ToolManager.Tool.TEXT
    };

    public Toolbar(Renderer renderer, ToolManager toolManager) {
        this.renderer = renderer;
        this.toolManager = toolManager;
    }

    public void render(int windowHeight) {
        long nvg = renderer.getNvg();

        nvgBeginPath(nvg);
        nvgRect(nvg, 0, TopBar.HEIGHT, WIDTH, windowHeight - TopBar.HEIGHT);
        nvgFillColor(nvg, Renderer.COLOR_PANEL);
        nvgFill(nvg);

        float buttonSize = 44;
        float padding = 8;
        float startY = TopBar.HEIGHT + padding;

        for (int i = 0; i < TOOLS.length; i++) {
            float y = startY + i * (buttonSize + padding);
            boolean isActive = toolManager.getActiveTool() == TOOLS[i];
            drawToolButton(nvg, padding, y, buttonSize, TOOL_LABELS[i], isActive, TOOLS[i]);
        }
    }

    private void drawToolButton(long nvg, float x, float y, float size, String label, boolean active, ToolManager.Tool tool) {
        NVGColor bgColor = active ? Renderer.COLOR_ACCENT : Renderer.COLOR_BACKGROUND;
        NVGColor textColor = tool == ToolManager.Tool.ERASER && active ? 
            Renderer.COLOR_DANGER : Renderer.COLOR_TEXT_PRIMARY;

        nvgBeginPath(nvg);
        nvgRoundedRect(nvg, x, y, size, size, 8);
        nvgFillColor(nvg, bgColor);
        nvgFill(nvg);

        if (active) {
            nvgStrokeColor(nvg, Renderer.COLOR_ACCENT_HOVER);
            nvgStrokeWidth(nvg, 2);
            nvgStroke(nvg);
        }

        nvgFontFace(nvg, "kanit");
        nvgFontSize(nvg, 20);
        nvgFillColor(nvg, textColor);
        nvgTextAlign(nvg, NVG_ALIGN_CENTER | NVG_ALIGN_MIDDLE);
        nvgText(nvg, x + size / 2, y + size / 2, label);
    }

    public boolean handleClick(float mouseX, float mouseY) {
        if (mouseX > WIDTH || mouseY < TopBar.HEIGHT) return false;

        float buttonSize = 44;
        float padding = 8;
        float startY = TopBar.HEIGHT + padding;

        for (int i = 0; i < TOOLS.length; i++) {
            float y = startY + i * (buttonSize + padding);
            if (mouseX >= padding && mouseX <= padding + buttonSize &&
                mouseY >= y && mouseY <= y + buttonSize) {
                toolManager.setActiveTool(TOOLS[i]);
                return true;
            }
        }

        return false;
    }

    public boolean isInBounds(float mouseX, float mouseY) {
        return mouseX <= WIDTH && mouseY >= TopBar.HEIGHT;
    }
}
