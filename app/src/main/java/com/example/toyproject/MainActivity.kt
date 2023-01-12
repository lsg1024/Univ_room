package com.example.toyproject

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.toyproject.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.security.MessageDigest


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    var wait:Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val tabLayout: TabLayout = binding.navView
        val viewPager2 : ViewPager2 = binding.viewPager2

        viewPager2.apply {
            adapter = FragmentAdapter(context as MainActivity)
            isUserInputEnabled = false
        }

        TabLayoutMediator(tabLayout, viewPager2){tab, position ->

            when(position) {
                0 -> {
                    tab.text = "지도"
                    tab.setIcon(R.drawable.baseline_map_24)
                }
                1 -> {
                    tab.text = "사용자"
                    tab.setIcon(R.drawable.baseline_account_circle_24)
                }
            }
        }.attach()

    }


    override fun onBackPressed() {
        if (System.currentTimeMillis() - wait >= 2000) {
            wait = System.currentTimeMillis()
            Snackbar.make(binding.root, "뒤로가기 버튼을 한번 더 누르면 종료됩니다.", Snackbar.LENGTH_LONG).show()
        } else {
            finish()
        }
    }


}