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
                } else if (Layout.isInCanvas((float) xpos, (float) ypos, windowWidth, windowHeight)) {
                    float canvasX = Layout.toCanvasX((float) xpos);
                    float canvasY = Layout.toCanvasY((float) ypos);
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

                    if (Layout.isInCanvas((float) mouseX, (float) mouseY, windowWidth, windowHeight)) {
                        float canvasX = Layout.toCanvasX((float) mouseX);
                        float canvasY = Layout.toCanvasY((float) mouseY);
                        toolManager.onMouseDown(canvas, canvasX, canvasY);
                    }
                } else if (action == GLFW_RELEASE) {
                    mouseDown = false;
                    colorPanel.handleRelease();

                    if (Layout.isInCanvas((float) mouseX, (float) mouseY, windowWidth, windowHeight)) {
                        float canvasX = Layout.toCanvasX((float) mouseX);
                        float canvasY = Layout.toCanvasY((float) mouseY);
                        toolManager.onMouseUp(canvas, canvasX, canvasY);
                    }
                }
            }
        });

        glfwSetKeyCallback(window, (win, key, scancode, action, mods) -> {
            if (action == GLFW_PRESS || action == GLFW_REPEAT) {
                if (colorPanel.isHexFieldFocused()) {
                    colorPanel.onKeyPressed(key);
                } else {
                    toolManager.onKeyPressed(canvas, key);

                    if (key == GLFW_KEY_ESCAPE) {
                        glfwSetWindowShouldClose(window, true);
                    }
                }
            }
        });

        glfwSetCharCallback(window, (win, codepoint) -> {
            if (colorPanel.isHexFieldFocused()) {
                colorPanel.onKeyTyped((char) codepoint);
            } else {
                toolManager.onKeyTyped(canvas, (char) codepoint);
            }
        });

        glfwSetWindowSizeCallback(window, (win, width, height) -> {
            windowWidth = width;
            windowHeight = height;
        });
    }
}
