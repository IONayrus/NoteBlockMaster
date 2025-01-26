package net.nayrus.noteblockmaster.render.utils;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.Mth;
import net.nayrus.noteblockmaster.utils.Utils;
import org.joml.Matrix4f;

import java.awt.*;

public class GeometryBuilder {

    public static void buildFlippedCone(Matrix4f matrix, VertexConsumer builder, Color color, float scale, float alpha, int resolution) {
        float red = color.getRed() / 255f, green = color.getGreen() / 255f, blue = color.getBlue() / 255f;

        float startX = 0 + (1 - scale) / 2, startY = 0 + (1 - scale) / 2, startZ = -1 + (1 - scale) / 2, endX = 1 - (1 - scale) / 2, endY = 1 - (1 - scale) / 2, endZ = 0 - (1 - scale) / 2;
        float midX = (startX + endX) / 2, midZ = (startZ + endZ) / 2;
        float dX = Math.abs(midX - startX), dZ = Math.abs(midX - startX);

        for(int i = 0; i < resolution; i++){
            float w1 = (i / (float)resolution) * Utils.PI * 2 + Utils.PI * 0.25F;
            float w2 = ((i + 1) /(float)resolution) * Utils.PI * 2 + Utils.PI * 0.25F;
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

    public static void buildHalfTorus(Matrix4f matrix, VertexConsumer builder, Color color, float scale, float radius, float innerRadius, float radialOffset, float alpha, int resolution) {
        float red = color.getRed() / 255f, green = color.getGreen() / 255f, blue = color.getBlue() / 255f;

        float startX = 0 + (1 - scale) / 2, startY = 0 + (1 - scale) / 2, startZ = -1 + (1 - scale) / 2, endX = 1 - (1 - scale) / 2, endY = 1 - (1 - scale) / 2, endZ = 0 - (1 - scale) / 2;
        float midY = (startY + endY) / 2, midX = (startX + endX) / 2, midZ = (startZ + endZ) / 2;
        float r = innerRadius * scale;
        float R = radius * scale;

        for(int i = 0; i< resolution / 2; i++) {
            float w1 = (i / (float) resolution) * Utils.PI * 2 + radialOffset + Utils.PI * 0.25F;
            float w2 = ((i + 1) / (float) resolution) * Utils.PI * 2 + radialOffset + Utils.PI * 0.25F;
            float cos1 = Mth.cos(w1), cos2 = Mth.cos(w2);
            float sin1 = Mth.sin(w1), sin2 = Mth.sin(w2);

            for (int k = 0; k < resolution; k++) {
                float w3 = (k / (float) resolution) * Utils.PI * 2 + Utils.PI * 0.25F;
                float w4 = ((k + 1) / (float) resolution) * Utils.PI * 2 + Utils.PI * 0.25F;
                float cos3 = Mth.cos(w3), cos4 = Mth.cos(w4);
                float sin3 = Mth.sin(w3), sin4 = Mth.sin(w4);

                builder.addVertex(matrix, midX + (R + r * cos3) * cos1, midY + r * sin3, midZ + (R + r * cos3) * sin1).setColor(red, green, blue, alpha);
                builder.addVertex(matrix, midX + (R + r * cos4) * cos1, midY + r * sin4, midZ + (R + r * cos4) * sin1).setColor(red, green, blue, alpha);
                builder.addVertex(matrix, midX + (R + r * cos4) * cos2, midY + r * sin4, midZ + (R + r * cos4) * sin2).setColor(red, green, blue, alpha);
                builder.addVertex(matrix, midX + (R + r * cos3) * cos2, midY + r * sin3, midZ + (R + r * cos3) * sin2).setColor(red, green, blue, alpha);
            }
        }
    }

    public static void buildTorus(Matrix4f matrix, VertexConsumer builder, Color color, float scale, float radius, float innerRadius, float alpha, int resolution) {
        buildTorus(matrix, builder, color, scale, radius, innerRadius, 0, alpha, resolution);
    }

    public static void buildTorus(Matrix4f matrix, VertexConsumer builder, Color color, float scale, float radius, float innerRadius, float radialOffset, float alpha, int resolution) {
        float red = color.getRed() / 255f, green = color.getGreen() / 255f, blue = color.getBlue() / 255f;

        float startX = 0 + (1 - scale) / 2, startY = 0 + (1 - scale) / 2, startZ = -1 + (1 - scale) / 2, endX = 1 - (1 - scale) / 2, endY = 1 - (1 - scale) / 2, endZ = 0 - (1 - scale) / 2;
        float midY = (startY + endY) / 2, midX = (startX + endX) / 2, midZ = (startZ + endZ) / 2;
        float r = innerRadius * scale;
        float R = radius * scale;

        for(int i = 0; i< resolution; i++) {
            float w1 = (i / (float) resolution) * Utils.PI * 2 + radialOffset + Utils.PI * 0.25F;
            float w2 = ((i + 1) / (float) resolution) * Utils.PI * 2 + radialOffset + Utils.PI * 0.25F;
            float cos1 = Mth.cos(w1), cos2 = Mth.cos(w2);
            float sin1 = Mth.sin(w1), sin2 = Mth.sin(w2);

            for (int k = 0; k < resolution; k++) {
                float w3 = (k / (float) resolution) * Utils.PI * 2 + Utils.PI * 0.25F;
                float w4 = ((k + 1) / (float) resolution) * Utils.PI * 2 + Utils.PI * 0.25F;
                float cos3 = Mth.cos(w3), cos4 = Mth.cos(w4);
                float sin3 = Mth.sin(w3), sin4 = Mth.sin(w4);

                builder.addVertex(matrix, midX + (R + r * cos3) * cos1, midY + r * sin3, midZ + (R + r * cos3) * sin1).setColor(red, green, blue, alpha);
                builder.addVertex(matrix, midX + (R + r * cos4) * cos1, midY + r * sin4, midZ + (R + r * cos4) * sin1).setColor(red, green, blue, alpha);
                builder.addVertex(matrix, midX + (R + r * cos4) * cos2, midY + r * sin4, midZ + (R + r * cos4) * sin2).setColor(red, green, blue, alpha);
                builder.addVertex(matrix, midX + (R + r * cos3) * cos2, midY + r * sin3, midZ + (R + r * cos3) * sin2).setColor(red, green, blue, alpha);
            }
        }
    }

    public static void buildTorusWithGradient(Matrix4f matrix, VertexConsumer builder, float scale, float radius, float innerRadius, float radialOffset, int resolution, CircularColorGradient gradient) {
        float startX = 0 + (1 - scale) / 2, startY = 0 + (1 - scale) / 2, startZ = -1 + (1 - scale) / 2, endX = 1 - (1 - scale) / 2, endY = 1 - (1 - scale) / 2, endZ = 0 - (1 - scale) / 2;
        float midY = (startY + endY) / 2, midX = (startX + endX) / 2, midZ = (startZ + endZ) / 2;
        float r = innerRadius * scale;
        float R = radius * scale;

        for(int i = 0; i< resolution; i++) {
            float w1 = (i / (float) resolution) * Utils.PI * 2 + radialOffset + Utils.PI * 0.25F;
            float w2 = ((i + 1) / (float) resolution) * Utils.PI * 2 + radialOffset + Utils.PI * 0.25F;
            float cos1 = Mth.cos(w1), cos2 = Mth.cos(w2);
            float sin1 = Mth.sin(w1), sin2 = Mth.sin(w2);
            Color lower = gradient.getColor(w1);
            Color higher = gradient.getColor(w2);

            for (int k = 0; k < resolution; k++) {
                float w3 = (k / (float) resolution) * Utils.PI * 2 + Utils.PI * 0.25F;
                float w4 = ((k + 1) / (float) resolution) * Utils.PI * 2 + Utils.PI * 0.25F;
                float cos3 = Mth.cos(w3), cos4 = Mth.cos(w4);
                float sin3 = Mth.sin(w3), sin4 = Mth.sin(w4);

                builder.addVertex(matrix, midX + (R + r * cos3) * cos1, midY + r * sin3, midZ + (R + r * cos3) * sin1).setColor(lower.getRGB());
                builder.addVertex(matrix, midX + (R + r * cos4) * cos1, midY + r * sin4, midZ + (R + r * cos4) * sin1).setColor(lower.getRGB());
                builder.addVertex(matrix, midX + (R + r * cos4) * cos2, midY + r * sin4, midZ + (R + r * cos4) * sin2).setColor(higher.getRGB());
                builder.addVertex(matrix, midX + (R + r * cos3) * cos2, midY + r * sin3, midZ + (R + r * cos3) * sin2).setColor(higher.getRGB());
            }
        }
    }

}
