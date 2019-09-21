package kr.devx.satcounter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import kr.devx.satcounter.Util.UserSetting;

import static android.content.Context.MODE_PRIVATE;

public class notificationFragment extends Fragment {

    private SATApplication satApplication;
    private int currentPage;
    private SharedPreferences appPreferences;

    private LinearLayout notificationMasterView;
    private TextView notificationMasterText;
    private Switch notificationMasterSwitch;
    private TextView notificationDesignClassic;
    private TextView notificationDesignNew;
    private TextView notificationDesignSimple;
    private LinearLayout notificationDesignColorView;
    private TextView notificationDesignColorText;
    private Switch notificationDesignColorSwitch;
    private LinearLayout notificationBootView;
    private TextView notificationBootText;
    private Switch notificationBootSwitch;
    private LinearLayout notificationWordView;
    private TextView notificationWordText;
    private Switch notificationWordSwitch;

    public static notificationFragment newInstance(int page) {
        notificationFragment fragment = new notificationFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_notification, container, false);
        initializeId(rootView);
        initializeSaved();
        ((MainActivity)getActivity()).onNotificationFragmentReady(this);

        return rootView;
    }

    public void onGetUserData(UserSetting userData) {
        restartBackgroundService();
    }

    public void onRefreshOrdered() {
        initializeSaved();
    }

    private void initializeId(View rootView) {
        notificationMasterView = rootView.findViewById(R.id.main_notificationMasterView);
        notificationMasterText = rootView.findViewById(R.id.main_notificationMasterText);
        notificationMasterSwitch = rootView.findViewById(R.id.main_notificationMasterSwitch);
        notificationDesignClassic = rootView.findViewById(R.id.main_notificationDesignClassic);
        notificationDesignNew = rootView.findViewById(R.id.main_notificationDesignNew);
        notificationDesignSimple = rootView.findViewById(R.id.main_notificationDesignSimple);
        notificationDesignColorView = rootView.findViewById(R.id.main_notificationColorView);
        notificationDesignColorText = rootView.findViewById(R.id.main_notificationColorText);
        notificationDesignColorSwitch = rootView.findViewById(R.id.main_notificationColorSwitch);
        notificationBootView = rootView.findViewById(R.id.main_notificationBootView);
        notificationBootText = rootView.findViewById(R.id.main_notificationBootText);
        notificationBootSwitch = rootView.findViewById(R.id.main_notificationBootSwitch);
        notificationWordView = rootView.findViewById(R.id.main_notificationWordView);
        notificationWordText = rootView.findViewById(R.id.main_notificationWordText);
        notificationWordSwitch = rootView.findViewById(R.id.main_notificationWordSwitch);

        notificationMasterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    appPreferences.edit().putInt("NOTIFICATION_MASTER", 1).apply();
                    notificationMasterView.setSelected(true);
                    notificationMasterText.setText(getString(R.string.main_notification_master_on));
                    notificationMasterText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                    appPreferences.edit().putInt("SERVICE_BOOT", 1).apply();
                    notificationBootSwitch.setChecked(true);
                    notificationBootView.setSelected(true);
                    notificationBootText.setText(getString(R.string.main_notification_boot_on));
                    notificationBootText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                    restartBackgroundService();
                } else {
                    appPreferences.edit().putInt("NOTIFICATION_MASTER", 0).apply();
                    notificationMasterView.setSelected(false);
                    notificationMasterText.setText(getString(R.string.main_notification_master_off));
                    notificationMasterText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                    restartBackgroundService();
                }
            }
        });
        notificationDesignClassic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNotificationDesign(0);
                restartBackgroundService();
            }
        });
        notificationDesignNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNotificationDesign(1);
                restartBackgroundService();
            }
        });
        notificationDesignSimple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNotificationDesign(2);
                restartBackgroundService();
            }
        });
        notificationDesignColorSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    appPreferences.edit().putInt("NOTIFICATION_COLOR", 1).apply();
                    notificationDesignColorView.setSelected(true);
                    notificationDesignColorText.setText(getString(R.string.main_notification_design_color_on));
                    notificationDesignColorText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                    restartBackgroundService();
                } else {
                    appPreferences.edit().putInt("NOTIFICATION_COLOR", 0).apply();
                    notificationDesignColorView.setSelected(false);
                    notificationDesignColorText.setText(getString(R.string.main_notification_design_color_off));
                    notificationDesignColorText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                    restartBackgroundService();
                }
            }
        });
        notificationBootSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    appPreferences.edit().putInt("SERVICE_BOOT", 1).apply();
                    notificationBootView.setSelected(true);
                    notificationBootText.setText(getString(R.string.main_notification_boot_on));
                    notificationBootText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                    ((MainActivity)getActivity()).onTalkFragments(true);
                } else {
                    appPreferences.edit().putInt("SERVICE_BOOT", 0).apply();
                    notificationBootView.setSelected(false);
                    notificationBootText.setText(getString(R.string.main_notification_boot_off));
                    notificationBootText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                    ((MainActivity)getActivity()).onTalkFragments(true);
                }
            }
        });
        notificationWordSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    appPreferences.edit().putInt("NOTIFICATION_WORD", 1).apply();
                    notificationWordView.setSelected(true);
                    notificationWordText.setText(getString(R.string.main_notification_word_on));
                    notificationWordText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                    restartBackgroundService();
                } else {
                    appPreferences.edit().putInt("NOTIFICATION_WORD", 0).apply();
                    notificationWordView.setSelected(false);
                    notificationWordText.setText(getString(R.string.main_notification_word_off));
                    notificationWordText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                    restartBackgroundService();
                }
            }
        });
    }

    private void initializeSaved() {
        int savedNotificationMaster = appPreferences.getInt("NOTIFICATION_MASTER", 0);
        if (savedNotificationMaster == 1) {
            notificationMasterSwitch.setChecked(true);
            notificationMasterView.setSelected(true);
            notificationMasterText.setText(getString(R.string.main_notification_master_on));
            notificationMasterText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
        } else {
            notificationMasterSwitch.setChecked(false);
            notificationMasterView.setSelected(false);
            notificationMasterText.setText(getString(R.string.main_notification_master_off));
            notificationMasterText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        }
        int savedNotificationDesign = appPreferences.getInt("NOTIFICATION_DESIGN", 0);
        setNotificationDesign(savedNotificationDesign);
        int savedNotificationBoot = appPreferences.getInt("SERVICE_BOOT", 0);
        if (savedNotificationBoot == 1) {
            notificationBootSwitch.setChecked(true);
            notificationBootView.setSelected(true);
            notificationBootText.setText(getString(R.string.main_notification_boot_on));
            notificationBootText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
        } else {
            notificationBootSwitch.setChecked(false);
            notificationBootView.setSelected(false);
            notificationBootText.setText(getString(R.string.main_notification_boot_off));
            notificationBootText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        }
        int savedNotificationWord = appPreferences.getInt("NOTIFICATION_WORD", 0);
        if (savedNotificationWord == 1) {
            notificationWordSwitch.setChecked(true);
            notificationWordView.setSelected(true);
            notificationWordText.setText(getString(R.string.main_notification_word_on));
            notificationWordText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
        } else {
            notificationWordSwitch.setChecked(false);
            notificationWordView.setSelected(false);
            notificationWordText.setText(getString(R.string.main_notification_word_off));
            notificationWordText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        }
        int savedNotificationColor = appPreferences.getInt("NOTIFICATION_COLOR", 0);
        if (savedNotificationColor == 1) {
            notificationDesignColorSwitch.setChecked(true);
            notificationDesignColorView.setSelected(true);
            notificationDesignColorText.setText(getString(R.string.main_notification_design_color_on));
            notificationDesignColorText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
        } else {
            notificationDesignColorSwitch.setChecked(false);
            notificationDesignColorView.setSelected(false);
            notificationDesignColorText.setText(getString(R.string.main_notification_design_color_off));
            notificationDesignColorText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        }
    }

    private void setNotificationDesign(int designCode) {
        appPreferences.edit().putInt("NOTIFICATION_DESIGN", designCode).apply();
        notificationDesignClassic.setSelected(false);
        notificationDesignClassic.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        notificationDesignNew.setSelected(false);
        notificationDesignNew.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        notificationDesignSimple.setSelected(false);
        notificationDesignSimple.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        switch (designCode) {
            case 0:
                notificationDesignColorView.setVisibility(View.GONE);
                notificationDesignClassic.setSelected(true);
                notificationDesignClassic.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                break;
            case 1:
                if (satApplication.userUniversity != null) notificationDesignColorView.setVisibility(View.VISIBLE);
                notificationDesignNew.setSelected(true);
                notificationDesignNew.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                break;
            case 2:
                if (satApplication.userUniversity != null) notificationDesignColorView.setVisibility(View.VISIBLE);
                notificationDesignSimple.setSelected(true);
                notificationDesignSimple.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
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
