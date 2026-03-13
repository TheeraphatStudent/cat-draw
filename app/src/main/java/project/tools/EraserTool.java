package project.tools;

import project.core.Canvas;

import java.util.ArrayList;
import java.util.List;

public class EraserTool {
    private final ToolManager toolManager;
    private boolean isErasing = false;
    private float startX, startY;
    private float currentX, currentY;
    private List<float[]> erasePath = new ArrayList<>();

    public EraserTool(ToolManager toolManager) {
        this.toolManager = toolManager;
    }

    public void onMouseDown(Canvas canvas, float x, float y) {
        isErasing = true;
        startX = x;
        startY = y;
        currentX = x;
        currentY = y;
        erasePath.clear();
        erasePath.add(new float[]{x, y});
    }

    public void onMouseDrag(Canvas canvas, float x, float y) {
        if (isErasing) {
            currentX = x;
            currentY = y;
            erasePath.add(new float[]{x, y});
            canvas.getActiveDrawLayer().eraseAt(x, y, toolManager.getEraserSize());
        }
    }

    public void onMouseUp(Canvas canvas, float x, float y) {
        if (isErasing) {
            canvas.getActiveDrawLayer().eraseAt(x, y, toolManager.getEraserSize());
        }
        isErasing = false;
        erasePath.clear();
    }

    public boolean isErasing() {
        return isErasing;
    }

    public float getCurrentX() { return currentX; }
    public float getCurrentY() { return currentY; }
    public List<float[]> getErasePath() { return erasePath; }
}
