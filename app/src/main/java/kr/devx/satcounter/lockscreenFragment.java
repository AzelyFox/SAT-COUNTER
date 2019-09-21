package kr.devx.satcounter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.Context.MODE_PRIVATE;

public class lockscreenFragment extends Fragment {

    private SATApplication satApplication;
    private int currentPage;
    private SharedPreferences appPreferences;

    private LinearLayout lockscreenMasterView;
    private TextView lockscreenMasterText;
    private Switch lockscreenMasterSwitch;
    private TextView lockscreenMasterDescription;
    private TextView lockscreenForegroundAlert;
    private TextView lockscreenModeSwipe;
    private TextView lockscreenModePin;
    private TextView lockscreenModePattern;
    private LinearLayout lockscreenFingerSection;
    private LinearLayout lockscreenFingerView;
    private TextView lockscreenFingerText;
    private Switch lockscreenFingerSwitch;
    private TextView lockscreenFingerDescription;
    private TextView lockscreenDesignFull;
    private TextView lockscreenDesignLight;
    private TextView lockscreenDesignMinimal;
    private LinearLayout lockscreenDesignColorView;
    private TextView lockscreenDesignColorText;
    private Switch lockscreenDesignColorSwitch;
    private LinearLayout lockscreenDesignWallpaperView;
    private TextView lockscreenDesignWallpaperText;
    private Switch lockscreenDesignWallpaperSwitch;
    private LinearLayout lockscreenBootView;
    private TextView lockscreenBootText;
    private Switch lockscreenBootSwitch;
    private TextView lockscreenBootDescription;
    
