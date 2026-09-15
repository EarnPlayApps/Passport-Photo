package com.earnplayapps.passportphoto;

public final class AdMavenConfig {
    private AdMavenConfig(){}
    // Paste the exact publisher ad/tag URL supplied by your AdMaven dashboard here.
    // No fake publisher ID is hard-coded. Empty means ads remain disabled until configured.
    public static final String PUBLISHER_TAG_URL = "";
    public static boolean enabled(){return PUBLISHER_TAG_URL != null && !PUBLISHER_TAG_URL.trim().isEmpty();}
}
