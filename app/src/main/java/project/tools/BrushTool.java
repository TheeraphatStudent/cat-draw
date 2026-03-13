package project.tools;

import project.core.Canvas;
import project.layers.DrawLayer;

public class BrushTool {
    private final ToolManager toolManager;
    private DrawLayer.Stroke currentStroke;
    private boolean isDrawing = false;
    private float lastX, lastY;

    public BrushTool(ToolManager toolManager) {
        this.toolManager = toolManager;
    }

    public void onMouseDown(Canvas canvas, float x, float y) {
        float[] rgba = toolManager.getColorRGBA();
        currentStroke = canvas.getActiveDrawLayer().createStroke(
            rgba[0], rgba[1], rgba[2], rgba[3],
            toolManager.getBrushSize(),
            false
        );
        currentStroke.addPoint(x, y);
        lastX = x;
        lastY = y;
        isDrawing = true;
    }

    public void onMouseDrag(Canvas canvas, float x, float y) {
        if (isDrawing && currentStroke != null) {
            interpolatePoints(lastX, lastY, x, y);
            lastX = x;
            lastY = y;
        }
    }

    public void onMouseUp(Canvas canvas, float x, float y) {
        if (isDrawing && currentStroke != null) {
            interpolatePoints(lastX, lastY, x, y);
        }
        currentStroke = null;
        isDrawing = false;
    }

    private void interpolatePoints(float x1, float y1, float x2, float y2) {
        float dist = distance(x1, y1, x2, y2);
        float brushSize = toolManager.getBrushSize();
        int steps = Math.max(1, (int) (dist / (brushSize * 0.3f)));

        for (int i = 1; i <= steps; i++) {
            float t = (float) i / steps;
            float ix = lerp(x1, x2, t);
            float iy = lerp(y1, y2, t);
            currentStroke.addPoint(ix, iy);
        }
    }

    private float distance(float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    private float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    public boolean isDrawing() {
        return isDrawing;
    }
}
