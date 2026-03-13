package project.tools;

import project.core.Canvas;
import project.layers.ImageLayer;
import project.layers.Layer;

import java.util.ArrayList;
import java.util.Iterator;
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
            
            eraseImageLayersAt(canvas, x, y, toolManager.getEraserSize());
        }
    }

    public void onMouseUp(Canvas canvas, float x, float y) {
        if (isErasing) {
            canvas.getActiveDrawLayer().eraseAt(x, y, toolManager.getEraserSize());
            eraseImageLayersAt(canvas, x, y, toolManager.getEraserSize());
        }
        isErasing = false;
        erasePath.clear();
    }

    private void eraseImageLayersAt(Canvas canvas, float x, float y, float radius) {
        Iterator<Layer> it = canvas.getLayers().iterator();
        while (it.hasNext()) {
            Layer layer = it.next();
            if (layer instanceof ImageLayer imageLayer) {
                if (imageLayer.contains(x, y)) {
                    it.remove();
                    System.out.println("Erased ImageLayer at " + x + ", " + y);
                }
            }
        }
    }

    public boolean isErasing() {
        return isErasing;
    }

    public float getCurrentX() { return currentX; }
    public float getCurrentY() { return currentY; }
    public List<float[]> getErasePath() { return erasePath; }
}
