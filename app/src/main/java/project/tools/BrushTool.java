package project.tools;

import project.core.Canvas;
import project.layers.DrawLayer;

public class BrushTool {
    private final ToolManager toolManager;
    private DrawLayer.Stroke currentStroke;
    private boolean isDrawing = false;

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
        isDrawing = true;
    }

    public void onMouseDrag(Canvas canvas, float x, float y) {
        if (isDrawing && currentStroke != null) {
            currentStroke.addPoint(x, y);
        }
    }

    public void onMouseUp(Canvas canvas, float x, float y) {
        if (isDrawing && currentStroke != null) {
            currentStroke.addPoint(x, y);
        }
        currentStroke = null;
        isDrawing = false;
    }

    public boolean isDrawing() {
        return isDrawing;
    }
}
