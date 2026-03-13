package project.ui;

import org.lwjgl.nanovg.NVGColor;
import project.core.FontRegistry;
import project.core.Renderer;
import project.tools.ToolManager;

import static org.lwjgl.nanovg.NanoVG.*;

public class ColorPanel {
    public static final int WIDTH = 200;

    private final Renderer renderer;
    private final ToolManager toolManager;

    private String hexInput = "FFFFFF";
    private StringBuilder hexInputBuffer = new StringBuilder("FFFFFF");
    private float opacity = 1.0f;
    private boolean draggingOpacity = false;
    private boolean hexFieldFocused = false;
    private boolean hexInputValid = true;
    private float hexFieldX, hexFieldY, hexFieldWidth, hexFieldHeight;

    private boolean brushSizeFieldFocused = false;
    private StringBuilder brushSizeInputBuffer = new StringBuilder("8");
    private boolean draggingBrushSize = false;
    private float brushSizeFieldX, brushSizeFieldY, brushSizeFieldWidth, brushSizeFieldHeight;
    private float brushSizeSliderX, brushSizeSliderY, brushSizeSliderWidth, brushSizeSliderHeight;

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

        nvgFontFace(nvg, FontRegistry.getFont("Kanit-Regular"));
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

        nvgFontFace(nvg, FontRegistry.getFont("Kanit-Regular"));
        nvgFontSize(nvg, 12);
        nvgFillColor(nvg, Renderer.COLOR_TEXT_SECONDARY);
        nvgText(nvg, x + padding, currentY, "Hex Color");
        currentY += 20;

        hexFieldX = x + padding;
        hexFieldY = currentY;
        hexFieldWidth = WIDTH - padding * 2;
        hexFieldHeight = 32;

        nvgBeginPath(nvg);
        nvgRoundedRect(nvg, hexFieldX, hexFieldY, hexFieldWidth, hexFieldHeight, 4);
        nvgFillColor(nvg, Renderer.COLOR_BACKGROUND);
        nvgFill(nvg);

        NVGColor borderColor = NVGColor.create();
        if (!hexInputValid) {
            Renderer.rgba(255, 76, 76, 255, borderColor);
        } else if (hexFieldFocused) {
            nvgRGBA((byte) 0x6C, (byte) 0x63, (byte) 0xFF, (byte) 0xFF, borderColor);
        } else {
            nvgRGBA((byte) 0xAA, (byte) 0xAA, (byte) 0xCC, (byte) 0xFF, borderColor);
        }
        nvgStrokeColor(nvg, borderColor);
        nvgStrokeWidth(nvg, hexFieldFocused ? 2 : 1);
        nvgStroke(nvg);

        nvgFontFace(nvg, FontRegistry.getFont("Kanit-Medium"));
        nvgFontSize(nvg, 13);
        nvgFillColor(nvg, Renderer.COLOR_TEXT_PRIMARY);
        nvgTextAlign(nvg, NVG_ALIGN_LEFT | NVG_ALIGN_MIDDLE);
        String displayText = "#" + hexInputBuffer.toString();
        if (hexFieldFocused) {
            displayText += "|";
        }
        nvgText(nvg, hexFieldX + 8, hexFieldY + hexFieldHeight / 2, displayText);
        currentY += 48;

        if (toolManager.getActiveTool() == ToolManager.Tool.BRUSH) {
            currentY = renderBrushSizeSection(nvg, x, currentY, padding);
        }

        nvgFontFace(nvg, FontRegistry.getFont("Kanit-Regular"));
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

        nvgFontFace(nvg, FontRegistry.getFont("Kanit-Regular"));
        nvgFontSize(nvg, 14);
        nvgFillColor(nvg, Renderer.COLOR_TEXT_SECONDARY);
        nvgTextAlign(nvg, NVG_ALIGN_LEFT | NVG_ALIGN_TOP);
        nvgText(nvg, x + padding, currentY, String.format("%.0f%%", opacity * 100));

        currentY += 32;

        nvgFontFace(nvg, FontRegistry.getFont("Kanit-Regular"));
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

