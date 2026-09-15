package com.earnplayapps.passportphoto;

import android.app.Activity;
import android.content.*;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.Manifest;

public class CameraActivity extends Activity {
    static final int CAM = 10, PERM = 11;

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        if (android.os.Build.VERSION.SDK_INT >= 23 && checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, PERM);
        } else open();
    }

    void open() {
        try {
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(i, CAM);
        } catch (Exception e) {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    @Override public void onRequestPermissionsResult(int r, String[] p, int[] g) {
        super.onRequestPermissionsResult(r, p, g);
        if (r == PERM && g.length > 0 && g[0] == PackageManager.PERMISSION_GRANTED) open();
        else finish();
    }

    @Override protected void onActivityResult(int r, int c, Intent d) {
        super.onActivityResult(r, c, d);
        if (r == CAM && c == RESULT_OK && d != null && d.getExtras() != null) {
            Object data = d.getExtras().get("data");
            if (data instanceof android.graphics.Bitmap) {
                android.graphics.Bitmap bitmap = (android.graphics.Bitmap) data;
                Intent i = new Intent(this, PhotoEditorActivity.class);
                PhotoEditorActivity.setPendingCameraBitmap(bitmap);
                startActivity(i);
            }
        }
        finish();
    }
}
