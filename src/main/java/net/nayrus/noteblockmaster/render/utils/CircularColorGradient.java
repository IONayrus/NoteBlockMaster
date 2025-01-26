package net.nayrus.noteblockmaster.render.utils;

import net.nayrus.noteblockmaster.utils.Utils;

import java.awt.*;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class CircularColorGradient {

    private final NavigableMap<Float, Color> gradientMap;

    public CircularColorGradient(NavigableMap<Float, Color> gradientMap) {
        this.gradientMap = gradientMap;
    }

    public Color getColor(float angle) {
        angle = Utils.normalizeAngle(angle);

        Map.Entry<Float, Color> lower = gradientMap.floorEntry(angle);
        Map.Entry<Float, Color> higher = gradientMap.ceilingEntry(angle);

        if (lower == null || higher == null) {
            lower = gradientMap.lastEntry();
            higher = gradientMap.firstEntry();
        }

        float range = Utils.normalizeAngle(higher.getKey() - lower.getKey());
        float factor = (range == 0) ? 0 : Utils.normalizeAngle(angle - lower.getKey()) / range;

        return RenderUtils.shiftColor(lower.getValue(), higher.getValue(), factor);
    }

    public static class Builder {
        private final NavigableMap<Float, Color> gradientMap = new TreeMap<>();

        public Builder addColor(float angle, Color color) {
            angle = Utils.normalizeAngle(angle);
            gradientMap.put(angle, color);
            return this;
        }

        public CircularColorGradient build() {
            if (gradientMap.isEmpty()) {
                throw new IllegalStateException("Gradient must have at least one point");
            }
            return new CircularColorGradient(gradientMap);
        }
    }
}
