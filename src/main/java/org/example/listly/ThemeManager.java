package org.example.listly;

import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;

public final class ThemeManager {

    private static final Map<String, Image> CACHE = new HashMap<>();

    // fileName = "p1.jpg" etc, located under resources/image
    public static Image get(String fileName) {
        return CACHE.computeIfAbsent(fileName, n -> {
            var url = ThemeManager.class.getResource("/image/" + n);
            if (url == null) {
                throw new IllegalArgumentException("Theme image not found: " + n);
            }
            return new Image(url.toExternalForm(), true);
        });
    }

    // call once at startup
    public static void preloadAll() {
        String[] files = {
                "p10.jpg", "p1.jpg", "p2.jpg", "p3.jpg", "p4.jpg",
                "p5.jpg", "p6.jpg", "p7.jpg", "p8.jpg", "p9.jpg",
                "g0.jpg", "g9.jpg"
        };
        for (String f : files) {
            get(f);
        }
    }

    private ThemeManager() { }
}
