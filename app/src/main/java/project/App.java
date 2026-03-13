package project;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import project.core.Canvas;
import project.core.InputHandler;
import project.core.Renderer;
import project.tools.ToolManager;
import project.ui.ColorPanel;
import project.ui.Toolbar;
import project.ui.TopBar;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class App {
    private long window;
    private int windowWidth = 1280;
    private int windowHeight = 800;
    private int framebufferWidth;
    private int framebufferHeight;

    private Renderer renderer;
    private Canvas canvas;
    private ToolManager toolManager;
    private InputHandler inputHandler;
    private TopBar topBar;
    private Toolbar toolbar;
    private ColorPanel colorPanel;

    public void run() {
        init();
        loop();
        cleanup();
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);

        window = glfwCreateWindow(windowWidth, windowHeight, "Cat Draw", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(window, pWidth, pHeight);

            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            if (vidmode != null) {
                glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
                );
            }

            IntBuffer fbWidth = stack.mallocInt(1);
            IntBuffer fbHeight = stack.mallocInt(1);
            glfwGetFramebufferSize(window, fbWidth, fbHeight);
            framebufferWidth = fbWidth.get(0);
            framebufferHeight = fbHeight.get(0);
        }

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        glfwShowWindow(window);

        GL.createCapabilities();

        renderer = new Renderer();
        renderer.init(windowWidth, windowHeight);

        toolManager = new ToolManager();
        canvas = new Canvas(renderer);
        
        topBar = new TopBar(renderer, canvas);
        toolbar = new Toolbar(renderer, toolManager);
        colorPanel = new ColorPanel(renderer, toolManager);

        inputHandler = new InputHandler(window, toolManager, canvas, topBar, toolbar, colorPanel);
        inputHandler.setupCallbacks();

        glfwSetFramebufferSizeCallback(window, (win, width, height) -> {
            framebufferWidth = width;
            framebufferHeight = height;
            glViewport(0, 0, width, height);
        });

        glfwSetWindowSizeCallback(window, (win, width, height) -> {
            windowWidth = width;
            windowHeight = height;
            renderer.updateSize(width, height);
        });
    }

    private void loop() {
        glClearColor(0.118f, 0.118f, 0.180f, 1.0f);

        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

            float pixelRatio = (float) framebufferWidth / windowWidth;
            renderer.beginFrame(windowWidth, windowHeight, pixelRatio);

            int canvasX = Toolbar.WIDTH;
            int canvasY = TopBar.HEIGHT;
            int canvasWidth = windowWidth - Toolbar.WIDTH - ColorPanel.WIDTH;
            int canvasHeight = windowHeight - TopBar.HEIGHT;
            canvas.render(canvasX, canvasY, canvasWidth, canvasHeight);

            topBar.render(windowWidth);
            toolbar.render(windowHeight, windowWidth);
            colorPanel.render(windowWidth, windowHeight);

            renderer.endFrame();

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    private void cleanup() {
        renderer.cleanup();
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        GLFWErrorCallback cb = glfwSetErrorCallback(null);
        if (cb != null) {
            cb.free();
        }
    }

    public static void main(String[] args) {
        new App().run();
    }
}
