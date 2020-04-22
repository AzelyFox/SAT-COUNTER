package kr.devx.satcounter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.yarolegovich.lovelydialog.LovelyChoiceDialog;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cn.iwgang.countdownview.CountdownView;
import devlight.io.library.ntb.NavigationTabBar;
import kr.devx.reviewer.Review;
import kr.devx.reviewer.Reviewer;
import kr.devx.satcounter.Util.DayUtil;
import kr.devx.satcounter.Util.ReviewDialog;
import kr.devx.satcounter.Util.SmartFragmentStatePagerAdapter;
import kr.devx.satcounter.Util.UserSetting;

import static kr.devx.satcounter.SATApplication.debugLog;
import static kr.devx.satcounter.Util.UserSetting.DATE_FORMAT;

public class MainActivity extends AppCompatActivity {

    private SATApplication satApplication;
    private SharedPreferences appPreferences;
    private boolean SETTING_EXIST = false;

    public UserSetting userData;

    private TextView headerDate;
    private TextView headerDayLeft;
    private CountdownView headerCountDown;

    private ViewPager mainViewPager;
    private NavigationTabBar mainViewNavigation;
    private SmartFragmentStatePagerAdapter mainViewAdapter;
    private FloatingActionButton mainFloatingActionButton;

    private LinearLayout lockscreenSetPinView;
    private TextView lockscreenSetPinText;
    private IndicatorDots lockscreenSetPinDots;
    private PinLockView lockscreenSetPinModule;
    private LinearLayout lockscreenSetPatternView;
    private TextView lockscreenSetPatternText;
    private PatternLockView lockscreenSetPatternModule;

    private generalFragment generalFragmentInstance;
    private notificationFragment notificationFragmentInstance;
    private floatingviewFragment floatingviewFragmentInstance;
    private lockscreenFragment lockscreenFragmentInstance;

    private boolean IS_PREMIUM_USER = true;

    @Override
    public void onCreate(Bundle onSavedInstance) {
        super.onCreate(onSavedInstance);
        setContentView(R.layout.main_activity);

        satApplication = (SATApplication) getApplication();
        appPreferences = getSharedPreferences("SETTING", MODE_PRIVATE);
        SETTING_EXIST = appPreferences.getBoolean("SETTING_FINISHED",false);

        initializeId();
        initializeData();

        if (!SETTING_EXIST) {
            makeUserSetting(false);
        }

        showReviewDialog(3);
    }

    private void initializeId() {
        headerDate = findViewById(R.id.main_headerDate);
        headerDayLeft = findViewById(R.id.main_headerDayLeft);
        headerCountDown = findViewById(R.id.main_headerCountDown);
        mainViewPager = findViewById(R.id.main_viewPager);
        mainViewNavigation = findViewById(R.id.main_viewNavigation);
        mainFloatingActionButton = findViewById(R.id.main_floatingActionView);
        lockscreenSetPinView = findViewById(R.id.main_lockscreenPinSetView);
        lockscreenSetPinText = findViewById(R.id.main_lockscreenPinSetText);
        lockscreenSetPinDots = findViewById(R.id.main_lockscreenPinSetDots);
        lockscreenSetPinModule = findViewById(R.id.main_lockscreenPinSetModule);
        lockscreenSetPatternView = findViewById(R.id.main_lockscreenPatternSetView);
        lockscreenSetPatternText = findViewById(R.id.main_lockscreenPatternSetText);
        lockscreenSetPatternModule = findViewById(R.id.main_lockscreenPatternSetModule);

        mainViewAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mainViewPager.setAdapter(mainViewAdapter);
        mainViewPager.setOffscreenPageLimit(3);
        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        models.add(
                new NavigationTabBar.Model.Builder(getResources().getDrawable(R.drawable.icon_cog), ContextCompat.getColor(this, R.color.colorAccent))
                    .selectedIcon(getResources().getDrawable(R.drawable.icon_cog))
                    .title(getString(R.string.main_tab_title_0))
                    .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(getResources().getDrawable(R.drawable.icon_bell), ContextCompat.getColor(this, R.color.colorAccent))
                        .selectedIcon(getResources().getDrawable(R.drawable.icon_bell))
                        .title(getString(R.string.main_tab_title_1))
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(getResources().getDrawable(R.drawable.icon_window), ContextCompat.getColor(this, R.color.colorAccent))
                        .selectedIcon(getResources().getDrawable(R.drawable.icon_window))
                        .title(getString(R.string.main_tab_title_2))
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(getResources().getDrawable(R.drawable.icon_lock), ContextCompat.getColor(this, R.color.colorAccent))
                        .selectedIcon(getResources().getDrawable(R.drawable.icon_lock))
                        .title(getString(R.string.main_tab_title_3))
                        .build()
        );
        mainViewNavigation.setModels(models);
        mainViewNavigation.setBgColor(ContextCompat.getColor(this, R.color.colorWhite));
        mainViewNavigation.setViewPager(mainViewPager, 0);
        mainViewNavigation.setBehaviorEnabled(true);
    }

