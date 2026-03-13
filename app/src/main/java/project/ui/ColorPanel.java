package project.ui;

import org.lwjgl.nanovg.NVGColor;
import project.core.Renderer;
import project.tools.ToolManager;

import static org.lwjgl.nanovg.NanoVG.*;

public class ColorPanel {
    public static final int WIDTH = 200;

    private final Renderer renderer;
    private final ToolManager toolManager;

    private String hexInput = "FFFFFF";
    private float opacity = 1.0f;
    private boolean draggingOpacity = false;

    private static final String[] PRESET_COLORS = {
        "#FFFFFF", "#000000", "#FF0000", "#00FF00",
        "#0000FF", "#FFFF00", "#FF00FF", "#00FFFF",
        "#FFA500", "#800080", "#FFC0CB", "#808080"
    };

    public ColorPanel(Renderer renderer, ToolManager toolManager) {
        this.renderer = renderer;
        this.toolManager = toolManager;
    }

    public void render(int windowWidth, int windowHeight) {
        long nvg = renderer.getNvg();
        float x = windowWidth - WIDTH;
        float y = TopBar.HEIGHT;
        float height = windowHeight - TopBar.HEIGHT;

        nvgBeginPath(nvg);
        nvgRect(nvg, x, y, WIDTH, height);
        nvgFillColor(nvg, Renderer.COLOR_PANEL);
        nvgFill(nvg);

        float padding = 12;
        float currentY = y + padding;

        nvgFontFace(nvg, "kanit");
        nvgFontSize(nvg, 16);
        nvgFillColor(nvg, Renderer.COLOR_TEXT_PRIMARY);
        nvgTextAlign(nvg, NVG_ALIGN_LEFT | NVG_ALIGN_TOP);
        nvgText(nvg, x + padding, currentY, "Color");
        currentY += 24;

        float swatchSize = 36;
        float swatchPadding = 6;
        int cols = 4;

        for (int i = 0; i < PRESET_COLORS.length; i++) {
            int row = i / cols;
            int col = i % cols;
            float sx = x + padding + col * (swatchSize + swatchPadding);
            float sy = currentY + row * (swatchSize + swatchPadding);

            NVGColor color = NVGColor.create();
            Renderer.hexColor(PRESET_COLORS[i], 1.0f, color);

            nvgBeginPath(nvg);
            nvgRoundedRect(nvg, sx, sy, swatchSize, swatchSize, 4);
            nvgFillColor(nvg, color);
            nvgFill(nvg);

            if (("#" + hexInput).equalsIgnoreCase(PRESET_COLORS[i])) {
                nvgStrokeColor(nvg, Renderer.COLOR_ACCENT);
                nvgStrokeWidth(nvg, 2);
                nvgStroke(nvg);
            }
        }

        currentY += 3 * (swatchSize + swatchPadding) + 16;

        nvgFontFace(nvg, "kanit");
        nvgFontSize(nvg, 14);
        nvgFillColor(nvg, Renderer.COLOR_TEXT_SECONDARY);
        nvgText(nvg, x + padding, currentY, "Hex");
        currentY += 20;

        nvgBeginPath(nvg);
        nvgRoundedRect(nvg, x + padding, currentY, WIDTH - padding * 2, 32, 4);
        nvgFillColor(nvg, Renderer.COLOR_BACKGROUND);
        nvgFill(nvg);

        nvgFontFace(nvg, "roboto");
        nvgFontSize(nvg, 16);
        nvgFillColor(nvg, Renderer.COLOR_TEXT_PRIMARY);
        nvgTextAlign(nvg, NVG_ALIGN_LEFT | NVG_ALIGN_MIDDLE);
        nvgText(nvg, x + padding + 8, currentY + 16, "#" + hexInput);
        currentY += 48;

        nvgFontFace(nvg, "kanit");
        nvgFontSize(nvg, 14);
        nvgFillColor(nvg, Renderer.COLOR_TEXT_SECONDARY);
        nvgTextAlign(nvg, NVG_ALIGN_LEFT | NVG_ALIGN_TOP);
        nvgText(nvg, x + padding, currentY, "Opacity");
        currentY += 20;

        float sliderWidth = WIDTH - padding * 2;
        float sliderHeight = 8;

        nvgBeginPath(nvg);
        nvgRoundedRect(nvg, x + padding, currentY, sliderWidth, sliderHeight, 4);
        nvgFillColor(nvg, Renderer.COLOR_BACKGROUND);
        nvgFill(nvg);

        nvgBeginPath(nvg);
        nvgRoundedRect(nvg, x + padding, currentY, sliderWidth * opacity, sliderHeight, 4);
        nvgFillColor(nvg, Renderer.COLOR_ACCENT);
        nvgFill(nvg);

        float knobX = x + padding + sliderWidth * opacity;
        nvgBeginPath(nvg);
        nvgCircle(nvg, knobX, currentY + sliderHeight / 2, 8);
        nvgFillColor(nvg, Renderer.COLOR_WHITE);
        nvgFill(nvg);

        currentY += 32;

        nvgFontFace(nvg, "roboto");
        nvgFontSize(nvg, 14);
        nvgFillColor(nvg, Renderer.COLOR_TEXT_SECONDARY);
        nvgTextAlign(nvg, NVG_ALIGN_LEFT | NVG_ALIGN_TOP);
        nvgText(nvg, x + padding, currentY, String.format("%.0f%%", opacity * 100));

        currentY += 32;

        nvgFontFace(nvg, "kanit");
        nvgFontSize(nvg, 14);
        nvgFillColor(nvg, Renderer.COLOR_TEXT_SECONDARY);
        nvgText(nvg, x + padding, currentY, "Preview");
        currentY += 20;

        NVGColor previewColor = NVGColor.create();
        Renderer.hexColor("#" + hexInput, opacity, previewColor);

        nvgBeginPath(nvg);
        nvgRoundedRect(nvg, x + padding, currentY, WIDTH - padding * 2, 48, 8);
        nvgFillColor(nvg, previewColor);
        nvgFill(nvg);

        nvgStrokeColor(nvg, Renderer.COLOR_TEXT_SECONDARY);
        nvgStrokeWidth(nvg, 1);
        nvgStroke(nvg);
    }

