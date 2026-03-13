package project.layers;

import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.system.MemoryUtil.*;

public class ImageLayer implements Layer {
    private int imageHandle = -1;
    private float x, y;
    private float width, height;
    private float originalWidth, originalHeight;
    private boolean loaded = false;

    public boolean loadFromFile(long nvg, String path) {
        imageHandle = nvgCreateImage(nvg, path, 0);
        if (imageHandle != 0) {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                IntBuffer w = stack.mallocInt(1);
                IntBuffer h = stack.mallocInt(1);
                nvgImageSize(nvg, imageHandle, w, h);
                originalWidth = w.get(0);
                originalHeight = h.get(0);
                width = originalWidth;
                height = originalHeight;
                loaded = true;
                return true;
            }
        }
        return false;
    }

    public boolean loadFromMemory(long nvg, ByteBuffer data) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);

            ByteBuffer image = STBImage.stbi_load_from_memory(data, w, h, comp, 4);
            if (image != null) {
                imageHandle = nvgCreateImageRGBA(nvg, w.get(0), h.get(0), 0, image);
                STBImage.stbi_image_free(image);

                if (imageHandle != 0) {
                    originalWidth = w.get(0);
                    originalHeight = h.get(0);
                    width = originalWidth;
                    height = originalHeight;
                    loaded = true;
                    return true;
                }
            }
        }
        return false;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public boolean contains(float px, float py) {
        return px >= x && px <= x + width && py >= y && py <= y + height;
    }

    @Override
    public void render(long nvg, int canvasX, int canvasY, int canvasWidth, int canvasHeight) {
        if (!loaded || imageHandle == -1)
            return;

        nvgSave(nvg);
        nvgScissor(nvg, canvasX, canvasY, canvasWidth, canvasHeight);

        float drawX = canvasX + x;
        float drawY = canvasY + y;

        NVGPaint paint = NVGPaint.calloc();
        nvgImagePattern(nvg, drawX, drawY, width, height, 0, imageHandle, 1.0f, paint);
        nvgBeginPath(nvg);
        nvgRect(nvg, drawX, drawY, width, height);
        nvgFillPaint(nvg, paint);
        nvgFill(nvg);
        paint.free();

        nvgRestore(nvg);
    }

    @Override
    public LayerType getType() {
        return LayerType.IMAGE;
    }

    public void cleanup(long nvg) {
        if (imageHandle != -1) {
            nvgDeleteImage(nvg, imageHandle);
            imageHandle = -1;
            loaded = false;
        }
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public boolean isLoaded() {
        return loaded;
    }
}
