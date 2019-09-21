package kr.devx.satcounter.Util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;

public class UnivData {
    private final String name;
    private final String slogan;
    private final Uri logo;
    private final int logoDrawable;
    private final String mainColorHex;
    private final String subColorHex;
    private final Context context;

    public UnivData(Context context, String name, String slogan, int logoDrawable, String mainColorHex) {
        this(context, name, slogan, logoDrawable, mainColorHex, null);
    }
    public UnivData(Context context, String name, String slogan, int logoDrawable, String mainColorHex, String subColorHex) {
        this.context = context;
        this.name = name;
        this.slogan = slogan;
        this.mainColorHex = mainColorHex;
        this.subColorHex = subColorHex;
        this.logoDrawable = logoDrawable;
        Resources resources = context.getResources();
        this.logo = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + resources.getResourcePackageName(logoDrawable) + '/' + resources.getResourceTypeName(logoDrawable) + '/' + resources.getResourceEntryName(logoDrawable) );
    }
    public String getName() {
        return name;
    }
    public String getSlogan() {
        return slogan;
    }
    public Uri getLogo() {
        return logo;
    }
    public int getLogoDrawable() {
        return logoDrawable;
    }
    public String getMainColorHex() {
        return mainColorHex;
    }
    public String getSubColorHex() {
        return subColorHex;
    }
}
