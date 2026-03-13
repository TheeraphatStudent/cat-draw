package project.core;

import org.lwjgl.nanovg.NVGColor;
import project.layers.DrawLayer;
import project.layers.ImageLayer;
import project.layers.Layer;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.nanovg.NanoVG.*;

public class Canvas {
    private final Renderer renderer;
    private final List<Layer> layers = new ArrayList<>();
    private DrawLayer activeDrawLayer;
    private final NVGColor backgroundColor = NVGColor.create();
    private float bgR = 1.0f, bgG = 1.0f, bgB = 1.0f, bgA = 1.0f;

    public Canvas(Renderer renderer) {
        this.renderer = renderer;
        activeDrawLayer = new DrawLayer();
        layers.add(activeDrawLayer);
        nvgRGBAf(bgR, bgG, bgB, bgA, backgroundColor);
    }

    public void render(int x, int y, int width, int height) {
        long nvg = renderer.getNvg();

        nvgSave(nvg);

        nvgBeginPath(nvg);
        nvgRect(nvg, x, y, width, height);
        nvgRGBAf(bgR, bgG, bgB, bgA, backgroundColor);
        nvgFillColor(nvg, backgroundColor);
        nvgFill(nvg);

        for (Layer layer : layers) {
            layer.render(nvg, x, y, width, height);
        }

        nvgRestore(nvg);
    }

    public void addLayer(Layer layer) {
        layers.add(layer);
    }

    public void removeLayer(Layer layer) {
        layers.remove(layer);
    }

    public DrawLayer getActiveDrawLayer() {
        return activeDrawLayer;
    }

    public void clear() {
        layers.clear();
        activeDrawLayer = new DrawLayer();
        layers.add(activeDrawLayer);
        bgR = 1.0f;
        bgG = 1.0f;
        bgB = 1.0f;
        bgA = 1.0f;
    }

    public void setBackgroundColor(float r, float g, float b, float a) {
        this.bgR = r;
        this.bgG = g;
        this.bgB = b;
        this.bgA = a;
    }

    public float[] getBackgroundColor() {
        return new float[]{bgR, bgG, bgB, bgA};
    }

    public boolean isBackgroundColor(float r, float g, float b) {
        float tolerance = 0.01f;
        return Math.abs(bgR - r) < tolerance && 
               Math.abs(bgG - g) < tolerance && 
               Math.abs(bgB - b) < tolerance;
    }

    public void addImageLayer(ImageLayer imageLayer) {
        layers.add(layers.size() - 1, imageLayer);
    }

    public List<Layer> getLayers() {
        return layers;
    }

    public Renderer getRenderer() {
        return renderer;
    }

    private int lastWidth = 800;
    private int lastHeight = 600;

    public void setSize(int width, int height) {
        this.lastWidth = width;
        this.lastHeight = height;
    }

    public int getWidth() {
        return lastWidth;
    }

    public int getHeight() {
        return lastHeight;
    }
}
