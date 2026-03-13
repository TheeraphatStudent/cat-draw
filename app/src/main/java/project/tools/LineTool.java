package project.tools;

import project.core.Canvas;
import project.layers.DrawLayer;

import java.util.ArrayList;
import java.util.List;

public class LineTool {
    private final ToolManager toolManager;
    private boolean isDrawing = false;
    private float startX, startY;
    private float endX, endY;

    public LineTool(ToolManager toolManager) {
        this.toolManager = toolManager;
    }

    public void onMouseDown(Canvas canvas, float x, float y) {
        isDrawing = true;
        startX = x;
        startY = y;
        endX = x;
        endY = y;
    }

    public void onMouseDrag(Canvas canvas, float x, float y) {
        if (isDrawing) {
            endX = x;
            endY = y;
        }
    }

    public void onMouseUp(Canvas canvas, float x, float y) {
        if (isDrawing) {
            endX = x;
            endY = y;

            List<float[]> points = computeDDALine(startX, startY, endX, endY);

            float[] rgba = toolManager.getColorRGBA();
            DrawLayer.Stroke stroke = canvas.getActiveDrawLayer().createStroke(
                rgba[0], rgba[1], rgba[2], rgba[3],
                toolManager.getBrushSize(),
                true
            );

            for (float[] p : points) {
                stroke.addPoint(p[0], p[1]);
            }
        }
        isDrawing = false;
    }

    private List<float[]> computeDDALine(float x1, float y1, float x2, float y2) {
        List<float[]> points = new ArrayList<>();

        float dx = x2 - x1;
        float dy = y2 - y1;

        int steps = (int) Math.max(Math.abs(dx), Math.abs(dy));
        if (steps == 0) {
            points.add(new float[]{x1, y1});
            return points;
        }

        float xIncrement = dx / steps;
        float yIncrement = dy / steps;

        float x = x1;
        float y = y1;

        for (int i = 0; i <= steps; i++) {
            points.add(new float[]{Math.round(x), Math.round(y)});
            x += xIncrement;
            y += yIncrement;
        }

        return points;
    }

    public boolean isDrawing() { return isDrawing; }
    public float getStartX() { return startX; }
    public float getStartY() { return startY; }
    public float getEndX() { return endX; }
    public float getEndY() { return endY; }
}
