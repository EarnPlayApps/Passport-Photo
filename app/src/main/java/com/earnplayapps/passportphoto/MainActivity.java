package com.earnplayapps.passportphoto;

import android.app.*;
import android.content.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import java.util.*;

public class MainActivity extends Activity {
    static final int PICK=1001;
    String pendingRequirementId="";
    int dp(int v){return (int)(v*getResources().getDisplayMetrics().density+.5f);}
    int navy(){return Color.rgb(19,39,75);} int blue(){return Color.rgb(38,105,226);} int muted(){return Color.rgb(91,105,128);}
    GradientDrawable shape(int fill,int stroke,float radius){GradientDrawable g=new GradientDrawable();g.setColor(fill);g.setCornerRadius(dp((int)radius));if(stroke!=0)g.setStroke(dp(1),stroke);return g;}
    TextView tv(String s,float z,int c){TextView t=new TextView(this);t.setText(s);t.setTextSize(z);t.setTextColor(c);return t;}
    TextView label(String s){TextView t=tv(s,13,muted());t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);t.setLetterSpacing(.08f);return t;}
    Button button(String s){Button b=new Button(this);b.setText(s);b.setTextSize(14);b.setTextColor(navy());b.setAllCaps(false);b.setMinHeight(dp(48));b.setPadding(dp(12),0,dp(12),0);b.setBackground(shape(Color.WHITE,Color.rgb(222,229,241),16));b.setElevation(dp(2));return b;}
    LinearLayout column(){LinearLayout l=new LinearLayout(this);l.setOrientation(LinearLayout.VERTICAL);return l;}
    void pageBase(LinearLayout l){l.setPadding(dp(18),dp(16),dp(18),dp(24));l.setBackgroundColor(Color.rgb(246,249,253));}

    @Override public void onCreate(Bundle b){super.onCreate(b);try{home();}catch(Exception e){emergency(e);}}
    void emergency(Exception e){LinearLayout l=column();pageBase(l);l.addView(tv("Passport Photo",28,navy()));l.addView(tv("Paparan utama sedang dipulihkan.",15,muted()));Button r=button("Cuba Semula");r.setOnClickListener(v->{try{home();}catch(Exception x){Toast.makeText(this,"Ralat: "+x.getClass().getSimpleName(),Toast.LENGTH_LONG).show();}});l.addView(r);setContentView(l);}

    TextView section(String s){TextView t=label(s);t.setTextSize(15);t.setTextColor(navy());t.setPadding(dp(2),dp(22),0,dp(10));return t;}
    TextView pill(String s){TextView t=tv(s,11,Color.WHITE);t.setGravity(Gravity.CENTER);t.setPadding(dp(12),dp(6),dp(12),dp(6));t.setBackground(shape(blue(),0,20));return t;}

    View hero(){
        LinearLayout card=column();card.setPadding(dp(18),dp(18),dp(18),dp(18));card.setBackground(shape(Color.WHITE,Color.rgb(207,222,246),24));card.setElevation(dp(5));
        LinearLayout top=new LinearLayout(this);top.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout copy=column();TextView p= pill("FUNGSI UTAMA");copy.addView(p,new LinearLayout.LayoutParams(dp(116),dp(30)));
        TextView h=tv("Passport Malaysia",24,navy());h.setTypeface(Typeface.DEFAULT,Typeface.BOLD);h.setPadding(0,dp(10),0,dp(3));copy.addView(h);
        TextView d=tv("Sediakan foto dengan saiz, latar dan semakan mengikut urusan Malaysia.",13,muted());d.setLineSpacing(0,1.1f);copy.addView(d);
        top.addView(copy,new LinearLayout.LayoutParams(0,-2,1));
        TextView arrow=tv("›",38,Color.WHITE);arrow.setGravity(Gravity.CENTER);arrow.setBackground(shape(blue(),0,50));top.addView(arrow,new LinearLayout.LayoutParams(dp(54),dp(54)));
        card.addView(top);
        LinearLayout actions=new LinearLayout(this);actions.setPadding(0,dp(16),0,0);
        actions.addView(heroAction("Ambil Foto","Camera",v->startActivity(new Intent(this,CameraActivity.class))),new LinearLayout.LayoutParams(0,dp(76),1));
        actions.addView(heroAction("Pilih Galeri","Gallery",v->pick(null)),new LinearLayout.LayoutParams(0,dp(76),1));
        actions.addView(heroAction("Guna Template","Preset",v->requirementsForMain("Passport Malaysia")),new LinearLayout.LayoutParams(0,dp(76),1));
        card.addView(actions);return card;
    }
    View heroAction(String a,String b,View.OnClickListener c){LinearLayout x=column();x.setGravity(Gravity.CENTER);x.setPadding(dp(4),dp(5),dp(4),dp(5));x.setBackground(shape(Color.rgb(248,251,255),Color.rgb(229,235,245),16));x.setOnClickListener(c);TextView i=tv(b,11,blue());i.setGravity(Gravity.CENTER);i.setTypeface(Typeface.DEFAULT,Typeface.BOLD);x.addView(i);TextView t=tv(a,12,navy());t.setGravity(Gravity.CENTER);t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);x.addView(t);return x;}

    View serviceCard(String name,String sub,View.OnClickListener c){LinearLayout x=column();x.setPadding(dp(14),dp(14),dp(12),dp(14));x.setBackground(shape(Color.WHITE,Color.rgb(225,231,241),20));x.setElevation(dp(2));x.setOnClickListener(c);TextView i=tv(name.substring(0,Math.min(3,name.length())).toUpperCase(),11,blue());i.setTypeface(Typeface.DEFAULT,Typeface.BOLD);x.addView(i);TextView n=tv(name,16,navy());n.setTypeface(Typeface.DEFAULT,Typeface.BOLD);n.setPadding(0,dp(8),0,dp(3));x.addView(n);TextView s=tv(sub,12,muted());s.setMaxLines(2);x.addView(s);return x;}
    void serviceRow(LinearLayout parent,String a,String as,String b,String bs,String c,String cs){LinearLayout row=new LinearLayout(this);row.setWeightSum(3);row.addView(serviceCard(a,as,v->requirementsForMain(a)),new LinearLayout.LayoutParams(0,dp(132),1));row.addView(space(dp(8)));row.addView(serviceCard(b,bs,v->requirementsForMain(b)),new LinearLayout.LayoutParams(0,dp(132),1));row.addView(space(dp(8)));row.addView(serviceCard(c,cs,v->requirementsForMain(c)),new LinearLayout.LayoutParams(0,dp(132),1));parent.addView(row);}
    View space(int w){Space s=new Space(this);s.setLayoutParams(new LinearLayout.LayoutParams(w,1));return s;}

    void categoryGrid(LinearLayout parent,String[] names){for(int start=0;start<names.length;start+=2){LinearLayout row=new LinearLayout(this);row.setWeightSum(2);for(int j=0;j<2;j++){int idx=start+j;if(idx<names.length){String n=names[idx];Button b=button(n);b.setGravity(Gravity.CENTER);b.setTextSize(13);b.setTypeface(Typeface.DEFAULT,Typeface.BOLD);b.setOnClickListener(v->{if(n.equals("Borang")||n.equals("Custom Size"))customBuilder(n);else if(n.equals("Profile")||n.equals("Sekolah")||n.equals("Universiti / Kolej")||n.equals("Biasiswa")||n.equals("Kerajaan / Penjawat Awam")||n.equals("Kerja / Resume")||n.equals("Profesional / Kad / Permit kerja"))generic(n);else requirements(n);});LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,dp(62),1);p.setMargins(0,0,j==0?dp(8):0,dp(8));row.addView(b,p);}else row.addView(new Space(this),new LinearLayout.LayoutParams(0,dp(62),1));}parent.addView(row);}}

    View toolCard(String name,String sub,View.OnClickListener c){LinearLayout x=column();x.setGravity(Gravity.CENTER_VERTICAL);x.setPadding(dp(14),dp(10),dp(14),dp(10));x.setBackground(shape(Color.WHITE,Color.rgb(225,231,241),18));x.setOnClickListener(c);TextView n=tv(name,14,navy());n.setTypeface(Typeface.DEFAULT,Typeface.BOLD);x.addView(n);x.addView(tv(sub,11,muted()));return x;}

    void home(){
        ScrollView sv=new ScrollView(this);LinearLayout root=column();pageBase(root);
        LinearLayout header=new LinearLayout(this);header.setGravity(Gravity.CENTER_VERTICAL);LinearLayout hc=column();TextView brand=tv("Passport Photo",28,navy());brand.setTypeface(Typeface.DEFAULT,Typeface.BOLD);hc.addView(brand);hc.addView(tv("Malaysia • Photo preparation, compliance & print",13,muted()));header.addView(hc,new LinearLayout.LayoutParams(0,-2,1));Button set=button("Settings");set.setOnClickListener(v->infoScreen());header.addView(set,new LinearLayout.LayoutParams(dp(92),dp(46)));root.addView(header);
        TextView tagline=tv("Foto rasmi, lebih mudah.",16,blue());tagline.setTypeface(Typeface.DEFAULT,Typeface.BOLD);tagline.setPadding(dp(2),dp(16),0,dp(12));root.addView(tagline);
        LinearLayout status=new LinearLayout(this);status.setGravity(Gravity.CENTER_VERTICAL);status.setPadding(dp(12),dp(8),dp(12),dp(8));status.setBackground(shape(ProManager.isPro(this)?Color.rgb(232,247,240):Color.rgb(238,244,255),0,14));status.addView(tv(ProManager.isPro(this)?"PRO • RM19.90 Lifetime • Ads off":"FREE • Core features",12,ProManager.isPro(this)?Color.rgb(27,118,72):blue()));root.addView(status);
        root.addView(hero());
        root.addView(section("PERKHIDMATAN UTAMA"));LinearLayout services=column();serviceRow(services,"MyOnline Passport","Permohonan dalam talian","Visa / MyVISA","VTR / VDR","Imigresen","Dokumen & pas");root.addView(services);
        root.addView(section("DOKUMEN RASMI"));categoryGrid(root,new String[]{"JPN","JPJ","PDRM","JKM / OKU","JHEV / ATM","JPA","KKM / Profesional","JAKIM / SPPIM","Agama Negeri","Bantuan / Zakat","PBT / Lesen / Permit","Agensi Kerajaan Lain"});
        root.addView(section("PENDIDIKAN"));categoryGrid(root,new String[]{"Sekolah","Universiti / Kolej","Biasiswa"});
        root.addView(section("KERJA"));categoryGrid(root,new String[]{"Kerajaan / Penjawat Awam","Kerja / Resume","Profesional / Kad / Permit kerja"});
        root.addView(section("ALAT TAMBAHAN"));categoryGrid(root,new String[]{"Borang","Profile","Custom Size"});
        root.addView(section("AKSES PANTAS"));LinearLayout quick=new LinearLayout(this);quick.setWeightSum(4);quick.addView(toolCard("Kamera","Ambil foto",v->startActivity(new Intent(this,CameraActivity.class))),new LinearLayout.LayoutParams(0,dp(72),1));quick.addView(space(dp(6)));quick.addView(toolCard("Galeri","Pilih foto",v->pick(null)),new LinearLayout.LayoutParams(0,dp(72),1));quick.addView(space(dp(6)));quick.addView(toolCard("Projek","History",v->projects()),new LinearLayout.LayoutParams(0,dp(72),1));quick.addView(space(dp(6)));quick.addView(toolCard("Pro","Lifetime",v->startActivity(new Intent(this,PaymentActivity.class))),new LinearLayout.LayoutParams(0,dp(72),1));root.addView(quick);
        TextView foot=tv("Pemprosesan lokal apabila boleh • Tiada muat naik foto automatik\nPenerimaan akhir tertakluk kepada pihak berkuasa berkaitan.",11,muted());foot.setGravity(Gravity.CENTER);foot.setPadding(dp(8),dp(20),dp(8),dp(10));root.addView(foot);
        sv.addView(root);setContentView(sv);
    }

    void requirementsForMain(String x){if(x.equals("MyOnline Passport")){List<Requirement>a=new ArrayList<>();for(Requirement r:MalaysiaRequirements.all())if(r.purpose.toLowerCase().contains("passport")&&r.agency.toLowerCase().contains("imigresen"))a.add(r);if(a.isEmpty())generic(x);else listScreen(x,a);return;}List<Requirement>a=new ArrayList<>();for(Requirement r:MalaysiaRequirements.all()){String p=r.purpose.toLowerCase();if(x.equals("Visa / MyVISA")&&p.contains("myvisa"))a.add(r);else if(x.equals("Imigresen")&&r.agency.toLowerCase().contains("imigresen"))a.add(r);else if(x.equals("Passport Malaysia")&&p.contains("pasport malaysia"))a.add(r);}if(a.isEmpty())generic(x);else listScreen(x,a);}
    void requirements(String cat){List<Requirement>a=new ArrayList<>();for(Requirement r:MalaysiaRequirements.all())if(r.category.equals(cat)||r.agency.toUpperCase().contains(cat.toUpperCase()))a.add(r);if(a.isEmpty())generic(cat);else listScreen(cat,a);}
    void listScreen(String cat,List<Requirement>ls){ScrollView sv=new ScrollView(this);LinearLayout r=column();pageBase(r);r.addView(tv(cat,25,navy()));TextView note=tv("Pilih urusan. Ukuran hanya dipaparkan jika sumber rasmi menyatakannya.",13,muted());note.setPadding(0,dp(5),0,dp(14));r.addView(note);for(Requirement x:ls){Button q=button(x.purpose+"\n"+x.size+" • "+x.background+" • "+x.evidence);q.setOnClickListener(v->requirementDetail(x));r.addView(q,new LinearLayout.LayoutParams(-1,dp(68)));((LinearLayout.LayoutParams)q.getLayoutParams()).setMargins(0,0,0,dp(8));}Button back=button("Kembali");back.setOnClickListener(v->home());r.addView(back);sv.addView(r);setContentView(sv);}
    void requirementDetail(Requirement x){String msg="Agency: "+x.agency+"\nState: "+x.state+"\nInstitution: "+x.institution+"\nPurpose: "+x.purpose+"\nApplicant: "+x.applicant+"\nPhoto role: "+x.photoRole+"\nQuantity: "+x.quantity+"\nSize: "+x.size+"\nBackground: "+x.background+"\nFace rules: "+x.faceRules+"\nFormat: "+x.format+"\nMax file size: "+x.maxFileSize+"\nEvidence: "+x.evidence+"\nStatus: "+x.status+"\nVerified: "+x.verifiedDate+"\n\nOfficial source:\n"+x.source;new AlertDialog.Builder(this).setTitle(x.purpose).setMessage(msg).setPositiveButton("Guna Requirement",(d,w)->pick(x)).setNeutralButton("Sumber Rasmi",(d,w)->{try{startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(x.source)));}catch(Exception e){Toast.makeText(this,"Sumber tidak dapat dibuka.",Toast.LENGTH_SHORT).show();}}).setNegativeButton("Kembali",null).show();}
    void generic(String name){new AlertDialog.Builder(this).setTitle(name).setMessage("Tiada ukuran universal rasmi dimasukkan untuk kategori ini. Jangan anggap passport-size sebagai ukuran tertentu. Jika borang atau institusi memberi ukuran sendiri, gunakan Custom Size.").setPositiveButton("Custom Size",(d,w)->customBuilder(name)).setNeutralButton("Pilih Foto",(d,w)->pick(null)).setNegativeButton("Kembali",null).show();}
    void customBuilder(String name){LinearLayout l=column();l.setPadding(dp(4),0,dp(4),0);EditText w=new EditText(this);w.setHint("Width (mm)");w.setInputType(2);l.addView(w);EditText h=new EditText(this);h.setHint("Height (mm)");h.setInputType(2);l.addView(h);Spinner bg=new Spinner(this);String[] o={"Tidak dinyatakan","Putih","Biru","Kelabu muda"};bg.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,o));l.addView(bg);new AlertDialog.Builder(this).setTitle(name+" • Custom Builder").setMessage("Masukkan ukuran daripada borang atau institusi. Ini bukan tuntutan rasmi universal.").setView(l).setPositiveButton("Pilih Foto",(d,which)->{try{int wm=Integer.parseInt(w.getText().toString().trim()),hm=Integer.parseInt(h.getText().toString().trim());if(wm<=0||hm<=0)throw new Exception();Intent i=new Intent(this,PhotoEditorActivity.class);i.putExtra("custom_width_mm",wm);i.putExtra("custom_height_mm",hm);i.putExtra("custom_background",bg.getSelectedItem().toString());startActivity(i);}catch(Exception e){Toast.makeText(this,"Masukkan width dan height dalam mm.",Toast.LENGTH_LONG).show();}}).setNegativeButton("Batal",null).show();}
    void pick(Requirement req){pendingRequirementId=req==null?"":req.id;Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.setType("image/*");i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);i.addCategory(Intent.CATEGORY_OPENABLE);startActivityForResult(i,PICK);}
    void projects(){LinearLayout r=column();pageBase(r);r.addView(tv("Projects / History",25,navy()));List<String>a=ProjectStore.all(this);if(a.isEmpty())r.addView(tv("Belum ada export.",15,muted()));else for(String s:a)r.addView(tv(s,14,navy()));Button clear=button("Clear History");clear.setOnClickListener(v->{ProjectStore.clear(this);projects();});r.addView(clear);Button back=button("Kembali");back.setOnClickListener(v->home());r.addView(back);setContentView(r);}
    void infoScreen(){new AlertDialog.Builder(this).setTitle("Passport Photo").setMessage("Malaysia only. Pemprosesan foto secara lokal apabila boleh. Tiada muat naik foto secara automatik.\n\nPro RM19.90 Lifetime. Google Play Billing dan AdMaven tidak digunakan dalam versi semasa.\n\nPenerimaan akhir foto tertakluk kepada pihak berkuasa berkaitan.").setPositiveButton("OK",null).show();}
    @Override protected void onActivityResult(int q,int res,Intent d){super.onActivityResult(q,res,d);if(q==PICK&&res==RESULT_OK&&d!=null){if(d.getClipData()!=null&&d.getClipData().getItemCount()>0){ArrayList<String>u=new ArrayList<>();for(int z=0;z<d.getClipData().getItemCount();z++)u.add(d.getClipData().getItemAt(z).getUri().toString());Intent i=new Intent(this,BatchActivity.class);i.putStringArrayListExtra("uris",u);if(!pendingRequirementId.isEmpty())i.putExtra("requirement_id",pendingRequirementId);startActivity(i);}else if(d.getData()!=null){Intent i=new Intent(this,PhotoEditorActivity.class);i.setData(d.getData());if(!pendingRequirementId.isEmpty())i.putExtra("requirement_id",pendingRequirementId);startActivity(i);}}}
}
