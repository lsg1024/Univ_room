package com.example.toyproject

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.toyproject.databinding.ActivityMainBinding
import com.example.toyproject.ui.map.MapFragment
import com.example.toyproject.ui.userpage.UserPageFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class MainActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityMainBinding
    private val binding get() = _binding
    var wait:Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val tabLayout: TabLayout = binding.navView
        val viewPager2 : ViewPager2 = binding.viewPager2

        viewPager2.apply {
            adapter = FragmentAdapter(context as MainActivity, )
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

    private inner class FragmentAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> {
                    val mapFragment = MapFragment()
                    when {

                        intent.hasExtra("mainIntent") -> {
                            val bundle = intent.getBundleExtra("mainIntent")
                            mapFragment.arguments = bundle
                        }

                        intent.hasExtra("topIntent")-> {
                            val bundle = intent.getBundleExtra("topIntent")
                            mapFragment.arguments = bundle
                        }
                    }
                    mapFragment
                }else -> {
                    UserPageFragment()
                }

            }
        }
    }

    companion object{
        private lateinit var binding: ActivityMainBinding

        fun hideNavi(state: Boolean){
            if (state) binding.navView.visibility = View.GONE else binding.navView.visibility = View.VISIBLE
        }
    }

}

