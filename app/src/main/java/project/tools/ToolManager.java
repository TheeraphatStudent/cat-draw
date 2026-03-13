package project.tools;

import project.core.Canvas;

public class ToolManager {
    public enum Tool {
        BRUSH,
        ERASER,
        LINE,
        TEXT,
        BUCKET,
        SELECT
    }

    private Tool activeTool = Tool.BRUSH;
    private float brushSize = 8.0f;
    private float eraserSize = 20.0f;
    private String activeColor = "#FFFFFF";
    private float opacity = 1.0f;

    private BrushTool brushTool;
    private EraserTool eraserTool;
    private LineTool lineTool;
    private TextTool textTool;
    private BucketTool bucketTool;
    private SelectTool selectTool;

    public ToolManager() {
        brushTool = new BrushTool(this);
        eraserTool = new EraserTool(this);
        lineTool = new LineTool(this);
        textTool = new TextTool(this);
        bucketTool = new BucketTool(this);
        selectTool = new SelectTool(this);
    }

    public void setActiveTool(Tool tool) {
        this.activeTool = tool;
    }

    public Tool getActiveTool() {
        return activeTool;
    }

    public void onMouseDown(Canvas canvas, float x, float y) {
        switch (activeTool) {
            case BRUSH -> brushTool.onMouseDown(canvas, x, y);
            case ERASER -> eraserTool.onMouseDown(canvas, x, y);
            case LINE -> lineTool.onMouseDown(canvas, x, y);
            case TEXT -> textTool.onMouseDown(canvas, x, y);
            case BUCKET -> bucketTool.onMouseDown(canvas, x, y);
            case SELECT -> selectTool.onMouseDown(canvas, x, y);
        }
    }

    public void onMouseDrag(Canvas canvas, float x, float y) {
        switch (activeTool) {
            case BRUSH -> brushTool.onMouseDrag(canvas, x, y);
            case ERASER -> eraserTool.onMouseDrag(canvas, x, y);
            case LINE -> lineTool.onMouseDrag(canvas, x, y);
            case TEXT -> textTool.onMouseDrag(canvas, x, y);
            case BUCKET -> bucketTool.onMouseDrag(canvas, x, y);
            case SELECT -> selectTool.onMouseDrag(canvas, x, y);
        }
    }

    public void onMouseUp(Canvas canvas, float x, float y) {
        switch (activeTool) {
            case BRUSH -> brushTool.onMouseUp(canvas, x, y);
            case ERASER -> eraserTool.onMouseUp(canvas, x, y);
            case LINE -> lineTool.onMouseUp(canvas, x, y);
            case TEXT -> textTool.onMouseUp(canvas, x, y);
            case BUCKET -> bucketTool.onMouseUp(canvas, x, y);
            case SELECT -> selectTool.onMouseUp(canvas, x, y);
        }
    }

    public void onKeyTyped(Canvas canvas, char c) {
        if (activeTool == Tool.TEXT) {
            textTool.onKeyTyped(canvas, c);
        }
    }

    public void onKeyPressed(Canvas canvas, int key) {
        if (activeTool == Tool.TEXT) {
            textTool.onKeyPressed(canvas, key);
        }
    }

    public static final float MIN_BRUSH_SIZE = 1.0f;
    public static final float MAX_BRUSH_SIZE = 128.0f;

    public float getBrushSize() { return brushSize; }
    public void setBrushSize(float size) { 
        this.brushSize = Math.max(MIN_BRUSH_SIZE, Math.min(MAX_BRUSH_SIZE, size)); 
    }

    public float getEraserSize() { return eraserSize; }
    public void setEraserSize(float size) { this.eraserSize = size; }

    public String getActiveColor() { return activeColor; }
    public void setActiveColor(String color) { this.activeColor = color; }

    public float getOpacity() { return opacity; }
    public void setOpacity(float opacity) { this.opacity = opacity; }

    public float[] getColorRGBA() {
        String hex = activeColor.startsWith("#") ? activeColor.substring(1) : activeColor;
        float r = Integer.parseInt(hex.substring(0, 2), 16) / 255f;
        float g = Integer.parseInt(hex.substring(2, 4), 16) / 255f;
        float b = Integer.parseInt(hex.substring(4, 6), 16) / 255f;
        return new float[]{r, g, b, opacity};
    }

    public BrushTool getBrushTool() { return brushTool; }
    public EraserTool getEraserTool() { return eraserTool; }
    public LineTool getLineTool() { return lineTool; }
    public TextTool getTextTool() { return textTool; }
    public BucketTool getBucketTool() { return bucketTool; }
    public SelectTool getSelectTool() { return selectTool; }
}
