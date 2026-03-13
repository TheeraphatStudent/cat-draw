package project.tools;

import project.core.Canvas;
import project.layers.DrawLayer;

import static org.lwjgl.glfw.GLFW.*;

public class TextTool {
    private final ToolManager toolManager;
    private boolean isEditing = false;
    private float textX, textY;
    private float boxWidth = 200;
    private float boxHeight = 30;
    private StringBuilder textBuffer = new StringBuilder();
    private float fontSize = 16.0f;
    private String fontName = "kanit-medium";
    
    private boolean isDraggingHandle = false;
    private int activeHandle = -1;

    public TextTool(ToolManager toolManager) {
        this.toolManager = toolManager;
    }

    public void onMouseDown(Canvas canvas, float x, float y) {
        if (!isEditing) {
            isEditing = true;
            textX = x;
            textY = y;
            textBuffer.setLength(0);
            boxWidth = 200;
            boxHeight = 30;
        } else {
            int handle = getHandleAt(x, y);
            if (handle >= 0) {
                isDraggingHandle = true;
                activeHandle = handle;
            } else if (!isInsideTextBox(x, y)) {
                commitText(canvas);
                isEditing = false;
            }
        }
    }

    public void onMouseDrag(Canvas canvas, float x, float y) {
        if (isDraggingHandle && activeHandle >= 0) {
            resizeBox(x, y);
        }
    }

    public void onMouseUp(Canvas canvas, float x, float y) {
        isDraggingHandle = false;
        activeHandle = -1;
    }

    private boolean isInsideTextBox(float x, float y) {
        return x >= textX && x <= textX + boxWidth &&
               y >= textY && y <= textY + boxHeight;
    }

    private int getHandleAt(float x, float y) {
        float handleSize = 8;
        float[][] handles = {
            {textX + boxWidth - handleSize/2, textY + boxHeight/2 - handleSize/2},
            {textX + boxWidth/2 - handleSize/2, textY + boxHeight - handleSize/2},
            {textX + boxWidth - handleSize/2, textY + boxHeight - handleSize/2}
        };
        
        for (int i = 0; i < handles.length; i++) {
            if (x >= handles[i][0] && x <= handles[i][0] + handleSize &&
                y >= handles[i][1] && y <= handles[i][1] + handleSize) {
                return i;
            }
        }
        return -1;
    }

    private void resizeBox(float x, float y) {
        float minWidth = 50;
        float minHeight = 20;
        
        switch (activeHandle) {
            case 0 -> boxWidth = Math.max(minWidth, x - textX);
            case 1 -> boxHeight = Math.max(minHeight, y - textY);
            case 2 -> {
                boxWidth = Math.max(minWidth, x - textX);
                boxHeight = Math.max(minHeight, y - textY);
            }
        }
    }

    public void onKeyTyped(Canvas canvas, char c) {
        if (isEditing && c >= 32 && c < 127) {
            textBuffer.append(c);
        }
    }

    public void onKeyPressed(Canvas canvas, int key) {
        if (!isEditing) return;

        if (key == GLFW_KEY_BACKSPACE && textBuffer.length() > 0) {
            textBuffer.deleteCharAt(textBuffer.length() - 1);
        } else if (key == GLFW_KEY_ENTER) {
            commitText(canvas);
            isEditing = false;
        } else if (key == GLFW_KEY_ESCAPE) {
            isEditing = false;
            textBuffer.setLength(0);
        }
    }

    private void commitText(Canvas canvas) {
        if (textBuffer.length() == 0) return;
        
        float[] rgba = toolManager.getColorRGBA();
        DrawLayer.Stroke stroke = canvas.getActiveDrawLayer().createStroke(
            rgba[0], rgba[1], rgba[2], rgba[3],
            fontSize,
            false
        );
        stroke.addPoint(textX, textY);
        stroke.text = textBuffer.toString();
        stroke.isText = true;
        stroke.fontName = fontName;
        
        textBuffer.setLength(0);
    }

    public boolean isEditing() { return isEditing; }
    public float getTextX() { return textX; }
    public float getTextY() { return textY; }
    public float getBoxWidth() { return boxWidth; }
    public float getBoxHeight() { return boxHeight; }
    public String getText() { return textBuffer.toString(); }
    public float getFontSize() { return fontSize; }
    public void setFontSize(float size) { this.fontSize = size; }
    public String getFontName() { return fontName; }
    public void setFontName(String name) { this.fontName = name; }
}
