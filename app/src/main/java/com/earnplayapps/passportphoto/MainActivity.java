package com.earnplayapps.passportphoto;

import android.app.*;
import android.content.*;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import java.util.*;

public class MainActivity extends Activity {
    static final int PICK = 1001;
    String pendingRequirementId = "";
    int dp(int v) { return (int)(v * getResources().getDisplayMetrics().density + .5f); }

    GradientDrawable bg(int color, float radius) {
        GradientDrawable g = new GradientDrawable(); g.setColor(color); g.setCornerRadius(dp((int)radius)); return g;
    }
    GradientDrawable strokeBg(int fill, int stroke, int width, float radius) {
        GradientDrawable g = bg(fill, radius); g.setStroke(dp(width), stroke); return g;
    }
    TextView text(String s, float size, int color) {
        TextView t = new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(color); return t;
    }
    TextView title(String s, int z) { TextView t=text(s,z,Color.rgb(23,35,61)); t.setPadding(0,dp(8),0,dp(6)); return t; }
    void base(LinearLayout r) { r.setPadding(dp(18),dp(18),dp(18),dp(28)); r.setBackgroundColor(Color.rgb(246,248,252)); }

    TextView section(String s) {
        TextView t=text(s,13,Color.rgb(75,89,115)); t.setTypeface(null,1); t.setLetterSpacing(.08f); t.setPadding(dp(2),dp(18),0,dp(8)); return t;
    }