    private void initializeData() {
        if (!SETTING_EXIST) {
            return;
        }

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

        if (generalFragmentInstance != null) generalFragmentInstance.onGetUserData(userData);
        if (notificationFragmentInstance != null) notificationFragmentInstance.onGetUserData(userData);
        long time = DayUtil.getMillisecondLeft(userData.getUserDate());
        headerCountDown.start(time);
        headerDate.setText(settingDateFormat.format(userData.getUserDate()));
        headerDayLeft.setText(getString(R.string.main_general_dayleft_prefix) + String.valueOf(DayUtil.getDaysLeft(userData.getUserDate())));
    }

    public void onGeneralFragmentReady(generalFragment instance) {
        generalFragmentInstance = instance;
        initializeData();
    }

    public void onNotificationFragmentReady(notificationFragment instance) {
        notificationFragmentInstance = instance;
    }

    public void onFloatingViewFragmentReady(floatingviewFragment instance) {
        floatingviewFragmentInstance = instance;
    }

    public void onLockscreenFragmentReady(lockscreenFragment instance) {
        lockscreenFragmentInstance = instance;
    }

    public void onTalkFragments(boolean shouldRefresh) {
        if (notificationFragmentInstance != null) notificationFragmentInstance.onRefreshOrdered();
        if (floatingviewFragmentInstance != null) floatingviewFragmentInstance.onRefreshOrdered();
        if (lockscreenFragmentInstance != null) lockscreenFragmentInstance.onRefreshOrdered();
    }

    public void setLockPin() {
        lockscreenSetPinView.setVisibility(View.VISIBLE);
        lockscreenSetPinText.setText(getString(R.string.main_lockscreen_set_pin_input));
        lockscreenSetPinDots.setPinLength(6);
        lockscreenSetPinDots.setIndicatorType(IndicatorDots.IndicatorType.FILL_WITH_ANIMATION);
        lockscreenSetPinModule.attachIndicatorDots(lockscreenSetPinDots);
        lockscreenSetPinModule.setPinLength(6);
        lockscreenSetPinModule.setPinLockListener(new PinLockListener() {
            int PIN_INSERT_STEP = 0;
            String savedPin = "";
            @Override
            public void onComplete(String pin) {
                if (PIN_INSERT_STEP == 0) {
                    savedPin = pin;
                    PIN_INSERT_STEP = 1;
                    lockscreenSetPinText.setText(getString(R.string.main_lockscreen_set_pin_confirm));
                    lockscreenSetPinModule.resetPinLockView();
                    return;
                }
                if (PIN_INSERT_STEP == 1) {
                    if (savedPin.equals(pin)) {
                        appPreferences.edit().putString("LOCKSCREEN_PIN" , savedPin).apply();
                        lockscreenSetPinText.setText(getString(R.string.main_lockscreen_set_pin_input));
                        lockscreenSetPinModule.resetPinLockView();
                        PIN_INSERT_STEP = 0;
                        lockscreenSetPinView.setVisibility(View.GONE);
                        if (lockscreenFragmentInstance != null) lockscreenFragmentInstance.onPinSet();
                    } else {
                        savedPin = "";
                        PIN_INSERT_STEP = 0;
                        lockscreenSetPinText.setText(getString(R.string.main_lockscreen_set_pin_wrong));
                        lockscreenSetPinModule.resetPinLockView();
                    }
                }
            }
            @Override
            public void onEmpty() { }
            @Override
            public void onPinChange(int pinLength, String intermediatePin) { }
        });
    }

    public void setLockPattern() {
        lockscreenSetPatternView.setVisibility(View.VISIBLE);
        lockscreenSetPatternText.setText(getString(R.string.main_lockscreen_set_pattern_input));
        lockscreenSetPatternModule.setAspectRatioEnabled(true);
        lockscreenSetPatternModule.setAspectRatio(PatternLockView.AspectRatio.ASPECT_RATIO_HEIGHT_BIAS);
        lockscreenSetPatternModule.setViewMode(PatternLockView.PatternViewMode.CORRECT);
        lockscreenSetPatternModule.setDotAnimationDuration(150);
        lockscreenSetPatternModule.setPathEndAnimationDuration(100);
        lockscreenSetPatternModule.setCorrectStateColor(ContextCompat.getColor(this, R.color.colorWhite));
        lockscreenSetPatternModule.setInStealthMode(false);
        lockscreenSetPatternModule.setTactileFeedbackEnabled(true);
        lockscreenSetPatternModule.setInputEnabled(true);
        lockscreenSetPatternModule.addPatternLockListener(new PatternLockViewListener() {
            int PATTERN_INSERT_STEP = 0;
            String savedPattern = "-1";
            @Override
            public void onStarted() { }
            @Override
            public void onProgress(List<PatternLockView.Dot> progressPattern) { }
            @Override
            public void onComplete(List<PatternLockView.Dot> pattern) {
                String currentPattern = PatternLockUtils.patternToString(lockscreenSetPatternModule, pattern);
                if (PATTERN_INSERT_STEP == 0) {
                    savedPattern = currentPattern;
                    PATTERN_INSERT_STEP = 1;
                    lockscreenSetPatternText.setText(getString(R.string.main_lockscreen_set_pattern_confirm));
                    return;
                }
                if (PATTERN_INSERT_STEP == 1) {
                    if (savedPattern.equals(currentPattern)) {
                        appPreferences.edit().putString("LOCKSCREEN_PATTERN" , savedPattern).apply();
                        lockscreenSetPatternText.setText(getString(R.string.main_lockscreen_set_pattern_input));
                        PATTERN_INSERT_STEP = 0;
                        lockscreenSetPatternView.setVisibility(View.GONE);
                        if (lockscreenFragmentInstance != null) lockscreenFragmentInstance.onPatternSet();
                    } else {
                        savedPattern = "-1";
                        PATTERN_INSERT_STEP = 0;
                        lockscreenSetPatternText.setText(getString(R.string.main_lockscreen_set_pattern_wrong));
                    }
                }
            }
            @Override
            public void onCleared() { }
        });
    }

