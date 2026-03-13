package project.core;

import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.nanovg.NanoVG.nvgCreateImageRGBA;

public class IconRegistry {
    private static final Map<String, Integer> icons = new HashMap<>();
    private static final List<String> iconKeys = new ArrayList<>();
    private static long nvgContext;
    private static boolean initialized = false;

    private static final String ICON_DIRECTORY = "/icons/";

    private static final String[] KNOWN_ICONS = {
            "brush.png",
            "eraser.png",
            "line.png",
            "text.png",
            "bucket.png",
            "mouse.png",
            "upload.png"
    };

    public static void init(long nvg) {
        if (initialized) {
            return;
        }

        nvgContext = nvg;

        for (String iconFile : KNOWN_ICONS) {
            String resourcePath = ICON_DIRECTORY + iconFile;
            String iconKey = iconFile.replace(".png", "").replace(".PNG", "");

            if (icons.containsKey(iconKey)) {
                continue;
            }

            try {
                int imageId = loadIconFromResource(resourcePath, iconKey);
                if (imageId != -1) {
                    icons.put(iconKey, imageId);
                    iconKeys.add(iconKey);
                    System.out.println("  Loaded icon: " + iconKey + " (id=" + imageId + ")");
                }
            } catch (Exception e) {
                System.err.println("  Warning: Could not load icon " + iconKey + ": " + e.getMessage());
            }
        }

        initialized = true;
        System.out.println("IconRegistry initialized with " + icons.size() + " icons: " + iconKeys);
    }

    private static int loadIconFromResource(String resourcePath, String name) {
        try {
            ByteBuffer imageData = loadResource(resourcePath);
            if (imageData == null) {
                return -1;
            }

            try (MemoryStack stack = MemoryStack.stackPush()) {
                IntBuffer w = stack.mallocInt(1);
                IntBuffer h = stack.mallocInt(1);
                IntBuffer comp = stack.mallocInt(1);

                ByteBuffer decodedImage = STBImage.stbi_load_from_memory(imageData, w, h, comp, 4);
                MemoryUtil.memFree(imageData);

                if (decodedImage != null) {
                    int imageId = nvgCreateImageRGBA(nvgContext, w.get(0), h.get(0), 0, decodedImage);
                    STBImage.stbi_image_free(decodedImage);
                    return imageId;
                } else {
                    System.err.println("  STB failed to decode icon " + name + ": " + STBImage.stbi_failure_reason());
                }
            }
        } catch (IOException e) {
            System.err.println("  Error loading icon " + name + ": " + e.getMessage());
        }
        return -1;
    }

    private static ByteBuffer loadResource(String resource) throws IOException {
        InputStream source = IconRegistry.class.getResourceAsStream(resource);
        if (source == null) {
            return null;
        }

        try (ReadableByteChannel rbc = Channels.newChannel(source)) {
            ByteBuffer buffer = MemoryUtil.memAlloc(8 * 1024);
            while (true) {
                int bytes = rbc.read(buffer);
                if (bytes == -1) {
                    break;
                }
                if (buffer.remaining() == 0) {
                    ByteBuffer newBuffer = MemoryUtil.memAlloc(buffer.capacity() * 2);
                    buffer.flip();
                    newBuffer.put(buffer);
                    MemoryUtil.memFree(buffer);
                    buffer = newBuffer;
                }
            }
            buffer.flip();
            return buffer;
        }
    }

    public static int get(String name) {
        if (!initialized) {
            System.err.println("IconRegistry not initialized! Call IconRegistry.init(nvg) first.");
            return -1;
        }

        Integer iconId = icons.get(name);
        return iconId != null ? iconId : -1;
    }

    public static boolean hasIcon(String name) {
        return icons.containsKey(name) && icons.get(name) != -1;
    }

    public static List<String> listIcons() {
        return new ArrayList<>(iconKeys);
    }

    public static int getIconCount() {
        return icons.size();
    }

    public static boolean isInitialized() {
        return initialized;
    }
}
