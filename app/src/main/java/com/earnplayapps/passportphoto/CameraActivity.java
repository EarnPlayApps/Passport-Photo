package com.earnplayapps.passportphoto;

import android.app.Activity;import android.content.Intent;import android.os.Bundle;import android.provider.MediaStore;

public class CameraActivity extends Activity{
 @Override public void onCreate(Bundle b){super.onCreate(b);try{Intent i=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);startActivityForResult(i,10);}catch(Exception e){setResult(RESULT_CANCELED);finish();}}
 @Override protected void onActivityResult(int r,int c,Intent d){super.onActivityResult(r,c,d);if(r==10&&c==RESULT_OK&&d!=null){Intent i=new Intent(this,PhotoEditorActivity.class);i.putExtra("camera_bitmap",d.getExtras().get("data"));startActivity(i);}finish();}
}
