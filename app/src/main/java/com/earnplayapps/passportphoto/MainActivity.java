package com.earnplayapps.passportphoto;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends android.app.Activity {
    private int dp(float v) { return (int) (v * getResources().getDisplayMetrics().density + 0.5f); }
    private Button action(String text) {
        Button b = new Button(this); b.setText(text); b.setAllCaps(false);
        b.setMinHeight(dp(52));
        b.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return b;
    }
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        LinearLayout root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20), dp(24), dp(20), dp(20)); root.setBackgroundColor(0xFFF5F7FB);
        TextView title = new TextView(this); title.setText("Passport Photo"); title.setTextSize(30); title.setTextColor(0xFF17233D); title.setGravity(Gravity.CENTER_HORIZONTAL);
        root.addView(title, new LinearLayout.LayoutParams(-1, dp(58)));
        TextView sub = new TextView(this); sub.setText("Malaysia • Passport & Document Photo"); sub.setTextSize(15); sub.setGravity(Gravity.CENTER_HORIZONTAL); sub.setTextColor(0xFF5B6475);
        root.addView(sub, new LinearLayout.LayoutParams(-1, dp(42)));
        Button passport = action("Passport Malaysia"); passport.setOnClickListener(v -> openGallery()); root.addView(passport);
        Button online = action("MyOnline Passport"); online.setOnClickListener(v -> openGallery()); root.addView(online);
        Button visa = action("Visa / MyVISA"); visa.setOnClickListener(v -> openGallery()); root.addView(visa);
        Button immigration = action("Imigresen"); immigration.setOnClickListener(v -> openGallery()); root.addView(immigration);
        TextView docs = new TextView(this); docs.setText("\nDOKUMEN RASMI\nJPN   JPJ   PDRM   JKM/OKU\nJHEV   JPA   KKM   JAKIM/SPPIM\n\nPENDIDIKAN   •   KERJA   •   BORANG\nPROFILE   •   CUSTOM SIZE"); docs.setTextSize(15); docs.setTextColor(0xFF3E485C); docs.setGravity(Gravity.CENTER); root.addView(docs, new LinearLayout.LayoutParams(-1, 0, 1));
        TextView footer = new TextView(this); footer.setText("Malaysia requirement engine • Local-first processing"); footer.setGravity(Gravity.CENTER); footer.setTextColor(0xFF7A8393); root.addView(footer, new LinearLayout.LayoutParams(-1, dp(36)));
        setContentView(root);
    }
    private void openGallery() {
        Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT); i.setType("image/*"); i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false); i.addCategory(Intent.CATEGORY_OPENABLE); startActivityForResult(i, 1001);
    }
}
