package com.example.workoutappgroupproject.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.example.workoutappgroupproject.dialog.CustomDialogPreference;
import com.example.workoutappgroupproject.dialog.DialogPrefFragCompat;
import com.example.workoutappgroupproject.locale.LocaleHelper;
import com.example.workoutappgroupproject.R;
import com.example.workoutappgroupproject.UserDB.User;
import com.example.workoutappgroupproject.activity.MainActivity;
import com.example.workoutappgroupproject.viewmodel.ExerciseViewModel;
import com.example.workoutappgroupproject.viewmodel.UserViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;
import java.util.Locale;

public class SettingsFragment extends PreferenceFragmentCompat {

    private static final int USER_ID = 0;
    private UserViewModel userViewModel;
    private String units_value;
    private List<User> users;

    String name;
    float height;
    float weight;
    int age;
    String units;


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            setHasOptionsMenu(true);
            actionBar.setTitle(R.string.menu_settings);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                backToMain();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    // back to home
    private void backToMain() {
        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView);
        replaceFragment(new ProfileFragment(),1,false);
//        bottomNavigationView.getMenu().getItem(0).setEnabled(true);
        bottomNavigationView.getMenu().getItem(1).setEnabled(false);
//        bottomNavigationView.getMenu().getItem(2).setEnabled(true);
        bottomNavigationView.setSelectedItemId(R.id.profileFragment); // select bottom nav bar item
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // init view model
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.getIsExists().observe(getViewLifecycleOwner(), isExists -> {
            if (isExists) {
                userViewModel.getAllUsers().observe(getViewLifecycleOwner(), users -> this.users = users);
            }
        });

        String key_theme = getString(R.string.pref_key_theme);
        String key_units = getString(R.string.pref_key_units);
        String key_language = getString(R.string.pref_key_language);
        String key_clear = getString(R.string.pref_key_clear);
        Preference theme_pref = findPreference(key_theme);
        Preference units_pref = findPreference(key_units);
        Preference language_pref = findPreference(key_language);
        CustomDialogPreference clear_pref = findPreference(key_clear);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String selectedLanguage = sharedPreferences.getString(key_language,"en");
        // change app language
        if (language_pref != null) {

            String[] array_names = getResources().getStringArray(R.array.Language_names);
            String[] array_values = getResources().getStringArray(R.array.Language_values);
            // get each string position
            int array_pos = -1;
            for(int i=0; i < array_values.length; i++)
                if(array_values[i].contains(selectedLanguage))
                    array_pos = i;

            language_pref.setSummary(array_names[array_pos]);
            language_pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    // Get selected language
                    String selectedLanguage = newValue.toString();
//                    preference.setSummary(selectedLanguage);
                    LocaleHelper.setLocale(getContext(),selectedLanguage);

                    Resources resources = getResources();
                    DisplayMetrics displayMetrics = resources.getDisplayMetrics();
                    Configuration configuration = resources.getConfiguration();
                    configuration.locale = new Locale(selectedLanguage);
                    resources.updateConfiguration(configuration,displayMetrics);

                    requireActivity().finish();
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);

                    return true;
                }
            });
        }
        // app theme listener
        if (theme_pref != null) {
            theme_pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean isChecked = false;
                    if (newValue instanceof Boolean)
                        isChecked = (Boolean) newValue;

                    if (isChecked) {
                        getPreferenceManager().getSharedPreferences().edit().putBoolean(key_theme, true).apply();
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    } else {
                        getPreferenceManager().getSharedPreferences().edit().putBoolean(key_theme, false).apply();
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    }

                    requireActivity().finish();
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);

                    return true;
                }
            });
        }
        // BMI units listener
        if (units_pref != null) {
            units_pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                    boolean isChecked = false;
                    if (newValue instanceof Boolean)
                        isChecked = (Boolean) newValue;

                    if (isChecked) {
                        System.out.println("key_units: "+isChecked);
                        units_value = "us";
                        getPreferenceManager().getSharedPreferences().edit().putString("units", "us").apply();
                    } else {
                        System.out.println("key_units: "+isChecked);
                        units_value = "metric";
                        getPreferenceManager().getSharedPreferences().edit().putString("units", "metric").apply();
                    }
                    changeData(units_value);
                    System.out.println("units_value: "+units_value);
                    return true;
                }
            });
        }
//        // clear app data
//        if (clear_pref != null) {
//            clear_pref.setOnPreferenceClickListener(preference -> {
//                userViewModel.deleteAllUsers();
//                exerciseViewModel.deleteAllExerciseByType("custom");
//
//                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(requireContext());
//                settings.edit().clear().apply();
//
//                requireActivity().finish();
//                Intent intent = new Intent(getActivity(), MainActivity.class);
//                startActivity(intent);
//
//                return true;
//            });
//        }

        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.getMenu().getItem(0).setEnabled(true);
        bottomNavigationView.getMenu().getItem(1).setEnabled(true);
        bottomNavigationView.getMenu().getItem(2).setEnabled(true);
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        if (preference instanceof CustomDialogPreference) {
            DialogFragment dialogFragment = DialogPrefFragCompat.newInstance(preference.getKey());
            dialogFragment.setTargetFragment(this, 0);
            dialogFragment.show(getFragmentManager(), null);
        } else super.onDisplayPreferenceDialog(preference);
    }

    // change user data (units)
    private void changeData(String newValue) {
        int id = USER_ID; // <-- first User object in list
        if (users != null) {

            name = users.get(id).getName();
            height = users.get(id).getHeight();
            weight = users.get(id).getWeight();
            age = users.get(id).getAge();

            String units_db = users.get(id).getUnits();
            units = newValue;

            System.out.println("units: "+units);
            System.out.println("units_db: "+units_db);

            boolean pending = !units.equals(units_db);
            getPreferenceManager().getSharedPreferences().edit().putBoolean("units_pending", pending).apply();
            System.out.println("units update: "+pending);
        }
    }

    private void replaceFragment(Fragment fragment, int dir, boolean backStack){
        FragmentManager fragmentManager =getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (dir == 1) fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        else if (dir == -1) fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        else if (dir == 2) fragmentTransaction.setCustomAnimations(R.anim.enter_from_top,R.anim.exit_to_bottom, R.anim.enter_from_bottom, R.anim.exit_to_top);

        fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
        if (backStack) fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

    }

    @Override
    public void onPause() {
        super.onPause();
    }
}