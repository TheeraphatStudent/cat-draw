package project.tools;

import project.core.Canvas;
import project.layers.DrawLayer;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class BucketTool {
    private final ToolManager toolManager;
    private static final int TOLERANCE = 0;

    public BucketTool(ToolManager toolManager) {
        this.toolManager = toolManager;
    }

    public void onMouseDown(Canvas canvas, float x, float y) {
        DrawLayer drawLayer = canvas.getActiveDrawLayer();
        if (drawLayer == null) return;

        float[] rgba = toolManager.getColorRGBA();
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();

        if (x < 0 || y < 0 || x >= canvasWidth || y >= canvasHeight) {
            return;
        }

        int startX = (int) x;
        int startY = (int) y;

        float[] targetColor = getColorAt(drawLayer, startX, startY, canvasWidth, canvasHeight);

        if (colorsMatch(targetColor, rgba, TOLERANCE)) {
            return;
        }

        floodFill(drawLayer, startX, startY, canvasWidth, canvasHeight, targetColor, rgba);
    }

    private void floodFill(DrawLayer drawLayer, int startX, int startY, 
                           int width, int height, float[] targetColor, float[] fillColor) {
        Queue<int[]> queue = new ArrayDeque<>();
        Set<Long> visited = new HashSet<>();

        queue.add(new int[]{startX, startY});
        visited.add(pointKey(startX, startY));

        while (!queue.isEmpty()) {
            int[] point = queue.poll();
            int px = point[0];
            int py = point[1];

            float[] currentColor = getColorAt(drawLayer, px, py, width, height);
            if (!colorsMatch(currentColor, targetColor, TOLERANCE)) {
                continue;
            }

            DrawLayer.Stroke stroke = drawLayer.createStroke(
                fillColor[0], fillColor[1], fillColor[2], fillColor[3],
                1.0f,
                false
            );
            stroke.addPoint(px, py);

            int[][] neighbors = {
                {px + 1, py},
                {px - 1, py},
                {px, py + 1},
                {px, py - 1}
            };

            for (int[] neighbor : neighbors) {
                int nx = neighbor[0];
                int ny = neighbor[1];

                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    long key = pointKey(nx, ny);
                    if (!visited.contains(key)) {
                        visited.add(key);
                        float[] neighborColor = getColorAt(drawLayer, nx, ny, width, height);
                        if (colorsMatch(neighborColor, targetColor, TOLERANCE)) {
                            queue.add(new int[]{nx, ny});
                        }
                    }
                }
            }

            if (visited.size() > 100000) {
                System.err.println("Flood fill limit reached");
                break;
            }
        }
    }

    private long pointKey(int x, int y) {
        return ((long) x << 32) | (y & 0xFFFFFFFFL);
    }

    private float[] getColorAt(DrawLayer drawLayer, int x, int y, int width, int height) {
        for (int i = drawLayer.getStrokes().size() - 1; i >= 0; i--) {
            DrawLayer.Stroke stroke = drawLayer.getStrokes().get(i);
            if (stroke.isText) continue;

            for (DrawLayer.StrokePoint point : stroke.points) {
                float halfSize = stroke.size / 2;
                if (x >= point.x - halfSize && x <= point.x + halfSize &&
                    y >= point.y - halfSize && y <= point.y + halfSize) {
                    return new float[]{stroke.r, stroke.g, stroke.b, stroke.a};
                }
            }
        }

        return new float[]{1.0f, 1.0f, 1.0f, 1.0f};
    }

    private boolean colorsMatch(float[] c1, float[] c2, int tolerance) {
        if (c1 == null || c2 == null) return false;

        float dr = Math.abs(c1[0] - c2[0]) * 255;
        float dg = Math.abs(c1[1] - c2[1]) * 255;
        float db = Math.abs(c1[2] - c2[2]) * 255;

        return dr <= tolerance && dg <= tolerance && db <= tolerance;
    }

    public void onMouseDrag(Canvas canvas, float x, float y) {
    }

    public void onMouseUp(Canvas canvas, float x, float y) {
    }
}
