package project.layers;

public interface Layer {
    void render(long nvg, int x, int y, int width, int height);
    LayerType getType();
    
    enum LayerType {
        DRAW,
        IMAGE,
        TEXT
    }
}