    public boolean handleClick(float mouseX, float mouseY, int windowWidth, int windowHeight) {
        float x = windowWidth - WIDTH;
        if (mouseX < x || mouseY < TopBar.HEIGHT) return false;

        float padding = 12;
        float currentY = TopBar.HEIGHT + padding + 24;

        float swatchSize = 36;
        float swatchPadding = 6;
        int cols = 4;

        for (int i = 0; i < PRESET_COLORS.length; i++) {
            int row = i / cols;
            int col = i % cols;
            float sx = x + padding + col * (swatchSize + swatchPadding);
            float sy = currentY + row * (swatchSize + swatchPadding);

            if (mouseX >= sx && mouseX <= sx + swatchSize &&
                mouseY >= sy && mouseY <= sy + swatchSize) {
                String color = PRESET_COLORS[i];
                hexInput = color.startsWith("#") ? color.substring(1) : color;
                toolManager.setActiveColor("#" + hexInput);
                return true;
            }
        }

        currentY += 3 * (swatchSize + swatchPadding) + 16 + 20 + 48;

        float sliderY = currentY;
        float sliderWidth = WIDTH - padding * 2;
        float sliderHeight = 8;

        if (mouseY >= sliderY - 8 && mouseY <= sliderY + sliderHeight + 8) {
            draggingOpacity = true;
            updateOpacity(mouseX, x + padding, sliderWidth);
            return true;
        }

        return false;
    }

    public void handleDrag(float mouseX, float mouseY, int windowWidth) {
        if (draggingOpacity) {
            float x = windowWidth - WIDTH;
            float padding = 12;
            float sliderWidth = WIDTH - padding * 2;
            updateOpacity(mouseX, x + padding, sliderWidth);
        }
    }

    public void handleRelease() {
        draggingOpacity = false;
    }

    private void updateOpacity(float mouseX, float sliderX, float sliderWidth) {
        opacity = Math.max(0, Math.min(1, (mouseX - sliderX) / sliderWidth));
        toolManager.setOpacity(opacity);
    }

    public boolean isInBounds(float mouseX, float mouseY, int windowWidth) {
        return mouseX >= windowWidth - WIDTH && mouseY >= TopBar.HEIGHT;
    }

    public void setHexInput(String hex) {
        this.hexInput = hex.startsWith("#") ? hex.substring(1) : hex;
        toolManager.setActiveColor("#" + hexInput);
    }

    public String getHexInput() {
        return hexInput;
    }
}
