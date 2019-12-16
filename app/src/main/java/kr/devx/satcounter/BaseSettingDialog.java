package kr.devx.satcounter;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import gun0912.tedbottompicker.TedBottomPicker;
import kr.devx.satcounter.Util.UnivData;
import kr.devx.satcounter.Util.UserSetting;

import static kr.devx.satcounter.Util.UserSetting.DATE_FORMAT;

public class BaseSettingDialog extends AppCompatDialog {

    public interface BaseSettingListener {
        void onSettingFinished(UserSetting newSetting);
    }

    private Context context;
    private SATApplication satApplication;
    private BaseSettingListener resultListener;
    private boolean skipIfPermissionGranted = false;

    private final static int SUNEUNG_2021_YEAR = 2020;
    private final static int SUNEUNG_2021_MONTH = 11;
    private final static int SUNEUNG_2021_DAY = 19;
    private final static int SUNEUNG_2022_YEAR = 2021;
    private final static int SUNEUNG_2022_MONTH = 11;
    private final static int SUNEUNG_2022_DAY = 18;
    private final static int SUNEUNG_2023_YEAR = 2022;
    private final static int SUNEUNG_2023_MONTH = 11;
    private final static int SUNEUNG_2023_DAY = 17;


    private enum SETTING_STEP {
        STEP_START, STEP_MODE, STEP_DATE, STEP_GOAL, STEP_FINISH;
    }

    private UserSetting newSetting;

    private SETTING_STEP currentStep;
    private Handler handler;

    private ImageView settingIcon;
    private TextView statusTextView, settingBottomBack, settingBottomNext;
    private View settingBottomIndexMode,settingBottomIndexDate,settingBottomIndexGoal;
    private RelativeLayout settingBottomView, settingBottomIndexView;

    private LinearLayout settingStartView;
    private FrameLayout settingPermissionWes;
    private TextView settingPermissionWesTitle, settingPermissionWesMessage;
    private boolean PERMISSION_ESSENTIAL_WES_GRANTED = false;

    private LinearLayout settingModeView;
    private TextView settingModeSuneung, settingModeSat,settingModeNormal;

    private LinearLayout settingDateView, settingPresetSuneung;
    private TextView settingDateShow, settingPreset;
    private TextView settingPresetSuneung2021, settingPresetSuneung2022, settingPresetSuneung2023;
    final Calendar settingDateCalendar = Calendar.getInstance();

    private LinearLayout settingGoalView, settingGoalUniversityView;
    private AppCompatAutoCompleteTextView settingGoalEditor;
    private AppCompatEditText settingGoalOptionalEditor;
    private ImageView settingGoalImage, settingGoalUniversityLogo;
    private TextView settingGoalImageButton, settingGoalUniversityName, settingGoalUniversitySlogan;

    private LinearLayout settingFinishView;
    private TextView settingFinishAdvance;
    
    public BaseSettingDialog(Context context, int theme, boolean skipIfPermissionGranted, BaseSettingListener listener) {
        super(context, theme);
        this.context = context;
        this.skipIfPermissionGranted = skipIfPermissionGranted;
        this.resultListener = listener;
    }

