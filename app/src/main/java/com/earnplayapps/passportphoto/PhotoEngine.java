package com.earnplayapps.passportphoto;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import java.io.ByteArrayOutputStream;

public final class PhotoEngine {
    private PhotoEngine() {}

    public static Bitmap rotate(Bitmap src, float degrees) {
        Matrix m = new Matrix();
        m.postRotate(degrees);
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), m, true);
    }

    public static Bitmap flipHorizontal(Bitmap src) {
        Matrix m = new Matrix();
        m.setScale(-1f, 1f);
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), m, true);
    }

    public static Bitmap cropRatio(Bitmap src, int widthMm, int heightMm) {
        return cropRatioAround(src, widthMm, heightMm, src.getWidth() / 2f, src.getHeight() * .42f);
    }

    public static Bitmap cropRatioAround(Bitmap src, int widthMm, int heightMm, float cx, float targetFaceY) {
        if (widthMm <= 0 || heightMm <= 0) return src;
        int w = src.getWidth(), h = src.getHeight();
        float ratio = (float) widthMm / heightMm;
        int nw = w, nh = Math.round(w / ratio);
        if (nh > h) { nh = h; nw = Math.round(h * ratio); }
        int left = Math.round(cx - nw / 2f);
        int top = Math.round(targetFaceY - nh * .42f);
        left = Math.max(0, Math.min(w - nw, left));
        top = Math.max(0, Math.min(h - nh, top));
        return Bitmap.createBitmap(src, left, top, nw, nh);
    }

    public static Bitmap autoCrop(Bitmap src, int widthMm, int heightMm) {
        if (widthMm <= 0 || heightMm <= 0) return src;
        FaceAnalyzer.Result f = FaceAnalyzer.analyze(src);
        if (f.found && f.count == 1) return cropRatioAround(src, widthMm, heightMm, f.midX, f.midY);
        return cropRatio(src, widthMm, heightMm);
    }

    public static int mmToPx(int mm) { return Math.max(1, Math.round(mm * 300f / 25.4f)); }

    public static Bitmap resizeForPrint(Bitmap src, int widthPx, int heightPx) {
        if (widthPx <= 0 || heightPx <= 0) return src;
        return Bitmap.createScaledBitmap(src, widthPx, heightPx, true);
    }

    public static Bitmap resizeForPrintMm(Bitmap src, int widthMm, int heightMm) {
        if (widthMm <= 0 || heightMm <= 0) return src;
        Bitmap cropped = autoCrop(src, widthMm, heightMm);
        return resizeForPrint(cropped, mmToPx(widthMm), mmToPx(heightMm));
    }

    public static Bitmap adjust(Bitmap src, float brightness, float contrast) {
        Bitmap out = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
        float c = Math.max(-255, Math.min(255, contrast));
        float factor = (259f * (c + 255f)) / (255f * (259f - c));
        for (int y = 0; y < src.getHeight(); y++) {
            for (int x = 0; x < src.getWidth(); x++) {
                int p = src.getPixel(x, y);
                int r = clamp(Math.round(factor * (Color.red(p) - 128) + 128 + brightness));
                int g = clamp(Math.round(factor * (Color.green(p) - 128) + 128 + brightness));
                int bl = clamp(Math.round(factor * (Color.blue(p) - 128) + 128 + brightness));
                out.setPixel(x, y, Color.argb(Color.alpha(p), r, g, bl));
            }
        }
        return out;
    }

    public static byte[] jpeg(Bitmap src, int quality) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, Math.max(1, Math.min(100, quality)), out);
        return out.toByteArray();
    }

    public static byte[] png(Bitmap src) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.PNG, 100, out);
        return out.toByteArray();
    }

    public static long estimateBytes(Bitmap src, int quality) { return jpeg(src, quality).length; }
    private static int clamp(int v) { return Math.max(0, Math.min(255, v)); }
}
