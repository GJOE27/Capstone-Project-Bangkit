package com.example.nutrisee.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import com.example.nutrisee.R
import com.example.nutrisee.databinding.ActivitySettingBinding
import java.util.Locale

class SettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
    }

    private fun setupAction() {
        binding.apply {
            btnLanguage.text = Locale.getDefault().language
            btnLanguage.setOnClickListener {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }
            btnBack.setOnClickListener {
                val intent = Intent(this@SettingActivity, HomeActivity::class.java)
                startActivity(intent)
            }
        }
    }

}