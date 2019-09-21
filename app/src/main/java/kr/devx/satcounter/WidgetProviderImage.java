package kr.devx.satcounter;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.RemoteViews;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import kr.devx.satcounter.Util.DayUtil;
import kr.devx.satcounter.Util.UserSetting;

import static kr.devx.satcounter.Util.UserSetting.DATE_FORMAT;

public class WidgetProviderImage extends AppWidgetProvider {

    SATApplication satApplication;
    SharedPreferences appPreferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        satApplication = (SATApplication) context.getApplicationContext();
        appPreferences = context.getSharedPreferences("SETTING", Context.MODE_PRIVATE);
        appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, getClass()));
        for (int id : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, id);
        }
    }

    private void updateAppWidget(Context context, AppWidgetManager widgetManager, int id) {
        RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.widget_image);
        UserSetting userData;
        if (satApplication.userSetting == null) {
            userData = new UserSetting();
            userData.setUserMode(UserSetting.USER_MODE.modeFromInteger(appPreferences.getInt("USER_MODE", 2)));
            userData.setUserGoal(appPreferences.getString("USER_GOAL", null));
            userData.setUserGoalOptional(appPreferences.getString("USER_GOAL_OPTIONAL", null));
            String image = appPreferences.getString("USER_GOAL_IMAGE", null);
            if (image != null) userData.setUserGoalImage(Uri.parse(image));
            String date = appPreferences.getString("USER_DATE", null);
            SimpleDateFormat settingDateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
            if (date != null) {
                try {
                    userData.setUserDate(settingDateFormat.parse(date));
                } catch (ParseException err) {
                    err.printStackTrace();
                }
            }
            satApplication.userSetting = userData;
            if (userData.getUserMode() == UserSetting.USER_MODE.MODE_SUNEUNG) {
                String univSaveName = userData.getUserGoal();
                if (satApplication.KoreaUnivMap.containsKey(univSaveName)) {
                    satApplication.userUniversity = satApplication.KoreaUnivMap.get(univSaveName);
                }
            }
            if (userData.getUserMode() == UserSetting.USER_MODE.MODE_SAT) {
                String univSaveName = userData.getUserGoal();
                if (satApplication.UsUnivMap.containsKey(univSaveName)) {
                    satApplication.userUniversity = satApplication.UsUnivMap.get(univSaveName);
                }
            }
        }
        userData = satApplication.userSetting;

        int WIDGET_COLOR_WHITE = appPreferences.getInt("WIDGET_COLOR", 0);

        view.setTextViewText(R.id.widget_image_dayLeft, context.getString(R.string.main_general_dayleft_prefix) + DayUtil.getDaysLeft(userData.getUserDate()));
        if (satApplication.userUniversity != null) {
            view.setImageViewResource(R.id.widget_image_image, satApplication.userUniversity.getLogoDrawable());
        } else {
            view.setViewVisibility(R.id.widget_image_image, View.GONE);
        }
        if (WIDGET_COLOR_WHITE == 1) {
            view.setTextColor(R.id.widget_image_dayLeft, ContextCompat.getColor(context, R.color.colorWhite));
        }
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        view.setOnClickPendingIntent(R.id.widget_rootView, pendingIntent);

        widgetManager.updateAppWidget(id, view);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }
}