    public void makeUserSetting(boolean skipPermissionIfGranted) {
        BaseSettingDialog baseSettingDialog = new BaseSettingDialog(this, R.style.FullDialogTheme, skipPermissionIfGranted, new BaseSettingDialog.BaseSettingListener() {
            @Override
            public void onSettingFinished(UserSetting newSetting) {
                if (newSetting == null) return;
                debugLog(SATApplication.LOG_LEVEL.DEV, "onSettingResult","SAVE START");
                appPreferences.edit().putInt("USER_MODE", newSetting.getUserMode().ordinal()).apply();
                debugLog(SATApplication.LOG_LEVEL.DEV, "onSettingResult","USER_MODE : " + newSetting.getUserMode().ordinal());
                appPreferences.edit().putString("USER_GOAL", newSetting.getUserGoal()).apply();
                debugLog(SATApplication.LOG_LEVEL.DEV, "onSettingResult","USER_GOAL : " + newSetting.getUserGoal());
                if (newSetting.getUserGoalOptional() != null) {
                    appPreferences.edit().putString("USER_GOAL_OPTIONAL", newSetting.getUserGoalOptional()).apply();
                    debugLog(SATApplication.LOG_LEVEL.DEV, "onSettingResult","USER_GOAL_OPTIONAL : " + newSetting.getUserGoalOptional());
                } else {
                    appPreferences.edit().remove("USER_GOAL_OPTIONAL").apply();
                    debugLog(SATApplication.LOG_LEVEL.DEV, "onSettingResult","USER_GOAL_OPTIONAL REMOVED");
                }
                if (newSetting.getUserGoalImage() != null) {
                    appPreferences.edit().putString("USER_GOAL_IMAGE", newSetting.getUserGoalImage().toString()).apply();
                    debugLog(SATApplication.LOG_LEVEL.DEV, "onSettingResult","USER_GOAL_IMAGE : " + newSetting.getUserGoalImage().toString());
                } else {
                    appPreferences.edit().remove("USER_GOAL_IMAGE").apply();
                    debugLog(SATApplication.LOG_LEVEL.DEV, "onSettingResult","USER_GOAL_IMAGE REMOVED");
                }
                SimpleDateFormat settingDateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
                appPreferences.edit().putString("USER_DATE", settingDateFormat.format(newSetting.getUserDate())).apply();
                debugLog(SATApplication.LOG_LEVEL.DEV, "onSettingResult","USER_DATE : " + settingDateFormat.format(newSetting.getUserDate()));
                appPreferences.edit().putBoolean("SETTING_FINISHED", true).apply();
                debugLog(SATApplication.LOG_LEVEL.DEV, "onSettingResult","SAVE FINISH");
                SETTING_EXIST = true;
                initializeData();
            }
        });
        baseSettingDialog.show();
    }

    private void showReviewDialog(int numberOfAccess) {
        ReviewDialog reviewDialog = new ReviewDialog(MainActivity.this, null);
        reviewDialog.setRateText("Would you rate me?")
                .setTitle("")
                .setForceMode(true)
                .setUpperBound(4)
                .setNegativeReviewListener(new ReviewDialog.NegativeReviewListener() {
                    @Override
                    public void onNegativeReview(int rating, String email, String message) {
                        Review review = new Review.Builder(SATApplication.getUserInfo(), rating).tag("SAT").title(email).content(message).build();
                        Reviewer.newReview("OC4HJMB39F42", review);
                        Toast.makeText(MainActivity.this, "Thank You!", Toast.LENGTH_SHORT).show();
                    }
                })
                .showAfter(numberOfAccess);
    }

    public static class MyPagerAdapter extends SmartFragmentStatePagerAdapter {
        private static int NUM_ITEMS = 4;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return generalFragment.newInstance(0);
                case 1:
                    return notificationFragment.newInstance(1);
                case 2:
                    return floatingviewFragment.newInstance(2);
                case 3:
                    return lockscreenFragment.newInstance(3);
                default:
                    return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return String.valueOf(position);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (lockscreenSetPinView.getVisibility() == View.VISIBLE) {
            lockscreenSetPinView.setVisibility(View.GONE);
            return;
        }
        if (lockscreenSetPatternView.getVisibility() == View.VISIBLE) {
            lockscreenSetPatternView.setVisibility(View.GONE);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
