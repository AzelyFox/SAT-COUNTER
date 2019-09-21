package kr.devx.satcounter;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.Context.MODE_PRIVATE;

public class floatingviewFragment extends Fragment {

    private SATApplication satApplication;
    private int currentPage;
    private SharedPreferences appPreferences;
    
    private LinearLayout floatingMasterView;
    private TextView floatingMasterText;
    private Switch floatingMasterSwitch;
    private LinearLayout floatingModeSection;
    private LinearLayout floatingModeView;
    private TextView floatingModeMove;
    private TextView floatingModeFixed;
    private TextView floatingModeDescription;
    private TextView floatingDesignBox;
    private TextView floatingDesignImage;
    private TextView floatingDesignText;
    private TextView floatingDesignMinimal;
    private LinearLayout floatingDesignColorView;
    private TextView floatingDesignColorText;
    private Switch floatingDesignColorSwitch;
    private SeekBar floatingSizeSeekBar;
    private LinearLayout floatingBootView;
    private TextView floatingBootText;
    private Switch floatingBootSwitch;

    public static floatingviewFragment newInstance(int page) {
        floatingviewFragment fragment = new floatingviewFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_floatingview, container, false);
        initializeId(rootView);
        initializeSaved();
        ((MainActivity)getActivity()).onFloatingViewFragmentReady(this);

