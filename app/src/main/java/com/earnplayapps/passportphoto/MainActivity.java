package com.earnplayapps.passportphoto;
import android.content.*;import android.net.Uri;import android.os.Bundle;import android.view.*;import android.widget.*;import java.util.List;
public class MainActivity extends android.app.Activity{
 static final int PICK=1001; int dp(int v){return(int)(v*getResources().getDisplayMetrics().density+.5f);} Button btn(String s){Button b=new Button(this);b.setText(s);b.setAllCaps(false);b.setMinHeight(dp(52));b.setLayoutParams(new LinearLayout.LayoutParams(-1,-2));return b;}
 public void onCreate(Bundle b){super.onCreate(b);home();}
 void home(){ScrollView sv=new ScrollView(this);LinearLayout r=new LinearLayout(this);r.setOrientation(LinearLayout.VERTICAL);r.setPadding(dp(20),dp(24),dp(20),dp(20));r.setBackgroundColor(0xfff5f7fb);sv.addView(r);
 TextView t=new TextView(this);t.setText("Passport Photo");t.setTextSize(30);t.setTextColor(0xff17233d);t.setGravity(17);r.addView(t,new LinearLayout.LayoutParams(-1,dp(60)));
 TextView s=new TextView(this);s.setText("Malaysia • Photo preparation & compliance");s.setGravity(17);s.setTextColor(0xff5b6475);r.addView(s,new LinearLayout.LayoutParams(-1,dp(40)));
 TextView u=new TextView(this);u.setText("UTAMA");u.setTextColor(0xff315d9a);r.addView(u);
 String[] main={"Passport Malaysia","MyOnline Passport","Visa / MyVISA","Imigresen"};for(String x:main){Button q=btn(x);q.setOnClickListener(v->pick());r.addView(q);}
 TextView d=new TextView(this);d.setText("DOKUMEN RASMI");d.setTextColor(0xff315d9a);d.setPadding(0,dp(16),0,dp(4));r.addView(d);
 String[] cats={"JPN","JPJ","PDRM","JKM / OKU","JHEV / ATM","JPA","KKM / Profesional","JAKIM / SPPIM","Agama Negeri","Bantuan / Zakat","PBT / Lesen / Permit","Agensi Kerajaan Lain"};for(String x:cats){Button q=btn(x);q.setOnClickListener(v->requirements(x));r.addView(q);}
 TextView e=new TextView(this);e.setText("PENDIDIKAN\nSekolah • Universiti / Kolej • Biasiswa\n\nKERJA\nKerajaan • Resume • Profesional\n\nTAMBAHAN\nBorang • Profile • Custom Size");e.setTextSize(15);e.setTextColor(0xff3e485c);e.setPadding(0,dp(18),0,dp(18));r.addView(e);setContentView(sv);}
 void pick(){Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.setType("image/*");i.addCategory(Intent.CATEGORY_OPENABLE);startActivityForResult(i,PICK);}
 void requirements(String cat){LinearLayout r=new LinearLayout(this);r.setOrientation(LinearLayout.VERTICAL);r.setPadding(dp(18),dp(20),dp(18),dp(20));r.setBackgroundColor(0xfff5f7fb);TextView t=new TextView(this);t.setText(cat+"\nMalaysia requirement profiles");t.setTextSize(24);t.setTextColor(0xff17233d);r.addView(t);List<Requirement> ls=MalaysiaRequirements.all();for(Requirement x:ls){if(x.category.equals(cat)||x.agency.toUpperCase().contains(cat.toUpperCase())){Button q=btn(x.purpose+"\n"+x.size+" • "+x.background);q.setOnClickListener(v->pick());r.addView(q);}}Button back=btn("Back to Home");back.setOnClickListener(v->home());r.addView(back);setContentView(r);}
 protected void onActivityResult(int q,int res,Intent d){super.onActivityResult(q,res,d);if(q==PICK&&res==RESULT_OK&&d!=null&&d.getData()!=null){Intent i=new Intent(this,PhotoEditorActivity.class);i.setData(d.getData());startActivity(i);}}
}