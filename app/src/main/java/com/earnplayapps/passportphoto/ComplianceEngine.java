package com.earnplayapps.passportphoto;

import android.graphics.Bitmap;
import android.graphics.Color;
import java.util.ArrayList;
import java.util.List;

public final class ComplianceEngine {
    public static final class Issue {
        public final String title, detail, status;
        Issue(String t, String d, String s) { title = t; detail = d; status = s; }
    }

    public static final class Report {
        public final List<Issue> issues;
        public final boolean pass;
        public final String summary;
        Report(List<Issue> i) {
            issues = i;
            boolean blocked = false;
            for (Issue x : i) if ("FAIL".equals(x.status) || "WARNING".equals(x.status)) blocked = true;
            pass = !blocked;
            summary = pass ? "PASS — READY TO EXPORT" : "CHECK REQUIRED — FIX OR VERIFY PHOTO";
        }
    }

    private ComplianceEngine() {}

    public static Report check(Bitmap b, Requirement r) {
        List<Issue> out = new ArrayList<>();
        if (b == null) {
            out.add(new Issue("Photo", "No photo loaded.", "FAIL"));
            return new Report(out);
        }
        if (r == null) {
            out.add(new Issue("Requirement", "No requirement selected. Use a documented custom specification when the form or institution provides its own requirement.", "WARNING"));
            return new Report(out);
        }

        FaceAnalyzer.Result f = FaceAnalyzer.analyze(b);
        if (!f.found) out.add(new Issue("Face", "No clear face detected. Use a front-facing photo.", "FAIL"));
        else if (f.count > 1) out.add(new Issue("Multiple faces", "More than one face detected. Select or crop the correct person.", "FAIL"));
        else if (f.confidence < 0.70f) out.add(new Issue("Head angle", "Face angle appears uncertain. Face the camera directly and verify the final crop.", "WARNING"));
        else out.add(new Issue("Face", "One face detected and approximately front-facing.", "PASS"));

        if (r.widthMm > 0 && r.heightMm > 0) {
            float required = (float) r.widthMm / r.heightMm;
            float actual = (float) b.getWidth() / b.getHeight();
            float tolerance = 0.01f;
            if (Math.abs(required - actual) / required <= tolerance)
                out.add(new Issue("Aspect ratio", r.widthMm + " × " + r.heightMm + " mm ratio matches the selected requirement.", "PASS"));
            else
                out.add(new Issue("Aspect ratio", "Current image ratio does not match " + r.widthMm + " × " + r.heightMm + " mm. Crop using the selected requirement.", "FAIL"));
            out.add(new Issue("Physical output", "The export engine will generate the selected physical dimensions. Pixels are not invented when the official source provides only millimetres.", "INFO"));
        } else {
            out.add(new Issue("Size", "Official source does not specify an exact measurement. No dimension is assumed.", "INFO"));
        }

        if (r.pixelWidth > 0 && r.pixelHeight > 0) {
            if (b.getWidth() == r.pixelWidth && b.getHeight() == r.pixelHeight)
                out.add(new Issue("Pixel dimensions", r.pixelWidth + " × " + r.pixelHeight + " px matches the selected requirement.", "PASS"));
            else
                out.add(new Issue("Pixel dimensions", "Final output must be " + r.pixelWidth + " × " + r.pixelHeight + " px. Current working image is " + b.getWidth() + " × " + b.getHeight() + " px.", "WARNING"));
        }

        String bg = r.background == null ? "" : r.background.toLowerCase();
        if (bg.contains("tidak dinyatakan") || bg.contains("unspecified") || bg.trim().isEmpty()) {
            out.add(new Issue("Background", "Background is not specified by this official source. No background rule is assumed.", "INFO"));
        } else if (bg.contains("putih")) {
            int sample = 0, white = 0;
            int sy = Math.max(1, b.getHeight() / 20), sx = Math.max(1, b.getWidth() / 20);
            for (int y = 0; y < b.getHeight(); y += sy) {
                for (int x = 0; x < b.getWidth(); x += sx) {
                    int p = b.getPixel(x, y);
                    sample++;
                    if (Color.red(p) > 235 && Color.green(p) > 235 && Color.blue(p) > 235) white++;
                }
            }
            if (sample > 0 && white / (float) sample < 0.35f)
                out.add(new Issue("Background sample", "Large parts of the sampled image are not white. Check or replace the background.", "WARNING"));
            else
                out.add(new Issue("Background", "Required white background appears plausible from colour sampling. Verify visually for shadows and uneven areas.", "INFO"));
        } else {
            out.add(new Issue("Background", "Required: " + r.background + ". Colour sampling alone cannot prove official compliance; verify visually.", "INFO"));
        }

        if (b.getWidth() < 600 || b.getHeight() < 600)
            out.add(new Issue("Resolution", "Source image is relatively small. Use the highest-quality original available.", "WARNING"));
        else
            out.add(new Issue("Resolution", "Source resolution is adequate for editing.", "PASS"));

        if (r.format != null && !r.format.trim().isEmpty() && !r.format.toLowerCase().contains("tidak dinyatakan"))
            out.add(new Issue("File format", "Required output format: " + r.format + ". Final encoded file will be validated during export.", "INFO"));
        if (r.maxFileSize != null && !r.maxFileSize.trim().isEmpty() && !r.maxFileSize.toLowerCase().contains("tidak dinyatakan"))
            out.add(new Issue("File size", "File-size rule: " + r.maxFileSize + ". Final encoded file will be validated during export.", "INFO"));
        if (r.faceRules != null && !r.faceRules.trim().isEmpty())
            out.add(new Issue("Official face rules", r.faceRules, "INFO"));

        return new Report(out);
    }
}