        return rootView;
    }

    public void onRefreshOrdered() {
        initializeSaved();
    }

    private void initializeId(View rootView) {
        floatingMasterView = rootView.findViewById(R.id.main_floatingMasterView);
        floatingMasterText = rootView.findViewById(R.id.main_floatingMasterText);
        floatingMasterSwitch = rootView.findViewById(R.id.main_floatingMasterSwitch);
        floatingModeSection = rootView.findViewById(R.id.main_floatingModeSection);
        floatingModeView = rootView.findViewById(R.id.main_floatingModeView);
        floatingModeMove = rootView.findViewById(R.id.main_floatingModeMove);
        floatingModeFixed = rootView.findViewById(R.id.main_floatingModeFixed);
        floatingModeDescription = rootView.findViewById(R.id.main_floatingModeDescription);
        floatingDesignBox = rootView.findViewById(R.id.main_floatingDesignBox);
        floatingDesignImage = rootView.findViewById(R.id.main_floatingDesignImage);
        floatingDesignText = rootView.findViewById(R.id.main_floatingDesignText);
        floatingDesignMinimal = rootView.findViewById(R.id.main_floatingDesignMinimal);
        floatingDesignColorView = rootView.findViewById(R.id.main_floatingColorView);
        floatingDesignColorText = rootView.findViewById(R.id.main_floatingColorText);
        floatingDesignColorSwitch = rootView.findViewById(R.id.main_floatingColorSwitch);
        floatingSizeSeekBar = rootView.findViewById(R.id.main_floatingSizeSeekBar);
        floatingBootView = rootView.findViewById(R.id.main_floatingBootView);
        floatingBootText = rootView.findViewById(R.id.main_floatingBootText);
        floatingBootSwitch = rootView.findViewById(R.id.main_floatingBootSwitch);

        floatingMasterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        if(!Settings.canDrawOverlays(getContext())) {
                            floatingMasterSwitch.setChecked(false);
                            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getContext().getPackageName()));
                            getContext().startActivity(intent);
                            Toast.makeText(getContext(), getString(R.string.setting_start_permission_mol_toast), Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                    if (appPreferences.getInt("FLOATING_DESIGN_BOX", 0) == 0 && appPreferences.getInt("FLOATING_DESIGN_IMAGE", 0) == 0
                        && appPreferences.getInt("FLOATING_DESIGN_TEXT", 0) == 0 && appPreferences.getInt("FLOATING_DESIGN_MINIMAL", 0) == 0) {
                        floatingMasterSwitch.setChecked(false);
                        Toast.makeText(getContext(), getString(R.string.main_floating_master_design), Toast.LENGTH_LONG).show();
                        return;
                    }
                    appPreferences.edit().putInt("FLOATING_MASTER", 1).apply();
                    floatingMasterView.setSelected(true);
                    floatingMasterText.setText(getString(R.string.main_floating_master_on));
                    floatingMasterText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                    appPreferences.edit().putInt("SERVICE_BOOT", 1).apply();
                    floatingBootSwitch.setChecked(true);
                    floatingBootView.setSelected(true);
                    floatingBootText.setText(getString(R.string.main_floating_boot_on));
                    floatingBootText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                    restartBackgroundService();
                } else {
                    appPreferences.edit().putInt("FLOATING_MASTER", 0).apply();
                    floatingMasterView.setSelected(false);
                    floatingMasterText.setText(getString(R.string.main_floating_master_off));
                    floatingMasterText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                    restartBackgroundService();
                }
            }
        });
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            floatingModeSection.setVisibility(View.GONE);
            floatingModeView.setVisibility(View.GONE);
            floatingModeDescription.setVisibility(View.GONE);
        }
        floatingModeMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFloatingMode(0);
                restartBackgroundService();
            }
        });
        floatingModeFixed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFloatingMode(1);
                restartBackgroundService();
            }
        });
        floatingDesignBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!floatingDesignBox.isSelected()) {
                    appPreferences.edit().putInt("FLOATING_DESIGN_BOX", 1).apply();
                    floatingDesignBox.setSelected(true);
                    floatingDesignBox.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                    restartBackgroundService();
                } else {
                    appPreferences.edit().putInt("FLOATING_DESIGN_BOX", 0).apply();
                    floatingDesignBox.setSelected(false);
                    floatingDesignBox.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                    restartBackgroundService();
                }
            }
        });
        floatingDesignImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!floatingDesignImage.isSelected()) {
                    appPreferences.edit().putInt("FLOATING_DESIGN_IMAGE", 1).apply();
                    floatingDesignImage.setSelected(true);
                    floatingDesignImage.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                    restartBackgroundService();
                } else {
                    appPreferences.edit().putInt("FLOATING_DESIGN_IMAGE", 0).apply();
                    floatingDesignImage.setSelected(false);
                    floatingDesignImage.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                    restartBackgroundService();
                }
            }
        });
        floatingDesignText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!floatingDesignText.isSelected()) {
                    appPreferences.edit().putInt("FLOATING_DESIGN_TEXT", 1).apply();
                    floatingDesignText.setSelected(true);
                    floatingDesignText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                    restartBackgroundService();
                } else {
                    appPreferences.edit().putInt("FLOATING_DESIGN_TEXT", 0).apply();
                    floatingDesignText.setSelected(false);
                    floatingDesignText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                    restartBackgroundService();
                }
            }
        });
        floatingDesignMinimal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!floatingDesignMinimal.isSelected()) {
                    appPreferences.edit().putInt("FLOATING_DESIGN_MINIMAL", 1).apply();
                    floatingDesignMinimal.setSelected(true);
                    floatingDesignMinimal.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                    restartBackgroundService();
                } else {
                    appPreferences.edit().putInt("FLOATING_DESIGN_MINIMAL", 0).apply();
                    floatingDesignMinimal.setSelected(false);
                    floatingDesignMinimal.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                    restartBackgroundService();
                }
            }
        });
        floatingDesignColorSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    appPreferences.edit().putInt("FLOATING_COLOR", 1).apply();
                    floatingDesignColorView.setSelected(true);
                    floatingDesignColorText.setText(getString(R.string.main_floating_design_color_on));
                    floatingDesignColorText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                    restartBackgroundService();
                } else {
                    appPreferences.edit().putInt("FLOATING_COLOR", 0).apply();
                    floatingDesignColorView.setSelected(false);
                    floatingDesignColorText.setText(getString(R.string.main_floating_design_color_off));
                    floatingDesignColorText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                    restartBackgroundService();
                }
            }
        });
        floatingSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                appPreferences.edit().putInt("FLOATING_SIZE", progress).apply();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                restartBackgroundService();
            }
        });
        floatingBootSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    appPreferences.edit().putInt("SERVICE_BOOT", 1).apply();
                    floatingBootView.setSelected(true);
                    floatingBootText.setText(getString(R.string.main_floating_boot_on));
                    floatingBootText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                    ((MainActivity)getActivity()).onTalkFragments(true);
                } else {
                    appPreferences.edit().putInt("SERVICE_BOOT", 0).apply();
                    floatingBootView.setSelected(false);
                    floatingBootText.setText(getString(R.string.main_floating_boot_off));
                    floatingBootText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                    ((MainActivity)getActivity()).onTalkFragments(true);
                }
            }
        });
    }

    private void initializeSaved() {
        int savedFloatingMaster = appPreferences.getInt("FLOATING_MASTER", 0);
        if (savedFloatingMaster == 1) {
            floatingMasterSwitch.setChecked(true);
            floatingMasterView.setSelected(true);
            floatingMasterText.setText(getString(R.string.main_floating_master_on));
            floatingMasterText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
        } else {
            floatingMasterSwitch.setChecked(false);
            floatingMasterView.setSelected(false);
            floatingMasterText.setText(getString(R.string.main_floating_master_off));
            floatingMasterText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        }
        int savedFloatingMode = appPreferences.getInt("FLOATING_MODE", 0);
        setFloatingMode(savedFloatingMode);
        int savedFloatingDesignBox = appPreferences.getInt("FLOATING_DESIGN_BOX", 0);
        if (savedFloatingDesignBox == 1) {
            floatingDesignBox.setSelected(true);
            floatingDesignBox.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
        } else {
            floatingDesignBox.setSelected(false);
            floatingDesignBox.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        }
        int savedFloatingDesignImage = appPreferences.getInt("FLOATING_DESIGN_IMAGE", 0);
        if (savedFloatingDesignImage == 1) {
            floatingDesignImage.setSelected(true);
            floatingDesignImage.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
        } else {
            floatingDesignImage.setSelected(false);
            floatingDesignImage.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        }
        int savedFloatingDesignText = appPreferences.getInt("FLOATING_DESIGN_TEXT", 0);
        if (savedFloatingDesignText == 1) {
            floatingDesignText.setSelected(true);
            floatingDesignText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
        } else {
            floatingDesignText.setSelected(false);
            floatingDesignText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        }
        int savedFloatingDesignMinimal = appPreferences.getInt("FLOATING_DESIGN_MINIMAL", 0);
        if (savedFloatingDesignMinimal == 1) {
            floatingDesignMinimal.setSelected(true);
            floatingDesignMinimal.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
        } else {
            floatingDesignMinimal.setSelected(false);
            floatingDesignMinimal.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        }
        if (satApplication.userUniversity == null) floatingDesignColorView.setVisibility(View.GONE);
        int savedFloatingDesignColor = appPreferences.getInt("FLOATING_COLOR", 0);
        if (savedFloatingDesignColor == 1) {
            floatingDesignColorSwitch.setChecked(true);
            floatingDesignColorView.setSelected(true);
            floatingDesignColorText.setText(getString(R.string.main_floating_design_color_on));
            floatingDesignColorText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
        } else {
            floatingDesignColorSwitch.setChecked(false);
            floatingDesignColorView.setSelected(false);
            floatingDesignColorText.setText(getString(R.string.main_floating_design_color_off));
            floatingDesignColorText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        }
        int savedFloatingSize = appPreferences.getInt("FLOATING_SIZE", 0);
        floatingSizeSeekBar.setProgress(savedFloatingSize);
        int savedFloatingBoot = appPreferences.getInt("SERVICE_BOOT", 0);
        if (savedFloatingBoot == 1) {
            floatingBootSwitch.setChecked(true);
            floatingBootView.setSelected(true);
            floatingBootText.setText(getString(R.string.main_floating_boot_on));
            floatingBootText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
        } else {
            floatingBootSwitch.setChecked(false);
            floatingBootView.setSelected(false);
            floatingBootText.setText(getString(R.string.main_floating_boot_off));
            floatingBootText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        }
    }

    private void setFloatingMode(int modeCode) {
        appPreferences.edit().putInt("FLOATING_MODE", modeCode).apply();
        floatingModeMove.setSelected(false);
        floatingModeMove.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        floatingModeFixed.setSelected(false);
        floatingModeFixed.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        switch (modeCode) {
            case 0:
                floatingModeMove.setSelected(true);
                floatingModeMove.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                floatingModeDescription.setText(getString(R.string.main_floating_mode_move_description));
                break;
            case 1:
                floatingModeFixed.setSelected(true);
                floatingModeFixed.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                floatingModeDescription.setText(getString(R.string.main_floating_mode_fixed_description));
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
