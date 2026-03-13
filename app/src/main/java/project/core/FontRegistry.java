package project.core;

import org.lwjgl.system.MemoryUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.nanovg.NanoVG.nvgCreateFontMem;

public class FontRegistry {
    private static final Map<String, Integer> fonts = new HashMap<>();
    private static final List<String> fontKeys = new ArrayList<>();
    private static int fallbackFontId = -1;
    private static long nvgContext;
    private static boolean initialized = false;

    private static final String[] FONT_DIRECTORIES = {
        "/fonts/",
        "/fonts/Kanit/"
    };

    public static void init(long nvg) {
        if (initialized) {
            return;
        }
        
        nvgContext = nvg;
        
        for (String dir : FONT_DIRECTORIES) {
            discoverFontsInDirectory(dir);
        }
        
        if (!fonts.isEmpty()) {
            fallbackFontId = fonts.values().iterator().next();
        }
        
        initialized = true;
        System.out.println("FontRegistry initialized with " + fonts.size() + " fonts: " + fontKeys);
    }

    private static void discoverFontsInDirectory(String directory) {
        try {
            List<String> fontFiles = listResourceFiles(directory, ".ttf");
            
            for (String fontFile : fontFiles) {
                String resourcePath = directory + fontFile;
                String fontKey = fontFile.replace(".ttf", "").replace(".TTF", "");
                
                if (fonts.containsKey(fontKey)) {
                    continue;
                }
                
                try {
                    ByteBuffer fontData = loadResource(resourcePath);
                    if (fontData != null) {
                        int fontId = nvgCreateFontMem(nvgContext, fontKey, fontData, false);
                        if (fontId != -1) {
                            fonts.put(fontKey, fontId);
                            fontKeys.add(fontKey);
                            System.out.println("  Loaded font: " + fontKey + " (id=" + fontId + ")");
                        } else {
                            System.err.println("  Failed to create font: " + fontKey);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("  Error loading font " + fontKey + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Error discovering fonts in " + directory + ": " + e.getMessage());
        }
    }

    private static List<String> listResourceFiles(String directory, String extension) {
        List<String> files = new ArrayList<>();
        
        try (InputStream is = FontRegistry.class.getResourceAsStream(directory)) {
            if (is == null) {
                return files;
            }
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.toLowerCase().endsWith(extension.toLowerCase())) {
                        files.add(line);
                    }
                }
            }
        } catch (IOException e) {
            String[] knownFonts = getKnownFontsForDirectory(directory);
            for (String font : knownFonts) {
                if (FontRegistry.class.getResourceAsStream(directory + font) != null) {
                    files.add(font);
                }
            }
        }
        
        return files;
    }

    private static String[] getKnownFontsForDirectory(String directory) {
        if (directory.contains("Kanit")) {
            return new String[] {
                "Kanit-Regular.ttf", "Kanit-Medium.ttf", "Kanit-SemiBold.ttf",
                "Kanit-Bold.ttf", "Kanit-Light.ttf", "Kanit-Thin.ttf",
                "Kanit-ExtraLight.ttf", "Kanit-ExtraBold.ttf", "Kanit-Black.ttf",
                "Kanit-Italic.ttf", "Kanit-MediumItalic.ttf", "Kanit-SemiBoldItalic.ttf",
                "Kanit-BoldItalic.ttf", "Kanit-LightItalic.ttf", "Kanit-ThinItalic.ttf",
                "Kanit-ExtraLightItalic.ttf", "Kanit-ExtraBoldItalic.ttf", "Kanit-BlackItalic.ttf"
            };
        }
        return new String[] {
            "Roboto-Regular.ttf", "Roboto-Bold.ttf", "Roboto-Light.ttf",
            "Roboto-Medium.ttf", "Roboto-Thin.ttf", "Roboto-Italic.ttf"
        };
    }

    private static ByteBuffer loadResource(String resource) throws IOException {
        InputStream source = FontRegistry.class.getResourceAsStream(resource);
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

    public static String getFont(String key) {
        if (!initialized) {
            System.err.println("FontRegistry not initialized! Call FontRegistry.init(nvg) first.");
            return key;
        }
        
        if (fonts.containsKey(key)) {
            return key;
        }
        
        System.err.println("Font not found: " + key + ", falling back to first available");
        
        if (!fontKeys.isEmpty()) {
            return fontKeys.get(0);
        }
        
        return key;
    }

    public static int getFontId(String key) {
        if (!initialized) {
            return -1;
        }
        
        Integer fontId = fonts.get(key);
        if (fontId != null) {
            return fontId;
        }
        
        System.err.println("Font not found: " + key + ", falling back to first available");
        return fallbackFontId;
    }

    public static List<String> listFonts() {
        return new ArrayList<>(fontKeys);
    }

    public static boolean hasFont(String key) {
        return fonts.containsKey(key);
    }

    public static int getFontCount() {
        return fonts.size();
    }

    public static boolean isInitialized() {
        return initialized;
    }
}
