You are a senior Java/OpenGL developer. Think step by step before writing any code.

## Project Overview
A desktop Paint application using:
- Java 21 + Gradle 9
- LWJGL 3.3.3 (OpenGL, GLFW, NanoVG, STB)
- Main class: `project.App`

## Actual Project Structure
```
app/
└── src/
    ├── main/
    │   ├── java/project/
    │   │   └── App.java          ← entry point (already exists, will be replaced)
    │   └── resources/
    │       ├── fonts/
    │       │   ├── Kanit-Regular.ttf
    │       │   └── Roboto-Regular.ttf
    │       └── icons/
    │           ├── brush.png
    │           ├── eraser.png
    │           ├── line.png
    │           ├── text.png
    │           └── upload.png
    └── test/
        ├── java/project/
        │   └── AppTest.java
        └── resources/
```

## build.gradle is already configured with:
- LWJGL 3.3.3 BOM (opengl, glfw, stb, nanovg)
- Cross-platform natives (windows + linux)
- mainClass = 'project.App'
- Java 21 toolchain

## Target File Tree to Implement
```
app/src/main/java/project/
├── App.java                        # GLFW window init + main loop
├── core/
│   ├── Renderer.java               # OpenGL + NanoVG render loop
│   ├── Canvas.java                 # Layer list management
│   └── InputHandler.java           # GLFW callbacks → tool actions
├── tools/
│   ├── ToolManager.java            # Active tool state machine
│   ├── BrushTool.java              # Freehand drawing
│   ├── EraserTool.java             # Hold+drag erase with red preview
│   ├── LineTool.java               # DDA straight line algorithm
│   └── TextTool.java               # Inline text with resize handles
├── ui/
│   ├── Toolbar.java                # Left sidebar with icon buttons
│   ├── ColorPanel.java             # Hex input + opacity slider
│   └── TopBar.java                 # App title + action buttons
└── layers/
    ├── Layer.java                  # Base layer interface
    ├── DrawLayer.java              # Stroke/pixel draw layer
    └── ImageLayer.java             # Drag & drop image layer
```
## Pre-Implementation Reasoning (do this before ANY code)

Think through and answer each point:

1. RENDER LOOP ARCHITECTURE
   - How will OpenGL + NanoVG coexist in the same frame?
   - What is the correct order: GL clear → draw canvas layers → NanoVG UI?
   - How will framebuffer size vs window size be handled for retina/HiDPI?

2. LAYER SYSTEM DESIGN  
   - How is List<Layer> rendered in order (bottom to top)?
   - How does DrawLayer store strokes — as pixel buffer or as stroke objects?
   - How does ImageLayer store its GL texture ID + position + size?

3. INPUT & TOOL STATE MACHINE
   - What GLFW callbacks are needed: cursor, mouse button, scroll, drop, key?
   - How does ToolManager switch active tool and route events?
   - How is drag state tracked (isMouseDown, startX/Y, lastX/Y)?

4. DDA LINE ALGORITHM
   - Given p1(x1,y1) and p2(x2,y2), how does DDA step through pixels?
   - How does the pixel list map to GL_POINTS or GL_LINES calls?
   - How is line thickness applied in OpenGL?

5. ERASER BEHAVIOR
   - How is the red preview line rendered on top of canvas while dragging?
   - On mouse release, how is the erased region removed from DrawLayer?
   - Should eraser operate per-pixel or per-stroke-object?

6. TEXT TOOL BEHAVIOR
   - How is inline text box opened at click position via NanoVG?
   - How are drag handles rendered and resized using NanoVG?
   - How is finalized text baked into a DrawLayer or kept as TextLayer?

