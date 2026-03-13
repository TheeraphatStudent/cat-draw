package project.core;

public final class Layout {
    public static final int TOPBAR_HEIGHT = 48;
    public static final int TOOLBAR_WIDTH = 60;
    public static final int COLOR_PANEL_WIDTH = 200;

    private Layout() {}

    public static int getCanvasX() {
        return TOOLBAR_WIDTH;
    }

    public static int getCanvasY() {
        return TOPBAR_HEIGHT;
    }

    public static int getCanvasWidth(int windowWidth) {
        return windowWidth - TOOLBAR_WIDTH - COLOR_PANEL_WIDTH;
    }

    public static int getCanvasHeight(int windowHeight) {
        return windowHeight - TOPBAR_HEIGHT;
    }

    public static float toCanvasX(float mouseX) {
        return mouseX - TOOLBAR_WIDTH;
    }

    public static float toCanvasY(float mouseY) {
        return mouseY - TOPBAR_HEIGHT;
    }

    public static boolean isInCanvas(float mouseX, float mouseY, int windowWidth, int windowHeight) {
        return mouseX >= TOOLBAR_WIDTH && 
               mouseX < windowWidth - COLOR_PANEL_WIDTH &&
               mouseY >= TOPBAR_HEIGHT && 
               mouseY < windowHeight;
    }
}
