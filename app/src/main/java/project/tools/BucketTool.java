package project.tools;

import project.core.Canvas;
import project.layers.DrawLayer;

public class BucketTool {
    private final ToolManager toolManager;

    public BucketTool(ToolManager toolManager) {
        this.toolManager = toolManager;
    }

    public void onMouseDown(Canvas canvas, float x, float y) {
        System.out.println("BucketTool clicked at: " + x + ", " + y);
        
        DrawLayer drawLayer = canvas.getActiveDrawLayer();
        if (drawLayer == null) {
            System.err.println("BucketTool: No active draw layer");
            return;
        }

        float[] rgba = toolManager.getColorRGBA();
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();

        if (x < 0 || y < 0 || x >= canvasWidth || y >= canvasHeight) {
            System.out.println("BucketTool: Click outside canvas bounds");
            return;
        }

        float fillSize = Math.max(canvasWidth, canvasHeight);
        DrawLayer.Stroke fillStroke = drawLayer.createFillStroke(
            rgba[0], rgba[1], rgba[2], rgba[3],
            0, 0, canvasWidth, canvasHeight
        );
        
        System.out.println("BucketTool: Created fill " + canvasWidth + "x" + canvasHeight);
    }

    public void onMouseDrag(Canvas canvas, float x, float y) {
    }

    public void onMouseUp(Canvas canvas, float x, float y) {
    }
}