    private float renderBrushSizeSection(long nvg, float x, float currentY, float padding) {
        nvgFontFace(nvg, FontRegistry.getFont("Kanit-Regular"));
        nvgFontSize(nvg, 14);
        nvgFillColor(nvg, Renderer.COLOR_TEXT_SECONDARY);
        nvgTextAlign(nvg, NVG_ALIGN_LEFT | NVG_ALIGN_TOP);
        nvgText(nvg, x + padding, currentY, "Brush Size");
        currentY += 20;

        brushSizeFieldX = x + padding;
        brushSizeFieldY = currentY;
        brushSizeFieldWidth = WIDTH - padding * 2;
        brushSizeFieldHeight = 32;

        nvgBeginPath(nvg);
        nvgRoundedRect(nvg, brushSizeFieldX, brushSizeFieldY, brushSizeFieldWidth, brushSizeFieldHeight, 4);
        nvgFillColor(nvg, Renderer.COLOR_BACKGROUND);
        nvgFill(nvg);

        NVGColor borderColor = NVGColor.create();
        if (brushSizeFieldFocused) {
            nvgRGBA((byte) 0x6C, (byte) 0x63, (byte) 0xFF, (byte) 0xFF, borderColor);
        } else {
            nvgRGBA((byte) 0xAA, (byte) 0xAA, (byte) 0xCC, (byte) 0xFF, borderColor);
        }
        nvgStrokeColor(nvg, borderColor);
        nvgStrokeWidth(nvg, brushSizeFieldFocused ? 2 : 1);
        nvgStroke(nvg);

        nvgFontFace(nvg, FontRegistry.getFont("Kanit-Medium"));
        nvgFontSize(nvg, 13);
        nvgFillColor(nvg, Renderer.COLOR_TEXT_PRIMARY);
        nvgTextAlign(nvg, NVG_ALIGN_LEFT | NVG_ALIGN_MIDDLE);
        String sizeText = brushSizeFieldFocused ? brushSizeInputBuffer.toString() + "|" : String.valueOf((int) toolManager.getBrushSize());
        nvgText(nvg, brushSizeFieldX + 8, brushSizeFieldY + brushSizeFieldHeight / 2, sizeText + " px");
        currentY += 40;

        brushSizeSliderX = x + padding;
        brushSizeSliderY = currentY;
        brushSizeSliderWidth = WIDTH - padding * 2;
        brushSizeSliderHeight = 8;

        nvgBeginPath(nvg);
        nvgRoundedRect(nvg, brushSizeSliderX, brushSizeSliderY, brushSizeSliderWidth, brushSizeSliderHeight, 4);
        nvgFillColor(nvg, Renderer.COLOR_BACKGROUND);
        nvgFill(nvg);

        float sizeRatio = (toolManager.getBrushSize() - ToolManager.MIN_BRUSH_SIZE) / 
                          (ToolManager.MAX_BRUSH_SIZE - ToolManager.MIN_BRUSH_SIZE);
        nvgBeginPath(nvg);
        nvgRoundedRect(nvg, brushSizeSliderX, brushSizeSliderY, brushSizeSliderWidth * sizeRatio, brushSizeSliderHeight, 4);
        nvgFillColor(nvg, Renderer.COLOR_ACCENT);
        nvgFill(nvg);

        float knobX = brushSizeSliderX + brushSizeSliderWidth * sizeRatio;
        nvgBeginPath(nvg);
        nvgCircle(nvg, knobX, brushSizeSliderY + brushSizeSliderHeight / 2, 8);
        nvgFillColor(nvg, Renderer.COLOR_WHITE);
        nvgFill(nvg);
        currentY += 24;

        nvgFontFace(nvg, FontRegistry.getFont("Kanit-Regular"));
        nvgFontSize(nvg, 12);
        nvgFillColor(nvg, Renderer.COLOR_TEXT_SECONDARY);
        nvgTextAlign(nvg, NVG_ALIGN_LEFT | NVG_ALIGN_TOP);
        nvgText(nvg, x + padding, currentY, String.format("%d px", (int) toolManager.getBrushSize()));
        currentY += 24;

        float previewSize = Math.min(toolManager.getBrushSize(), 80);
        float previewCenterX = x + WIDTH / 2;
        float previewCenterY = currentY + 40;

        NVGColor previewColor = NVGColor.create();
        Renderer.hexColor("#" + hexInput, opacity, previewColor);

        nvgBeginPath(nvg);
        nvgCircle(nvg, previewCenterX, previewCenterY, previewSize / 2);
        nvgFillColor(nvg, previewColor);
        nvgFill(nvg);

        nvgStrokeColor(nvg, Renderer.COLOR_TEXT_SECONDARY);
        nvgStrokeWidth(nvg, 1);
        nvgStroke(nvg);

        currentY += 88;
        return currentY;
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

        float hexFieldYPos = currentY + 3 * (swatchSize + swatchPadding) + 16 + 20;
        if (mouseX >= x + padding && mouseX <= x + WIDTH - padding &&
            mouseY >= hexFieldYPos && mouseY <= hexFieldYPos + 32) {
            hexFieldFocused = true;
            brushSizeFieldFocused = false;
            return true;
        } else {
            hexFieldFocused = false;
        }

        currentY += 3 * (swatchSize + swatchPadding) + 16 + 20 + 48;

        if (toolManager.getActiveTool() == ToolManager.Tool.BRUSH) {
            if (mouseX >= brushSizeFieldX && mouseX <= brushSizeFieldX + brushSizeFieldWidth &&
                mouseY >= brushSizeFieldY && mouseY <= brushSizeFieldY + brushSizeFieldHeight) {
                brushSizeFieldFocused = true;
                hexFieldFocused = false;
                brushSizeInputBuffer = new StringBuilder(String.valueOf((int) toolManager.getBrushSize()));
                return true;
            } else {
                if (brushSizeFieldFocused) {
                    applyBrushSizeInput();
                }
                brushSizeFieldFocused = false;
            }

            if (mouseY >= brushSizeSliderY - 8 && mouseY <= brushSizeSliderY + brushSizeSliderHeight + 8 &&
                mouseX >= brushSizeSliderX && mouseX <= brushSizeSliderX + brushSizeSliderWidth) {
                draggingBrushSize = true;
                updateBrushSizeFromSlider(mouseX);
                return true;
            }

            currentY += 176;
        }

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
        if (draggingBrushSize) {
            updateBrushSizeFromSlider(mouseX);
        }
    }

