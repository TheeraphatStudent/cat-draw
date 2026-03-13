package project.ui;

import org.lwjgl.nanovg.NVGColor;
import project.core.Canvas;
import project.core.Renderer;

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

        nvgFontFace(nvg, "kanit");
        nvgFontSize(nvg, 24);
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

        nvgFontFace(nvg, "roboto");
        nvgFontSize(nvg, 14);
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
