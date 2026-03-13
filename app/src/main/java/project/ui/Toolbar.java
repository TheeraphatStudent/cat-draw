package project.ui;

import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGPaint;
import project.core.FontRegistry;
import project.core.IconRegistry;
import project.core.Renderer;
import project.tools.ToolManager;

import static org.lwjgl.nanovg.NanoVG.*;

public class Toolbar {
    public static final int WIDTH = 60;

    private final Renderer renderer;
    private final ToolManager toolManager;

    private static final String[] TOOL_LABELS = {"Brush", "Eraser", "Line", "Text", "Fill", "Select"};
    private static final String[] TOOL_ICONS = {"brush", "eraser", "line", "text", "bucket", "mouse"};
    private static final ToolManager.Tool[] TOOLS = {
        ToolManager.Tool.BRUSH,
        ToolManager.Tool.ERASER,
        ToolManager.Tool.LINE,
        ToolManager.Tool.TEXT,
        ToolManager.Tool.BUCKET,
        ToolManager.Tool.SELECT
    };

    public Toolbar(Renderer renderer, ToolManager toolManager) {
        this.renderer = renderer;
        this.toolManager = toolManager;
    }

    public void render(int windowHeight, int windowWidth) {
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
            drawToolButton(nvg, padding, y, buttonSize, TOOL_ICONS[i], TOOL_LABELS[i], isActive, TOOLS[i]);
        }

        renderActiveToolIndicator(nvg, windowWidth);
    }

    private void drawToolButton(long nvg, float x, float y, float size, String iconName, String label, boolean active, ToolManager.Tool tool) {
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

        if (IconRegistry.hasIcon(iconName)) {
            int iconId = IconRegistry.get(iconName);
            float iconSize = 24;
            float iconX = x + (size - iconSize) / 2;
            float iconY = y + (size - iconSize) / 2;
            
            NVGPaint paint = NVGPaint.calloc();
            nvgImagePattern(nvg, iconX, iconY, iconSize, iconSize, 0, iconId, 1.0f, paint);
            nvgBeginPath(nvg);
            nvgRect(nvg, iconX, iconY, iconSize, iconSize);
            nvgFillPaint(nvg, paint);
            nvgFill(nvg);
            paint.free();
        } else {
            nvgFontFace(nvg, FontRegistry.getFont("Kanit-Medium"));
            nvgFontSize(nvg, 11);
            nvgFillColor(nvg, textColor);
            nvgTextAlign(nvg, NVG_ALIGN_CENTER | NVG_ALIGN_MIDDLE);
            nvgText(nvg, x + size / 2, y + size / 2, label);
        }
    }

    private void renderActiveToolIndicator(long nvg, int windowWidth) {
        float indicatorHeight = 28;
        float indicatorY = TopBar.HEIGHT;
        float indicatorX = WIDTH;
        float indicatorWidth = windowWidth - WIDTH - ColorPanel.WIDTH;

        nvgBeginPath(nvg);
        nvgRect(nvg, indicatorX, indicatorY, indicatorWidth, indicatorHeight);
        NVGColor indicatorBg = NVGColor.create();
        Renderer.rgba(30, 30, 46, 220, indicatorBg);
        nvgFillColor(nvg, indicatorBg);
        nvgFill(nvg);

        ToolManager.Tool activeTool = toolManager.getActiveTool();
        int toolIndex = getToolIndex(activeTool);
        String toolName = toolIndex >= 0 ? TOOL_LABELS[toolIndex] : "Unknown";
        float brushSize = toolManager.getBrushSize();

        nvgFontFace(nvg, FontRegistry.getFont("Kanit-Regular"));
        nvgFontSize(nvg, 12);
        nvgFillColor(nvg, Renderer.COLOR_TEXT_SECONDARY);
        nvgTextAlign(nvg, NVG_ALIGN_LEFT | NVG_ALIGN_MIDDLE);
        
        String statusText = "Tool: " + toolName + "  Size: " + (int) brushSize + "px";
        nvgText(nvg, indicatorX + 12, indicatorY + indicatorHeight / 2, statusText);
    }

    private int getToolIndex(ToolManager.Tool tool) {
        for (int i = 0; i < TOOLS.length; i++) {
            if (TOOLS[i] == tool) return i;
        }
        return -1;
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
