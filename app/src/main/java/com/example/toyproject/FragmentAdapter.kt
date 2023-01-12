package com.example.toyproject

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.toyproject.ui.map.MapFragment
import com.example.toyproject.ui.userpage.UserPageFragment

class FragmentAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

    private val pages = 2
    override fun getItemCount(): Int = pages

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> MapFragment()
            else -> UserPageFragment()
        }
    }
}