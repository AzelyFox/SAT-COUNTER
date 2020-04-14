package kr.devx.satcounter;

import android.annotation.SuppressLint;
import android.app.*;
import android.content.*;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.*;
import android.provider.MediaStore;
import android.support.v4.app.*;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.ncorti.slidetoact.SlideToActView;

import kr.devx.catcher.Catcher;

import kr.devx.satcounter.Util.DayUtil;
import kr.devx.satcounter.Util.FingerPrint;
import kr.devx.satcounter.Util.UserSetting;

import static kr.devx.satcounter.Util.UserSetting.DATE_FORMAT;

public class BackgroundService extends Service {

    private SATApplication satApplication;
    private SharedPreferences appPreferences;
    private NotificationManager notificationManager;
    private Notification notification;
    private WindowManager windowManager;
    private ScreenReceiver screenReceiver;
    private BroadcastReceiver timeReceiver;
    private FingerPrint fingerPrint;
    private View floatingBox, floatingImage, floatingText, floatingMinimal;
    private View lockView;
    private UserSetting userData;

    private int NOTIFICATION_MASTER = 0;
    private int FLOATING_MASTER = 0;
    private int LOCKSCREEN_MASTER = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        satApplication = (SATApplication) getApplicationContext();
        appPreferences = getSharedPreferences("SETTING", MODE_PRIVATE);
        notificationManager = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        NOTIFICATION_MASTER = appPreferences.getInt("NOTIFICATION_MASTER", 0);
        FLOATING_MASTER = appPreferences.getInt("FLOATING_MASTER", 0);
        LOCKSCREEN_MASTER = appPreferences.getInt("LOCKSCREEN_MASTER", 0);
        if (NOTIFICATION_MASTER == 1 || FLOATING_MASTER == 1 || LOCKSCREEN_MASTER == 1) getUserData();
        else stopSelf();
        if (NOTIFICATION_MASTER == 1 || LOCKSCREEN_MASTER == 1) {
            timeReceiver = new BroadcastReceiver(){
                @Override
                public void onReceive(Context context, Intent intent) {
                    if(intent.getAction() != null && intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0) {
                        try {
                            Log.d("SATCOUNTER","BackgroundService : on SELF : TIME TICK");
                            if (DayUtil.isNewDateTime()) {
                                Log.d("SATCOUNTER","BackgroundService : on SELF : DATE CHANGED");
                                notificationEnabled();
                            }
                            if (lockView != null && lockView.getWindowToken() != null) {
                                TextView lockClock = lockView.findViewById(R.id.lockscreen_clock);
                                TextView lockDate = lockView.findViewById(R.id.lockscreen_date);
                                lockClock.setText(DayUtil.getClock());
                                lockDate.setText(DayUtil.getDate());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            kr.devx.catcher.Log log = new kr.devx.catcher.Log.Builder(SATApplication.getUserInfo(), 3).tag("SAT").title(e.getMessage()).content(Log.getStackTraceString(e)).build();
                            Catcher.newLog("FC95J37G0IF2", log);
                        }
                    }
                }
            };
            IntentFilter timeFilter = new IntentFilter();
            timeFilter.addAction(Intent.ACTION_TIME_TICK);
            registerReceiver(timeReceiver, timeFilter);
            notificationEnabled();
        }
        if (FLOATING_MASTER == 1) floatingEnabled();
        if (LOCKSCREEN_MASTER == 1) {
            screenReceiver = new ScreenReceiver();
            IntentFilter screenFilter = new IntentFilter();
            screenFilter.addAction(Intent.ACTION_SCREEN_ON);
            screenFilter.addAction(Intent.ACTION_SCREEN_OFF);
            registerReceiver(screenReceiver, screenFilter);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (notificationManager == null) {
            notificationManager = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
            notificationManager.cancel(SATApplication.NOTIFICATION_ID);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true);
        }
        if (windowManager != null) {
            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            if (floatingBox != null) windowManager.removeView(floatingBox);
            if (floatingImage != null) windowManager.removeView(floatingImage);
            if (floatingText != null) windowManager.removeView(floatingText);
            if (floatingMinimal != null) windowManager.removeView(floatingMinimal);
            if (lockView != null && lockView.getWindowToken() != null) {
                try {
                    windowManager.removeView(lockView);
                    if (fingerPrint != null && Build.VERSION.SDK_INT >= 23) fingerPrint.cancelAuthentication();
                }
                catch (IllegalArgumentException e) { e.printStackTrace(); }
            }
        }
        if (screenReceiver != null) unregisterReceiver(screenReceiver);
        if (timeReceiver != null) unregisterReceiver(timeReceiver);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            if (notificationManager == null) notificationManager = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
            if (notification != null) notificationManager.notify(SATApplication.NOTIFICATION_ID, notification);
            if (appPreferences != null) if (NOTIFICATION_MASTER == 0 && FLOATING_MASTER == 0 && LOCKSCREEN_MASTER == 0) stopSelf();
        } catch (Exception e) {
            e.printStackTrace();
            kr.devx.catcher.Log log = new kr.devx.catcher.Log.Builder(SATApplication.getUserInfo(), 2).tag("SAT").title(e.getMessage()).content(Log.getStackTraceString(e)).build();
            Catcher.newLog("FC95J37G0IF2", log);
        }
        if (LOCKSCREEN_MASTER == 1 && intent != null && intent.hasExtra("ACTION")) {
            if (intent.getStringExtra("ACTION").equals(Intent.ACTION_SCREEN_ON)) {
                // FINGERPRINT AVAILABLE?
            }
            if (intent.getStringExtra("ACTION").equals(Intent.ACTION_SCREEN_OFF)) {
                if (fingerPrint != null && Build.VERSION.SDK_INT >= 23) fingerPrint.cancelAuthentication();
                lockscreenEnabled();
            }
        }
        return START_STICKY_COMPATIBILITY;
    }

    private void getUserData() {
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

    private void lockscreenEnabled() {

        if (lockView != null && lockView.isAttachedToWindow()) {
            Log.d("SATCOUNTER","BackgroundService : on SELF : there is already lockView shown");
            final LinearLayout lockPinView = lockView.findViewById(R.id.lockscreen_pinView);
            IndicatorDots lockPinDots = lockView.findViewById(R.id.lockscreen_pinDots);
            final PinLockView lockPinModule = lockView.findViewById(R.id.lockscreen_pinModule);
            final LinearLayout lockPatternView = lockView.findViewById(R.id.lockscreen_patternView);
            final PatternLockView lockPatternModule = lockView.findViewById(R.id.lockscreen_patternModule);
            final TextView lockStatus = lockView.findViewById(R.id.lockscreen_status);
            lockPinModule.resetPinLockView();
            lockPatternModule.clearPattern();
            lockStatus.setText(null);
            return;
        }

        final int lockscreenMode = appPreferences.getInt("LOCKSCREEN_MODE", 0);
        final int lockscreenFingerPrint = appPreferences.getInt("LOCKSCREEN_FINGERPRINT", 0);
        final int lockscreenDesign = appPreferences.getInt("LOCKSCREEN_DESIGN", 0);
        final int lockscreenColor = appPreferences.getInt("LOCKSCREEN_COLOR", 0);
        final int lockscreenWallpaper = appPreferences.getInt("LOCKSCREEN_WALLPAPER", 0);

        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock(Context.KEYGUARD_SERVICE);
        keyguardLock.disableKeyguard();

        DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
        int displayWidth = metrics.widthPixels;
        int displayHeight = metrics.heightPixels;

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        switch (lockscreenDesign) {
            case 0:
                lockView = inflater.inflate(R.layout.lockscreen_full, null);
                TextView fullGoal = lockView.findViewById(R.id.lockscreen_fullGoal);
                TextView fullDayLeft = lockView.findViewById(R.id.lockscreen_fullDayLeft);
                TextView fullSlogan = lockView.findViewById(R.id.lockscreen_fullSlogan);
                TextView fullOptional = lockView.findViewById(R.id.lockscreen_fullGoalOptional);
                TextView fullWord = lockView.findViewById(R.id.lockscreen_fullWord);
                ImageView fullImage = lockView.findViewById(R.id.lockscreen_fullImage);
                fullDayLeft.setText(getString(R.string.main_general_dayleft_prefix) + DayUtil.getDaysLeft(userData.getUserDate()));
                fullWord.setText(satApplication.randomFromWordList());
                if (satApplication.userUniversity != null) {
                    fullImage.setImageResource(satApplication.userUniversity.getLogoDrawable());
                    fullGoal.setText(satApplication.userUniversity.getName());
                    fullSlogan.setText(satApplication.userUniversity.getSlogan());
                } else {
                    if (userData.getUserGoalImage() != null) fullImage.setImageURI(userData.getUserGoalImage());
                    fullGoal.setText(userData.getUserGoal());
                    fullSlogan.setVisibility(View.GONE);
                }
                if (!TextUtils.isEmpty(userData.getUserGoalOptional())) fullOptional.setText(userData.getUserGoalOptional());
                else fullOptional.setVisibility(View.GONE);
                if (lockscreenWallpaper == 1) {
                    fullGoal.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
                    fullDayLeft.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
                    fullSlogan.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
                    fullOptional.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
                    fullWord.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
                }
                if (lockscreenColor == 1 && satApplication.userUniversity != null) {
                    fullGoal.setTextColor(Color.parseColor(satApplication.userUniversity.getMainColorHex()));
                    fullDayLeft.setTextColor(Color.parseColor(satApplication.userUniversity.getMainColorHex()));
                    fullSlogan.setTextColor(Color.parseColor(satApplication.userUniversity.getMainColorHex()));
                    fullOptional.setTextColor(Color.parseColor(satApplication.userUniversity.getMainColorHex()));
                    fullWord.setTextColor(Color.parseColor(satApplication.userUniversity.getMainColorHex()));
                    if (satApplication.userUniversity.getSubColorHex() != null) {
                        fullOptional.setTextColor(Color.parseColor(satApplication.userUniversity.getSubColorHex()));
                        fullSlogan.setTextColor(Color.parseColor(satApplication.userUniversity.getSubColorHex()));
                    }
                }
                break;
            case 1:
                lockView = inflater.inflate(R.layout.lockscreen_light, null);
                TextView lightGoal = lockView.findViewById(R.id.lockscreen_lightGoal);
                TextView lightDayLeft = lockView.findViewById(R.id.lockscreen_lightDayLeft);
                TextView lightWord = lockView.findViewById(R.id.lockscreen_lightWord);
                ImageView lightImage = lockView.findViewById(R.id.lockscreen_lightImage);
                lightGoal.setText(userData.getUserGoal());
                if (satApplication.userUniversity != null) {
                    lightImage.setImageResource(satApplication.userUniversity.getLogoDrawable());
                    lightGoal.setText(satApplication.userUniversity.getName());
                }
                else if (userData.getUserGoalImage() != null) lightImage.setImageURI(userData.getUserGoalImage());
                else lightImage.setVisibility(View.GONE);
                lightDayLeft.setText(getString(R.string.main_general_dayleft_prefix) + DayUtil.getDaysLeft(userData.getUserDate()));
                lightWord.setText(satApplication.randomFromWordList());
                if (lockscreenWallpaper == 1) {
                    lightGoal.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
                    lightDayLeft.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
                    lightWord.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
                }
                if (lockscreenColor == 1 && satApplication.userUniversity != null) {
                    lightGoal.setTextColor(Color.parseColor(satApplication.userUniversity.getMainColorHex()));
                    lightDayLeft.setTextColor(Color.parseColor(satApplication.userUniversity.getMainColorHex()));
                    lightWord.setTextColor(Color.parseColor(satApplication.userUniversity.getMainColorHex()));
                    if (satApplication.userUniversity.getSubColorHex() != null)
                        lightDayLeft.setTextColor(Color.parseColor(satApplication.userUniversity.getSubColorHex()));
                }
                break;
            case 2:
                lockView = inflater.inflate(R.layout.lockscreen_minimal, null);
                ImageView minimalImage = lockView.findViewById(R.id.lockscreen_minimalImage);
                TextView minimalDayLeft = lockView.findViewById(R.id.lockscreen_minimalDayLeft);
                if (satApplication.userUniversity != null) minimalImage.setImageResource(satApplication.userUniversity.getLogoDrawable());
                else if (userData.getUserGoalImage() != null) minimalImage.setImageURI(userData.getUserGoalImage());
                else minimalImage.setVisibility(View.GONE);
                minimalDayLeft.setText(getString(R.string.main_general_dayleft_prefix) + DayUtil.getDaysLeft(userData.getUserDate()));
                if (lockscreenWallpaper == 1) minimalDayLeft.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
                if (lockscreenColor == 1 && satApplication.userUniversity != null) minimalDayLeft.setTextColor(Color.parseColor(satApplication.userUniversity.getMainColorHex()));
                break;
        }
        TextView lockClock = lockView.findViewById(R.id.lockscreen_clock);
        TextView lockDate = lockView.findViewById(R.id.lockscreen_date);
        final LinearLayout lockRoot = lockView.findViewById(R.id.lockscreen_root);
        final LinearLayout lockBody = lockView.findViewById(R.id.lockscreen_body);
        final TextView lockStatus = lockView.findViewById(R.id.lockscreen_status);
        final LinearLayout lockSwipeView = lockView.findViewById(R.id.lockscreen_swipeView);
        SlideToActView lockSwipeModule = lockView.findViewById(R.id.lockscreen_swipeModule);
        final LinearLayout lockPinView = lockView.findViewById(R.id.lockscreen_pinView);
        IndicatorDots lockPinDots = lockView.findViewById(R.id.lockscreen_pinDots);
        final PinLockView lockPinModule = lockView.findViewById(R.id.lockscreen_pinModule);
        final LinearLayout lockPatternView = lockView.findViewById(R.id.lockscreen_patternView);
        final PatternLockView lockPatternModule = lockView.findViewById(R.id.lockscreen_patternModule);
        lockRoot.setBackgroundColor(ContextCompat.getColor(this, R.color.colorWhite));
        lockClock.setTextColor(ContextCompat.getColor(this, R.color.colorBlack));
        lockDate.setTextColor(ContextCompat.getColor(this, R.color.colorBlack));
        lockStatus.setTextColor(ContextCompat.getColor(this, R.color.colorBlack));
        lockSwipeModule.setOuterColor(ContextCompat.getColor(this, R.color.colorBlack));
        lockSwipeModule.setInnerColor(ContextCompat.getColor(this, R.color.colorWhite));
        lockSwipeModule.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
        lockPinModule.setTextColor(ContextCompat.getColor(this, R.color.colorBlack));
        lockPatternModule.setNormalStateColor(ContextCompat.getColor(this, R.color.colorBlack));
        if (lockscreenWallpaper == 1) {
            lockRoot.setBackgroundColor(ContextCompat.getColor(this, R.color.colorBlack));
            lockClock.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
            lockDate.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
            lockStatus.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
            lockSwipeModule.setOuterColor(ContextCompat.getColor(this, R.color.colorWhite));
            lockSwipeModule.setInnerColor(ContextCompat.getColor(this, R.color.colorBlack));
            lockSwipeModule.setTextColor(ContextCompat.getColor(this, R.color.colorBlack));
            lockPinModule.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
            lockPinModule.setDeleteButtonPressedColor(ContextCompat.getColor(this, R.color.colorWhite));
            lockPinDots.setDotEmptyDrawable(R.drawable.dot_empty);
            lockPinDots.setDotFillDrawable(R.drawable.dot_white);
            lockPatternModule.setNormalStateColor(ContextCompat.getColor(this, R.color.colorWhite));
        }
        if (lockscreenColor == 1 && satApplication.userUniversity != null) {
            lockStatus.setTextColor(Color.parseColor(satApplication.userUniversity.getMainColorHex()));
            lockSwipeModule.setInnerColor(Color.parseColor(satApplication.userUniversity.getMainColorHex()));
            lockPinModule.setTextColor(Color.parseColor(satApplication.userUniversity.getMainColorHex()));
            lockPatternModule.setNormalStateColor(Color.parseColor(satApplication.userUniversity.getMainColorHex()));
        }

        lockClock.setText(DayUtil.getClock());
        lockDate.setText(DayUtil.getDate());

        lockView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lockBody.setVisibility(View.GONE);
                if (Build.VERSION.SDK_INT >= 23 && lockscreenFingerPrint == 1) {
                    lockStatus.setText(getString(R.string.lockscreen_status_finger));
                    if (fingerPrint != null) fingerPrint.cancelAuthentication();
                        fingerPrint = new FingerPrint(BackgroundService.this);
                    if (fingerPrint.isFingerHarWare() && fingerPrint.isFingerPassCode()) {
                        fingerPrint.initialize(new FingerprintManager.AuthenticationCallback() {
                            @Override
                            public void onAuthenticationError(int errorCode, CharSequence errString) {
                                SATApplication.debugLog(SATApplication.LOG_LEVEL.DEV, "onFingerError", errString);
                                lockStatus.setText(getString(R.string.lockscreen_status_retry_finger));
                                super.onAuthenticationError(errorCode, errString);
                            }

                            @Override
                            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                                SATApplication.debugLog(SATApplication.LOG_LEVEL.DEV, "onFingerHelp", helpString);
                                lockStatus.setText(getString(R.string.lockscreen_status_retry_finger));
                                super.onAuthenticationHelp(helpCode, helpString);
                            }

                            @Override
                            public void onAuthenticationFailed() {
                                SATApplication.debugLog(SATApplication.LOG_LEVEL.DEV, "onFingerFail", "");
                                lockStatus.setText(getString(R.string.lockscreen_status_retry_finger));
                                super.onAuthenticationFailed();
                            }

                            @Override
                            public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                                SATApplication.debugLog(SATApplication.LOG_LEVEL.DEV, "onFingerSucceed", result);
                                windowManager.removeView(lockView);
                                super.onAuthenticationSucceeded(result);
                            }
                        });
                    }
                }
            }
        });

        switch (lockscreenMode) {
            case 0:
                lockSwipeView.setVisibility(View.VISIBLE);
                lockPinView.setVisibility(View.GONE);
                lockPatternView.setVisibility(View.GONE);
                lockSwipeModule.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
                    @Override
                    public void onSlideComplete(SlideToActView slideToActView) {
                        try {
                            windowManager.removeView(lockView);
                            if (Build.VERSION.SDK_INT >= 23 && fingerPrint != null)
                                fingerPrint.cancelAuthentication();
                        } catch (Exception e) {
                            e.printStackTrace();
                            kr.devx.catcher.Log log = new kr.devx.catcher.Log.Builder(SATApplication.getUserInfo(), 4).tag("SAT").title(e.getMessage()).content(Log.getStackTraceString(e)).build();
                            Catcher.newLog("FC95J37G0IF2", log);
                        }
                    }
                });
                break;
            case 1:
                lockSwipeView.setVisibility(View.GONE);
                lockPinView.setVisibility(View.VISIBLE);
                lockPatternView.setVisibility(View.GONE);
                lockPinDots.setPinLength(6);
                lockPinDots.setIndicatorType(IndicatorDots.IndicatorType.FILL_WITH_ANIMATION);
                lockPinModule.attachIndicatorDots(lockPinDots);
                lockPinModule.setPinLength(6);
                lockPinModule.setPinLockListener(new PinLockListener() {
                    @Override
                    public void onComplete(String pin) {
                        String correctPin = appPreferences.getString("LOCKSCREEN_PIN", "");
                        if (pin.equals(correctPin)) {
                            try {
                                windowManager.removeView(lockView);
                                if (Build.VERSION.SDK_INT >= 23 && fingerPrint != null)
                                    fingerPrint.cancelAuthentication();
                            } catch (Exception e) {
                                e.printStackTrace();
                                kr.devx.catcher.Log log = new kr.devx.catcher.Log.Builder(SATApplication.getUserInfo(), 4).tag("SAT").title(e.getMessage()).content(Log.getStackTraceString(e)).build();
                                Catcher.newLog("FC95J37G0IF2", log);
                            }
                        }
                        else {
                            lockStatus.setText(getString(R.string.lockscreen_status_retry_pin));
                            lockPinModule.resetPinLockView();
                        }
                    }
                    @Override
                    public void onEmpty() { }
                    @Override
                    public void onPinChange(int pinLength, String intermediatePin) { }
                });
                break;
            case 2:
                lockSwipeView.setVisibility(View.GONE);
                lockPinView.setVisibility(View.GONE);
                lockPatternView.setVisibility(View.VISIBLE);
                lockPatternModule.setAspectRatioEnabled(true);
                lockPatternModule.setAspectRatio(PatternLockView.AspectRatio.ASPECT_RATIO_HEIGHT_BIAS);
                lockPatternModule.setViewMode(PatternLockView.PatternViewMode.CORRECT);
                lockPatternModule.setDotAnimationDuration(150);
                lockPatternModule.setPathEndAnimationDuration(100);
                lockPatternModule.setCorrectStateColor(ContextCompat.getColor(this, R.color.colorWhite));
                lockPatternModule.setInStealthMode(false);
                lockPatternModule.setTactileFeedbackEnabled(true);
                lockPatternModule.setInputEnabled(true);
                lockPatternModule.addPatternLockListener(new PatternLockViewListener() {
                    @Override
                    public void onStarted() { }
                    @Override
                    public void onProgress(List<PatternLockView.Dot> progressPattern) { }
                    @Override
                    public void onComplete(List<PatternLockView.Dot> pattern) {
                        String correctPattern = appPreferences.getString("LOCKSCREEN_PATTERN", "");
                        String currentPattern = PatternLockUtils.patternToString(lockPatternModule, pattern);
                        if (currentPattern.equals(correctPattern)) {
                            try {
                                windowManager.removeView(lockView);
                                if (Build.VERSION.SDK_INT >= 23 && fingerPrint != null)
                                    fingerPrint.cancelAuthentication();
                            } catch (Exception e) {
                                e.printStackTrace();
                                kr.devx.catcher.Log log = new kr.devx.catcher.Log.Builder(SATApplication.getUserInfo(), 4).tag("SAT").title(e.getMessage()).content(Log.getStackTraceString(e)).build();
                                Catcher.newLog("FC95J37G0IF2", log);
                            }
                        }
                        else {
                            lockStatus.setText(getString(R.string.lockscreen_status_retry_pattern));
                            lockPatternModule.clearPattern();
                        }
                    }
                    @Override
                    public void onCleared() { }
                });
                break;
        }

        int LAYOUT_FLAG;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }else{
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }
        final WindowManager.LayoutParams windowParams = new WindowManager.LayoutParams(displayWidth, displayHeight, LAYOUT_FLAG, WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        try {
            if (!lockView.isAttachedToWindow()) {
                windowManager.addView(lockView, windowParams);
                Log.d("SATCOUNTER","BackgroundService : on SELF : lockView Created");
            } else {
                Log.d("SATCOUNTER","BackgroundService : on SELF : there is already attached lockView");
            }
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.service_error), Toast.LENGTH_SHORT).show();
            Toast.makeText(this, e + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            kr.devx.catcher.Log log = new kr.devx.catcher.Log.Builder(SATApplication.getUserInfo(), 4).tag("SAT").title(e.getMessage()).content(Log.getStackTraceString(e)).build();
            Catcher.newLog("FC95J37G0IF2", log);
        }
    }

    private void floatingEnabled() {
        final int floatingDesignBox = appPreferences.getInt("FLOATING_DESIGN_BOX", 0);
        final int floatingDesignImage = appPreferences.getInt("FLOATING_DESIGN_IMAGE", 0);
        final int floatingDesignText = appPreferences.getInt("FLOATING_DESIGN_TEXT", 0);
        final int floatingDesignMinimal = appPreferences.getInt("FLOATING_DESIGN_MINIMAL", 0);

        if (floatingDesignBox == 1) showFloatingDesign(0);
        if (floatingDesignImage == 1) showFloatingDesign(1);
        if (floatingDesignText == 1) showFloatingDesign(2);
        if (floatingDesignMinimal == 1) showFloatingDesign(3);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void showFloatingDesign(final int designCode) {
        final int floatingMode = appPreferences.getInt("FLOATING_MODE", 0);
        final int floatingColor = appPreferences.getInt("FLOATING_COLOR", 0);
        final int floatingSize = appPreferences.getInt("FLOATING_SIZE",0);
        final int floatingDesignBox_X = appPreferences.getInt("FLOATING_DESIGN_BOX_X", 0);
        final int floatingDesignBox_Y = appPreferences.getInt("FLOATING_DESIGN_BOX_Y", 0);
        final int floatingDesignImage_X = appPreferences.getInt("FLOATING_DESIGN_IMAGE_X", 0);
        final int floatingDesignImage_Y = appPreferences.getInt("FLOATING_DESIGN_IMAGE_Y", 0);
        final int floatingDesignText_X = appPreferences.getInt("FLOATING_DESIGN_TEXT_X", 0);
        final int floatingDesignText_Y = appPreferences.getInt("FLOATING_DESIGN_TEXT_Y", 0);
        final int floatingDesignMinimal_X = appPreferences.getInt("FLOATING_DESIGN_MINIMAL_X", 0);
        final int floatingDesignMinimal_Y = appPreferences.getInt("FLOATING_DESIGN_MINIMAL_Y", 0);

        DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
        int displayWidth = metrics.widthPixels;
        int displayHeight = metrics.heightPixels;
        if (displayWidth > displayHeight) {
            int temp = displayWidth;
            displayWidth = displayHeight;
            displayHeight = temp;
        }
        int windowWidth = 0, windowHeight = 0;
        switch (designCode) {
            case 0:
                windowHeight = displayHeight/10 + (displayHeight/100) * floatingSize;
                windowWidth = (int) (windowHeight * 1.5);
                break;
            case 1:
                windowWidth = displayWidth/6 + (displayWidth/60) * floatingSize;
                windowHeight = (int) (windowWidth * 1.1);
                break;
            case 2:
                windowHeight = displayHeight/12 + (displayHeight/120) * floatingSize;
                windowWidth = (int) (windowHeight * 2);
                break;
            case 3:
                windowHeight = displayHeight/20 + (displayHeight/200) * floatingSize;
                windowWidth = displayWidth/5 + (displayWidth/50) * floatingSize;
                break;
        }
        final WindowManager.LayoutParams windowParams;
        int LAYOUT_FLAG_0, LAYOUT_FLAG_1;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG_0 = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            LAYOUT_FLAG_1 = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }else{
            LAYOUT_FLAG_0 = WindowManager.LayoutParams.TYPE_PHONE;
            LAYOUT_FLAG_1 = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
        }
        if (floatingMode == 0) {
            windowParams = new WindowManager.LayoutParams(windowWidth, windowHeight, LAYOUT_FLAG_0, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        } else {
            windowParams = new WindowManager.LayoutParams(windowWidth, windowHeight, LAYOUT_FLAG_1, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        }
        switch (designCode) {
            case 0:
                windowParams.x = floatingDesignBox_X;
                windowParams.y = floatingDesignBox_Y;
                break;
            case 1:
                windowParams.x = floatingDesignImage_X;
                windowParams.y = floatingDesignImage_Y;
                break;
            case 2:
                windowParams.x = floatingDesignText_X;
                windowParams.y = floatingDesignText_Y;
                break;
            case 3:
                windowParams.x = floatingDesignMinimal_X;
                windowParams.y = floatingDesignMinimal_Y;
                break;
        }
        windowParams.gravity = Gravity.CENTER;

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final ViewGroup view;
        final LinearLayout rootView;
        switch (designCode) {
            case 0:
                view =  (ViewGroup) inflater.inflate(R.layout.floating_box, null);
                rootView = view.findViewById(R.id.floating_rootView);
                ImageView boxImage = view.findViewById(R.id.floating_box_image);
                TextView boxHeader = view.findViewById(R.id.floating_box_headerText);
                TextView boxBody = view.findViewById(R.id.floating_box_bodyText);
                TextView boxFooter = view.findViewById(R.id.floating_box_footerText);
                if (satApplication.userUniversity != null) {
                    boxImage.setImageResource(satApplication.userUniversity.getLogoDrawable());
                    boxHeader.setText(satApplication.userUniversity.getName());
                    boxBody.setText(getString(R.string.main_general_dayleft_prefix) + DayUtil.getDaysLeft(userData.getUserDate()));
                    if (!TextUtils.isEmpty(userData.getUserGoalOptional())) {
                        boxFooter.setText(userData.getUserGoalOptional());
                    } else {
                        boxFooter.setText(satApplication.randomFromWordList());
                    }
                    if (floatingColor == 1) {
                        boxHeader.setTextColor(Color.parseColor(satApplication.userUniversity.getMainColorHex()));
                        boxBody.setTextColor(Color.parseColor(satApplication.userUniversity.getMainColorHex()));
                        boxFooter.setTextColor(Color.parseColor(satApplication.userUniversity.getMainColorHex()));
                        if (satApplication.userUniversity.getSubColorHex() != null) boxFooter.setTextColor(Color.parseColor(satApplication.userUniversity.getSubColorHex()));
                    }
                } else {
                    boxImage.setVisibility(View.GONE);
                    boxHeader.setText(userData.getUserGoal());
                    boxBody.setText(getString(R.string.main_general_dayleft_prefix) + DayUtil.getDaysLeft(userData.getUserDate()));
                    if (!TextUtils.isEmpty(userData.getUserGoalOptional())) {
                        boxFooter.setText(userData.getUserGoalOptional());
                    } else {
                        boxFooter.setText(satApplication.randomFromWordList());
                    }
                }
                floatingBox = view;
                break;
            case 1:
                view =  (ViewGroup) inflater.inflate(R.layout.floating_image, null);
                rootView = view.findViewById(R.id.floating_rootView);
                ImageView imageImage = view.findViewById(R.id.floating_image_image);
                TextView imageDayLeft = view.findViewById(R.id.floating_image_dayLeft);
                imageDayLeft.setText(getString(R.string.main_general_dayleft_prefix) + DayUtil.getDaysLeft(userData.getUserDate()));
                if (satApplication.userUniversity != null) {
                    imageImage.setImageResource(satApplication.userUniversity.getLogoDrawable());
                    if (floatingColor == 1) {
                        imageDayLeft.setTextColor(Color.parseColor(satApplication.userUniversity.getMainColorHex()));
                    }
                } else {
                    try {
                        Bitmap myBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), userData.getUserGoalImage());
                        imageImage.setImageBitmap(myBitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                        imageImage.setVisibility(View.GONE);
                    }
                }
                floatingImage = view;
                break;
            case 2:
                view =  (ViewGroup) inflater.inflate(R.layout.floating_text, null);
                rootView = view.findViewById(R.id.floating_rootView);
                TextView textHeader = view.findViewById(R.id.floating_text_header);
                TextView textBody = view.findViewById(R.id.floating_text_body);
                TextView textFooter = view.findViewById(R.id.floating_text_footer);
                textHeader.setText(userData.getUserGoal());
                textFooter.setText(getString(R.string.main_general_dayleft_prefix) + DayUtil.getDaysLeft(userData.getUserDate()));
                textBody.setText(satApplication.randomFromWordList());
                if (!TextUtils.isEmpty(userData.getUserGoalOptional())) textBody.setText(userData.getUserGoalOptional());
                if (satApplication.userUniversity != null && floatingColor == 1) {
                    textHeader.setTextColor(Color.parseColor(satApplication.userUniversity.getMainColorHex()));
                    textBody.setTextColor(Color.parseColor(satApplication.userUniversity.getMainColorHex()));
                    textFooter.setTextColor(Color.parseColor(satApplication.userUniversity.getMainColorHex()));
                    if (satApplication.userUniversity.getSubColorHex() != null) {
                        textBody.setTextColor(Color.parseColor(satApplication.userUniversity.getSubColorHex()));
                        textFooter.setTextColor(Color.parseColor(satApplication.userUniversity.getSubColorHex()));
                    }
                }
                floatingText = view;
                break;
            case 3:
                view =  (ViewGroup) inflater.inflate(R.layout.floating_minimal, null);
                rootView = view.findViewById(R.id.floating_rootView);
                TextView minimalText = view.findViewById(R.id.floating_minimal_text);
                minimalText.setText(getString(R.string.main_general_dayleft_prefix) + DayUtil.getDaysLeft(userData.getUserDate()));
                if (satApplication.userUniversity != null && floatingColor == 1) minimalText.setTextColor(Color.parseColor(satApplication.userUniversity.getMainColorHex()));
                floatingMinimal = view;
                break;
            default:
                view = null;
                rootView = null;
        }
        rootView.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = windowParams.x;
                        initialY = windowParams.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        switch (designCode) {
                            case 0:
                                appPreferences.edit().putInt("FLOATING_DESIGN_BOX_X", windowParams.x).apply();
                                appPreferences.edit().putInt("FLOATING_DESIGN_BOX_Y", windowParams.y).apply();
                                break;
                            case 1:
                                appPreferences.edit().putInt("FLOATING_DESIGN_IMAGE_X", windowParams.x).apply();
                                appPreferences.edit().putInt("FLOATING_DESIGN_IMAGE_Y", windowParams.y).apply();
                                break;
                            case 2:
                                appPreferences.edit().putInt("FLOATING_DESIGN_TEXT_X", windowParams.x).apply();
                                appPreferences.edit().putInt("FLOATING_DESIGN_TEXT_Y", windowParams.y).apply();
                                break;
                            case 3:
                                appPreferences.edit().putInt("FLOATING_DESIGN_MINIMAL_X", windowParams.x).apply();
                                appPreferences.edit().putInt("FLOATING_DESIGN_MINIMAL_Y", windowParams.y).apply();
                                break;
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        windowParams.x = initialX + (int) (event.getRawX() - initialTouchX);
                        windowParams.y = initialY + (int) (event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(view, windowParams);
                        return true;
                }
                return true;
            }
        });
        try {
            windowManager.addView(view, windowParams);
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.service_error), Toast.LENGTH_SHORT).show();
            Toast.makeText(this, e + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            kr.devx.catcher.Log log = new kr.devx.catcher.Log.Builder(SATApplication.getUserInfo(), 4).tag("SAT").title(e.getMessage()).content(Log.getStackTraceString(e)).build();
            Catcher.newLog("FC95J37G0IF2", log);
        }
    }

    private void notificationEnabled() {
        int notificationDesign = appPreferences.getInt("NOTIFICATION_DESIGN", 0);
        int notificationWordEnabled = appPreferences.getInt("NOTIFICATION_WORD", 0);
        int notificationColorEnabled = appPreferences.getInt("NOTIFICATION_COLOR", 0);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        String NOTIFICATION_CHANNEL_ID = SATApplication.CHANNEL_NOTIFICATIONSERVICE;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, getString(R.string.channel_notificationservice), NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription(getString(R.string.channel_notificationservice));
            notificationChannel.enableLights(false);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{ 0 });
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        switch (notificationDesign) {
            case 0 :
                builder.setContentTitle(userData.getUserGoal())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(contentIntent)
                        .setAutoCancel(false)
                        .setSubText(getString(R.string.main_general_dayleft_prefix) + DayUtil.getDaysLeft(userData.getUserDate()))
                        .setOngoing(true)
                        .setShowWhen(false)
                        .setWhen(System.currentTimeMillis())
                        .setColorized(true)
                        .setVibrate(new long[]{0L});
                if (notificationWordEnabled == 1) {
                    if (!TextUtils.isEmpty(userData.getUserGoalOptional())) {
                        builder.setContentTitle(userData.getUserGoalOptional());
                    } else {
                        builder.setContentTitle(userData.getUserGoal());
                    }
                    String selectedWord = satApplication.randomFromWordList();
                    builder.setContentText(selectedWord);
                    builder.setStyle(new NotificationCompat.BigTextStyle().bigText(selectedWord));
                } else {
                    if (!TextUtils.isEmpty(userData.getUserGoalOptional())) {
                        builder.setContentTitle(userData.getUserGoal());
                        builder.setContentText(userData.getUserGoalOptional());
                        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(userData.getUserGoalOptional()));
                    } else {
                        builder.setContentTitle(userData.getUserGoal());
                    }
                }
                if (satApplication.userUniversity != null) {
                    builder.setSmallIcon(satApplication.userUniversity.getLogoDrawable());
                    builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), satApplication.userUniversity.getLogoDrawable()));
                    if (notificationWordEnabled == 1) {
                        if (!TextUtils.isEmpty(userData.getUserGoalOptional())) {
                            builder.setContentTitle(userData.getUserGoalOptional());
                        } else {
                            builder.setContentTitle(satApplication.userUniversity.getName());
                        }
                        String selectedWord = satApplication.randomFromWordList();
                        builder.setContentText(selectedWord);
                        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(selectedWord));
                    } else {
                        if (!TextUtils.isEmpty(userData.getUserGoalOptional())) {
                            builder.setContentTitle(satApplication.userUniversity.getName());
                            builder.setContentText(userData.getUserGoalOptional());
                            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(userData.getUserGoalOptional()));
                        } else {
                            builder.setContentTitle(satApplication.userUniversity.getName());
                            builder.setContentText(satApplication.userUniversity.getSlogan());
                            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(satApplication.userUniversity.getSlogan()));
                        }
                    }
                }
                break;
            case 1:
                RemoteViews newLayout = new RemoteViews(getPackageName(), R.layout.notification_new);
                if (satApplication.userUniversity != null) {
                    builder.setSmallIcon(satApplication.userUniversity.getLogoDrawable());
                    newLayout.setImageViewBitmap(R.id.notification_new_image, BitmapFactory.decodeResource(getResources(), satApplication.userUniversity.getLogoDrawable()));
                    newLayout.setTextViewText(R.id.notification_new_dayleft, getString(R.string.main_general_dayleft_prefix) + DayUtil.getDaysLeft(userData.getUserDate()));
                    newLayout.setTextViewText(R.id.notification_new_goal, satApplication.userUniversity.getName());
                    if (notificationColorEnabled == 1) {
                        newLayout.setTextColor(R.id.notification_new_goal, Color.parseColor(satApplication.userUniversity.getMainColorHex()));
                        newLayout.setTextColor(R.id.notification_new_text1, Color.parseColor(satApplication.userUniversity.getMainColorHex()));
                        newLayout.setTextColor(R.id.notification_new_text2, Color.parseColor(satApplication.userUniversity.getMainColorHex()));
                        if (satApplication.userUniversity.getSubColorHex() != null) {
                            newLayout.setTextColor(R.id.notification_new_text1, Color.parseColor(satApplication.userUniversity.getSubColorHex()));
                            newLayout.setTextColor(R.id.notification_new_text2, Color.parseColor(satApplication.userUniversity.getSubColorHex()));
                        }
                    }
                    if (!TextUtils.isEmpty(userData.getUserGoalOptional())) {
                        if (notificationWordEnabled == 1) {
                            newLayout.setTextViewText(R.id.notification_new_text1, userData.getUserGoalOptional());
                            newLayout.setTextViewText(R.id.notification_new_text2, satApplication.randomFromWordList());
                        } else {
                            newLayout.setTextViewText(R.id.notification_new_text1, satApplication.userUniversity.getSlogan());
                            newLayout.setTextViewText(R.id.notification_new_text2, userData.getUserGoalOptional());
                        }
                    } else {
                        if (notificationWordEnabled == 1) {
                            newLayout.setTextViewText(R.id.notification_new_text1, satApplication.userUniversity.getSlogan());
                            newLayout.setTextViewText(R.id.notification_new_text2, satApplication.randomFromWordList());
                        } else {
                            newLayout.setTextViewText(R.id.notification_new_text1, satApplication.userUniversity.getSlogan());
                            newLayout.setViewVisibility(R.id.notification_new_text2, View.GONE);
                        }
                    }
                } else {
                    builder.setSmallIcon(R.mipmap.ic_launcher);
                    if (userData.getUserGoalImage() != null) {
                        try {
                            Bitmap myBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), userData.getUserGoalImage());
                            newLayout.setImageViewBitmap(R.id.notification_new_image, myBitmap);
                        } catch (Exception e) {
                            e.printStackTrace();
                            newLayout.setViewVisibility(R.id.notification_new_imageHolder, View.GONE);
                        }
                    } else {
                        newLayout.setViewVisibility(R.id.notification_new_imageHolder, View.GONE);
                    }
                    newLayout.setTextViewText(R.id.notification_new_dayleft, getString(R.string.main_general_dayleft_prefix) + DayUtil.getDaysLeft(userData.getUserDate()));
                    newLayout.setTextViewText(R.id.notification_new_goal, userData.getUserGoal());
                    if (!TextUtils.isEmpty(userData.getUserGoalOptional())) {
                        if (notificationWordEnabled == 1) {
                            newLayout.setTextViewText(R.id.notification_new_text1, userData.getUserGoalOptional());
                            newLayout.setTextViewText(R.id.notification_new_text2, satApplication.randomFromWordList());
                        } else {
                            newLayout.setTextViewText(R.id.notification_new_text1, userData.getUserGoalOptional());
                            newLayout.setViewVisibility(R.id.notification_new_text2, View.GONE);
                        }
                    } else {
                        if (notificationWordEnabled == 1) {
                            newLayout.setTextViewText(R.id.notification_new_text1, satApplication.randomFromWordList());
                            newLayout.setViewVisibility(R.id.notification_new_text2, View.GONE);
                        } else {
                            newLayout.setViewVisibility(R.id.notification_new_text1, View.GONE);
                            newLayout.setViewVisibility(R.id.notification_new_text2, View.GONE);
                        }
                    }
                }
                builder.setContentIntent(contentIntent)
                        .setAutoCancel(false)
                        .setSubText(getString(R.string.main_general_dayleft_prefix) + DayUtil.getDaysLeft(userData.getUserDate()))
                        .setOngoing(true)
                        .setShowWhen(false)
                        .setColorized(true)
                        .setWhen(System.currentTimeMillis())
                        .setCustomBigContentView(newLayout)
                        .setVibrate(new long[]{0L});
                break;
            case 2:
                RemoteViews simpleLayout = new RemoteViews(getPackageName(), R.layout.notification_simple);
                if (satApplication.userUniversity != null) {
                    builder.setSmallIcon(satApplication.userUniversity.getLogoDrawable());
                    simpleLayout.setImageViewBitmap(R.id.notification_simple_image, BitmapFactory.decodeResource(getResources(), satApplication.userUniversity.getLogoDrawable()));
                    if (notificationColorEnabled == 1)
                        simpleLayout.setTextColor(R.id.notification_simple_dayleft, Color.parseColor(satApplication.userUniversity.getMainColorHex()));
                } else {
                    builder.setSmallIcon(R.mipmap.ic_launcher);
                    if (userData.getUserGoalImage() != null) {
                        try {
                            Bitmap myBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), userData.getUserGoalImage());
                            simpleLayout.setImageViewBitmap(R.id.notification_simple_image, myBitmap);
                        } catch (Exception e) {
                            e.printStackTrace();
                            simpleLayout.setViewVisibility(R.id.notification_simple_image, View.GONE);
                            simpleLayout.setViewVisibility(R.id.notification_simple_goal, View.VISIBLE);
                            simpleLayout.setTextViewText(R.id.notification_simple_goal, userData.getUserGoalOptional());
                        }
                    } else {
                        simpleLayout.setViewVisibility(R.id.notification_simple_image, View.GONE);
                        simpleLayout.setViewVisibility(R.id.notification_simple_goal, View.VISIBLE);
                        simpleLayout.setTextViewText(R.id.notification_simple_goal, userData.getUserGoalOptional());
                    }
                }
                if (notificationWordEnabled == 1) {
                    simpleLayout.setViewVisibility(R.id.notification_simple_linetext, View.VISIBLE);
                    simpleLayout.setTextViewText(R.id.notification_simple_linetext, satApplication.randomFromWordList());
                }
                simpleLayout.setTextViewText(R.id.notification_simple_dayleft, getString(R.string.main_general_dayleft_prefix) + DayUtil.getDaysLeft(userData.getUserDate()));
                builder.setContentIntent(contentIntent)
                        .setAutoCancel(false)
                        .setSubText(getString(R.string.main_general_dayleft_prefix) + DayUtil.getDaysLeft(userData.getUserDate()))
                        .setOngoing(true)
                        .setShowWhen(false)
                        .setColorized(true)
                        .setWhen(System.currentTimeMillis())
                        .setCustomContentView(simpleLayout)
                        .setVibrate(new long[]{0L});
                break;
            default:
                break;
        }

        notification = builder.build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(SATApplication.NOTIFICATION_ID, notification);
            Log.d("SATCOUNTER","BackgroundService : on SELF : startForeground");
        } else {
            notificationManager.notify(SATApplication.NOTIFICATION_ID, notification);
            Log.d("SATCOUNTER","BackgroundService : on SELF : startForeground Not Called");
        }
    }

}