    View actionCard(String name, String sub, boolean primary, View.OnClickListener click) {
        LinearLayout c=new LinearLayout(this); c.setOrientation(LinearLayout.VERTICAL); c.setPadding(dp(18),dp(15),dp(18),dp(15));
        c.setBackground(strokeBg(primary?Color.rgb(232,239,255):Color.WHITE, primary?Color.rgb(190,207,245):Color.rgb(224,229,237),1,18));
        c.setElevation(dp(primary?5:2)); c.setOnClickListener(click);
        TextView a=text(name,primary?18:16,Color.rgb(24,38,68)); a.setTypeface(null,1); c.addView(a);
        TextView b=text(sub,13,Color.rgb(92,104,126)); b.setPadding(0,dp(4),0,0); c.addView(b);
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,primary?dp(88):dp(76)); p.setMargins(0,0,0,dp(10)); c.setLayoutParams(p); return c;
    }

    Button btn(String s) {
        Button b=new Button(this); b.setText(s); b.setTextSize(14); b.setTextColor(Color.rgb(32,45,70)); b.setAllCaps(false); b.setMinHeight(dp(50)); b.setPadding(dp(14),dp(4),dp(14),dp(4)); b.setBackground(strokeBg(Color.WHITE,Color.rgb(221,226,235),1,16)); b.setElevation(dp(2)); return b;
    }

    @Override public void onCreate(Bundle b) { super.onCreate(b); try { home(); } catch(Exception e) { emergencyHome(e); } }

    void emergencyHome(Exception e) {
        LinearLayout r=new LinearLayout(this); r.setOrientation(LinearLayout.VERTICAL); base(r);
        r.addView(title("Passport Photo",28));
        TextView m=text("App sedang memulihkan paparan utama.",15,Color.rgb(80,91,112)); r.addView(m);
        Button retry=btn("Cuba Semula"); retry.setOnClickListener(v->{try{home();}catch(Exception x){Toast.makeText(this,"Ralat permulaan: "+x.getClass().getSimpleName(),Toast.LENGTH_LONG).show();}}); r.addView(retry);
        setContentView(r);
    }

    void home() {
        ScrollView sv=new ScrollView(this); LinearLayout r=new LinearLayout(this); r.setOrientation(LinearLayout.VERTICAL); base(r);
        LinearLayout head=new LinearLayout(this); head.setOrientation(LinearLayout.VERTICAL); head.setPadding(dp(20),dp(20),dp(20),dp(20)); head.setBackground(strokeBg(Color.WHITE,Color.rgb(222,228,238),1,22)); head.setElevation(dp(3));
        TextView brand=text("Passport Photo",29,Color.rgb(20,35,65)); brand.setTypeface(null,1); head.addView(brand);
        TextView sub=text("Malaysia • Photo preparation, compliance & print",14,Color.rgb(92,103,124)); sub.setPadding(0,dp(6),0,dp(12)); head.addView(sub);
        LinearLayout stat=new LinearLayout(this); stat.setGravity(Gravity.CENTER_VERTICAL); stat.setPadding(dp(12),dp(8),dp(12),dp(8)); stat.setBackground(bg(ProManager.isPro(this)?Color.rgb(232,247,240):Color.rgb(242,245,250),14));
        TextView dot=text("●",12,ProManager.isPro(this)?Color.rgb(31,132,83):Color.rgb(49,91,206)); stat.addView(dot);
        TextView st=text(ProManager.isPro(this)?"  PRO • RM19.90 Lifetime • Ads off":"  FREE • Core features",13,Color.rgb(62,74,96)); stat.addView(st); head.addView(stat);
        r.addView(head,new LinearLayout.LayoutParams(-1,-2));

        r.addView(section("UTAMA"));
        r.addView(actionCard("Passport Malaysia","Foto passport & panduan keperluan Malaysia",true,v->requirementsForMain("Passport Malaysia")));
        r.addView(actionCard("MyOnline Passport","Semak dan sediakan foto untuk urusan dalam talian",false,v->requirementsForMain("MyOnline Passport")));
        r.addView(actionCard("Visa / MyVISA","Keperluan foto VTR / VDR yang disokong",false,v->requirementsForMain("Visa / MyVISA")));
        r.addView(actionCard("Imigresen","Dokumen dan pas imigresen Malaysia",false,v->requirementsForMain("Imigresen")));

        r.addView(section("DOKUMEN RASMI"));
        addCategoryGrid(r,new String[]{"JPN","JPJ","PDRM","JKM / OKU","JHEV / ATM","JPA","KKM / Profesional","JAKIM / SPPIM","Agama Negeri","Bantuan / Zakat","PBT / Lesen / Permit","Agensi Kerajaan Lain"},false);
        r.addView(section("PENDIDIKAN"));
        addCategoryGrid(r,new String[]{"Sekolah","Universiti / Kolej","Biasiswa"},false);
        r.addView(section("KERJA"));
        addCategoryGrid(r,new String[]{"Kerajaan / Penjawat Awam","Kerja / Resume","Profesional / Kad / Permit kerja"},false);
        r.addView(section("TAMBAHAN"));
        addCategoryGrid(r,new String[]{"Borang","Profile","Custom Size"},false);

        r.addView(section("TOOLS"));
        LinearLayout tools=new LinearLayout(this); tools.setOrientation(LinearLayout.VERTICAL); tools.setPadding(dp(14),dp(14),dp(14),dp(4)); tools.setBackground(strokeBg(Color.WHITE,Color.rgb(224,229,237),1,20));
        Button cam=btn("Camera  •  Ambil gambar"); cam.setOnClickListener(v->startActivity(new Intent(this,CameraActivity.class))); tools.addView(cam);
        Button gal=btn("Gallery  •  Pilih foto / multiple photos"); gal.setOnClickListener(v->pick(null)); tools.addView(gal);
        Button pro=btn("Pro  •  RM19.90 Lifetime"); pro.setOnClickListener(v->startActivity(new Intent(this,PaymentActivity.class))); tools.addView(pro);
        Button projects=btn("Projects / History"); projects.setOnClickListener(v->projects()); tools.addView(projects);
        Button settings=btn("Settings  •  Privacy  •  Help  •  About"); settings.setOnClickListener(v->infoScreen()); tools.addView(settings);
        r.addView(tools,new LinearLayout.LayoutParams(-1,-2));

        TextView foot=text("Pemprosesan foto secara lokal apabila boleh. Tiada muat naik foto secara automatik.\nKeputusan akhir penerimaan tertakluk kepada pihak berkuasa berkaitan.",12,Color.rgb(105,115,133)); foot.setGravity(Gravity.CENTER); foot.setPadding(dp(10),dp(18),dp(10),dp(6)); r.addView(foot);
        sv.addView(r); setContentView(sv);
    }

    void addCategoryGrid(LinearLayout parent,String[] items,boolean unused){
        for(String x:items){
            Button q=btn(x); q.setGravity(Gravity.CENTER_VERTICAL); q.setOnClickListener(v->{
                if(x.equals("Borang")||x.equals("Custom Size")) customBuilder(x); else if(x.equals("Profile")||x.equals("Sekolah")||x.equals("Universiti / Kolej")||x.equals("Biasiswa")||x.equals("Kerajaan / Penjawat Awam")||x.equals("Kerja / Resume")||x.equals("Profesional / Kad / Permit kerja")) generic(x); else requirements(x);
            }); parent.addView(q,new LinearLayout.LayoutParams(-1,dp(54)));
            ((LinearLayout.LayoutParams)q.getLayoutParams()).setMargins(0,0,0,dp(8));
        }
    }

    void requirementsForMain(String x){
        if(x.equals("MyOnline Passport")){Requirement r=new Requirement("MY-MYONLINE-PASSPORT","UTAMA","Jabatan Imigresen Malaysia","MyOnline Passport","Pemohon Malaysia","passport",1,0,0,"Passport photo / ukuran fizikal tidak dinyatakan pada pengesahan muat naik","Putih","Memandang tepat; mata terbuka; mulut tertutup; rambut tidak melindungi wajah; dahi tidak ditutup sepenuhnya; pakaian gelap menutupi bahu/dada; tiada cermin mata/kanta lekap berwarna; tiada aksesori kepala; tiada cahaya flash pada dahi","JPG/format sistem","Tidak dinyatakan","OFFICIAL_EXPLICIT","ACTIVE","https://imigresen-online.imi.gov.my/eservices/myPasport");listScreen(x,Collections.singletonList(r));return;}
        List<Requirement>a=new ArrayList<>(); for(Requirement r:MalaysiaRequirements.all()){String p=r.purpose.toLowerCase(); if(x.equals("Visa / MyVISA")&&p.contains("myvisa"))a.add(r); else if(x.equals("Imigresen")&&r.agency.toLowerCase().contains("imigresen"))a.add(r); else if(x.equals("Passport Malaysia")&&p.contains("pasport malaysia"))a.add(r);} if(a.isEmpty())generic(x);else listScreen(x,a);
    }
    void requirements(String cat){List<Requirement>a=new ArrayList<>();for(Requirement r:MalaysiaRequirements.all())if(r.category.equals(cat)||r.agency.toUpperCase().contains(cat.toUpperCase()))a.add(r);if(a.isEmpty())generic(cat);else listScreen(cat,a);}
    void listScreen(String cat,List<Requirement>ls){ScrollView sv=new ScrollView(this);LinearLayout r=new LinearLayout(this);r.setOrientation(LinearLayout.VERTICAL);base(r);r.addView(title(cat,25));TextView note=title("Pilih urusan dahulu. Tekan satu urusan untuk lihat syarat dan sumber rasmi. Tiada ukuran akan dicipta jika sumber rasmi tidak menyatakannya.",14);note.setTextColor(Color.rgb(91,100,117));r.addView(note);for(Requirement x:ls){Button q=btn(x.purpose+"\n"+x.size+" • "+x.background+" • "+x.evidence);q.setOnClickListener(v->requirementDetail(x));r.addView(q);}Button custom=btn("Custom Size / Form Requirement");custom.setOnClickListener(v->customBuilder(cat));r.addView(custom);Button back=btn("Back");back.setOnClickListener(v->home());r.addView(back);sv.addView(r);setContentView(sv);}
    void requirementDetail(Requirement x){String msg="Agency: "+x.agency+"\nState: "+x.state+"\nInstitution: "+x.institution+"\nPurpose: "+x.purpose+"\nApplicant: "+x.applicant+"\nPhoto role: "+x.photoRole+"\nQuantity: "+x.quantity+"\nSize: "+x.size+"\nBackground: "+x.background+"\nFace rules: "+x.faceRules+"\nFormat: "+x.format+"\nMax file size: "+x.maxFileSize+"\nEvidence: "+x.evidence+"\nStatus: "+x.status+"\nVerified: "+x.verifiedDate+"\n\nOfficial source:\n"+x.source;new AlertDialog.Builder(this).setTitle(x.purpose).setMessage(msg).setPositiveButton("Use Requirement",(d,w)->pick(x)).setNeutralButton("Open Official Source",(d,w)->{try{startActivity(new Intent(Intent.ACTION_VIEW,android.net.Uri.parse(x.source)));}catch(Exception e){Toast.makeText(this,"Source tidak dapat dibuka.",Toast.LENGTH_SHORT).show();}}).setNegativeButton("Back",null).show();}
    void generic(String name){new AlertDialog.Builder(this).setTitle(name).setMessage("Tiada ukuran universal rasmi dimasukkan untuk kategori ini. Jangan anggap passport-size sebagai ukuran tertentu. Jika borang/institusi memberi ukuran sendiri, gunakan Custom Size.").setPositiveButton("Custom Size",(d,w)->customBuilder(name)).setNeutralButton("Choose Photo",(d,w)->pick(null)).setNegativeButton("Back",null).show();}
    void customBuilder(String name){LinearLayout l=new LinearLayout(this);l.setOrientation(LinearLayout.VERTICAL);l.setPadding(dp(4),0,dp(4),0);EditText width=new EditText(this);width.setHint("Width (mm)");width.setInputType(2);l.addView(width);EditText height=new EditText(this);height.setHint("Height (mm)");height.setInputType(2);l.addView(height);Spinner bg=new Spinner(this);String[] opts={"Tidak dinyatakan","Putih","Biru","Kelabu muda"};bg.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,opts));l.addView(bg);new AlertDialog.Builder(this).setTitle(name+" • Custom Builder").setMessage("Masukkan ukuran tepat daripada borang/institusi. Nilai ini bukan tuntutan rasmi Passport Photo.").setView(l).setPositiveButton("Choose Photo",(d,w)->{try{int wm=Integer.parseInt(width.getText().toString().trim()),hm=Integer.parseInt(height.getText().toString().trim());if(wm<=0||hm<=0)throw new Exception();Intent i=new Intent(this,PhotoEditorActivity.class);i.putExtra("custom_width_mm",wm);i.putExtra("custom_height_mm",hm);i.putExtra("custom_background",bg.getSelectedItem().toString());startActivity(i);}catch(Exception e){Toast.makeText(this,"Masukkan width dan height dalam mm.",Toast.LENGTH_LONG).show();}}).setNegativeButton("Cancel",null).show();}
    void pick(Requirement req){pendingRequirementId=req==null?"":req.id;Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.setType("image/*");i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);i.addCategory(Intent.CATEGORY_OPENABLE);startActivityForResult(i,PICK);}void pick(){pick(null);}
    void projects(){LinearLayout r=new LinearLayout(this);r.setOrientation(LinearLayout.VERTICAL);base(r);r.addView(title("Projects / History",25));List<String> all=ProjectStore.all(this);if(all.isEmpty())r.addView(title("Belum ada export.",15));else for(String s:all)r.addView(title(s,15));Button clear=btn("Clear History");clear.setOnClickListener(v->{ProjectStore.clear(this);projects();});r.addView(clear);Button back=btn("Back");back.setOnClickListener(v->home());r.addView(back);setContentView(r);}
    void infoScreen(){new AlertDialog.Builder(this).setTitle("Passport Photo").setMessage("Malaysia only. Photos are processed locally where possible. No automatic photo upload. Final acceptance remains with the relevant authority.\n\nPrivacy\nDelete saved projects/history from Projects.\n\nMonetization is currently disabled while the core photo system is completed. Pro remains RM19.90 Lifetime.").setPositiveButton("OK",null).show();}
    @Override protected void onActivityResult(int q,int res,Intent d){super.onActivityResult(q,res,d);if(q==PICK&&res==RESULT_OK&&d!=null){if(d.getClipData()!=null&&d.getClipData().getItemCount()>0){int n=d.getClipData().getItemCount();ArrayList<String> uris=new ArrayList<>();for(int z=0;z<n;z++){android.net.Uri u=d.getClipData().getItemAt(z).getUri();try{getContentResolver().takePersistableUriPermission(u,Intent.FLAG_GRANT_READ_URI_PERMISSION);}catch(Exception ignored){}uris.add(u.toString());}Intent i=new Intent(this,BatchActivity.class);i.putStringArrayListExtra("uris",uris);if(!pendingRequirementId.isEmpty())i.putExtra("requirement_id",pendingRequirementId);startActivity(i);}else if(d.getData()!=null){Intent i=new Intent(this,PhotoEditorActivity.class);i.setData(d.getData());if(!pendingRequirementId.isEmpty())i.putExtra("requirement_id",pendingRequirementId);try{getContentResolver().takePersistableUriPermission(d.getData(),Intent.FLAG_GRANT_READ_URI_PERMISSION);}catch(Exception ignored){}startActivity(i);}pendingRequirementId="";}}
}
