package com.example.myapplicationevent.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.myapplicationevent.R
import com.example.myapplicationevent.ui.setting.SettingPreferences
import kotlinx.coroutines.launch

class SettingFragment : Fragment() {

    private lateinit var settingPreferences: SettingPreferences
    private lateinit var switchDarkMode: Switch

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        switchDarkMode = view.findViewById(R.id.switch_dark_mode)
        settingPreferences = SettingPreferences(requireContext())

        lifecycleScope.launch {
            settingPreferences.darkModeFlow.collect { isDarkMode ->
                switchDarkMode.isChecked = isDarkMode
                if (isDarkMode) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
        }

        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                settingPreferences.saveDarkMode(isChecked)
            }
        }
    }
}
