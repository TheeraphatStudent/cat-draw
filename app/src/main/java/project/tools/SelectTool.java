package project.tools;

import project.core.Canvas;
import project.layers.ImageLayer;
import project.layers.Layer;

public class SelectTool {
    private final ToolManager toolManager;
    
    private Layer selectedLayer = null;
    private float dragStartX, dragStartY;
    private float elementStartX, elementStartY;
    private boolean isDragging = false;

    public SelectTool(ToolManager toolManager) {
        this.toolManager = toolManager;
    }

    public void onMouseDown(Canvas canvas, float x, float y) {
        selectedLayer = null;
        isDragging = false;

        for (int i = canvas.getLayers().size() - 1; i >= 0; i--) {
            Layer layer = canvas.getLayers().get(i);
            
            if (layer instanceof ImageLayer imageLayer) {
                if (imageLayer.contains(x, y)) {
                    selectedLayer = layer;
                    dragStartX = x;
                    dragStartY = y;
                    elementStartX = imageLayer.getX();
                    elementStartY = imageLayer.getY();
                    isDragging = true;
                    return;
                }
            }
        }
    }

    public void onMouseDrag(Canvas canvas, float x, float y) {
        if (!isDragging || selectedLayer == null) return;

        float dx = x - dragStartX;
        float dy = y - dragStartY;

        if (selectedLayer instanceof ImageLayer imageLayer) {
            imageLayer.setPosition(elementStartX + dx, elementStartY + dy);
        }
    }

    public void onMouseUp(Canvas canvas, float x, float y) {
        isDragging = false;
    }

    public Layer getSelectedLayer() {
        return selectedLayer;
    }

    public boolean hasSelection() {
        return selectedLayer != null;
    }

    public void clearSelection() {
        selectedLayer = null;
        isDragging = false;
    }

    public float[] getSelectionBounds() {
        if (selectedLayer == null) return null;

        if (selectedLayer instanceof ImageLayer imageLayer) {
            return new float[]{
                imageLayer.getX(),
                imageLayer.getY(),
                imageLayer.getWidth(),
                imageLayer.getHeight()
            };
        }

        return null;
    }

    public boolean isDragging() {
        return isDragging;
    }
}
