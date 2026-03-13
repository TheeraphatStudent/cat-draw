package project.core;

import org.lwjgl.glfw.*;
import project.tools.ToolManager;
import project.ui.ColorPanel;
import project.ui.Toolbar;
import project.ui.TopBar;

import static org.lwjgl.glfw.GLFW.*;

public class InputHandler {
    private final long window;
    private final ToolManager toolManager;
    private final Canvas canvas;
    private final TopBar topBar;
    private final Toolbar toolbar;
    private final ColorPanel colorPanel;

    private double mouseX, mouseY;
    private boolean mouseDown = false;
    private int windowWidth = 1280;
    private int windowHeight = 800;

    public InputHandler(long window, ToolManager toolManager, Canvas canvas,
                        TopBar topBar, Toolbar toolbar, ColorPanel colorPanel) {
        this.window = window;
        this.toolManager = toolManager;
        this.canvas = canvas;
        this.topBar = topBar;
        this.toolbar = toolbar;
        this.colorPanel = colorPanel;
    }

    public void setupCallbacks() {
        glfwSetCursorPosCallback(window, (win, xpos, ypos) -> {
            mouseX = xpos;
            mouseY = ypos;

            topBar.handleMouseMove((float) xpos, (float) ypos);

            if (mouseDown) {
                if (colorPanel.isInBounds((float) xpos, (float) ypos, windowWidth)) {
                    colorPanel.handleDrag((float) xpos, (float) ypos, windowWidth);
                } else if (!toolbar.isInBounds((float) xpos, (float) ypos) &&
                           !topBar.isInBounds((float) xpos, (float) ypos)) {
                    float canvasX = (float) xpos - Toolbar.WIDTH;
                    float canvasY = (float) ypos - TopBar.HEIGHT;
                    toolManager.onMouseDrag(canvas, canvasX, canvasY);
                }
            }
        });

        glfwSetMouseButtonCallback(window, (win, button, action, mods) -> {
            if (button == GLFW_MOUSE_BUTTON_LEFT) {
                if (action == GLFW_PRESS) {
                    mouseDown = true;

                    if (topBar.handleClick((float) mouseX, (float) mouseY)) {
                        return;
                    }

                    if (toolbar.handleClick((float) mouseX, (float) mouseY)) {
                        return;
                    }

                    if (colorPanel.handleClick((float) mouseX, (float) mouseY, windowWidth, windowHeight)) {
                        return;
                    }

                    if (mouseX > Toolbar.WIDTH && mouseX < windowWidth - ColorPanel.WIDTH &&
                        mouseY > TopBar.HEIGHT) {
                        float canvasX = (float) mouseX - Toolbar.WIDTH;
                        float canvasY = (float) mouseY - TopBar.HEIGHT;
                        toolManager.onMouseDown(canvas, canvasX, canvasY);
                    }
                } else if (action == GLFW_RELEASE) {
                    mouseDown = false;
                    colorPanel.handleRelease();

                    if (mouseX > Toolbar.WIDTH && mouseX < windowWidth - ColorPanel.WIDTH &&
                        mouseY > TopBar.HEIGHT) {
                        float canvasX = (float) mouseX - Toolbar.WIDTH;
                        float canvasY = (float) mouseY - TopBar.HEIGHT;
                        toolManager.onMouseUp(canvas, canvasX, canvasY);
                    }
                }
            }
        });

        glfwSetKeyCallback(window, (win, key, scancode, action, mods) -> {
            if (action == GLFW_PRESS || action == GLFW_REPEAT) {
                toolManager.onKeyPressed(canvas, key);

                if (key == GLFW_KEY_ESCAPE) {
                    glfwSetWindowShouldClose(window, true);
                }
            }
        });

        glfwSetCharCallback(window, (win, codepoint) -> {
            toolManager.onKeyTyped(canvas, (char) codepoint);
        });

        glfwSetWindowSizeCallback(window, (win, width, height) -> {
            windowWidth = width;
            windowHeight = height;
        });
    }
}
