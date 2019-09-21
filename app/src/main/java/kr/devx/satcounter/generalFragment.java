package kr.devx.satcounter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.yarolegovich.lovelydialog.LovelyChoiceDialog;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.text.SimpleDateFormat;
import java.util.Locale;

import kr.devx.satcounter.Util.DayUtil;
import kr.devx.satcounter.Util.UserSetting;

import static android.content.Context.MODE_PRIVATE;
import static kr.devx.satcounter.Util.UserSetting.DATE_FORMAT;

public class generalFragment extends Fragment {

    private SATApplication satApplication;
    private int currentPage;
    private SharedPreferences appPreferences;

    private TextView generalSupport, generalPremiun;
    private TextView generalSet;
    private LinearLayout generalView, generalModeView, generalDateView, generalDayLeftView, generalGoalView, generalSloganView, generalColorView, generalMessageView, generalImageView;
    private TextView generalMode, generalDate, generalDayLeft, generalGoal, generalSlogan, generalColor, generalMessage;
    private ImageView generalImage;
    private LinearLayout generalWidgetColorView;
    private TextView generalWidgetColorText;
    private Switch generalWidgetColorSwitch;
    private TextView generalFooterDev, generalFooterVersion, generalFooterLicense;

    private LinearLayout postAdHolder;

    public static generalFragment newInstance(int currentPage) {
        generalFragment fragment = new generalFragment();
        Bundle args = new Bundle();
        args.putInt("currentPage", currentPage);
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
        View rootView = inflater.inflate(R.layout.fragment_general, container, false);
        initializeId(rootView);
        initializedSaved();
        ((MainActivity)getActivity()).onGeneralFragmentReady(this);

        generalFooterVersion.setText(BuildConfig.VERSION_NAME);

        return rootView;
    }

    public void onUserPremium() {
        postAdHolder.setVisibility(View.GONE);
        generalPremiun.setVisibility(View.GONE);
    }

