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

        float[] colorAtClick = getColorAtPoint(drawLayer, (int) x, (int) y);
        float[] bgColor = canvas.getBackgroundColor();

        boolean isBackground = (colorAtClick == null) || 
            (Math.abs(colorAtClick[0] - bgColor[0]) < 0.01f &&
             Math.abs(colorAtClick[1] - bgColor[1]) < 0.01f &&
             Math.abs(colorAtClick[2] - bgColor[2]) < 0.01f);

        if (isBackground) {
            canvas.setBackgroundColor(rgba[0], rgba[1], rgba[2], rgba[3]);
            System.out.println("BucketTool: Changed background color");
        } else {
            drawLayer.createFillStroke(
                rgba[0], rgba[1], rgba[2], rgba[3],
                0, 0, canvasWidth, canvasHeight
            );
            System.out.println("BucketTool: Created fill " + canvasWidth + "x" + canvasHeight);
        }
    }

    private float[] getColorAtPoint(DrawLayer drawLayer, int x, int y) {
        for (int i = drawLayer.getStrokes().size() - 1; i >= 0; i--) {
            DrawLayer.Stroke stroke = drawLayer.getStrokes().get(i);
            if (stroke.isText) continue;
            
            if (stroke.isFill) {
                if (x >= stroke.fillX && x <= stroke.fillX + stroke.fillW &&
                    y >= stroke.fillY && y <= stroke.fillY + stroke.fillH) {
                    return new float[]{stroke.r, stroke.g, stroke.b, stroke.a};
                }
                continue;
            }

            for (DrawLayer.StrokePoint point : stroke.points) {
                float halfSize = stroke.size / 2;
                if (x >= point.x - halfSize && x <= point.x + halfSize &&
                    y >= point.y - halfSize && y <= point.y + halfSize) {
                    return new float[]{stroke.r, stroke.g, stroke.b, stroke.a};
                }
            }
        }
        return null;
    }

    public void onMouseDrag(Canvas canvas, float x, float y) {
    }

    public void onMouseUp(Canvas canvas, float x, float y) {
    }
}
