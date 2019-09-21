package kr.devx.satcounter.Util;

import android.content.Context;
import android.net.Uri;
import java.util.Date;

import kr.devx.satcounter.R;

public class UserSetting {
    public final static String DATE_FORMAT = "yyyy/MM/dd";
    public enum USER_MODE {
        MODE_SUNEUNG, MODE_SAT, MODE_NORMAL;
        public static USER_MODE modeFromInteger(int x) {
            switch(x) {
                case 0:
                    return MODE_SUNEUNG;
                case 1:
                    return MODE_SAT;
                case 2:
                    return MODE_NORMAL;
            }
            return null;
        }
    }
    private USER_MODE mode;
    private String goal;
    private String goalOptional;
    private Uri goalImage;
    private Date date;
    private String userThemeColor;
    private String userTitleColor;
    private String userMessageColor;
    private String userSloganColor;

    public UserSetting() { }

    public void setUserMode(USER_MODE mode) {
        this.mode = mode;
    }
    public void setUserGoal(String goal) {
        this.goal = goal;
    }
    public void setUserGoalOptional(String goalOptional) {
        this.goalOptional = goalOptional;
    }
    public void setUserGoalImage(Uri goalImagePath) {
        this.goalImage = goalImagePath;
    }
    public void setUserDate(Date date) {
        this.date = date;
    }

    public USER_MODE getUserMode() {
        return mode;
    }
    public String getUserGoal() {
        return goal;
    }
    public String getUserGoalOptional() {
        return goalOptional;
    }
    public Uri getUserGoalImage() {
        return goalImage;
    }
    public Date getUserDate() {
        return date;
    }

    public String nameFromValue(Context context, USER_MODE value) {
        switch (value) {
            case MODE_SUNEUNG:
                return context.getString(R.string.setting_mode_name_suneung);
            case MODE_SAT:
                return context.getString(R.string.setting_mode_name_sat);
            case MODE_NORMAL:
                return context.getString(R.string.setting_mode_name_normal);
        }
        return null;
    }
}
