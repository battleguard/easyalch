package script.util;

import java.awt.*;
import java.util.Random;

public class Rand {
    private static final Random r = new Random();
    public static int nextInt(int min, int max) {
        return r.nextInt(max - min) + min;
    }

    public static Point nextPoint(Rectangle rec) {
        return new Point(nextInt(rec.x, rec.x + rec.width), nextInt(rec.y, rec.y + rec.height));
    }
}