    public void handleRelease() {
        draggingOpacity = false;
        draggingBrushSize = false;
    }

    private void updateOpacity(float mouseX, float sliderX, float sliderWidth) {
        opacity = Math.max(0, Math.min(1, (mouseX - sliderX) / sliderWidth));
        toolManager.setOpacity(opacity);
    }

    private void updateBrushSizeFromSlider(float mouseX) {
        float ratio = Math.max(0, Math.min(1, (mouseX - brushSizeSliderX) / brushSizeSliderWidth));
        float size = ToolManager.MIN_BRUSH_SIZE + ratio * (ToolManager.MAX_BRUSH_SIZE - ToolManager.MIN_BRUSH_SIZE);
        toolManager.setBrushSize(size);
        brushSizeInputBuffer = new StringBuilder(String.valueOf((int) toolManager.getBrushSize()));
    }

    private void applyBrushSizeInput() {
        try {
            int size = Integer.parseInt(brushSizeInputBuffer.toString().trim());
            toolManager.setBrushSize(size);
        } catch (NumberFormatException e) {
            brushSizeInputBuffer = new StringBuilder(String.valueOf((int) toolManager.getBrushSize()));
        }
    }

    public boolean isInBounds(float mouseX, float mouseY, int windowWidth) {
        return mouseX >= windowWidth - WIDTH && mouseY >= TopBar.HEIGHT;
    }

    public void setHexInput(String hex) {
        this.hexInput = hex.startsWith("#") ? hex.substring(1) : hex;
        this.hexInputBuffer = new StringBuilder(hexInput);
        this.hexInputValid = true;
        toolManager.setActiveColor("#" + hexInput);
    }

    public String getHexInput() {
        return hexInput;
    }

    public boolean isHexFieldFocused() {
        return hexFieldFocused || brushSizeFieldFocused;
    }

    public void unfocusHexField() {
        hexFieldFocused = false;
        brushSizeFieldFocused = false;
    }

    public void onKeyTyped(char c) {
        if (hexFieldFocused) {
            if (isValidHexChar(c) && hexInputBuffer.length() < 8) {
                hexInputBuffer.append(Character.toUpperCase(c));
                validateAndApplyHex();
            }
        } else if (brushSizeFieldFocused) {
            if (c >= '0' && c <= '9' && brushSizeInputBuffer.length() < 3) {
                brushSizeInputBuffer.append(c);
            }
        }
    }