7. COLOR PANEL
   - How is hex string (#RRGGBB) parsed to float RGBA for OpenGL?
   - How does opacity slider modify the alpha channel live?
   - How is active color shared globally across all tools?

## Implementation Order (one file at a time)
```
PHASE 1 — Core Foundation
  [1] App.java              → GLFW window (1280x800) + GL context + loop
  [2] core/Renderer.java    → NanoVG init, frame begin/end, font loading
  [3] core/Canvas.java      → Layer list, add/remove/render layers
  [4] core/InputHandler.java→ All GLFW callbacks wired to ToolManager

PHASE 2 — Tools
  [5] tools/ToolManager.java → Enum{BRUSH,ERASER,LINE,TEXT}, switch logic
  [6] tools/BrushTool.java   → Mouse drag → circle stamps at cursor
  [7] tools/EraserTool.java  → Hold+drag → red preview → erase on release
  [8] tools/LineTool.java    → DDA algorithm → GL_LINES on release
  [9] tools/TextTool.java    → Click → input box → resize handles → bake

PHASE 3 — UI (NanoVG)
  [10] ui/TopBar.java    → 48px top: title (Kanit) + Upload + Clear buttons
  [11] ui/Toolbar.java   → 60px left: icon buttons per tool, active highlight
  [12] ui/ColorPanel.java→ 200px right: hex field + opacity slider + swatch

PHASE 4 — Layers
  [13] layers/Layer.java      → Interface: render(nvg), getType()
  [14] layers/DrawLayer.java  → Stores stroke list, renders via OpenGL
  [15] layers/ImageLayer.java → STB load → GL texture → draggable quad

## Design System (apply consistently across all UI files)
Colors:
  Background:      #1E1E2E
  Panel:           #2A2A3D
  Accent:          #6C63FF
  Accent hover:    #857DFF
  Text primary:    #FFFFFF
  Text secondary:  #AAAACC
  Danger/Eraser:   #FF4C4C

Typography:
  Headings/Labels: Kanit-Regular.ttf   (loaded from resources/fonts/)
  Inputs/Values:   Roboto-Regular.ttf  (loaded from resources/fonts/)

Icons:
  Render PNG icons via NanoVG image API (nvgCreateImage)
  Size: 24x24px centered in 44x44px button hit area

Layout:
  TopBar height:    48px  (top)
  Toolbar width:    60px  (left, below TopBar)
  ColorPanel width: 200px (right, below TopBar)
  Canvas area:      remaining center space

## Output Rules
- Output ONE file at a time in the implementation order above
- Begin each file with its full path as a comment: // app/src/main/java/project/...
- All classes must be in package `project.*` matching folder structure
- After completing each file, stop and ask: "Ready for [next file name]?"
- State any assumption clearly before the code block
- Never skip ahead or combine multiple files in one response
```

# AGENTS_FLOW.md
> Auto-updated by agent after each task completes.  
> Format: `[STATUS] Task — notes`  
> Statuses: `1 TODO` · `2 IN PROGRESS` · `3 DONE` · `4 BLOCKED`

---

## PHASE 1 — Core Foundation

| # | File | Status | Notes |
|---|------|--------|-------|
| 1 | `App.java` | 3 DONE | GLFW window 1280x800, GL context, main loop |
| 2 | `core/Renderer.java` | 3 DONE | NanoVG init, Kanit font variants loaded, frame begin/end |
| 3 | `core/Canvas.java` | 3 DONE | Layer list, add/remove/render layers |
| 4 | `core/InputHandler.java` | 3 DONE | GLFW callbacks, removed drag-drop (using FileDialog) |

---

## PHASE 2 — Tools

| # | File | Status | Notes |
|---|------|--------|-------|
| 5 | `tools/ToolManager.java` | 3 DONE | Enum{BRUSH,ERASER,LINE,TEXT}, switch logic |
| 6 | `tools/BrushTool.java` | 3 DONE | Mouse drag → circle stamps at cursor |
| 7 | `tools/EraserTool.java` | 3 DONE | Hold+drag → erase on release |
| 8 | `tools/LineTool.java` | 3 DONE | DDA algorithm → GL_LINES on release |
| 9 | `tools/TextTool.java` | 3 DONE | Click canvas → text box → resize handles → bake on ENTER |

---

## PHASE 3 — UI (NanoVG)

| # | File | Status | Notes |
|---|------|--------|-------|
| 10 | `ui/TopBar.java` | 3 DONE | 48px top: title (kanit-semibold) + Upload (FileDialog) + Clear |
| 11 | `ui/Toolbar.java` | 3 DONE | 60px left: icon buttons per tool, active highlight |
| 12 | `ui/ColorPanel.java` | 3 DONE | Hex field READ-ONLY, opacity slider, color swatches |

---

## PHASE 4 — Layers

| # | File | Status | Notes |
|---|------|--------|-------|
| 13 | `layers/Layer.java` | 3 DONE | Interface: render(nvg), getType() |
| 14 | `layers/DrawLayer.java` | 3 DONE | Strokes + text support, renders via NanoVG |
| 15 | `layers/ImageLayer.java` | 3 DONE | STB load → NanoVG image → positioned quad |

---

## Progress Summary

```
Total:       15 tasks
- Done:      15
- In Progress: 0
- Blocked:   0
- Remaining: 0
```

---

## Agent Instructions

When completing a task, update this file by:

1. Changing the status emoji in the table row
2. Adding a short note if anything was assumed or skipped
3. Updating the Progress Summary counts at the bottom
4. Appending a log entry below

---

## Agent Update Log

| Timestamp | File | Status | Agent Note |
|-----------|------|--------|------------|
| 2026-03-13 | All 15 files | 3 DONE | Initial implementation complete |
| 2026-03-13 | `ui/ColorPanel.java` | 3 DONE | CHANGE 1: Hex field now read-only (display only) |
| 2026-03-13 | `tools/TextTool.java` | 3 DONE | CHANGE 2: Click canvas to place text, resize handles, bake on ENTER |
| 2026-03-13 | `layers/DrawLayer.java` | 3 DONE | Added text stroke support for TextTool |
| 2026-03-13 | `ui/TopBar.java` | 3 DONE | CHANGE 3: Upload uses FileDialog instead of drag-drop |
| 2026-03-13 | `core/InputHandler.java` | 3 DONE | Removed glfwSetDropCallback |
| 2026-03-13 | `core/Renderer.java` | 3 DONE | Loaded Kanit font variants (regular, medium, semibold, bold, light) |
| 2026-03-13 | `core/FontRegistry.java` | 3 DONE | NEW: Auto-discover fonts from resources/fonts/ |
| 2026-03-13 | `layers/ImageLayer.java` | 3 DONE | BUG 1 FIX: Use stbi_load_from_memory for non-ASCII paths |
| 2026-03-13 | `core/IconRegistry.java` | 3 DONE | BUG 2 FIX: Load icons from resources with fallback support |
| 2026-03-13 | `core/Renderer.java` | 3 DONE | Added IconRegistry.init(nvg) call |
| 2026-03-13 | `ui/ColorPanel.java` | 3 DONE | FEATURE 1: Live hex input with validation, paste support, normalization |
| 2026-03-13 | `core/InputHandler.java` | 3 DONE | Route keyboard to ColorPanel when hex field focused |
| 2026-03-13 | `tools/BucketTool.java` | 3 DONE | FEATURE 2: Flood fill implementation with iterative queue |
| 2026-03-13 | `tools/SelectTool.java` | 3 DONE | FEATURE 3: Element drag/reposition for ImageLayers |
| 2026-03-13 | `tools/ToolManager.java` | 3 DONE | Added BUCKET and SELECT to Tool enum |
| 2026-03-13 | `ui/Toolbar.java` | 3 DONE | Icon rendering with fallback, active tool indicator strip |
| 2026-03-13 | `App.java` | 3 DONE | Pass windowWidth to Toolbar.render() |