    @Override
    protected void onCreate(Bundle onSavedInstanceState) {
        super.onCreate(onSavedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(context, R.color.colorWhite)));
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);
        setContentView(R.layout.setting_dialog);

        satApplication = (SATApplication) context.getApplicationContext();

        initializeId();
        stepStart(null);
    }

    private void initializeId() {
        settingIcon = findViewById(R.id.setting_icon);
        statusTextView = findViewById(R.id.setting_status);
        settingBottomView = findViewById(R.id.setting_bottom);
        settingBottomBack = findViewById(R.id.setting_bottomBack);
        settingBottomNext = findViewById(R.id.setting_bottomNext);
        settingBottomIndexView = findViewById(R.id.setting_bottomIndex);
        settingBottomIndexMode = findViewById(R.id.setting_bottomIndexMode);
        settingBottomIndexDate = findViewById(R.id.setting_bottomIndexDate);
        settingBottomIndexGoal = findViewById(R.id.setting_bottomIndexGoal);
        settingStartView = findViewById(R.id.setting_start);
        settingPermissionWes = findViewById(R.id.setting_start_permission_wes);
        settingPermissionWesTitle = findViewById(R.id.setting_start_permission_wes_title);
        settingPermissionWesMessage = findViewById(R.id.setting_start_permission_wes_message);
        settingModeView = findViewById(R.id.setting_mode);
        settingModeSuneung = findViewById(R.id.setting_mode_suneung);
        settingModeSat= findViewById(R.id.setting_mode_sat);
        settingModeNormal = findViewById(R.id.setting_mode_normal);
        settingDateView = findViewById(R.id.setting_date);
        settingPresetSuneung = findViewById(R.id.setting_datePresetSuneungView);
        settingDateShow = findViewById(R.id.setting_dateShow);
        settingPreset = findViewById(R.id.setting_datePreset);
        settingPresetSuneung2021 = findViewById(R.id.setting_datePresetSuneung2021);
        settingPresetSuneung2022 = findViewById(R.id.setting_datePresetSuneung2022);
        settingPresetSuneung2023 = findViewById(R.id.setting_datePresetSuneung2023);
        settingGoalView = findViewById(R.id.setting_goal);
        settingGoalEditor = findViewById(R.id.setting_goalEditText);
        settingGoalOptionalEditor = findViewById(R.id.setting_goalOptionalEditText);
        settingGoalImage = findViewById(R.id.setting_goalImage);
        settingGoalImageButton = findViewById(R.id.setting_goalImageButton);
        settingGoalUniversityView = findViewById(R.id.setting_goalUniversity);
        settingGoalUniversityLogo = findViewById(R.id.setting_goalUniversityLogo);
        settingGoalUniversityName = findViewById(R.id.setting_goalUniversityName);
        settingGoalUniversitySlogan = findViewById(R.id.setting_goalUniversitySlogan);
        settingFinishView = findViewById(R.id.setting_finish);
        settingFinishAdvance = findViewById(R.id.setting_finish_advance);

        handler = new Handler();

        settingBottomBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingBottomBack.setClickable(false);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        settingBottomBack.setClickable(true);
                    }
                },600);
                switch (currentStep) {
                    case STEP_START:
                        break;
                    case STEP_MODE:
                        stepStart(settingModeView);
                        break;
                    case STEP_DATE:
                        stepMode(settingDateView);
                        break;
                    case STEP_GOAL:
                        stepDate(settingGoalView);
                        break;
                    case STEP_FINISH:
                        stepGoal(settingFinishView);
                        break;
                }
            }
        });
        settingBottomNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingBottomNext.setClickable(false);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        settingBottomNext.setClickable(true);
                    }
                },600);
                switch (currentStep) {
                    case STEP_START:
                        stepMode(settingStartView);
                        break;
                    case STEP_MODE:
                        stepDate(settingModeView);
                        break;
                    case STEP_DATE:
                        stepGoal(settingDateView);
                        break;
                    case STEP_GOAL:
                        stepFinish(settingGoalView);
                        break;
                    case STEP_FINISH:
                        break;
                }
            }
        });

    }

    private void stepStart(View fadeOutView) {
        currentStep = SETTING_STEP.STEP_START;
        fadeOutOldView(fadeOutView);

        Animation iconAnimation = AnimationUtils.loadAnimation(context, R.anim.floatrepeat);
        settingIcon.startAnimation(iconAnimation);
        newSetting = new UserSetting();
        settingBottomIndexView.setVisibility(View.INVISIBLE);
        settingPermissionWes.setSelected(false);
        settingPermissionWesTitle.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        settingPermissionWesMessage.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            PERMISSION_ESSENTIAL_WES_GRANTED = true;
            settingPermissionWes.setSelected(true);
            settingPermissionWesTitle.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
            settingPermissionWesMessage.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
            // TODO EVERYTHING GRANTED
            settingBottomNext.setVisibility(View.VISIBLE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                PERMISSION_ESSENTIAL_WES_GRANTED = true;
                settingPermissionWes.setSelected(true);
                settingPermissionWesTitle.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
                settingPermissionWesMessage.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
                // TODO CHECK EVERY ESSENTIAL GRANTED
                settingBottomNext.setVisibility(View.VISIBLE);
            }
        }

        settingPermissionWes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionListener permissionlistener = new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        PERMISSION_ESSENTIAL_WES_GRANTED = true;
                        settingPermissionWes.setSelected(true);
                        settingPermissionWesTitle.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
                        settingPermissionWesMessage.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
                        // TODO CHECK EVERY ESSENTIAL GRANTED
                        statusTextView.setText(context.getString(R.string.setting_start_pass));
                        settingBottomNext.setVisibility(View.VISIBLE);
                    }
                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        if (deniedPermissions == null || deniedPermissions.size() == 0) return;
                        PERMISSION_ESSENTIAL_WES_GRANTED = false;
                        settingPermissionWes.setSelected(false);
                        settingPermissionWesTitle.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                        settingPermissionWesMessage.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));
                    }
                };
                TedPermission.with(context)
                        .setPermissionListener(permissionlistener)
                        .setDeniedMessage(context.getString(R.string.setting_start_permission_block_nonessential))
                        .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .check();
            }
        });

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                statusTextView.setText(context.getString(R.string.setting_hello1));
                YoYo.with(Techniques.FadeInUp).duration(500).playOn(statusTextView);
            }
        },500);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                statusTextView.setText(context.getString(R.string.setting_hello2));
                YoYo.with(Techniques.FadeInUp).duration(500).playOn(statusTextView);
            }
        },2000);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                statusTextView.setText(context.getString(R.string.setting_start));
                settingStartView.setVisibility(View.VISIBLE);
                settingBottomView.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.FadeInUp).duration(500).playOn(settingStartView);
                // TODO CHECK EVERY ESSENTIAL GRANTED
                if (PERMISSION_ESSENTIAL_WES_GRANTED) {
                    statusTextView.setText(context.getString(R.string.setting_start_pass));
                    if (skipIfPermissionGranted) {
                        statusTextView.setText(context.getString(R.string.setting_hello2));
                        stepMode(settingStartView);
                    }
                }
            }
        },3500);
    }

    private void stepMode(View fadeOutView) {
        currentStep = SETTING_STEP.STEP_MODE;
        fadeOutOldView(fadeOutView);

        settingBottomBack.setVisibility(View.INVISIBLE);
        settingBottomNext.setVisibility(View.INVISIBLE);
        settingBottomIndexView.setVisibility(View.VISIBLE);
        settingBottomIndexMode.setSelected(true);
        settingBottomIndexDate.setSelected(false);
        settingBottomIndexGoal.setSelected(false);
        settingModeSuneung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentStep != SETTING_STEP.STEP_MODE) return;
                settingModeSat.setSelected(false);
                settingModeSat.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));
                settingModeNormal.setSelected(false);
                settingModeNormal.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));
                if (!settingModeSuneung.isSelected()) {
                    newSetting.setUserMode(UserSetting.USER_MODE.MODE_SUNEUNG);
                    settingModeSuneung.setSelected(true);
                    settingModeSuneung.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
                    settingBottomNext.setVisibility(View.VISIBLE);
                } else {
                    settingModeSuneung.setSelected(false);
                    settingModeSuneung.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));
                    settingBottomNext.setVisibility(View.INVISIBLE);
                }
            }
        });
        settingModeSat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentStep != SETTING_STEP.STEP_MODE) return;
                settingModeSuneung.setSelected(false);
                settingModeSuneung.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));
                settingModeNormal.setSelected(false);
                settingModeNormal.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));
                if (!settingModeSat.isSelected()) {
                    newSetting.setUserMode(UserSetting.USER_MODE.MODE_SAT);
                    settingModeSat.setSelected(true);
                    settingModeSat.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
                    settingBottomNext.setVisibility(View.VISIBLE);
                } else {
                    settingModeSat.setSelected(false);
                    settingModeSat.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));
                    settingBottomNext.setVisibility(View.INVISIBLE);
                }
            }
        });
        settingModeNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentStep != SETTING_STEP.STEP_MODE) return;
                settingModeSuneung.setSelected(false);
                settingModeSuneung.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));
                settingModeSat.setSelected(false);
                settingModeSat.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));
                if (!settingModeNormal.isSelected()) {
                    newSetting.setUserMode(UserSetting.USER_MODE.MODE_NORMAL);
                    settingModeNormal.setSelected(true);
                    settingModeNormal.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
                    settingBottomNext.setVisibility(View.VISIBLE);
                } else {
                    settingModeNormal.setSelected(false);
                    settingModeNormal.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));
                    settingBottomNext.setVisibility(View.INVISIBLE);
                }
            }
        });

        if (newSetting.getUserMode() != null) {
            switch (newSetting.getUserMode()) {
                case MODE_SUNEUNG:
                    settingModeSuneung.setSelected(true);
                    settingModeSuneung.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
                    settingBottomNext.setVisibility(View.VISIBLE);
                    break;
                case MODE_SAT:
                    settingModeSat.setSelected(true);
                    settingModeSat.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
                    settingBottomNext.setVisibility(View.VISIBLE);
                    break;
                case MODE_NORMAL:
                    settingModeNormal.setSelected(true);
                    settingModeNormal.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
                    settingBottomNext.setVisibility(View.VISIBLE);
                    break;
            }
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                statusTextView.setText(context.getString(R.string.setting_mode));
                settingModeView.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.FadeInUp).duration(500).playOn(settingModeView);
            }
        },500);

    }

    private void stepDate(View fadeOutView) {
        currentStep = SETTING_STEP.STEP_DATE;
        fadeOutOldView(fadeOutView);

        settingBottomBack.setVisibility(View.VISIBLE);
        settingBottomNext.setVisibility(View.INVISIBLE);
        settingBottomIndexView.setVisibility(View.VISIBLE);
        settingBottomIndexMode.setSelected(false);
        settingBottomIndexDate.setSelected(true);
        settingBottomIndexGoal.setSelected(false);
        final DatePickerDialog.OnDateSetListener settingDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                if (currentStep != SETTING_STEP.STEP_DATE) return;
                settingDateCalendar.set(Calendar.YEAR, year);
                settingDateCalendar.set(Calendar.MONTH, monthOfYear);
                settingDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                newSetting.setUserDate(settingDateCalendar.getTime());
                SimpleDateFormat settingDateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
                settingDateShow.setText(settingDateFormat.format(settingDateCalendar.getTime()));
                settingPresetSuneung2021.setSelected(false);
                settingPresetSuneung2021.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));
                settingPresetSuneung2022.setSelected(false);
                settingPresetSuneung2022.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));
                settingPresetSuneung2023.setSelected(false);
                settingPresetSuneung2023.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));
                settingBottomNext.setVisibility(View.VISIBLE);
            }
        };
        settingDateShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentStep != SETTING_STEP.STEP_DATE) return;
                DatePickerDialog dateDialog = new DatePickerDialog(context, settingDateListener, settingDateCalendar.get(Calendar.YEAR), settingDateCalendar.get(Calendar.MONTH), settingDateCalendar.get(Calendar.DAY_OF_MONTH));
                dateDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                dateDialog.show();
            }
        });
        settingPresetSuneung2021.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentStep != SETTING_STEP.STEP_DATE) return;
                settingPresetSuneung2022.setSelected(false);
                settingPresetSuneung2022.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));
                settingPresetSuneung2023.setSelected(false);
                settingPresetSuneung2023.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));
                if (!settingPresetSuneung2021.isSelected()) {
                    settingDateCalendar.set(Calendar.YEAR, SUNEUNG_2021_YEAR);
                    settingDateCalendar.set(Calendar.MONTH, SUNEUNG_2021_MONTH - 1);
                    settingDateCalendar.set(Calendar.DAY_OF_MONTH, SUNEUNG_2021_DAY);
                    newSetting.setUserDate(settingDateCalendar.getTime());
                    SimpleDateFormat settingDateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
                    settingDateShow.setText(settingDateFormat.format(settingDateCalendar.getTime()));
                    settingPresetSuneung2021.setSelected(true);
                    settingPresetSuneung2021.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
                    settingBottomNext.setVisibility(View.VISIBLE);
                } else {
                    settingPresetSuneung2021.setSelected(false);
                    settingPresetSuneung2021.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));
                    settingBottomNext.setVisibility(View.INVISIBLE);
                }
            }
        });
        settingPresetSuneung2022.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentStep != SETTING_STEP.STEP_DATE) return;
                settingPresetSuneung2021.setSelected(false);
                settingPresetSuneung2021.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));
                settingPresetSuneung2023.setSelected(false);
                settingPresetSuneung2023.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));
                if (!settingPresetSuneung2022.isSelected()) {
                    settingDateCalendar.set(Calendar.YEAR, SUNEUNG_2022_YEAR);
                    settingDateCalendar.set(Calendar.MONTH, SUNEUNG_2022_MONTH - 1);
                    settingDateCalendar.set(Calendar.DAY_OF_MONTH, SUNEUNG_2022_DAY);
                    newSetting.setUserDate(settingDateCalendar.getTime());
                    SimpleDateFormat settingDateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
                    settingDateShow.setText(settingDateFormat.format(settingDateCalendar.getTime()));
                    settingPresetSuneung2022.setSelected(true);
                    settingPresetSuneung2022.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
                    settingBottomNext.setVisibility(View.VISIBLE);
                } else {
                    settingPresetSuneung2022.setSelected(false);
                    settingPresetSuneung2022.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));
                    settingBottomNext.setVisibility(View.INVISIBLE);
                }
            }
        });
        settingPresetSuneung2023.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentStep != SETTING_STEP.STEP_DATE) return;
                settingPresetSuneung2021.setSelected(false);
                settingPresetSuneung2021.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));
                settingPresetSuneung2022.setSelected(false);
                settingPresetSuneung2022.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));
                if (!settingPresetSuneung2023.isSelected()) {
                    settingDateCalendar.set(Calendar.YEAR, SUNEUNG_2023_YEAR);
                    settingDateCalendar.set(Calendar.MONTH, SUNEUNG_2023_MONTH - 1);
                    settingDateCalendar.set(Calendar.DAY_OF_MONTH, SUNEUNG_2023_DAY);
                    newSetting.setUserDate(settingDateCalendar.getTime());
                    SimpleDateFormat settingDateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
                    settingDateShow.setText(settingDateFormat.format(settingDateCalendar.getTime()));
                    settingPresetSuneung2023.setSelected(true);
                    settingPresetSuneung2023.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
                    settingBottomNext.setVisibility(View.VISIBLE);
                } else {
                    settingPresetSuneung2023.setSelected(false);
                    settingPresetSuneung2023.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));
                    settingBottomNext.setVisibility(View.INVISIBLE);
                }
            }
        });

        if (newSetting.getUserDate() != null) {
            settingDateCalendar.setTime(newSetting.getUserDate());
            SimpleDateFormat settingDateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
            settingDateShow.setText(settingDateFormat.format(settingDateCalendar.getTime()));
            settingBottomNext.setVisibility(View.VISIBLE);
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                statusTextView.setText(context.getString(R.string.setting_date));
                settingDateView.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.FadeInUp).duration(500).playOn(settingDateView);
            }
        },500);
        switch (newSetting.getUserMode()) {
            case MODE_SUNEUNG:
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        settingPreset.setVisibility(View.VISIBLE);
                        YoYo.with(Techniques.FadeInUp).duration(500).playOn(settingPreset);
                        settingPresetSuneung.setVisibility(View.VISIBLE);
                        YoYo.with(Techniques.FadeInUp).duration(500).playOn(settingPresetSuneung);
                    }
                },500);
                break;
            case MODE_SAT:
                settingPresetSuneung.setVisibility(View.INVISIBLE);
                break;
            case MODE_NORMAL:
                settingPresetSuneung.setVisibility(View.INVISIBLE);
                break;
        }
    }

    private void stepGoal(View fadeOutView) {
        currentStep = SETTING_STEP.STEP_GOAL;
        fadeOutOldView(fadeOutView);

        settingBottomBack.setVisibility(View.VISIBLE);
        settingBottomNext.setVisibility(View.INVISIBLE);
        settingBottomIndexView.setVisibility(View.VISIBLE);
        settingBottomIndexMode.setSelected(false);
        settingBottomIndexDate.setSelected(false);
        settingBottomIndexGoal.setSelected(true);
        settingGoalUniversityView.setVisibility(View.GONE);
        settingGoalImage.setVisibility(View.VISIBLE);
        settingGoalImageButton.setVisibility(View.VISIBLE);
        settingGoalEditor.setHint(context.getString(R.string.setting_goal_hint));
        if (newSetting.getUserMode() == UserSetting.USER_MODE.MODE_SUNEUNG) {
            settingGoalEditor.setHint(context.getString(R.string.setting_goal_hint_college));
            settingGoalEditor.setCompletionHint(context.getString(R.string.setting_goal_hint_college));
            List<String> koreaUnivNameList = new ArrayList<>(satApplication.KoreaUnivMap.keySet());
            ArrayAdapter<String> koreaUnivAdapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, koreaUnivNameList);
            settingGoalEditor.setAdapter(koreaUnivAdapter);
            settingGoalEditor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    settingGoalEditor.showDropDown();
                }
            });
        }
        if (newSetting.getUserMode() == UserSetting.USER_MODE.MODE_SAT) {
            settingGoalEditor.setHint(context.getString(R.string.setting_goal_hint_college));
            settingGoalEditor.setCompletionHint(context.getString(R.string.setting_goal_hint_college));
            List<String> UsUnivNameList = new ArrayList<>(satApplication.UsUnivMap.keySet());
            ArrayAdapter<String> UsUnivAdapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, UsUnivNameList);
            settingGoalEditor.setAdapter(UsUnivAdapter);
            settingGoalEditor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    settingGoalEditor.showDropDown();
                }
            });
        }
        settingGoalEditor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String userInput = s.toString();
                newSetting.setUserGoal(userInput);
                newSetting.setUserGoalImage(null);
                settingGoalImage.setVisibility(View.VISIBLE);
                settingGoalImageButton.setVisibility(View.VISIBLE);
                settingGoalUniversityView.setVisibility(View.GONE);
                if (newSetting.getUserMode() == UserSetting.USER_MODE.MODE_SUNEUNG && userInput.length() > 0) {
                    if (satApplication.KoreaUnivMap.containsKey(userInput)) {
                        UnivData selectedUniv = satApplication.KoreaUnivMap.get(userInput);
                        newSetting.setUserGoalImage(selectedUniv.getLogo());
                        try {
                            RequestOptions options = new RequestOptions().fitCenter();
                            Glide.with(context).applyDefaultRequestOptions(options).load(selectedUniv.getLogo()).into(settingGoalUniversityLogo);
                        } catch (NullPointerException err) {
                            err.printStackTrace();
                        }
                        settingGoalImage.setVisibility(View.GONE);
                        settingGoalImageButton.setVisibility(View.GONE);
                        settingGoalUniversityView.setVisibility(View.VISIBLE);
                        settingGoalUniversityName.setText(selectedUniv.getName());
                        settingGoalUniversityName.setTextColor(Color.parseColor(selectedUniv.getMainColorHex()));
                        settingGoalUniversitySlogan.setText(selectedUniv.getSlogan());
                        settingGoalUniversitySlogan.setTextColor(Color.parseColor(selectedUniv.getSubColorHex() != null ? selectedUniv.getSubColorHex() : selectedUniv.getMainColorHex()));
                    }
                }
                if (newSetting.getUserMode() == UserSetting.USER_MODE.MODE_SAT && userInput.length() > 0) {
                    if (satApplication.UsUnivMap.containsKey(userInput)) {
                        UnivData selectedUniv = satApplication.UsUnivMap.get(userInput);
                        newSetting.setUserGoalImage(selectedUniv.getLogo());
                        try {
                            RequestOptions options = new RequestOptions().fitCenter();
                            Glide.with(context).applyDefaultRequestOptions(options).load(selectedUniv.getLogo()).into(settingGoalUniversityLogo);
                        } catch (NullPointerException err) {
                            err.printStackTrace();
                        }
                        settingGoalImage.setVisibility(View.GONE);
                        settingGoalImageButton.setVisibility(View.GONE);
                        settingGoalUniversityView.setVisibility(View.VISIBLE);
                        settingGoalUniversityName.setText(selectedUniv.getName());
                        settingGoalUniversityName.setTextColor(Color.parseColor(selectedUniv.getMainColorHex()));
                        settingGoalUniversitySlogan.setText(selectedUniv.getSlogan());
                        settingGoalUniversitySlogan.setTextColor(Color.parseColor(selectedUniv.getSubColorHex() != null ? selectedUniv.getSubColorHex() : selectedUniv.getMainColorHex()));
                    }
                }
                if (userInput.length() > 0) {
                    settingBottomNext.setVisibility(View.VISIBLE);
                } else {
                    settingBottomNext.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });
        settingGoalOptionalEditor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String userInput = s.toString();
                newSetting.setUserGoalOptional(userInput);
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });
        settingGoalImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TedBottomPicker tedBottomPicker = new TedBottomPicker.Builder(context)
                    .setOnImageSelectedListener(new TedBottomPicker.OnImageSelectedListener() {
                        @Override
                        public void onImageSelected(Uri uri) {
                            newSetting.setUserGoalImage(uri);
                            RequestOptions options = new RequestOptions().fitCenter();
                            Glide.with(context).applyDefaultRequestOptions(options).load(uri).into(settingGoalImage);
                        }
                    })
                    .create();
                tedBottomPicker.show(((MainActivity)context).getSupportFragmentManager());
            }
        });

        if (newSetting.getUserGoal() != null || newSetting.getUserGoalImage() != null || newSetting.getUserGoalOptional() != null) {
            if (newSetting.getUserGoal() != null) {
                settingGoalEditor.setText(newSetting.getUserGoal());
                settingBottomNext.setVisibility(View.VISIBLE);
                settingGoalImage.setVisibility(View.VISIBLE);
                settingGoalImageButton.setVisibility(View.VISIBLE);
                settingGoalUniversityView.setVisibility(View.GONE);
                if (newSetting.getUserMode() == UserSetting.USER_MODE.MODE_SUNEUNG && satApplication.KoreaUnivMap.containsKey(newSetting.getUserGoal())) {
                    UnivData selectedUniv = satApplication.KoreaUnivMap.get(newSetting.getUserGoal());
                    try {
                        RequestOptions options = new RequestOptions().fitCenter();
                        Glide.with(context).applyDefaultRequestOptions(options).load(selectedUniv.getLogo()).into(settingGoalUniversityLogo);
                    } catch (NullPointerException err) {
                        err.printStackTrace();
                    }
                    settingGoalImage.setVisibility(View.GONE);
                    settingGoalImageButton.setVisibility(View.GONE);
                    settingGoalUniversityView.setVisibility(View.VISIBLE);
                    settingGoalUniversityName.setText(selectedUniv.getName());
                    settingGoalUniversityName.setTextColor(Color.parseColor(selectedUniv.getMainColorHex()));
                    settingGoalUniversitySlogan.setText(selectedUniv.getSlogan());
                    settingGoalUniversitySlogan.setTextColor(Color.parseColor(selectedUniv.getSubColorHex() != null ? selectedUniv.getSubColorHex() : selectedUniv.getMainColorHex()));
                }
                if (newSetting.getUserMode() == UserSetting.USER_MODE.MODE_SAT && satApplication.UsUnivMap.containsKey(newSetting.getUserGoal())) {
                    UnivData selectedUniv = satApplication.UsUnivMap.get(newSetting.getUserGoal());
                    try {
                        RequestOptions options = new RequestOptions().fitCenter();
                        Glide.with(context).applyDefaultRequestOptions(options).load(selectedUniv.getLogo()).into(settingGoalUniversityLogo);
                    } catch (NullPointerException err) {
                        err.printStackTrace();
                    }
                    settingGoalImage.setVisibility(View.GONE);
                    settingGoalImageButton.setVisibility(View.GONE);
                    settingGoalUniversityView.setVisibility(View.VISIBLE);
                    settingGoalUniversityName.setText(selectedUniv.getName());
                    settingGoalUniversityName.setTextColor(Color.parseColor(selectedUniv.getMainColorHex()));
                    settingGoalUniversitySlogan.setText(selectedUniv.getSlogan());
                    settingGoalUniversitySlogan.setTextColor(Color.parseColor(selectedUniv.getSubColorHex() != null ? selectedUniv.getSubColorHex() : selectedUniv.getMainColorHex()));
                }
            }
            if (newSetting.getUserGoalOptional() != null) settingGoalOptionalEditor.setText(newSetting.getUserGoalOptional());
            if (newSetting.getUserGoalImage() != null) {
                RequestOptions options = new RequestOptions().fitCenter();
                Glide.with(context).applyDefaultRequestOptions(options).load(newSetting.getUserGoalImage()).into(settingGoalImage);
            }
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                statusTextView.setText(context.getString(R.string.setting_goal));
                settingGoalView.setVisibility(View.VISIBLE);
                settingBottomNext.setText(context.getString(R.string.setting_advance));
                YoYo.with(Techniques.FadeInUp).duration(500).playOn(settingGoalView);
            }
        },500);
    }

    private void stepFinish(View fadeOutView) {
        currentStep = SETTING_STEP.STEP_FINISH;
        fadeOutOldView(fadeOutView);

        // LISTENER DATA SET
        settingBottomBack.setVisibility(View.VISIBLE);
        settingBottomNext.setVisibility(View.INVISIBLE);
        settingBottomIndexView.setVisibility(View.INVISIBLE);
        settingBottomIndexMode.setSelected(false);
        settingBottomIndexDate.setSelected(false);
        settingBottomIndexGoal.setSelected(false);
        settingFinishAdvance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultListener.onSettingFinished(newSetting);
                BaseSettingDialog.this.dismiss();
            }
        });

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                statusTextView.setText(context.getString(R.string.setting_finish));
                settingFinishView.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.FadeInUp).duration(500).playOn(settingFinishView);
            }
        },500);
    }

    private void fadeOutOldView(View oldView) {
        if (oldView == null) return;
        YoYo.with(Techniques.FadeOut).duration(500).playOn(oldView);
        oldView.setVisibility(View.GONE);
    }

}