    private void initializeId(View rootView) {
        generalSupport = rootView.findViewById(R.id.main_generalSupport);
        generalPremiun = rootView.findViewById(R.id.main_generalPremium);
        generalSet = rootView.findViewById(R.id.main_generalSet);
        generalView = rootView.findViewById(R.id.main_generalView);
        generalModeView = rootView.findViewById(R.id.main_generalModeView);
        generalMode = rootView.findViewById(R.id.main_generalMode);
        generalDateView = rootView.findViewById(R.id.main_generalDateView);
        generalDate = rootView.findViewById(R.id.main_generalDate);
        generalDayLeftView = rootView.findViewById(R.id.main_generalLeftView);
        generalDayLeft = rootView.findViewById(R.id.main_generalLeft);
        generalGoalView = rootView.findViewById(R.id.main_generalGoalView);
        generalGoal = rootView.findViewById(R.id.main_generalGoal);
        generalSloganView = rootView.findViewById(R.id.main_generalSloganView);
        generalSlogan = rootView.findViewById(R.id.main_generalSlogan);
        generalColorView = rootView.findViewById(R.id.main_generalColorView);
        generalColor = rootView.findViewById(R.id.main_generalColor);
        generalMessageView = rootView.findViewById(R.id.main_generalMessageView);
        generalMessage = rootView.findViewById(R.id.main_generalMessage);
        generalImageView = rootView.findViewById(R.id.main_generalImageView);
        generalImage = rootView.findViewById(R.id.main_generalImage);
        generalWidgetColorView = rootView.findViewById(R.id.main_generalWidgetColorView);
        generalWidgetColorText = rootView.findViewById(R.id.main_generalWidgetColorText);
        generalWidgetColorSwitch = rootView.findViewById(R.id.main_generalWidgetColorSwitch);
        generalFooterDev = rootView.findViewById(R.id.main_generalFooterDev);
        generalFooterVersion = rootView.findViewById(R.id.main_generalFooterVersion);
        generalFooterLicense = rootView.findViewById(R.id.main_generalFooterLicense);
        postAdHolder = rootView.findViewById(R.id.main_generalAdView);

        generalSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LovelyStandardDialog(getContext(), LovelyStandardDialog.ButtonLayout.VERTICAL)
                        .setTopColorRes(R.color.colorAccent)
                        .setButtonsColorRes(R.color.colorBlack)
                        .setIcon(R.drawable.icon_mail)
                        .setTitle(R.string.support_title)
                        .setMessage(R.string.support_message)
                        .setPositiveButton(R.string.support_mail, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto","tiram2sue@naver.com", null));
                                emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                                startActivity(Intent.createChooser(emailIntent, "Send Email"));
                            }
                        })
                        .setNegativeButton(R.string.support_close, null)
                        .show();
            }
        });
        generalPremiun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] options = {getString(R.string.premium_rate), getString(R.string.premium_americano)};
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, android.R.id.text1, options);
                new LovelyChoiceDialog(getContext())
                        .setTopColorRes(R.color.colorAccent2)
                        .setTitle(R.string.premium_title)
                        .setIcon(R.drawable.icon_coffee)
                        .setMessage(R.string.premium_message)
                        .setItems(adapter, new LovelyChoiceDialog.OnItemSelectedListener<String>() {
                            @Override
                            public void onItemSelected(int position, String item) {
                                switch (position) {
                                    case 0:
                                        try {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getContext().getPackageName())));
                                        } catch (android.content.ActivityNotFoundException err) {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getContext().getPackageName())));
                                        }
                                        break;
                                    case 1:
                                        ((MainActivity)getActivity()).onUserPremiumRequest();
                                        break;
                                }
                            }
                        })
                        .show();
            }
        });
        generalSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).makeUserSetting(true);
            }
        });
        generalWidgetColorSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    appPreferences.edit().putInt("WIDGET_COLOR", 1).apply();
                    generalWidgetColorView.setSelected(true);
                    generalWidgetColorText.setText(getString(R.string.main_general_widget_color_on));
                    generalWidgetColorText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
                } else {
                    appPreferences.edit().putInt("WIDGET_COLOR", 0).apply();
                    generalWidgetColorView.setSelected(false);
                    generalWidgetColorText.setText(getString(R.string.main_general_widget_color_off));
                    generalWidgetColorText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                }
            }
        });
        generalFooterLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), OssLicensesMenuActivity.class));
            }
        });
    }

    private void initializedSaved() {
        int savedWidgetColor = appPreferences.getInt("WIDGET_COLOR", 0);
        if (savedWidgetColor == 1) {
            generalWidgetColorSwitch.setChecked(true);
            generalWidgetColorView.setSelected(true);
            generalWidgetColorText.setText(getString(R.string.main_general_widget_color_on));
            generalWidgetColorText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
        } else {
            generalWidgetColorSwitch.setChecked(false);
            generalWidgetColorView.setSelected(false);
            generalWidgetColorText.setText(getString(R.string.main_general_widget_color_off));
            generalWidgetColorText.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        }
    }

    public void onGetUserData(UserSetting userData) {
        generalView.setVisibility(View.VISIBLE);
        SimpleDateFormat settingDateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        generalMode.setText(userData.nameFromValue(getContext(), userData.getUserMode()));
        generalDate.setText(settingDateFormat.format(userData.getUserDate()));
        generalDayLeft.setText(getContext().getString(R.string.main_general_dayleft_prefix) + String.valueOf(DayUtil.getDaysLeft(userData.getUserDate())));
        generalGoal.setText(userData.getUserGoal());
        generalMessageView.setVisibility(View.GONE);
        generalMessage.setText(userData.getUserGoalOptional());
        if (!TextUtils.isEmpty(userData.getUserGoalOptional())) {
            generalMessageView.setVisibility(View.VISIBLE);
        }
        generalImageView.setVisibility(View.GONE);
        if (userData.getUserGoalImage() != null) {
            generalImageView.setVisibility(View.VISIBLE);
            RequestOptions options = new RequestOptions().fitCenter();
            Glide.with(this).applyDefaultRequestOptions(options).load(userData.getUserGoalImage()).into(generalImage);
        }
        generalSlogan.setText(null);
        generalSloganView.setVisibility(View.GONE);
        generalColor.setText(null);
        generalColorView.setVisibility(View.GONE);

        satApplication.userUniversity = null;
        if (userData.getUserMode() == UserSetting.USER_MODE.MODE_SUNEUNG) {
            String univSaveName = userData.getUserGoal();
            if (satApplication.KoreaUnivMap.containsKey(univSaveName)) {
                satApplication.userUniversity = satApplication.KoreaUnivMap.get(univSaveName);
                generalGoal.setText(satApplication.userUniversity.getName());
                generalSloganView.setVisibility(View.VISIBLE);
                generalSlogan.setText(satApplication.userUniversity.getSlogan());
                String mainColorHex = satApplication.userUniversity.getMainColorHex();
                String subColorHex = satApplication.userUniversity.getSubColorHex();
                String colorHtml = "<font color=\"" + mainColorHex + "\">" + mainColorHex + "</font>";
                if (subColorHex != null) colorHtml += "<br><font color=\"" + subColorHex + "\">" + subColorHex + "</font>";
                generalColorView.setVisibility(View.VISIBLE);
                generalColor.setText(Html.fromHtml(colorHtml));
            }
        }
        if (userData.getUserMode() == UserSetting.USER_MODE.MODE_SAT) {
            String univSaveName = userData.getUserGoal();
            if (satApplication.UsUnivMap.containsKey(univSaveName)) {
                satApplication.userUniversity = satApplication.UsUnivMap.get(univSaveName);
                generalGoal.setText(satApplication.userUniversity.getName());
                generalSloganView.setVisibility(View.VISIBLE);
                generalSlogan.setText(satApplication.userUniversity.getSlogan());
                String mainColorHex = satApplication.userUniversity.getMainColorHex();
                String subColorHex = satApplication.userUniversity.getSubColorHex();
                String colorHtml = "<font color=\"" + mainColorHex + "\">" + mainColorHex + "</font>";
                if (subColorHex != null) colorHtml += "<br><font color=\"" + subColorHex + "\">" + subColorHex + "</font>";
                generalColorView.setVisibility(View.VISIBLE);
                generalColor.setText(Html.fromHtml(colorHtml));
            }
        }

    }
}
