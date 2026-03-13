package project.tools;

import project.core.Canvas;
import project.layers.DrawLayer;

import static org.lwjgl.glfw.GLFW.*;

public class TextTool {
    private final ToolManager toolManager;
    private boolean isEditing = false;
    private float textX, textY;
    private StringBuilder textBuffer = new StringBuilder();
    private float fontSize = 24.0f;

    public TextTool(ToolManager toolManager) {
        this.toolManager = toolManager;
    }

    public void onMouseDown(Canvas canvas, float x, float y) {
        if (!isEditing) {
            isEditing = true;
            textX = x;
            textY = y;
            textBuffer.setLength(0);
        } else {
            commitText(canvas);
            isEditing = false;
        }
    }

    public void onMouseDrag(Canvas canvas, float x, float y) {
    }

    public void onMouseUp(Canvas canvas, float x, float y) {
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
    }

    public boolean isEditing() { return isEditing; }
    public float getTextX() { return textX; }
    public float getTextY() { return textY; }
    public String getText() { return textBuffer.toString(); }
    public float getFontSize() { return fontSize; }
    public void setFontSize(float size) { this.fontSize = size; }
}