    public void onKeyPressed(int key) {
        if (hexFieldFocused) {
            if (key == org.lwjgl.glfw.GLFW.GLFW_KEY_BACKSPACE && hexInputBuffer.length() > 0) {
                hexInputBuffer.deleteCharAt(hexInputBuffer.length() - 1);
                validateAndApplyHex();
            } else if (key == org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER) {
                if (hexInputValid && hexInputBuffer.length() >= 3) {
                    hexInput = normalizeHex(hexInputBuffer.toString());
                    hexInputBuffer = new StringBuilder(hexInput);
                    toolManager.setActiveColor("#" + hexInput);
                }
                hexFieldFocused = false;
            } else if (key == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE) {
                hexInputBuffer = new StringBuilder(hexInput);
                hexInputValid = true;
                hexFieldFocused = false;
            } else if (key == org.lwjgl.glfw.GLFW.GLFW_KEY_V && 
                       (org.lwjgl.glfw.GLFW.glfwGetKey(org.lwjgl.glfw.GLFW.glfwGetCurrentContext(), 
                        org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_CONTROL) == org.lwjgl.glfw.GLFW.GLFW_PRESS ||
                        org.lwjgl.glfw.GLFW.glfwGetKey(org.lwjgl.glfw.GLFW.glfwGetCurrentContext(), 
                        org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_CONTROL) == org.lwjgl.glfw.GLFW.GLFW_PRESS)) {
                pasteFromClipboard();
            }
        } else if (brushSizeFieldFocused) {
            if (key == org.lwjgl.glfw.GLFW.GLFW_KEY_BACKSPACE && brushSizeInputBuffer.length() > 0) {
                brushSizeInputBuffer.deleteCharAt(brushSizeInputBuffer.length() - 1);
            } else if (key == org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER) {
                applyBrushSizeInput();
                brushSizeFieldFocused = false;
            } else if (key == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE) {
                brushSizeInputBuffer = new StringBuilder(String.valueOf((int) toolManager.getBrushSize()));
                brushSizeFieldFocused = false;
            }
        }
    }

    private void pasteFromClipboard() {
        try {
            String clipboard = org.lwjgl.glfw.GLFW.glfwGetClipboardString(
                org.lwjgl.glfw.GLFW.glfwGetCurrentContext());
            if (clipboard != null) {
                String cleaned = clipboard.replace("#", "").toUpperCase();
                StringBuilder validChars = new StringBuilder();
                for (char c : cleaned.toCharArray()) {
                    if (isValidHexChar(c) && validChars.length() + hexInputBuffer.length() < 8) {
                        validChars.append(c);
                    }
                }
                hexInputBuffer.append(validChars);
                validateAndApplyHex();
            }
        } catch (Exception e) {
            System.err.println("Failed to paste from clipboard: " + e.getMessage());
        }
    }

    private boolean isValidHexChar(char c) {
        return (c >= '0' && c <= '9') || (c >= 'A' && c <= 'F') || (c >= 'a' && c <= 'f');
    }

    private void validateAndApplyHex() {
        String hex = hexInputBuffer.toString();
        
        if (hex.length() < 3) {
            hexInputValid = hex.isEmpty() || hex.chars().allMatch(c -> isValidHexChar((char) c));
            return;
        }

        if (hex.length() == 3 || hex.length() == 4 || hex.length() == 6 || hex.length() == 8) {
            hexInputValid = true;
            String normalized = normalizeHex(hex);
            hexInput = normalized;
            toolManager.setActiveColor("#" + normalized);
        } else {
            hexInputValid = hex.chars().allMatch(c -> isValidHexChar((char) c));
        }
    }

    private String normalizeHex(String hex) {
        if (hex.length() == 3) {
            return "" + hex.charAt(0) + hex.charAt(0) 
                      + hex.charAt(1) + hex.charAt(1) 
                      + hex.charAt(2) + hex.charAt(2);
        } else if (hex.length() == 4) {
            return "" + hex.charAt(0) + hex.charAt(0) 
                      + hex.charAt(1) + hex.charAt(1) 
                      + hex.charAt(2) + hex.charAt(2)
                      + hex.charAt(3) + hex.charAt(3);
        }
        return hex;
    }
}
