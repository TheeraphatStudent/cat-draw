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

    public Canvas(Renderer renderer) {
        this.renderer = renderer;
        activeDrawLayer = new DrawLayer();
        layers.add(activeDrawLayer);
    }

    public void render(int x, int y, int width, int height) {
        long nvg = renderer.getNvg();

        nvgSave(nvg);

        nvgBeginPath(nvg);
        nvgRect(nvg, x, y, width, height);
        nvgFillColor(nvg, Renderer.COLOR_CANVAS_BG);
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
