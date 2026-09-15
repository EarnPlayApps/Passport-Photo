package com.earnplayapps.passportphoto;

import java.util.Arrays;
import java.util.List;

public final class MalaysiaRequirements {
    private MalaysiaRequirements() {}
    public static List<Requirement> all() {
        return Arrays.asList(
            new Requirement("MY-PASSPORT-CHILD-U4","UTAMA","Jabatan Imigresen Malaysia","Pasport Malaysia - kanak-kanak bawah 4 tahun","Kanak-kanak <4","passport",1,35,50,"35 x 50 mm","Putih","Pandangan terus; mata terbuka; mulut tertutup; tiada bayang","JPG/PNG","Tidak dinyatakan","OFFICIAL_EXPLICIT","ACTIVE","https://www.imi.gov.my/index.php/perkhidmatan-utama/pasport/pasport-malaysia-antarabangsa/"),
            new Requirement("MY-ESD-MYVISA-VTR","UTAMA","ESD / Jabatan Imigresen Malaysia","MyVISA VTR","Pemohon","visa",1,35,50,"35 x 50 mm","Putih","Muka penuh; mata terbuka; ekspresi neutral; tidak senget; tiada bingkai","JPG","25 KB","OFFICIAL_EXPLICIT","ACTIVE","https://esd.imi.gov.my/portal/photo-requirements/"),
            new Requirement("MY-ESD-MYVISA-VDR","UTAMA","ESD / Jabatan Imigresen Malaysia","MyVISA VDR","Pemohon","visa",1,35,50,"35 x 50 mm","Putih atau biru","Muka penuh; mata terbuka; ekspresi neutral; tidak senget; tiada bingkai","JPG","25 KB","OFFICIAL_EXPLICIT","ACTIVE","https://esd.imi.gov.my/portal/photo-requirements/"),
            new Requirement("MY-JIM-STUDENT-PASS","IMIGRESEN","Jabatan Imigresen Malaysia","Pas Pelajar","Pemohon","passport",1,35,50,"35 x 50 mm","Putih","Foto jelas","JPG/print","Tidak dinyatakan","OFFICIAL_EXPLICIT","ACTIVE","https://www.imi.gov.my/index.php/perkhidmatan-utama/pas/pas-pelajar/"),
            new Requirement("MY-PDRM-PENSION-CARD","PDRM","Polis Diraja Malaysia","Kad Pesara PDRM","Pesara","passport",1,40,50,"40 x 50 mm","Biru","Muka menghadap kamera; mata terbuka; mulut tertutup; tiada cermin mata/lensa; tiada bayang","Tidak dinyatakan","Minimum 50 KB","OFFICIAL_EXPLICIT","ACTIVE","https://epesara.rmp.gov.my/perkhidmatan/kad-pesara"),
            new Requirement("MY-JPJ-LDL","JPJ","Jabatan Pengangkutan Jalan Malaysia","Lesen Belajar Memandu (LDL)","Pemohon","licence",1,25,32,"25 x 32 mm","Putih","Foto berwarna","Tidak dinyatakan","Tidak dinyatakan","OFFICIAL_EXPLICIT","ACTIVE","https://www.jpj.gov.my/pusat-media-3/informasi-perkhidmatan-jpj/pemandu/permohonan-lesen-belajar-memandu-ldl/"),
            new Requirement("MY-MBDK-MYLESSEN","PBT","Majlis Bandaraya Diraja Klang","MyLesen","Pemohon","licence",1,35,50,"35 x 50 mm","Putih","Tiada bayang","Tidak dinyatakan","20 MB","OFFICIAL_EXPLICIT","ACTIVE","https://mylesen.mbdk.gov.my/register"),
            new Requirement("MY-JPN-CITIZEN-ARTICLE14","JPN","Jabatan Pendaftaran Negara","Taraf Kewarganegaraan Perkara 14","Pemohon","passport",3,0,0,"Passport-size / ukuran tidak dinyatakan","Biru","3 keping foto terkini","Tidak dinyatakan","Tidak dinyatakan","OFFICIAL_EXPLICIT","ACTIVE","https://www.jpn.gov.my/perkhidmatan/warganegara/permohonan-taraf-kewarganegaraan-di-bawah-perkara-14-perlembagaan-persekutuan-kewarganegaraan-kelahiran-dalam-negara/"),
            new Requirement("MY-SPPIM-MARRIAGE","AGAMA","JAKIM / SPPIM","Pendaftaran Perkahwinan","Pemohon","passport",1,0,0,"Passport-size / ukuran tidak dinyatakan","Tidak dinyatakan","Mengikut keperluan urusan negeri","Tidak dinyatakan","Tidak dinyatakan","OFFICIAL_EXPLICIT","ACTIVE","https://www.malaysia.gov.my/my/digital-services/marriage-registration-sppim")
        );
    }
}
