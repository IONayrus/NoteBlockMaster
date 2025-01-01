package net.nayrus.noteblockmaster.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

import java.awt.*;

public class RenderUtils {

    public static void renderFlippedCone(Matrix4f matrix, VertexConsumer builder, Color color, float scale) {
        float red = color.getRed() / 255f, green = color.getGreen() / 255f, blue = color.getBlue() / 255f, alpha = .33f;

        float startX = 0 + (1 - scale) / 2, startY = 0 + (1 - scale) / 2, startZ = -1 + (1 - scale) / 2, endX = 1 - (1 - scale) / 2, endY = 1 - (1 - scale) / 2, endZ = 0 - (1 - scale) / 2;
        float midX = (startX + endX) / 2, midZ = (startZ + endZ) / 2;
        float dX = Math.abs(midX - startX), dZ = Math.abs(midX - startX);

        int resolution = 32;

        for(int i = 0; i < resolution; i++){
            float w1 = (i / (float)resolution) * (float) Math.PI * 2;
            float w2 = ((i + 1) /(float)resolution) * (float) Math.PI * 2;
            //Top
            builder.addVertex(matrix, midX, endY, midZ).setColor(red, green, blue, alpha);
            builder.addVertex(matrix, midX + dX * Mth.cos(w1), endY, midZ + dZ * Mth.sin(w1)).setColor(red, green, blue, alpha);
            builder.addVertex(matrix, midX + dX * Mth.cos(w2), endY, midZ + dZ * Mth.sin(w2)).setColor(red, green, blue, alpha);
            //Side
            builder.addVertex(matrix, midX, startY, midZ).setColor(red, green, blue, alpha);
            builder.addVertex(matrix, midX + dX * Mth.cos(w1), endY, midZ + dZ * Mth.sin(w1)).setColor(red, green, blue, alpha);
            builder.addVertex(matrix, midX + dX * Mth.cos(w2), endY, midZ + dZ * Mth.sin(w2)).setColor(red, green, blue, alpha);
        }
    }



}
