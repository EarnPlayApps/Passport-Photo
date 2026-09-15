package com.earnplayapps.passportphoto;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.*;

public class PaymentActivity extends Activity {
    private static final int QR_N = 45;
    private static final String QR_B64 = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAD+eo0r+AQXsbDQQC6HcaC6AXQDPhXQC6qwqK6AQTqDqQQD+qqqr+AADnzKAACqWhOokAWNSPQbwC7tCaqqALA2xpyQDaYo+7SABDU6ajAAOZayooABktr2aAAG/aSNMARini7ngDLP+mjyAOpZt4tgDPcej4yAOKuLOzQC31NjuEATlqO7ywBi5h5h2ABvmu7ggCuPi3zKALCe7SiACv3qyv2AAES4JEQD+MKK6oAQSR79FgC6lK+v6AXTP9S7gC62a4iaAQSdKypAD+96gwiAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=";

    int dp(int v){return (int)(v*getResources().getDisplayMetrics().density+.5f);}
    TextView t(String s,float z){TextView v=new TextView(this);v.setText(s);v.setTextSize(z);v.setTextColor(Color.rgb(23,35,61));v.setPadding(0,dp(6),0,dp(6));return v;}

    @Override public void onCreate(Bundle b){super.onCreate(b); build();}

    private void build(){
        LinearLayout root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(dp(18),dp(18),dp(18),dp(18)); root.setBackgroundColor(Color.rgb(245,247,251));
        root.addView(t("Passport Photo Pro",28));
        root.addView(t("RM19.90 • Lifetime • 1 device",17));
        root.addView(t("Bayar menggunakan Touch 'n Go eWallet",16));
        QRView qr=new QRView(this); root.addView(qr,new LinearLayout.LayoutParams(-1,dp(300)));
        TextView info=t("Scan QR di atas untuk membuat bayaran.\nSelepas bayaran, masukkan activation code yang diberikan.",14); info.setTextColor(Color.rgb(91,100,117)); root.addView(info);
        EditText code=new EditText(this); code.setHint("Activation code"); code.setSingleLine(true); root.addView(code);
        Button activate=new Button(this); activate.setText("ACTIVATE PRO"); activate.setOnClickListener(v->{if(ProManager.activate(this,code.getText().toString())){Toast.makeText(this,"Pro activated",Toast.LENGTH_LONG).show();finish();}else Toast.makeText(this,"Invalid activation code",Toast.LENGTH_LONG).show();}); root.addView(activate,new LinearLayout.LayoutParams(-1,dp(52)));
        Button paid=new Button(this); paid.setText("Saya Dah Bayar"); paid.setOnClickListener(v->Toast.makeText(this,"Masukkan activation code selepas pembayaran.",Toast.LENGTH_LONG).show()); root.addView(paid,new LinearLayout.LayoutParams(-1,dp(52)));
        Button back=new Button(this); back.setText("Back"); back.setOnClickListener(v->finish()); root.addView(back,new LinearLayout.LayoutParams(-1,dp(52)));
        setContentView(root);
    }

    public static class QRView extends View {
        private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG); private final byte[] bytes;
        QRView(Context c){super(c); bytes=Base64.decode(QR_B64,Base64.DEFAULT); p.setColor(Color.rgb(233,30,99)); setBackgroundColor(Color.WHITE);}
        private boolean dark(int i){int bi=i>>3;int bit=7-(i&7);return bi<bytes.length && ((bytes[bi]>>bit)&1)==1;}
        @Override protected void onDraw(Canvas c){super.onDraw(c);float side=Math.min(getWidth(),getHeight());float cell=side/QR_N;float left=(getWidth()-side)/2f,top=(getHeight()-side)/2f;for(int y=0;y<QR_N;y++)for(int x=0;x<QR_N;x++)if(dark(y*QR_N+x))c.drawRect(left+x*cell,top+y*cell,left+(x+1)*cell+0.4f,top+(y+1)*cell+0.4f,p);}
    }
}
