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
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
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
import kr.devx.satcounter.Util.DayUtil;
import kr.devx.satcounter.Util.SmartFragmentStatePagerAdapter;
import kr.devx.satcounter.Util.UserSetting;

import static kr.devx.satcounter.SATApplication.debugLog;
import static kr.devx.satcounter.Util.UserSetting.DATE_FORMAT;

public class MainActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler {

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

    private BillingProcessor billProcessor;
    private LinearLayout bannerAdHolder, postAdHolder;
    private InterstitialAd screenAdView;
    private boolean IS_PREMIUM_USER = false;

    @Override
    public void onCreate(Bundle onSavedInstance) {
        super.onCreate(onSavedInstance);
        setContentView(R.layout.main_activity);

        satApplication = (SATApplication) getApplication();
        appPreferences = getSharedPreferences("SETTING", MODE_PRIVATE);
        SETTING_EXIST = appPreferences.getBoolean("SETTING_FINISHED",false);

        billProcessor = new BillingProcessor(this, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAj8qKbfOB9sk9CgKgd6WGdpocGN7m9AT8Q7jrQ4TsH/R3vfG6WgxIZeGOAdeNSn1887Mj6s8AmlTU4a1ipnCdoYbZOy2tEv0z/skuZEdJWSawBaRQYexQuiB8QkcJrd8LzRhgihP/l9/XvzYIrpXqChz0rDYKEn6ZvbfislZUCmGTmNkNWXilIALiodQOweWE5lqyDScBgjfmONbu7Qc2xOdZ4oJKjdeexysRHloSLixbHeoz1rvjP46BoAxAMW6oKVzqsOzYl/DcKo+w0NZCmT0WyhqdI5wGrB/MwFiBa1Pm/LlNPRD5shu97An+tXvnHURQj7cqgZ4u0IG6uMoeeQIDAQAB", this);
        billProcessor.initialize();
        screenAdView = new InterstitialAd(this);
        screenAdView.setAdUnitId("ca-app-pub-8781765548929244/3032199121");

        initializeId();
        initializeData();

        if (!SETTING_EXIST) {
            makeUserSetting(false);
        }

        checkEventAvailable();
    }

    private void applyPremium() {
        IS_PREMIUM_USER = true;
        bannerAdHolder.setVisibility(View.GONE);
        if (generalFragmentInstance != null) generalFragmentInstance.onUserPremium();
    }

    public void onUserPremiumRequest() {
        billProcessor .purchase(this, "donation_coffee");
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
        bannerAdHolder = findViewById(R.id.main_footerView);

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
        if (generalFragmentInstance != null && IS_PREMIUM_USER) generalFragmentInstance.onUserPremium();
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
        if (!IS_PREMIUM_USER) screenAdView.loadAd(new AdRequest.Builder().build());
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
                        if (!IS_PREMIUM_USER) screenAdView.show();
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
        if (!IS_PREMIUM_USER) screenAdView.loadAd(new AdRequest.Builder().build());
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
                        if (!IS_PREMIUM_USER) screenAdView.show();
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

    private void checkEventAvailable() {
        new GetEventTask().execute();
    }

    private void onEventAvailable() {
        mainFloatingActionButton.show();
        mainFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] options = {getString(R.string.release_event_copy), getString(R.string.release_event_mail), getString(R.string.release_event_close)};
                ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, options);
                new LovelyChoiceDialog(MainActivity.this)
                        .setTopColorRes(R.color.colorWhite)
                        .setTitle(R.string.release_event_title)
                        .setIcon(R.drawable.icon_magic)
                        .setMessage(R.string.release_event_message)
                        .setItems(adapter, new LovelyChoiceDialog.OnItemSelectedListener<String>() {
                            @Override
                            public void onItemSelected(int position, String item) {
                                switch (position) {
                                    case 0:
                                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                        ClipData clip = ClipData.newPlainText(getString(R.string.app_name), "https://play.google.com/store/apps/details?id=kr.devx.satcounter");
                                        clipboard.setPrimaryClip(clip);
                                        Toast.makeText(MainActivity.this, getString(R.string.release_event_copy_ok), Toast.LENGTH_SHORT).show();
                                        break;
                                    case 1:
                                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto","tiram2sue@naver.com", null));
                                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                                        startActivity(Intent.createChooser(emailIntent, "Send Email"));
                                        break;
                                }
                            }
                        })
                        .show();
            }
        });
    }

    public void makeUserSetting(boolean skipPermissionIfGranted) {
        if (!IS_PREMIUM_USER) screenAdView.loadAd(new AdRequest.Builder().build());
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
                if (!IS_PREMIUM_USER) screenAdView.show();
            }
        });
        baseSettingDialog.show();
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
        if (!billProcessor.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (billProcessor != null && billProcessor.isInitialized()) {
            billProcessor.loadOwnedPurchasesFromGoogle();
            boolean isDonatedCoffee = billProcessor.isPurchased("donation_coffee");
            if (isDonatedCoffee) applyPremium();
        }
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
        if (billProcessor != null) {
            billProcessor.release();
        }
        super.onDestroy();
    }

    @Override
    public void onBillingInitialized() {
        SATApplication.debugLog(SATApplication.LOG_LEVEL.CRI, "BILL] onBillingInitialized", "");
        billProcessor.loadOwnedPurchasesFromGoogle();
        boolean isDonatedCoffee = billProcessor.isPurchased("donation_coffee");
        if (isDonatedCoffee) applyPremium();
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        SATApplication.debugLog(SATApplication.LOG_LEVEL.CRI, "BILL] onProductPurchased : ", productId);
        if (productId.equals("donation_coffee")) {
            applyPremium();
        }
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        SATApplication.debugLog(SATApplication.LOG_LEVEL.CRI, "BILL] onBillingError : ", errorCode);
        for(String sku : billProcessor.listOwnedProducts()) {
            SATApplication.debugLog(SATApplication.LOG_LEVEL.CRI, "BILL] onPurchaseHistoryRestored : ", sku);
            if (sku.equals("donation_coffee")) {
                applyPremium();
            }
        }
    }

    @Override
    public void onPurchaseHistoryRestored() {
        for(String sku : billProcessor.listOwnedProducts()) {
            SATApplication.debugLog(SATApplication.LOG_LEVEL.CRI, "BILL] onPurchaseHistoryRestored : ", sku);
            if (sku.equals("donation_coffee")) {
                applyPremium();
            }
        }
    }

    public class GetEventTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            String result = "";
            try {
                URL url = new URL(SATApplication.SERVER_EVENT_AVAILABLE);
                urlConnection = (HttpURLConnection) url.openConnection();
                int code = urlConnection.getResponseCode();
                if(code == 200){
                    InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String line = "";
                    while ((line = bufferedReader.readLine()) != null)
                        result += line;
                    inputStream.close();
                }
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
            return result;

        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("ON")) {
                onEventAvailable();
            }
            super.onPostExecute(result);
        }

    }

}