    public static lockscreenFragment newInstance(int page) {
        lockscreenFragment fragment = new lockscreenFragment();
        Bundle args = new Bundle();
        args.putInt("currentPage", page);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentPage = getArguments().getInt("currentPage", 0);
        appPreferences = getActivity().getSharedPreferences("SETTING", MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        satApplication = (SATApplication) getContext().getApplicationContext();
        View rootView = inflater.inflate(R.layout.fragment_lockscreen, container, false);
        initializeId(rootView);
        initializeSaved();
        ((MainActivity)getActivity()).onLockscreenFragmentReady(this);

        return rootView;
    }

    public void onRefreshOrdered() {
        initializeSaved();
    }

    private void initializeId(View rootView) {
        lockscreenMasterView = rootView.findViewById(R.id.main_lockscreenMasterView);
        lockscreenMasterText = rootView.findViewById(R.id.main_lockscreenMasterText);
        lockscreenMasterSwitch = rootView.findViewById(R.id.main_lockscreenMasterSwitch);
        lockscreenMasterDescription = rootView.findViewById(R.id.main_lockscreenMasterDescription);
        lockscreenForegroundAlert = rootView.findViewById(R.id.main_lockscreenForegroundAlert);
        lockscreenModeSwipe = rootView.findViewById(R.id.main_lockscreenModeSwipe);
        lockscreenModePin = rootView.findViewById(R.id.main_lockscreenModePin);
        lockscreenModePattern = rootView.findViewById(R.id.main_lockscreenModePattern);
        lockscreenFingerSection = rootView.findViewById(R.id.main_lockscreenFingerprintSection);
        lockscreenFingerView = rootView.findViewById(R.id.main_lockscreenFingerprintView);
        lockscreenFingerText = rootView.findViewById(R.id.main_lockscreenFingerprintText);
        lockscreenFingerSwitch = rootView.findViewById(R.id.main_lockscreenFingerprintSwitch);
        lockscreenFingerDescription = rootView.findViewById(R.id.main_lockscreenFingerprintDescription);
        lockscreenDesignFull = rootView.findViewById(R.id.main_lockscreenDesignFull);
        lockscreenDesignLight = rootView.findViewById(R.id.main_lockscreenDesignLight);
        lockscreenDesignMinimal = rootView.findViewById(R.id.main_lockscreenDesignMinimal);
        lockscreenDesignColorView = rootView.findViewById(R.id.main_lockscreenColorView);
        lockscreenDesignColorText = rootView.findViewById(R.id.main_lockscreenColorText);
        lockscreenDesignColorSwitch = rootView.findViewById(R.id.main_lockscreenColorSwitch);
        lockscreenDesignWallpaperView = rootView.findViewById(R.id.main_lockscreenWallpaperView);
        lockscreenDesignWallpaperText = rootView.findViewById(R.id.main_lockscreenWallpaperText);
        lockscreenDesignWallpaperSwitch = rootView.findViewById(R.id.main_lockscreenWallpaperSwitch);
        lockscreenBootView = rootView.findViewById(R.id.main_lockscreenBootView);
        lockscreenBootText = rootView.findViewById(R.id.main_lockscreenBootText);
        lockscreenBootSwitch = rootView.findViewById(R.id.main_lockscreenBootSwitch);
        lockscreenBootDescription = rootView.findViewById(R.id.main_lockscreenBootDescription);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            lockscreenForegroundAlert.setVisibility(View.VISIBLE);
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            lockscreenFingerSection.setVisibility(View.GONE);
            lockscreenFingerView.setVisibility(View.GONE);
            lockscreenFingerDescription.setVisibility(View.GONE);
        } else {
            try {
                FingerprintManager fingerprintManager = (FingerprintManager) getContext().getSystemService(Context.FINGERPRINT_SERVICE);
                if (fingerprintManager == null || !fingerprintManager.isHardwareDetected()) {
                    lockscreenFingerSection.setVisibility(View.GONE);
                    lockscreenFingerView.setVisibility(View.GONE);
                    lockscreenFingerDescription.setVisibility(View.GONE);
                } else if (!fingerprintManager.hasEnrolledFingerprints()) {
                    lockscreenFingerText.setText(getString(R.string.main_lockscreen_fingerprint_notenroll));
                }
            } catch (Exception e) {
                e.printStackTrace();
                lockscreenFingerSection.setVisibility(View.GONE);
                lockscreenFingerView.setVisibility(View.GONE);
                lockscreenFingerDescription.setVisibility(View.GONE);
            }
        }
        lockscreenMasterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        if(!Settings.canDrawOverlays(getContext())) {
                            lockscreenMasterSwitch.setChecked(false);
                            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getContext().getPackageName()));
                            getContext().startActivity(intent);
                            Toast.makeText(getContext(), getString(R.string.setting_start_permission_mol_toast), Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                    appPreferences.edit().putInt("LOCKSCREEN_MASTER", 1).apply();
                    lockscreenMasterView.setSelected(true);
                    lockscreenMasterText.setText(getString(R.string.main_lockscreen_master_on));
                    lockscreenMasterText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                    lockscreenMasterDescription.setVisibility(View.VISIBLE);
                    appPreferences.edit().putInt("SERVICE_BOOT", 1).apply();
                    lockscreenBootSwitch.setChecked(true);
                    lockscreenBootView.setSelected(true);
                    lockscreenBootText.setText(getString(R.string.main_lockscreen_boot_on));
                    lockscreenBootText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                    lockscreenBootDescription.setVisibility(View.GONE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        appPreferences.edit().putInt("NOTIFICATION_MASTER", 1).apply();
                        ((MainActivity)getActivity()).onTalkFragments(true);
                    }
                    restartBackgroundService();
                } else {
                    appPreferences.edit().putInt("LOCKSCREEN_MASTER", 0).apply();
                    lockscreenMasterView.setSelected(false);
                    lockscreenMasterText.setText(getString(R.string.main_lockscreen_master_off));
                    lockscreenMasterText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                    lockscreenMasterDescription.setVisibility(View.GONE);
                    lockscreenBootDescription.setVisibility(View.GONE);
                    restartBackgroundService();
                }
            }
        });
        lockscreenModeSwipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setlockscreenMode(0);
            }
        });
        lockscreenModePin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).setLockPin();
            }
        });
        lockscreenModePattern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).setLockPattern();
            }
        });
        lockscreenFingerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (appPreferences.getInt("LOCKSCREEN_MODE", 0) == 0) {
                        lockscreenFingerSwitch.setChecked(false);
                        Toast.makeText(getContext(), getString(R.string.main_lockscreen_fingerprint_description), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    appPreferences.edit().putInt("LOCKSCREEN_FINGERPRINT", 1).apply();
                    lockscreenFingerView.setSelected(true);
                    lockscreenFingerText.setText(getString(R.string.main_lockscreen_fingerprint_on));
                    lockscreenFingerText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                } else {
                    appPreferences.edit().putInt("LOCKSCREEN_FINGERPRINT", 0).apply();
                    lockscreenFingerView.setSelected(false);
                    lockscreenFingerText.setText(getString(R.string.main_lockscreen_fingerprint_off));
                    lockscreenFingerText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                }
            }
        });
        lockscreenDesignFull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setlockscreenDesign(0);
            }
        });
        lockscreenDesignLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setlockscreenDesign(1);
            }
        });
        lockscreenDesignMinimal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setlockscreenDesign(2);
            }
        });
        if (satApplication.userUniversity == null) lockscreenDesignColorView.setVisibility(View.GONE);
        lockscreenDesignColorSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    appPreferences.edit().putInt("LOCKSCREEN_COLOR", 1).apply();
                    lockscreenDesignColorView.setSelected(true);
                    lockscreenDesignColorText.setText(getString(R.string.main_lockscreen_design_color_on));
                    lockscreenDesignColorText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                } else {
                    appPreferences.edit().putInt("LOCKSCREEN_COLOR", 0).apply();
                    lockscreenDesignColorView.setSelected(false);
                    lockscreenDesignColorText.setText(getString(R.string.main_lockscreen_design_color_off));
                    lockscreenDesignColorText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                }
            }
        });
        lockscreenDesignWallpaperSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    appPreferences.edit().putInt("LOCKSCREEN_WALLPAPER", 1).apply();
                    lockscreenDesignWallpaperView.setSelected(true);
                    lockscreenDesignWallpaperText.setText(getString(R.string.main_lockscreen_design_wallpaper_on));
                    lockscreenDesignWallpaperText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                } else {
                    appPreferences.edit().putInt("LOCKSCREEN_WALLPAPER", 0).apply();
                    lockscreenDesignWallpaperView.setSelected(false);
                    lockscreenDesignWallpaperText.setText(getString(R.string.main_lockscreen_design_wallpaper_off));
                    lockscreenDesignWallpaperText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                }
            }
        });
        lockscreenBootSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    appPreferences.edit().putInt("SERVICE_BOOT", 1).apply();
                    lockscreenBootView.setSelected(true);
                    lockscreenBootText.setText(getString(R.string.main_lockscreen_boot_on));
                    lockscreenBootText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                    lockscreenBootDescription.setVisibility(View.GONE);
                    ((MainActivity)getActivity()).onTalkFragments(true);
                } else {
                    appPreferences.edit().putInt("SERVICE_BOOT", 0).apply();
                    lockscreenBootView.setSelected(false);
                    lockscreenBootText.setText(getString(R.string.main_lockscreen_boot_off));
                    lockscreenBootText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                    if (appPreferences.getInt("LOCKSCREEN_MASTER", 0) == 1) lockscreenBootDescription.setVisibility(View.VISIBLE);
                    ((MainActivity)getActivity()).onTalkFragments(true);
                }
            }
        });
    }

    private void initializeSaved() {
        int savedLockscreenMaster = appPreferences.getInt("LOCKSCREEN_MASTER", 0);
        int savedLockscreenMode = appPreferences.getInt("LOCKSCREEN_MODE", 0);
        int savedLockscreenFingerPrint = appPreferences.getInt("LOCKSCREEN_FINGERPRINT", 0);
        int savedLockscreenDesign = appPreferences.getInt("LOCKSCREEN_DESIGN", 0);
        int savedLockscreenColor = appPreferences.getInt("LOCKSCREEN_COLOR", 0);
        int savedLockscreenWallpaper = appPreferences.getInt("LOCKSCREEN_WALLPAPER", 0);
        int savedLockscreenBoot = appPreferences.getInt("SERVICE_BOOT", 0);
        if (savedLockscreenMaster == 1) {
            lockscreenMasterSwitch.setChecked(true);
            lockscreenMasterView.setSelected(true);
            lockscreenMasterText.setText(getString(R.string.main_lockscreen_master_on));
            lockscreenMasterText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
            lockscreenMasterDescription.setVisibility(View.VISIBLE);
        } else {
            lockscreenMasterSwitch.setChecked(false);
            lockscreenMasterView.setSelected(false);
            lockscreenMasterText.setText(getString(R.string.main_lockscreen_master_off));
            lockscreenMasterText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
            lockscreenMasterDescription.setVisibility(View.GONE);
            lockscreenBootDescription.setVisibility(View.GONE);
        }
        setlockscreenMode(savedLockscreenMode);
        if (savedLockscreenFingerPrint == 1) {
            lockscreenFingerSwitch.setChecked(true);
            lockscreenFingerView.setSelected(true);
            lockscreenFingerText.setText(getString(R.string.main_lockscreen_fingerprint_on));
            lockscreenFingerText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
        } else {
            lockscreenFingerSwitch.setChecked(false);
            lockscreenFingerView.setSelected(false);
            lockscreenFingerText.setText(getString(R.string.main_lockscreen_fingerprint_off));
            lockscreenFingerText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        }
        setlockscreenDesign(savedLockscreenDesign);
        if (savedLockscreenColor == 1) {
            lockscreenDesignColorSwitch.setChecked(true);
            lockscreenDesignColorView.setSelected(true);
            lockscreenDesignColorText.setText(getString(R.string.main_lockscreen_design_color_on));
            lockscreenDesignColorText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
        } else {
            lockscreenDesignColorSwitch.setChecked(false);
            lockscreenDesignColorView.setSelected(false);
            lockscreenDesignColorText.setText(getString(R.string.main_lockscreen_design_color_off));
            lockscreenDesignColorText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        }
        if (savedLockscreenWallpaper == 1) {
            lockscreenDesignWallpaperSwitch.setChecked(true);
            lockscreenDesignWallpaperView.setSelected(true);
            lockscreenDesignWallpaperText.setText(getString(R.string.main_lockscreen_design_wallpaper_on));
            lockscreenDesignWallpaperText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
        } else {
            lockscreenDesignWallpaperSwitch.setChecked(false);
            lockscreenDesignWallpaperView.setSelected(false);
            lockscreenDesignWallpaperText.setText(getString(R.string.main_lockscreen_design_wallpaper_off));
            lockscreenDesignWallpaperText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        }
        if (savedLockscreenBoot == 1) {
            lockscreenBootSwitch.setChecked(true);
            lockscreenBootView.setSelected(true);
            lockscreenBootText.setText(getString(R.string.main_lockscreen_boot_on));
            lockscreenBootText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
            lockscreenBootDescription.setVisibility(View.GONE);
        } else {
            lockscreenBootSwitch.setChecked(false);
            lockscreenBootView.setSelected(false);
            lockscreenBootText.setText(getString(R.string.main_lockscreen_boot_off));
            lockscreenBootText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
            if (savedLockscreenMaster == 1) lockscreenBootDescription.setVisibility(View.VISIBLE);
        }
    }

    public void onPinSet() {
        setlockscreenMode(1);
    }

    public void onPatternSet() {
        setlockscreenMode(2);
    }

    private void setlockscreenMode(int modeCode) {
        appPreferences.edit().putInt("LOCKSCREEN_MODE", modeCode).apply();
        lockscreenModeSwipe.setSelected(false);
        lockscreenModeSwipe.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        lockscreenModePin.setSelected(false);
        lockscreenModePin.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        lockscreenModePattern.setSelected(false);
        lockscreenModePattern.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        switch (modeCode) {
            case 0:
                lockscreenModeSwipe.setSelected(true);
                lockscreenModeSwipe.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                appPreferences.edit().putInt("LOCKSCREEN_FINGERPRINT", 0).apply();
                lockscreenFingerSwitch.setChecked(false);
                lockscreenFingerView.setSelected(false);
                lockscreenFingerText.setText(getString(R.string.main_lockscreen_fingerprint_off));
                lockscreenFingerText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                break;
            case 1:
                lockscreenModePin.setSelected(true);
                lockscreenModePin.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                break;
            case 2:
                lockscreenModePattern.setSelected(true);
                lockscreenModePattern.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                break;
        }
    }

    private void setlockscreenDesign(int designCode) {
        appPreferences.edit().putInt("LOCKSCREEN_DESIGN", designCode).apply();
        lockscreenDesignFull.setSelected(false);
        lockscreenDesignFull.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        lockscreenDesignLight.setSelected(false);
        lockscreenDesignLight.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        lockscreenDesignMinimal.setSelected(false);
        lockscreenDesignMinimal.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        switch (designCode) {
            case 0:
                lockscreenDesignFull.setSelected(true);
                lockscreenDesignFull.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                break;
            case 1:
                lockscreenDesignLight.setSelected(true);
                lockscreenDesignLight.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                break;
            case 2:
                lockscreenDesignMinimal.setSelected(true);
                lockscreenDesignMinimal.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                break;
        }
    }

    private void restartBackgroundService() {
        try {
            Intent intent = new Intent(getContext(), BackgroundService.class);
            getContext().stopService(intent);
            getContext().startService(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
