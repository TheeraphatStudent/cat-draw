package project.layers;

import org.lwjgl.nanovg.NVGColor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.lwjgl.nanovg.NanoVG.*;

public class DrawLayer implements Layer {
    private final List<Stroke> strokes = new ArrayList<>();

    public static class StrokePoint {
        public float x, y;
        public StrokePoint(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    public static class Stroke {
        public List<StrokePoint> points = new ArrayList<>();
        public float r, g, b, a;
        public float size;
        public boolean isLine;
        public boolean isText = false;
        public String text = "";
        public String fontName = "kanit-medium";

        public Stroke(float r, float g, float b, float a, float size, boolean isLine) {
            this.r = r;
            this.g = g;
            this.b = b;
            this.a = a;
            this.size = size;
            this.isLine = isLine;
        }

        public void addPoint(float x, float y) {
            points.add(new StrokePoint(x, y));
        }

        public boolean intersectsRect(float rx, float ry, float rw, float rh) {
            for (StrokePoint p : points) {
                if (p.x >= rx && p.x <= rx + rw && p.y >= ry && p.y <= ry + rh) {
                    return true;
                }
            }
            return false;
        }
    }

    public Stroke createStroke(float r, float g, float b, float a, float size, boolean isLine) {
        Stroke stroke = new Stroke(r, g, b, a, size, isLine);
        strokes.add(stroke);
        return stroke;
    }

    public void eraseAt(float x, float y, float radius) {
        float rx = x - radius;
        float ry = y - radius;
        float rw = radius * 2;
        float rh = radius * 2;

        Iterator<Stroke> it = strokes.iterator();
        while (it.hasNext()) {
            Stroke stroke = it.next();
            if (stroke.intersectsRect(rx, ry, rw, rh)) {
                it.remove();
            }
        }
    }

    public void clear() {
        strokes.clear();
    }

    @Override
    public void render(long nvg, int x, int y, int width, int height) {
        NVGColor color = NVGColor.create();

        nvgSave(nvg);
        nvgScissor(nvg, x, y, width, height);

        for (Stroke stroke : strokes) {
            if (stroke.points.isEmpty()) continue;

            nvgRGBAf(stroke.r, stroke.g, stroke.b, stroke.a, color);

            if (stroke.isText && !stroke.points.isEmpty()) {
                StrokePoint p = stroke.points.get(0);
                nvgFontFace(nvg, stroke.fontName);
                nvgFontSize(nvg, stroke.size);
                nvgFillColor(nvg, color);
                nvgTextAlign(nvg, NVG_ALIGN_LEFT | NVG_ALIGN_TOP);
                nvgText(nvg, x + p.x, y + p.y, stroke.text);
            } else if (stroke.isLine && stroke.points.size() >= 2) {
                nvgBeginPath(nvg);
                StrokePoint p0 = stroke.points.get(0);
                nvgMoveTo(nvg, x + p0.x, y + p0.y);
                for (int i = 1; i < stroke.points.size(); i++) {
                    StrokePoint p = stroke.points.get(i);
                    nvgLineTo(nvg, x + p.x, y + p.y);
                }
                nvgStrokeColor(nvg, color);
                nvgStrokeWidth(nvg, stroke.size);
                nvgLineCap(nvg, NVG_ROUND);
                nvgStroke(nvg);
            } else {
                for (StrokePoint p : stroke.points) {
                    nvgBeginPath(nvg);
                    nvgCircle(nvg, x + p.x, y + p.y, stroke.size / 2);
                    nvgFillColor(nvg, color);
                    nvgFill(nvg);
                }
            }
        }

        nvgRestore(nvg);
    }

    @Override
    public LayerType getType() {
        return LayerType.DRAW;
    }

    public List<Stroke> getStrokes() {
        return strokes;
    }
}
