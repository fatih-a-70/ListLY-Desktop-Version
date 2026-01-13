package org.example.listly;

import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;

public class ImageCache {
    private static final Map<String, Image> cache = new HashMap<>();

    public static void preload(String... paths) {
        for (String p : paths) {
            if (!cache.containsKey(p)) {
                Image img = new Image(ImageCache.class.getResourceAsStream(p));
                cache.put(p, img);
            }
        }
    }

    public static Image get(String path) {
        Image img = cache.get(path);
        if (img == null) {
            img = new Image(ImageCache.class.getResourceAsStream(path));
            cache.put(path, img);
        }
        return img;
    }
}
