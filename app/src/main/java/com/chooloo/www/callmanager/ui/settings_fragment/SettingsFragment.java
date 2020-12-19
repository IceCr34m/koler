package com.chooloo.www.callmanager.ui.settings_fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.CheckBoxPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.chooloo.www.callmanager.R;
import com.chooloo.www.callmanager.ui.main.MainActivity;
import com.chooloo.www.callmanager.util.PermissionUtils;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static android.Manifest.permission.READ_PHONE_STATE;

public class SettingsFragment extends PreferenceFragmentCompat implements SettingsMvpView {

    private SettingsPresenter<SettingsMvpView> mPresenter;

    public static SettingsFragment newInstance() {
        Bundle args = new Bundle();
        SettingsFragment fragment = new SettingsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUp();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference, rootKey);

        findPreference(getString(R.string.pref_app_color_key)).setOnPreferenceChangeListener((preference, newValue) -> {
            mPresenter.refresh();
            return mPresenter.onListPreferenceChange(preference, newValue);
        });

        findPreference(getString(R.string.pref_app_theme_key)).setOnPreferenceChangeListener((preference, newValue) -> {
            mPresenter.refresh();
            return mPresenter.onListPreferenceChange(preference, newValue);
        });

        findPreference(getString(R.string.pref_reject_call_timer_key)).setOnPreferenceChangeListener((preference, newValue) -> mPresenter.onListPreferenceChange(preference, newValue));
        findPreference(getString(R.string.pref_answer_call_timer_key)).setOnPreferenceChangeListener(((preference, newValue) -> mPresenter.onListPreferenceChange(preference, newValue)));
        findPreference(getString(R.string.pref_default_page_key)).setOnPreferenceChangeListener((preference, newValue) -> mPresenter.onListPreferenceChange(preference, newValue));
        findPreference(getString(R.string.pref_sim_select_key)).setOnPreferenceChangeListener((preference, newValue) -> mPresenter.onListPreferenceChange(preference, newValue));
        findPreference(getString(R.string.pref_is_biometric_key)).setOnPreferenceChangeListener((preference, newValue) -> mPresenter.onSwitchPreferenceChange(preference, newValue));

        PermissionUtils.checkPermissionsGranted(getContext(), new String[]{READ_PHONE_STATE}, true);
        setupSimSelection();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPresenter.onRequestPermissionResult(requestCode, grantResults);
    }

    @Override
    public String[] getPermissions() {
        return new String[0];
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.onDetach();
    }

    @Override
    public void setUp() {
        mPresenter = new SettingsPresenter<>();
        mPresenter.onAttach(this);
    }

    @Override
    public void setListPreferenceSummary(Preference preference, Object newValue) {
        ListPreference listPreference = (ListPreference) preference;
        CharSequence[] entries = listPreference.getEntries();
        listPreference.setSummary(entries[listPreference.findIndexOfValue((String) newValue)]);
    }

    @Override
    public void setCheckBoxPreferenceSummary(Preference preference, Object newValue) {
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) preference;
        checkBoxPreference.setSummary(checkBoxPreference.getSummary());
    }

    @Override
    public void setSwitchPreferenceSummary(Preference preference, Object newValue) {
        SwitchPreference switchPreference = (SwitchPreference) preference;
        switchPreference.setSummary(switchPreference.getSummary());
    }

    @Override
    public void setupSimSelection() {
        if (!PermissionUtils.checkPermissionsGranted(getContext(), new String[]{READ_PHONE_STATE}, true)) {
            Toast.makeText(getContext(), "No permission, please give permission to read phone state", Toast.LENGTH_LONG).show();
            return;
        }

        ListPreference simSelectionPreference = (ListPreference) findPreference(getString(R.string.pref_sim_select_key));

        @SuppressLint("MissingPermission")
        List<SubscriptionInfo> subscriptionInfoList = SubscriptionManager.from(getContext()).getActiveSubscriptionInfoList();
        int simCount = subscriptionInfoList.size();

        if (simCount == 1) {
            simSelectionPreference.setSummary(getString(R.string.pref_sim_select_disabled));
            simSelectionPreference.setEnabled(false);
        } else {
            List<CharSequence> simsEntries = new ArrayList<>();

            for (int i = 0; i < simCount; i++) {
                SubscriptionInfo si = subscriptionInfoList.get(i);
                Timber.i("Sim info " + i + " : " + si.getDisplayName());
                simsEntries.add(si.getDisplayName());
            }

            CharSequence[] simsEntriesList = simsEntries.toArray(new CharSequence[simsEntries.size()]);
            simSelectionPreference.setEntries(simsEntriesList);
            // simsEntries.add(getString(R.string.pref_sim_select_ask_entry));
            CharSequence[] simsEntryValues = {"0", "1"};
            simSelectionPreference.setEntryValues(simsEntryValues);
        }
    }

    @Override
    public void goToMainActivity() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean hasPermissions() {
        return true;
    }

    @Override
    public boolean hasPermission(String permission) {
        return true;
    }

    @Override
    public void askForPermissions() {
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showMessage(int stringResId) {
        showMessage(getString(stringResId));
    }

    @Override
    public void showError(String message) {
        showMessage(message);
    }

    @Override
    public void showError(int stringResId) {
        showMessage(stringResId);
    }
}
