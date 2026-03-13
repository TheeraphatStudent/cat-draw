package project.ui;

import org.lwjgl.nanovg.NVGColor;
import project.core.Canvas;
import project.core.FontRegistry;
import project.core.Renderer;
import project.layers.ImageLayer;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.FilenameFilter;

import static org.lwjgl.nanovg.NanoVG.*;

public class TopBar {
    public static final int HEIGHT = 48;

    private final Renderer renderer;
    private final Canvas canvas;
    private boolean uploadHovered = false;
    private boolean clearHovered = false;

    public TopBar(Renderer renderer, Canvas canvas) {
        this.renderer = renderer;
        this.canvas = canvas;
    }

    public void render(int windowWidth) {
        long nvg = renderer.getNvg();

        nvgBeginPath(nvg);
        nvgRect(nvg, 0, 0, windowWidth, HEIGHT);
        nvgFillColor(nvg, Renderer.COLOR_PANEL);
        nvgFill(nvg);

        nvgFontFace(nvg, FontRegistry.getFont("Kanit-SemiBold"));
        nvgFontSize(nvg, 18);
        nvgFillColor(nvg, Renderer.COLOR_TEXT_PRIMARY);
        nvgTextAlign(nvg, NVG_ALIGN_LEFT | NVG_ALIGN_MIDDLE);
        nvgText(nvg, 16, HEIGHT / 2f, "Cat Draw");

        float buttonY = 8;
        float buttonHeight = 32;
        float buttonWidth = 80;

        float uploadX = windowWidth - 180;
        drawButton(nvg, uploadX, buttonY, buttonWidth, buttonHeight, "Upload", uploadHovered);

        float clearX = windowWidth - 90;
        drawButton(nvg, clearX, buttonY, buttonWidth, buttonHeight, "Clear", clearHovered);
    }

    private void drawButton(long nvg, float x, float y, float w, float h, String label, boolean hovered) {
        NVGColor bgColor = hovered ? Renderer.COLOR_ACCENT_HOVER : Renderer.COLOR_ACCENT;

        nvgBeginPath(nvg);
        nvgRoundedRect(nvg, x, y, w, h, 6);
        nvgFillColor(nvg, bgColor);
        nvgFill(nvg);

        nvgFontFace(nvg, FontRegistry.getFont("Kanit-Medium"));
        nvgFontSize(nvg, 13);
        nvgFillColor(nvg, Renderer.COLOR_TEXT_PRIMARY);
        nvgTextAlign(nvg, NVG_ALIGN_CENTER | NVG_ALIGN_MIDDLE);
        nvgText(nvg, x + w / 2, y + h / 2, label);
    }

    public boolean handleClick(float mouseX, float mouseY) {
        if (mouseY > HEIGHT) return false;

        int windowWidth = renderer.getWindowWidth();
        float buttonY = 8;
        float buttonHeight = 32;
        float buttonWidth = 80;

        float uploadX = windowWidth - 180;
        if (mouseX >= uploadX && mouseX <= uploadX + buttonWidth &&
            mouseY >= buttonY && mouseY <= buttonY + buttonHeight) {
            openFileChooser();
            return true;
        }

        float clearX = windowWidth - 90;
        if (mouseX >= clearX && mouseX <= clearX + buttonWidth &&
            mouseY >= buttonY && mouseY <= buttonY + buttonHeight) {
            canvas.clear();
            return true;
        }

        return false;
    }

    private void openFileChooser() {
        new Thread(() -> {
            FileDialog dialog = new FileDialog((Frame) null, "Select Image", FileDialog.LOAD);
            dialog.setFilenameFilter((dir, name) -> {
                String lower = name.toLowerCase();
                return lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg");
            });
            dialog.setVisible(true);
            
            String file = dialog.getFile();
            String dir = dialog.getDirectory();
            dialog.dispose();
            
            if (file != null && dir != null) {
                String fullPath = dir + file;
                loadImage(fullPath);
            }
        }).start();
    }

    private void loadImage(String path) {
        ImageLayer imageLayer = new ImageLayer();
        long nvg = renderer.getNvg();
        
        if (imageLayer.loadFromFile(nvg, path)) {
            imageLayer.setPosition(50, 50);
            canvas.addImageLayer(imageLayer);
            System.out.println("Image loaded: " + path);
        } else {
            System.err.println("Failed to load image: " + path);
        }
    }

    public void handleMouseMove(float mouseX, float mouseY) {
        if (mouseY > HEIGHT) {
            uploadHovered = false;
            clearHovered = false;
            return;
        }

        int windowWidth = renderer.getWindowWidth();
        float buttonY = 8;
        float buttonHeight = 32;
        float buttonWidth = 80;

        float uploadX = windowWidth - 180;
        uploadHovered = mouseX >= uploadX && mouseX <= uploadX + buttonWidth &&
                        mouseY >= buttonY && mouseY <= buttonY + buttonHeight;

        float clearX = windowWidth - 90;
        clearHovered = mouseX >= clearX && mouseX <= clearX + buttonWidth &&
                       mouseY >= buttonY && mouseY <= buttonY + buttonHeight;
    }

    public boolean isInBounds(float mouseX, float mouseY) {
        return mouseY <= HEIGHT;
    }
}
