### Project Structure
```
src/main/java/project/
├── App.java               # Entry, GLFW window init
├── core/
│   ├── Renderer.java      # OpenGL render loop
│   ├── Canvas.java        # Layer management
│   └── InputHandler.java  # Mouse/keyboard/drop events
├── tools/
│   ├── BrushTool.java
│   ├── EraserTool.java
│   ├── LineTool.java      # DDA implementation
│   └── TextTool.java
├── ui/
│   ├── Toolbar.java       # Left sidebar icons
│   ├── ColorPanel.java    # Hex input + opacity
│   └── TopBar.java        # File actions
└── layers/
    └── ImageLayer.java    # Drag & drop image
resources/
├── fonts/
│   ├── Kanit-Regular.ttf
│   └── Roboto-Regular.ttf
└── icons/
    ├── brush.png
    ├── eraser.png
    ├── text.png
    └── line.png
```
